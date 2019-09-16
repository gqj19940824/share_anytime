package com.unity.me.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.JsonUtil;
import com.unity.me.client.CmsClient;
import com.unity.me.client.RbacMeClient;
import com.unity.me.client.SystemMeClient;
import com.unity.me.entity.GroupInfo;
import com.unity.me.entity.MessageReciver;
import com.unity.me.entity.PushMessageLog;
import com.unity.me.entity.UsersGroupInfo;
import com.unity.me.enums.BizTypeEnum;
import com.unity.me.enums.PushTypeEnum;
import com.unity.me.pojos.PushMessageLogPO;
import com.unity.me.service.GroupInfoServiceImpl;
import com.unity.me.service.MessageReciverServiceImpl;
import com.unity.me.service.PushMessageLogServiceImpl;
import com.unity.me.service.UsersGroupInfoServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 推送消息和公告消息
 *
 * @author creator
 * 生成时间 2019-02-12 12:47:36
 */
@Controller
@RequestMapping("/pushmessagelog")
@Slf4j
public class PushMessageLogController extends BaseWebController {
    @Autowired
    PushMessageLogServiceImpl service;
    @Autowired
    MessageReciverServiceImpl messageReceiverService;
    @Autowired
    UsersGroupInfoServiceImpl usersGroupInfoService;
    @Autowired
    GroupInfoServiceImpl groupInfoService;
    @Autowired
    RbacMeClient rbacClient;
    @Autowired
    CmsClient cmsClient;
    @Autowired
    SystemMeClient systemClient;

    /**
     * 模块入口
     *
     * @param model  MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @return 返回视图
     */
    @RequestMapping("/view/moduleEntrance/{iframe}")
    public String moduleEntrance(Model model, @PathVariable("iframe") String iframe) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("button", JSON.toJSONString(rbacClient.getMenuButton(iframe)));
        return "PushMessageLogList";
    }

    /**
     * 添加或修改表达入口
     *
     * @param model  MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id     推送消息和公告消息id
     * @return 返回视图
     */
    @RequestMapping(value = "/view/editEntrance/{iframe}")
    public String editEntrance(Model model, @PathVariable("iframe") String iframe, String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        /*PushMessageLog pushMessageLog = service.getById(id);
        if (pushMessageLog != null) {
            String groupInfoByUserIds = getGroupInfoByUserIds(pushMessageLog.getRecordStatus());
            String dataResource = rbacClient.getDataResourceByStatus(pushMessageLog.getRecordStatus());
            model.addAttribute("groupInfoByUserIds", groupInfoByUserIds);//小组信息
            model.addAttribute("dateResource", dataResource);//亦庄控股
        } else {
            String groupInfoByUserIds = getGroupInfoByUserIds(-1);
            String dataResource = rbacClient.getDataResourceByStatus(-1);
            model.addAttribute("groupInfoByUserIds", groupInfoByUserIds);//小组信息
            model.addAttribute("dateResource", dataResource);//亦庄控股
        }*/
        if (id != null) {
            PushMessageLog entity = service.getById(id);
            if (entity == null) {
                model.addAttribute("entity", "{}");
                model.addAttribute("messageReceiverByGroupInfoIsNull", "[]");
                model.addAttribute("messageReceiverByGroupInfo", "[]");
                model.addAttribute("content", "''");
            } else {
                List<MessageReciver> messageReceivers = messageReceiverService.getMessageReceiverByGroupInfo(Long.valueOf(id));
                List<String> messageReceiverByGroupInfo = Lists.newArrayList();
                messageReceivers.forEach(messageReceiver -> {
                    messageReceiverByGroupInfo.add(messageReceiver.getPbkUserInfoId() + "." + messageReceiver.getGroupId());
                });
                List<Long> messageReceiverByGroupInfoIsNull = messageReceiverService.getMessageReceiverByGroupInfoIsNull(Long.valueOf(id));
                model.addAttribute("entity", JSON.toJSONString(convert2Map(entity)));
                model.addAttribute("content", "'" + entity.getContent() + "'");
                model.addAttribute("messageReceiverByGroupInfoIsNull", JSON.toJSONString(messageReceiverByGroupInfoIsNull));
                model.addAttribute("messageReceiverByGroupInfo", JSON.toJSONString(messageReceiverByGroupInfo));

            }
        } else {
            model.addAttribute("entity", "{}");
            model.addAttribute("messageReceiverByGroupInfoIsNull", "[]");
            model.addAttribute("messageReceiverByGroupInfo", "[]");
            model.addAttribute("content", "''");
        }
        return "PushMessageLogEdit";
    }

    /**
     * 获取一页数据
     *
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {

        LambdaQueryWrapper<PushMessageLog> ew = wrapper(search);
        //查询自己创建的推送消息
        Customer customer = LoginContextHolder.getRequestAttributes();
        String s = customer.getId() + "." + customer.getName();
        ew.eq(PushMessageLog::getCreator, s);
        IPage p = service.page(search.getPageable(), ew);
        return success(PageElementGrid.<Map<String, Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build());

    }


    /**
     * 添加或修改
     *
     * @param pushMessageLogPO 推送消息和公告消息实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> save(@RequestBody PushMessageLogPO pushMessageLogPO) {

        if (pushMessageLogPO.getBizType() == null || BizTypeEnum.of(pushMessageLogPO.getBizType()) == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "业务类型为空或业务类型不匹配");
        }
        if (StringUtils.isBlank(pushMessageLogPO.getContent().trim())) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "消息内容为空");
        }
        /*if (StringUtils.isEmpty(pushMessageLogPO.getTextContent())) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "文本消息内容为空");
        }*/
        if (StringUtils.isEmpty(pushMessageLogPO.getTitle().trim())) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "标题为空");
        }
        if (pushMessageLogPO.getId() != null) {
            PushMessageLog messageLog = service.getById(pushMessageLogPO.getId());
            if (messageLog.getRecordStatus().intValue() == YesOrNoEnum.YES.getType()) {
                return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "消息已经发布,不能再次编辑!!");
            }
        }
        service.saveOrUpdatePushMessageLog(pushMessageLogPO);
        return success("操作成功");
    }


    /**
     * 消息发布
     *
     * @param id 推送消息和公告消息id
     * @return
     */
    @GetMapping("/release/{id}")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> release(@PathVariable("id") Long id) {
        if (id == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "消息为空");
        }
        PushMessageLog pushMessageLog = service.getById(id);
        if (pushMessageLog == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "消息不存在");
        }
        if (pushMessageLog.getRecordStatus() != YesOrNoEnum.YES.getType()) {
            service.releaseMessageUmeng(pushMessageLog);
            return success("操作成功");
        } else {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "请勿重复发送消息!");
        }
    }

    /**
     * 获取数据
     *
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {

        LambdaQueryWrapper<PushMessageLog> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2List(list)).build();
        return success(result);

    }

    /**
     * 查询条件转换
     *
     * @param search 统一查询对象
     * @return
     */
    private LambdaQueryWrapper<PushMessageLog> wrapper(SearchElementGrid search) {
        LambdaQueryWrapper<PushMessageLog> ew = null;
        if (search != null) {
            if (search.getCond() != null) {
                search.getCond().findRule("gmtCreate").forEach(r -> {
                    r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule("gmtModified").forEach(r -> {
                    r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(PushMessageLog.class);

        } else {
            ew = new LambdaQueryWrapper<PushMessageLog>();
        }

        ew.orderBy(true, false, PushMessageLog::getSort);

        return ew;
    }


    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<PushMessageLog> list) {

        return JsonUtil.<PushMessageLog>ObjectToList(list,
                new String[]{"id", "sort", "notes", "textContent","content", "cmsDocId", "title", "bizType", "pushType", "os", "recordStatus", "alias", "taskId", "imgUrl", "bizModel"},
                (m, entity) -> {
                    adapterField(m, entity);
                }
        );
    }

    /**
     * 将实体 转换为 Map
     *
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(PushMessageLog ent) {
        return JsonUtil.<PushMessageLog>ObjectToMap(ent,
                new String[]{"id", "sort", "notes", "textContent", "cmsDocId", "title", "bizType", "pushType", "os", "recordStatus", "alias", "taskId", "imgUrl", "bizModel"},
                (m, entity) -> {
                    adapterField(m, entity);
                }
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, PushMessageLog entity) {
        if (!StringUtils.isEmpty(entity.getCreator())) {
            if (entity.getCreator().indexOf(ConstString.SEPARATOR_POINT) > -1) {
                m.put("creator", entity.getCreator().split(ConstString.SPLIT_POINT)[1]);
            } else {
                m.put("creator", entity.getCreator());
            }
        }
        if (!StringUtils.isEmpty(entity.getEditor())) {
            if (entity.getEditor().indexOf(ConstString.SEPARATOR_POINT) > -1) {
                m.put("editor", entity.getEditor().split(ConstString.SPLIT_POINT)[1]);
            } else {
                m.put("editor", entity.getEditor());
            }
        }

        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
        m.put("bizTypeTitle", BizTypeEnum.of(entity.getBizType()).getName());
        m.put("pushTypeTitle", PushTypeEnum.of(entity.getPushType()).getName());
//        m.put("osTitle", OsEnum.of(entity.getOs()).getName());
//        m.put("recordStatusTitle", RecordStatusEnum.of(entity.getRecordStatus()).getName());
    }

    /**
     * 批量删除
     *
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/del/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>> del(@PathVariable("ids") String ids) {
        service.removeByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success(null);
    }


    /**
     * 更改排序
     *
     * @param id
     * @param up 1 下降 0 上升
     * @return
     */
    @PostMapping("/changeOrder/{id}/{up}")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@PathVariable Integer id, @PathVariable Integer up) {
        PushMessageLog entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<PushMessageLog> wrapper = new LambdaQueryWrapper();

        String msg = "";
        if (up == 1) {
            wrapper.lt(PushMessageLog::getSort, sort);
            msg = "已经是最后一条数据";
            wrapper.orderByDesc(PushMessageLog::getSort);
        } else {
            wrapper.gt(PushMessageLog::getSort, sort);
            msg = "已经是第一条数据";
            wrapper.orderByAsc(PushMessageLog::getSort);
        }


        PushMessageLog entity1 = service.getOne(wrapper);
        if (entity1 == null) throw new UnityRuntimeException(msg);

        entity.setSort(entity1.getSort());

        PushMessageLog entityA = new PushMessageLog();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        PushMessageLog entityB = new PushMessageLog();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }



    /**
     * 查询分组以及分组成员信息   tree结构
     *
     * @return 支部列表
     * @author wangbin
     * @since 2019年2月21日09:25:29
     */
    @GetMapping("/getGroupInfoByUserIds")
    public String getGroupInfoByUserIds(Integer recordStstus) {
        LambdaQueryWrapper<GroupInfo> ew = new LambdaQueryWrapper<>();
        Customer customer = LoginContextHolder.getRequestAttributes();
        String s = customer.getId() + "." + customer.getName();
        ew.eq(GroupInfo::getCreator, s);
        List<GroupInfo> groupInfoList = groupInfoService.list(ew);
        List<Map<String, Object>> dataResourceTreeList = Lists.newArrayList();
        List<GroupInfo> departmentListData = Lists.newArrayList();
        groupInfoList.forEach(groupInfo -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", groupInfo.getId().toString() + "." + "1");
            map.put("name", groupInfo.getGroupName());
            map.put("type", "1");
            //map.put("id",department.getId());
            dataResourceTreeList.add(map);
        });
        groupInfoList.forEach(groupInfo -> {
            //根据分组查询人员
            List<UsersGroupInfo> usersGroupInfo = usersGroupInfoService.getUsersGroupInfo(groupInfo.getId());
            List<Long> userIds = usersGroupInfo.parallelStream().map(UsersGroupInfo::getRbacUserId).collect(Collectors.toList());
            //根据人员信息  跨库查询userName
            Map<String, Object> userInfoByIdIn = rbacClient.findUserInfoByIdIn(userIds);
            usersGroupInfo.forEach(usersGroupInfo1 -> {
                Iterator<String> iterator = userInfoByIdIn.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (usersGroupInfo1.getRbacUserId().equals(Long.valueOf(key))) {
                        GroupInfo depar = new GroupInfo();
                        depar.setId(usersGroupInfo1.getRbacUserId());
                        Map<String, Object> user = (Map<String, Object>) userInfoByIdIn.get(key);
                        depar.setGroupName(user.get("name").toString());
                        depar.setNotes(groupInfo.getId().toString().concat(".").concat("1"));
                        departmentListData.add(depar);
                    }
                }
            });
        });
        recursiveDepartment(dataResourceTreeList, departmentListData, recordStstus);
        String format = GsonUtils.format(dataResourceTreeList);
        return format;
    }

    private void recursiveDepartment(List<Map<String, Object>> dataResourceTreeList, List<GroupInfo> departmentList, Integer recordStstus) {
        dataResourceTreeList.forEach(tree -> {
            List<Map<String, Object>> childDataResourceTreeList = Lists.newArrayList();
            departmentList.forEach(department -> {
                if (tree.get("id").toString().equals(department.getNotes())) {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("id", department.getId() + "." + (tree.get("id").toString().split("\\.")[0]));
                    map.put("name", department.getGroupName());
                    map.put("type", "2");
                    if (recordStstus == 1) {
                        map.put("disabled", "true");
                    }

                    childDataResourceTreeList.add(map);
                }
            });
            tree.put("items", childDataResourceTreeList);
        });
    }


}

