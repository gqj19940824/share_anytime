package com.unity.me.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.me.client.RbacMeClient;
import com.unity.me.client.SystemMeClient;
import com.unity.me.entity.GroupInfo;
import com.unity.me.entity.UsersGroupInfo;
import com.unity.me.pojos.GroupInfoPO;
import com.unity.me.service.GroupInfoServiceImpl;
import com.unity.me.service.UsersGroupInfoServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分组信息
 * @author creator
 * 生成时间 2019-02-12 12:47:34
 */
@Controller
@RequestMapping("/groupinfo")
public class GroupInfoController extends BaseWebController {
    @Autowired
    GroupInfoServiceImpl service;
    @Autowired
    UsersGroupInfoServiceImpl usersGroupInfoService;
    @Autowired
    RbacMeClient rbacClient;

    @Autowired
    SystemMeClient systemClient;

    /**
     * 模块入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @return 返回视图
     */
    @RequestMapping("/view/moduleEntrance/{iframe}")
    public String moduleEntrance(Model model,@PathVariable("iframe") String iframe) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("button", JSON.toJSONString(rbacClient.getMenuButton(iframe)));
        return "GroupInfoList";
    }

    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id 分组信息id
     * @return 返回视图
     */
    @RequestMapping(value = "/view/editEntrance/{iframe}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        String dataResource = rbacClient.getDataResource();
        model.addAttribute("dateResource", dataResource);

        if(id!=null){
            GroupInfo entity = service.getById(id);
            if(entity==null) {
                model.addAttribute("entity", "{}");
                model.addAttribute("longList2", "[]");
            } else {
                List<UsersGroupInfo> receiverIds = usersGroupInfoService.list(new QueryWrapper<UsersGroupInfo>().lambda()
                        .eq(UsersGroupInfo::getIdMeGroupInfo, id));
                List<Long> longList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(receiverIds)){
                    longList = receiverIds.parallelStream().map(UsersGroupInfo::getRbacUserId).collect(Collectors.toList());
                }
                model.addAttribute("entity", JSON.toJSONString(convert2Map(entity)));
                model.addAttribute("longList2", JSON.toJSONString(longList));
            }
        }
        else{
            model.addAttribute("entity", "{}");
            model.addAttribute("longList2", "[]");
        }
        return "GroupInfoEdit";
    }
    
     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<GroupInfo> ew = wrapper(search);
        Customer customer = LoginContextHolder.getRequestAttributes();
        String s = customer.getId() + "." + customer.getName();
        ew.eq(GroupInfo::getCreator ,s);
        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    
         /**
     * 添加或修改
     * @param groupInfoPO 分组信息实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody GroupInfoPO groupInfoPO) {
        if(StringUtils.isEmpty(groupInfoPO.getGroupName())){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST,"分组名称为空");
        }
        List<Long> usersGroupInfoList = groupInfoPO.getUsersGroupInfoList();
        if (CollectionUtils.isEmpty(usersGroupInfoList)){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST,"分组人员为空");
        }
        Customer customer = LoginContextHolder.getRequestAttributes();

        GroupInfo groupInfo = new GroupInfo();
        if (groupInfoPO.getId() != null){
            groupInfo.setId(groupInfoPO.getId());
            //消息的更新人
        }
        groupInfo.setGroupName(groupInfoPO.getGroupName());
        groupInfo.setNotes(groupInfoPO.getNotes());
        service.saveOrUpdate(groupInfo);
        List<UsersGroupInfo> usersGroupInfos = new ArrayList<>();

        if (groupInfoPO.getId() == null){
            for (Long usersGroupInfo : usersGroupInfoList) {
                UsersGroupInfo usersGroupInfo1 = new UsersGroupInfo();
                usersGroupInfo1.setRbacUserId(usersGroupInfo);
                usersGroupInfo1.setIdMeGroupInfo(groupInfo.getId());
                usersGroupInfo1.setNotes("备注");
                usersGroupInfos.add(usersGroupInfo1);
            }
        }else {
            List<UsersGroupInfo> receiverIds = usersGroupInfoService.list(new QueryWrapper<UsersGroupInfo>().lambda()
                    .eq(UsersGroupInfo::getIdMeGroupInfo, groupInfo.getId()));
            if (CollectionUtils.isNotEmpty(receiverIds)){
                List<Long> longList = receiverIds.parallelStream().map(UsersGroupInfo::getId).collect(Collectors.toList());
                usersGroupInfoService.removeByIds(longList);
            }
            for (Long usersGroupInfo : usersGroupInfoList) {
                UsersGroupInfo usersGroupInfo1 = new UsersGroupInfo();
                usersGroupInfo1.setIdMeGroupInfo(groupInfo.getId());
                usersGroupInfo1.setRbacUserId(usersGroupInfo);
                usersGroupInfo1.setNotes("备注");
                usersGroupInfos.add(usersGroupInfo1);
            }
        }
        usersGroupInfoService.saveOrUpdateBatch(usersGroupInfos);
        return success(InnovationConstant.SUCCESS);
    }

    
     /**
     * 获取数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<GroupInfo> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2List(list)).build();
        return success(result);

    }

    /**
     * 查询条件转换
     * @param search 统一查询对象
     * @return
     */
    private LambdaQueryWrapper<GroupInfo> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<GroupInfo> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule("gmtCreate").forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule("gmtModified").forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(GroupInfo.class);

        }
        else{
            ew = new LambdaQueryWrapper<GroupInfo>();
        }

        ew.orderBy(true, false,GroupInfo::getSort);
        
        return ew;
    }
    
    
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<GroupInfo> list){
       
        return JsonUtil.<GroupInfo>ObjectToList(list,
                new String[]{ "id","sort","notes","groupName" }, 
                (m, entity) -> {
                    adapterField(m, entity);
                }
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(GroupInfo ent){
        return JsonUtil.<GroupInfo>ObjectToMap(ent,
                new String[]{ "id","sort","notes","groupName" }, 
                (m, entity) -> {
                    adapterField(m,entity);
                }
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,GroupInfo entity){
        if(!StringUtils.isEmpty(entity.getCreator())) {
            if(entity.getCreator().indexOf(ConstString.SEPARATOR_POINT)>-1) {
                m.put("creator", entity.getCreator().split(ConstString.SPLIT_POINT)[1]);
            } else {
                m.put("creator", entity.getCreator());
            }
        }
        if(!StringUtils.isEmpty(entity.getEditor())) {
            if(entity.getEditor().indexOf(ConstString.SEPARATOR_POINT)>-1) {
                m.put("editor", entity.getEditor().split(ConstString.SPLIT_POINT)[1]);
            } else {
                m.put("editor", entity.getEditor());
            }
        }
        
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
    }
    
    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/del/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  del(@PathVariable("ids") String ids) {
        service.removeByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success(InnovationConstant.SUCCESS);
    }

    /**
     * 分组删除操作
     * @param groupInfoId  分组ID
     * @return null
     */
    @DeleteMapping("/deleteGroupInfo/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  deleteGroupInfo(@PathVariable("ids") Integer groupInfoId) {
        GroupInfo groupInfo = service.getById(groupInfoId);
        groupInfo.setIsDeleted(1);
        service.updateById(groupInfo);
        return success(InnovationConstant.SUCCESS);
    }


    /**
     * 更改排序
     * @param id
     * @param up 1 下降 0 上升
     * @return
     */
    @PostMapping("/changeOrder/{id}/{up}")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@PathVariable Integer id,@PathVariable Integer up){
        GroupInfo entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<GroupInfo> wrapper = new LambdaQueryWrapper();

        String msg ="";
        if(up==1) {
            wrapper.lt(GroupInfo::getSort, sort);
            msg ="已经是最后一条数据";
            wrapper.orderByDesc(GroupInfo::getSort);
        }
        else {
            wrapper.gt(GroupInfo::getSort, sort);
            msg ="已经是第一条数据";
            wrapper.orderByAsc(GroupInfo::getSort);
        }


        GroupInfo entity1 = service.getOne(wrapper);
        if(entity1==null) {
            throw new UnityRuntimeException(msg);
        }

        entity.setSort(entity1.getSort());

        GroupInfo entityA = new GroupInfo();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        GroupInfo entityB = new GroupInfo();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }
}

