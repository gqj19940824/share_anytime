package com.unity.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sun.org.apache.xpath.internal.operations.Gt;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.RedisConstants;
import com.unity.common.constant.SafetyConstant;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.utils.UUIDUtil;
import com.unity.system.entity.Dic;
import com.unity.system.entity.DicGroup;
import com.unity.system.service.DicGroupServiceImpl;
import com.unity.system.service.DicServiceImpl;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典组和字典项管理
 * @author zhang
 * 生成时间 2019-07-23 16:34:47
 */
@RestController
@RequestMapping("/dic")
public class DicController extends BaseWebController {

    @Resource
    private DicServiceImpl dicService;

    @Resource
    private DicGroupServiceImpl dicGroupService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 添加或修改字典组
     *
     * @param entity 实体
     * @return
     */
    @PostMapping("/dicGroup/save")
    public Mono<ResponseEntity<SystemResponse<Object>>> dicGroupSave(@RequestBody DicGroup entity) {
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        String groupName = entity.getGroupName();
        if (StringUtils.isEmpty(groupName)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "字典组名称不可为空");
        }
        if (groupName.length() > 20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "字典组名称长度过长");
        }
        String notes = entity.getNotes();
        if (StringUtils.isNotEmpty(notes) && notes.length() > 255) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "备注长度过长");
        }

        Long id = entity.getId();
        String key = RedisConstants.DICGROUP;
        // 新增
        if (id == null) {
            String groupCode = entity.getGroupCode();
            if (StringUtils.isEmpty(groupCode)) {
                return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "字典组CODE不可为空");
            }
            if (groupCode.length() > 20) {
                return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "字典组CODE长度过长");
            }
            int count = dicGroupService.count(new LambdaQueryWrapper<DicGroup>().eq(DicGroup::getGroupCode, groupCode));
            // groupCode重复性校验
            if (count > 0) {
                return error(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS, "字典组CODE已存在");
            }
            dicGroupService.save(entity);
            redisTemplate.opsForHash().put(key, groupCode, JSON.toJSONString(entity));
        } else { // 编辑
            DicGroup dicGroup = dicGroupService.getById(id);
            if (dicGroup == null) {
                return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "数据不存在");
            }
            dicGroup.setGroupName(entity.getGroupName());
            dicGroup.setNotes(entity.getNotes());
            dicGroupService.updateById(dicGroup);
            redisTemplate.opsForHash().put(key, dicGroup.getGroupCode(), JSON.toJSONString(dicGroup));
        }
        return success(null);
    }

    /**
     * 分页获取获取字典组列表
     *
     * @return
     */
    @PostMapping("dicGroup/list")
    public Mono<ResponseEntity<SystemResponse<Object>>> dicGroupLlist(@RequestBody PageEntity<DicGroup> pageEntity) {
        if (pageEntity == null || pageEntity.getPageable() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        DicGroup entity = pageEntity.getEntity();

        LambdaQueryWrapper<DicGroup> qw = new LambdaQueryWrapper<DicGroup>().orderByDesc(DicGroup::getSort);
        if (entity != null && StringUtils.isNotBlank(entity.getGroupName())){
            qw.like(DicGroup::getGroupName, entity.getGroupName());
        }
        IPage<DicGroup> p = dicGroupService.page(pageEntity.getPageable(), qw);
        PageElementGrid result = PageElementGrid.<DicGroup>newInstance()
                .total(p.getTotal())
                .items(p.getRecords()).build();
        return success(result);
    }

    /**
     * 批量删除
     *
     * @param map id列表用英文逗号分隔
     * @return
     */
    @PostMapping("/dicGroup/del")
    public Mono<ResponseEntity<SystemResponse<Object>>> dicGroupDel(@RequestBody Map<String, String> map) {
        if (MapUtils.isEmpty(map) || StringUtils.isBlank(MapUtils.getString(map, "ids"))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        String key = RedisConstants.DICGROUP;
        List<Long> ids = ConvertUtil.arrString2Long(MapUtils.getString(map, "ids").split(ConstString.SPLIT_COMMA));
        ids.forEach(e -> {
            DicGroup dicGroup = dicGroupService.getById(e);
            if (dicGroup != null) {
                redisTemplate.opsForHash().delete(key, dicGroup.getGroupCode());
                dicGroupService.removeById(e);
            }

        });
        return success(null);
    }

    /**
     * 查询详情
     *
     * @param map groupCode 字典组CODE
     * @return obj
     */
    @PostMapping("/dicGroup/getDicGroupByGroupCode")
    public Mono<ResponseEntity<SystemResponse<Object>>> getDicGroupByGroupCode(@RequestBody Map<String, String> map) {
        String hashKey = "groupCode";
        if (MapUtils.isEmpty(map) || StringUtils.isBlank(MapUtils.getString(map, hashKey))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        String key = RedisConstants.DICGROUP;
        String groupCode = MapUtils.getString(map, hashKey);
        String groupStr = (String) redisTemplate.opsForHash().get(key, groupCode);
        if (StringUtils.isEmpty(groupStr)) {
            synchronized (this) {
                groupStr = (String) redisTemplate.opsForHash().get(key, groupCode);
                if (StringUtils.isEmpty(groupStr)){
                    DicGroup dicGroupByGroupCode = dicGroupService.getDicGroupByGroupCode(groupCode);
                    redisTemplate.opsForHash().put(key, groupCode, JSON.toJSONString(dicGroupByGroupCode));
                    return success(dicGroupByGroupCode);
                }
            }
        }
        return success(JSON.parseObject(groupStr, DicGroup.class));
    }

    /**
     * 添加或修改
     *
     * @param entity 实体
     * @return
     */
    @PostMapping("/dic/save")
    public Mono<ResponseEntity<SystemResponse<Object>>> save(@RequestBody Dic entity) {

        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        String dicValue = entity.getDicValue();
        if (StringUtils.isNotBlank(dicValue) && dicValue.length() > 255) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "字典值长度过长");
        }
        String notes = entity.getNotes();
        if (StringUtils.isNotEmpty(notes) && notes.length() > 255) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "备注长度过长");
        }
        // 新增
        if (entity.getId() == null) {
            String groupCode = entity.getGroupCode();
            if (StringUtils.isBlank(groupCode)) {
                return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "字典组CODE不可为空");
            }
            DicGroup dicGroup = dicGroupService.getOne(new LambdaQueryWrapper<DicGroup>().eq(DicGroup::getGroupCode, groupCode));
            if (dicGroup == null) {
                return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "字典组不存在");
            }
            String dicCode = entity.getDicCode();
            if (StringUtils.isBlank(dicCode)) {
                return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "字典CODE不可为空");
            }
            if (dicCode.length() > 20) {
                return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "字典CODE长度过长");
            }
            int count = dicService.count(new LambdaQueryWrapper<Dic>().eq(Dic::getDicCode, dicCode).eq(Dic::getGroupCode, groupCode));
            if (count > 0) {
                return error(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS, "字典CODE已存在");
            }
            dicService.save(entity);
        } else {

            Dic dic = dicService.getById(entity.getId());
            if (dic == null) {
                return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "数据不存在");
            }
            dic.setDicValue(entity.getDicValue());
            dic.setNotes(entity.getNotes());
            dic.setGmtModified(System.currentTimeMillis());
            if (StringUtils.isNotBlank(entity.getStatus())){
                dic.setStatus(entity.getStatus());
            }
            dicService.saveOrUpdate(dic);

            String key = RedisConstants.DIC_PREFIX + dic.getGroupCode();
            redisTemplate.opsForHash().put(key, dic.getDicCode(), JSON.toJSONString(dic));
        }
        return success(null);
    }

    /**
     * 获取数据
     *
     * @return
     */
    @PostMapping("/dic/list")
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody Map<String, Object> map) {

        if (MapUtils.isEmpty(map) || StringUtils.isBlank(MapUtils.getString(map, "groupCode"))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        LambdaQueryWrapper<Dic> ew = new LambdaQueryWrapper<>();
        ew.eq(Dic::getGroupCode, MapUtils.getString(map, "groupCode")).orderByAsc(Dic::getSort);
        if(StringUtils.isNotBlank(MapUtils.getString(map, "dicName"))) {
            ew.like(Dic::getDicName,MapUtils.getString(map, "dicName"));
        }
        return success(dicService.list(ew));
    }

    /**
     * 批量删除
     *
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @PostMapping("/dic/del")
    public Mono<ResponseEntity<SystemResponse<Object>>> del(@RequestBody Map<String, String> ids) {
        if (MapUtils.isEmpty(ids) || StringUtils.isBlank(MapUtils.getString(ids, "ids"))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }

        List<Long> ids1 = ConvertUtil.arrString2Long(MapUtils.getString(ids, "ids").split(ConstString.SPLIT_COMMA));

        // 删除redis
        ids1.forEach(e->{
            Dic byId = dicService.getById(e);
            if (byId == null){
                return;
            }
            dicService.removeById(e);
            redisTemplate.opsForHash().delete(RedisConstants.DIC_PREFIX + byId.getGroupCode(), byId.getDicCode());
        });

        return success(null);
    }

    /**
     * 查询详情
     *
     * @param id id列表用英文逗号分隔
     * @return
     */
    @PostMapping("/dic/getById")
    public Mono<ResponseEntity<SystemResponse<Object>>> getDicById(@RequestBody Map<String, String> id) {
        if (MapUtils.isEmpty(id) || StringUtils.isBlank(MapUtils.getString(id, "id"))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }

        return success(dicService.getById(MapUtils.getLong(id, "id")));
    }

    /**
     * 根据字典code查询提示语
     *
     * @param map
     * @return
     */
    @PostMapping("/dic/getPromptDetail")
    public Mono<ResponseEntity<SystemResponse<Object>>> getPromptDetail(@RequestBody Map<String, String> map) {
        if (MapUtils.isEmpty(map) || StringUtils.isBlank(MapUtils.getString(map, "dicCode"))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }

        return success(dicService.getOne(new LambdaQueryWrapper<Dic>().eq(Dic::getGroupCode, SafetyConstant.PROMPT).eq(Dic::getDicCode, MapUtils.getString(map, "dicCode"))));
    }

    /**
     * 更改排序
     *
     * @param map id
     *            up 1 下降 0 上升
     * @return
     */
    @PostMapping("/dic/changeOrder")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@RequestBody Map<String, Object> map) {
        if (MapUtils.isEmpty(map)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        Integer up = MapUtils.getInteger(map, "up");
        Long id = MapUtils.getLong(map, "id");
        Dic entity = dicService.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<Dic> wrapper = new LambdaQueryWrapper<Dic>();

        String msg = "";
        if (up == 1) {
            wrapper.gt(Dic::getSort, sort);
            msg = "已经是最后一条数据";
            wrapper.orderByAsc(Dic::getSort);
        } else {
            wrapper.lt(Dic::getSort, sort);
            msg = "已经是第一条数据";
            wrapper.orderByDesc(Dic::getSort);
        }


        Dic entity1 = dicService.getOne(wrapper, false);
        if (entity1 == null) {
            throw new UnityRuntimeException(msg);
        }

        entity.setSort(entity1.getSort());

        Dic entitya = new Dic();
        entitya.setId(entity.getId());
        entitya.setSort(entity1.getSort());
        dicService.updateById(entitya);

        Dic entityb = new Dic();
        entityb.setId(entity1.getId());
        entityb.setSort(sort);
        dicService.updateById(entityb);

        return success("移动成功");
    }

    /**
     * 根据多个字典组CODE查询对应的字典项列表
     *
     * @param map 逗号分隔的多个groupCode
     * @return
     */
    @PostMapping("/dic/getDicsByGroupCodes")
    public Mono<ResponseEntity<SystemResponse<Object>>> getDicsByGroupCodes(@RequestBody Map<String, Object> map) {
        if (MapUtils.isEmpty(map) || StringUtils.isBlank(MapUtils.getString(map, "groupCodes"))){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "缺少必要请求参数");
        }
        String groupCodes = MapUtils.getString(map, "groupCodes");
        String[] split = groupCodes.split(",");

        Map<String, Object> resultMap = new HashMap<>(split.length);
        Arrays.asList(split).forEach(e->{
            // TODO 缓存优化
            List<Dic> list = dicService.list(new LambdaQueryWrapper<Dic>().eq(Dic::getGroupCode, e).orderByAsc(Dic::getSort));
            resultMap.put(e, list);
        });
        return success(resultMap);
    }

    /**
     * 行业类别列表查询
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-09-24 09:36
     */
    @PostMapping("/category/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> getIndustryCategorys(@RequestBody PageEntity<Dic> pageEntity){

        LambdaQueryWrapper<Dic> qw = new LambdaQueryWrapper<>();

        if (pageEntity != null && pageEntity.getEntity() != null){

            Dic entity = pageEntity.getEntity();

            // 组名
            String groupCode = entity.getGroupCode();
            if (StringUtils.isBlank(groupCode)){
                return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "缺少必要请求参数");
            }

            qw.eq(Dic::getGroupCode, groupCode);

            // 类别
            String dicValue = entity.getDicValue();
            if (StringUtils.isNotBlank(dicValue)){
                qw.like(Dic::getDicValue, dicValue);
            }
            // 状态
            String status = entity.getStatus();
            if (StringUtils.isNotBlank(status)){
                qw.eq(Dic::getStatus, status);
            }
        }
        IPage<Dic> page = dicService.page(pageEntity.getPageable(), qw);
        PageElementGrid result = PageElementGrid.<Dic>newInstance().total(page.getTotal()).items(page.getRecords()).build();
        return success(result);
    }

    /**
     * 行业类别添加或修改
     *
     * @param entity 实体
     * @return
     */
    @PostMapping("category/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdateIndustryCategory(@RequestBody Dic entity) {

        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        String dicValue = entity.getDicValue();
        if (StringUtils.isBlank(dicValue) || dicValue.length() > 20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "类别名称必填且长度不可超过20位字符");
        }
        // 新增
        if (entity.getId() == null) {
            String groupCode = entity.getGroupCode();
            if (StringUtils.isBlank(groupCode)){
                return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
            }
            entity.setDicCode(UUIDUtil.getUUID());
            entity.setStatus("1");
            dicService.save(entity);
        } else {

            Dic dic = dicService.getById(entity.getId());
            if (dic == null) {
                return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "数据不存在");
            }
            dic.setDicValue(entity.getDicValue());
            dic.setGmtModified(System.currentTimeMillis());
            if (StringUtils.isNotBlank(entity.getStatus())){
                dic.setStatus(entity.getStatus());
            }
            dicService.saveOrUpdate(dic);

            String key = RedisConstants.DIC_PREFIX + dic.getGroupCode();
            redisTemplate.opsForHash().put(key, dic.getDicCode(), JSON.toJSONString(dic));
        }
        return success(null);
    }

}