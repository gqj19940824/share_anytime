
package com.unity.rbac.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.RedisConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.BeanUtils;
import com.unity.common.util.DateUtils;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.HashRedisUtils;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.dao.DepartmentDao;
import com.unity.rbac.entity.Department;
import com.unity.rbac.entity.UserDepartment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * 组织架构业务处理
 * <p>
 * ClassName: DepartmentService
 * date: 2018-12-12 20:21:06
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Slf4j
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentDao, Department> implements IService<Department> {

    private final UserDepartmentServiceImpl userDepartmentService;
    private final HashRedisUtils hashRedisUtils;
    private final RedisTemplate<String, String> redisTemplate;
    private final DicUtils dicUtils;
    private final UserServiceImpl userService;


    public DepartmentServiceImpl(UserDepartmentServiceImpl userDepartmentService, HashRedisUtils hashRedisUtils, RedisTemplate redisTemplate, DicUtils dicUtils, UserServiceImpl userService) {
        this.userDepartmentService = userDepartmentService;
        this.hashRedisUtils = hashRedisUtils;
        this.redisTemplate = redisTemplate;
        this.dicUtils = dicUtils;
        this.userService = userService;
    }

    /**
     * 新增 or 修改 --> 组织架构
     *
     * @param dto 包含组织架构信息
     * @author gengjiajia
     * @since 2018/12/11 15:40
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateDepartment(Department dto) {
        dto.setName(dto.getName().trim());
        if (dto.getId() == null) {
            Department department = new Department();
            BeanUtils.copyProperties(dto,department);
            department.setIsDeleted(YesOrNoEnum.NO.getType());
            department.setUseStatus(YesOrNoEnum.YES.getType());
            super.save(department);
            //保存公司到redis
            saveOrUpdateDepartmentToRedis(department);
            //更新redis中公司排序
            reOrderDepartment();
        } else {
            //修改
            Department department = super.getById(dto.getId());
            if (department == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                        .message("未查询到该单位")
                        .build();
            }
            department.setName(dto.getName());
            department.setDepType(dto.getDepType());
            department.setAddress(dto.getAddress());
            department.setPhone(dto.getPhone());
            department.setNotes(dto.getNotes());
            super.updateById(department);
            //保存公司到redis
            saveOrUpdateDepartmentToRedis(department);
        }
    }

    /**
     * 更新公司信息到redis
     *
     * @param department 公司信息
     * @author gengjiajia
     * @since 2019/07/11 17:00
     */
    private void saveOrUpdateDepartmentToRedis(Department department) {
        String key = RedisConstants.DEPARTMENT.concat(department.getId().toString());
        hashRedisUtils.removeValueByKey(key);
        hashRedisUtils.putValueByKey(key, GsonUtils.map(GsonUtils.format(department)));
    }

    /**
     * 删除指定的组织架构
     *
     * @param id 组织架构id
     * @author gengjiajia
     * @since 2018/12/12 09:09
     */
    @Transactional(rollbackFor = Exception.class)
    public void delDepartment(Long id) {
        //是否存在与用户的关系
        if (0 < userDepartmentService.count(new QueryWrapper<UserDepartment>().lambda()
                .eq(UserDepartment::getIdRbacDepartment, id))) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("当前组织架构下存在关联用户，请先解除")
                    .build();
        }
        super.removeById(id);
        //从redis移除该公司
        hashRedisUtils.removeValueByKey(RedisConstants.DEPARTMENT.concat(id.toString()));
        //更新redis中单位排序
        reOrderDepartment();
    }

    /**
     * 上移/下移
     *
     * @param department 需要移动的实体 up 1 上移  0 下移
     * @author JH
     * @date 2019/7/24 11:35
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeOrder(Department department) {
        Department entity = this.getById(department.getId());
        Integer up = department.getUp();
        Long sort = entity.getSort();
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        String msg;
        if (up == YesOrNoEnum.YES.getType()) {
            wrapper.gt(Department::getSort, sort);
            msg = "已经是第一条数据";
            wrapper.orderByAsc(Department::getSort);
        } else {
            wrapper.lt(Department::getSort, sort);
            msg = "已经是最后一条数据";
            wrapper.orderByDesc(Department::getSort);
        }
        Department entity1 = this.getOne(wrapper, false);
        if (entity1 == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message(msg)
                    .build();
        }
        Department entitya = new Department();
        entity.setSort(entity1.getSort());
        entitya.setSort(entity1.getSort());
        entitya.setId(entity.getId());
        Department entityb = new Department();
        this.updateById(entitya);
        entityb.setId(entity1.getId());
        entityb.setSort(sort);
        this.updateById(entityb);
        //更新redis中公司排序
        reOrderDepartment();
    }

    /**
     * 更新redis中单位排序
     *
     * @author JH
     * @date 2019/7/24 16:46
     */
    @Async
    public void reOrderDepartment() {
        //将公司排序信息放入redis
        List<Department> list = super.list(new LambdaQueryWrapper<Department>().orderByDesc(Department::getSort));
        List<Long> ids = list.stream().map(Department::getId).collect(toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            String s = JSON.toJSONString(ids);
            redisTemplate.delete(RedisConstants.DEPARTMENT_ORDER_LIST);
            redisTemplate.opsForValue().set(RedisConstants.DEPARTMENT_ORDER_LIST, s);
            log.info("====== 《com.unity.rbac.service.DepartmentServiceImpl.reOrderDepartment()》 更新单位排序到缓存数量 {}", ids.size());
        }
    }

    /**
     * 获取单位列表
     *
     * @param pageEntity 分页参数
     * @return 单位列表
     * @author gengjiajia
     * @since 2019/09/17 17:20
     */
    public PageElementGrid<Map<String, Object>> listByPage(PageEntity<Department> pageEntity) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<Department>().eq(Department::getUseStatus, YesOrNoEnum.YES.getType()).orderByDesc(Department::getSort);
        Department entity = pageEntity.getEntity();
        if(entity != null && entity.getDepType() != null){
            wrapper.eq(Department::getDepType,entity.getDepType());
        }
        if(entity != null && StringUtils.isNotBlank(entity.getName())){
            wrapper.like(Department::getName,entity.getName());
        }
        if(entity != null && entity.getUseStatus() != null){
            wrapper.eq(Department::getUseStatus,entity.getUseStatus());
        }
        IPage<Department> page = super.page(pageEntity.getPageable(),wrapper);
        return PageElementGrid.<Map<String, Object>>newInstance()
                .total(page.getTotal())
                .items(convert2List(page.getRecords()))
                .build();
    }

    /**
     * 变更状态
     *
     * @param dto 包含单位id及状态
     * @author gengjiajia
     * @since 2019/09/18 10:05
     */
    public void changeStatus(Department dto) {
        Department department = super.getById(dto.getId());
        if (department == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("单位信息不存在")
                    .build();
        }
        if (!dto.getUseStatus().equals(department.getUseStatus())) {
            department.setUseStatus(dto.getUseStatus());
            super.updateById(department);
            //单位状态变更，对应的用户状态要发生变化
            userService.updateIsLockByIdRbacDepartment(dto.getUseStatus().equals(YesOrNoEnum.YES.getType())
                            ? YesOrNoEnum.NO.getType() : YesOrNoEnum.YES.getType(),
                    department.getId());
        }
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return 返回转换后的列表
     */
    private List<Map<String, Object>> convert2List(List<Department> list) {
        List<Long> ids = list.stream().map(Department::getId).collect(toList());
        Long idBySortAsc = 0L;
        Long idBySortDesc = 0L;
        if(CollectionUtils.isNotEmpty(ids)){
            idBySortAsc = baseMapper.getTheFirstDepartmentBySortAsc(ids);
            idBySortDesc = baseMapper.getTheFirstDepartmentBySortDesc(ids);
        }
        Long ascId = idBySortAsc;
        Long descId = idBySortDesc;
        return JsonUtil.ObjectToList(list,
                (m,entity) -> adapterField(m,entity,ascId,descId)
                , Department::getId, Department::getGmtModified, Department::getName,Department::getPhone,
                Department::getAddress,Department::getNotes,Department::getDepType,Department::getUseStatus
        );
    }


    /**
     * 字段适配
     *
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,Department entity,Long idBySortAsc,Long idBySortDesc){
        m.put("gmtGmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
        m.put("last",entity.getId().equals(idBySortAsc) ? YesOrNoEnum.YES.getType() : YesOrNoEnum.NO.getType());
        m.put("first",entity.getId().equals(idBySortDesc) ? YesOrNoEnum.YES.getType() : YesOrNoEnum.NO.getType());
        Dic dic = dicUtils.getDicByCode(UserConstants.DEP_TYPE, entity.getDepType().toString());
        if(dic != null){
            m.put("depTypeTitle",dic.getDicValue());
        }
    }

    /**
     * 单位列表获取单位类型下拉框数据
     *
     * @return 单位类型下拉框数据
     * @author gengjiajia
     * @since 2019/09/24 10:31
     */
    public List<Map<String, Object>> getDepTypeSelectListToDepList() {
        List<Dic> dicList = dicUtils.getDicsByGroupCode(UserConstants.DEP_TYPE);
        return JsonUtil.ObjectToList(dicList,
                null
                , Dic::getDicCode,Dic::getDicValue
        );
    }
}
