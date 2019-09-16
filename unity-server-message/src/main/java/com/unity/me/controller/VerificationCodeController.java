
package com.unity.me.controller;



import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.me.client.RbacMeClient;
import com.unity.me.client.SystemMeClient;
import com.unity.me.entity.VerificationCode;
import com.unity.me.enums.MessageTypeEnum;
import com.unity.me.service.VerificationCodeServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;







/**
 * 短信验证码
 * @author creator
 * 生成时间 2019-01-24 20:07:24
 */
@Controller
@RequestMapping("/verificationcode")
public class VerificationCodeController extends BaseWebController {
    @Autowired
    VerificationCodeServiceImpl service;
    
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
        return "VerificationCodeList";
    }

    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id 短信验证码id
     * @return 返回视图
     */
    @RequestMapping(value = "/view/editEntrance/{iframe}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        if(id!=null){
            VerificationCode entity = service.getById(id);
            model.addAttribute("entity", JSON.toJSONString(entity));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "VerificationCodeEdit";
    }
    
     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<VerificationCode> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    
         /**
     * 添加或修改
     * @param entity 短信验证码实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody VerificationCode entity) {
        
        service.saveOrUpdate(entity);
        return success(null);
    }

    
     /**
     * 获取数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<VerificationCode> ew = wrapper(search);

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
    private LambdaQueryWrapper<VerificationCode> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<VerificationCode> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule("gmtCreate").forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule("gmtModified").forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(VerificationCode.class);

        }
        else{
            ew = new LambdaQueryWrapper<VerificationCode>();
        }

        ew.orderBy(true, false,VerificationCode::getSort);
        
        return ew;
    }
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<VerificationCode> list){
       
        return JsonUtil.<VerificationCode>ObjectToList(list,
                new String[]{ "id","sort","notes","phone","expiryTime","verificationCode","messageType" }, 
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
    private Map<String, Object> convert2Map(VerificationCode ent){
        return JsonUtil.<VerificationCode>ObjectToMap(ent,
                new String[]{ "id","sort","notes","phone","expiryTime","verificationCode","messageType" }, 
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
    private void adapterField(Map<String, Object> m,VerificationCode entity){
        if(!StringUtils.isEmpty(entity.getCreator())) {
            if(entity.getCreator().indexOf(ConstString.SEPARATOR_POINT)>1)
                m.put("creator", entity.getCreator().split(ConstString.SPLIT_POINT)[1]);
            else
                m.put("creator", entity.getCreator());
        }
        if(!StringUtils.isEmpty(entity.getEditor())) {
            if(entity.getEditor().indexOf(ConstString.SEPARATOR_POINT)>1)
                m.put("editor", entity.getEditor().split(ConstString.SPLIT_POINT)[1]);
            else
                m.put("editor", entity.getEditor());
        }
        
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
        m.put("messageTypeTitle", MessageTypeEnum.of(entity.getMessageType()).getName());
    }
    
    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/del/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  del(@PathVariable("ids") String ids) {
        service.removeByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success(null);
    }


    /**
     * 更改排序
     * @param id
     * @param up 1 下降 0 上升
     * @return
     */
    @PostMapping("/changeOrder/{id}/{up}")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@PathVariable Integer id,@PathVariable Integer up){
        VerificationCode entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<VerificationCode> wrapper = new LambdaQueryWrapper();

        String msg ="";
        if(up==1) {
            wrapper.lt(VerificationCode::getSort, sort);
            msg ="已经是最后一条数据";
            wrapper.orderByDesc(VerificationCode::getSort);
        }
        else {
            wrapper.gt(VerificationCode::getSort, sort);
            msg ="已经是第一条数据";
            wrapper.orderByAsc(VerificationCode::getSort);
        }


        VerificationCode entity1 = service.getOne(wrapper);
        if(entity1==null) throw new UnityRuntimeException(msg);

        entity.setSort(entity1.getSort());

        VerificationCode entityA = new VerificationCode();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        VerificationCode entityB = new VerificationCode();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }
}

