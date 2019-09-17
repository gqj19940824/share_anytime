
package com.unity.rbac.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.RedisConstants;
import com.unity.common.enums.UserAccountLevelEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.DateUtils;
import com.unity.common.util.FileReaderUtil;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.ExcelStyleUtil;
import com.unity.common.utils.HashRedisUtils;
import com.unity.rbac.dao.DepartmentDao;
import com.unity.rbac.entity.Department;
import com.unity.rbac.entity.UserDepartment;
import com.unity.rbac.enums.DepartmentDepTypeEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final SystemConfiguration systemConfiguration;
    private final HashRedisUtils hashRedisUtils;
    private final RedisTemplate<String, String> redisTemplate;
    private final Integer NAME_MAX_LENGTH = 50;

    /**
     * 标题行数
     **/
    private static final int HEADERS_LENGTH = 7;
    /**
     * 每行列数
     */
    private static final int CELL_LENGTH = 2;

    public DepartmentServiceImpl(UserDepartmentServiceImpl userDepartmentService, SystemConfiguration systemConfiguration, HashRedisUtils hashRedisUtils, RedisTemplate redisTemplate) {
        this.userDepartmentService = userDepartmentService;
        this.systemConfiguration = systemConfiguration;
        this.hashRedisUtils = hashRedisUtils;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据单位级别查询单位的id列表
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-08-01 15:44
     */
    public List<Long> getDepartmentIdsByLevel(Integer level) {
        return baseMapper.getDepartmentIdsByLevel(level);
    }

    /**
     * 根据单位级别查询单位的id列表
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-08-01 15:44
     */
    public List<Department> postDepartmentAndChildDepartList(List<String> gradationList) {
        //获取单位信息 排序查询
        String s = redisTemplate.opsForValue().get(RedisConstants.DEPARTMENT_ORDER_LIST);
        List<Long> orderByList = JSON.parseArray(s, Long.class);
        return list(new LambdaQueryWrapper<Department>().in(Department::getGradationCode, gradationList).or().likeRight(Department::getGradationCode, gradationList.get(gradationList.size()-1)).orderByDesc(Department::getSort));
    }

    /**
     * 根据多个单位id获取相对应的的user信息
     *
     * @param ids 多个单位id
     * @return 相对应的的user信息
     * @author lifeihong
     * @since 2019-06-30
     */
    public List<Department> listUserInDept(List<Long> ids) {
        Long[] idsArr = new Long[ids.size()];
        idsArr = ids.toArray(idsArr);
        return baseMapper.listUserInDept(idsArr);
    }

    /**
     * 功能描述: <br>获取所有的单位附带用户列表
     *
     * @return: java.util.List<com.unity.rbac.entity.Department>  所有的单位附带用户列表
     * @author: gengzhiqiang
     * @since: 2019/7/1 11:09
     */
    public List<Department> listUserInDeptAll() {
        return baseMapper.listUserInDeptAll();
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
        checkDepSaveOrUpdateInfo(dto);
        dto.setName(dto.getName().trim());
        if (dto.getId() == null) {
            Department department = new Department();
            department.setDepType(dto.getDepType() == null ? DepartmentDepTypeEnum.COMPANY.getType() : dto.getDepType());
            department.setName(dto.getName());
            department.setNotes(dto.getNotes());
            department.setIsDeleted(YesOrNoEnum.NO.getType());
            if (dto.getIdParent() != null) {
                department.setIdParent(dto.getIdParent());
                //组装编号
                Department departmentParent = super.getById(dto.getIdParent());
                if (departmentParent == null) {
                    throw UnityRuntimeException.newInstance()
                            .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                            .message("未获取到上级单位信息")
                            .build();
                }
                //三级单位不可添加四级单位
                if (UserAccountLevelEnum.THIRD_COMPANY.getId() <= departmentParent.getLevel()) {
                    throw UnityRuntimeException.newInstance()
                            .code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                            .message("三级单位不可增加子公司")
                            .build();
                }
                int count = super.count2(new QueryWrapper<Department>().lambda()
                        .eq(Department::getIdParent, dto.getIdParent()));
                department.setGradationCode(departmentParent.getGradationCode() + "." + (++count));
                department.setLevel(departmentParent.getLevel() + 1);
            } else {
                //统计第一级别的数量，用来定义编号
                int count = super.count2(new QueryWrapper<Department>().lambda()
                        .isNull(Department::getIdParent));
                department.setIdParent(null);
                department.setLevel(1);
                department.setGradationCode("." + (++count));
            }
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
            department.setDepType(dto.getDepType() == null ? DepartmentDepTypeEnum.COMPANY.getType() : dto.getDepType());
            department.setName(dto.getName());
            if (StringUtils.isNotEmpty(dto.getNotes())) {
                department.setNotes(dto.getNotes());
            }
            super.updateById(department);
            //保存公司到redis
            saveOrUpdateDepartmentToRedis(department);
        }
    }

    private void checkDepSaveOrUpdateInfo(Department dto) {
        if (StringUtils.isBlank(dto.getName())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未检测到单位名称")
                    .build();
        }
        if (dto.getName().length() > NAME_MAX_LENGTH) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH)
                    .message("单位名称字数限制50字")
                    .build();
        }
        if (dto.getId() == null && 0 < super.count(new QueryWrapper<Department>().lambda()
                .eq(Department::getName, dto.getName()))) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                    .message("单位名称已存在")
                    .build();

        } else if (dto.getId() != null && 0 < super.count(new QueryWrapper<Department>().lambda()
                .ne(Department::getId, dto.getId())
                .eq(Department::getName, dto.getName()))) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                    .message("单位名称已存在")
                    .build();
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
     * 获取组织架构所属人员ID
     *
     * @param departmentIds 组织ID列表
     * @author jiaww
     * @since 2019/02/20 11:14
     */
    public List<Long> getUserIdsByDepartmentIds(List<Long> departmentIds) {
        List<Long> userIdsByDepartmentIds;
        if (!departmentIds.isEmpty()) {
            userIdsByDepartmentIds = baseMapper.getUserIdsByDepartmentIds(departmentIds.parallelStream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
            return userIdsByDepartmentIds;
        } else {
            userIdsByDepartmentIds = Lists.newArrayList();
        }
        return userIdsByDepartmentIds;
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
        //是否存在下级关系
        if (0 < super.count(new QueryWrapper<Department>().lambda()
                .eq(Department::getIdParent, id))) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("当前组织架构下存在下级组织，请先解除")
                    .build();
        }
        super.removeById(id);
        //从redis移除该公司
        hashRedisUtils.removeValueByKey(RedisConstants.DEPARTMENT.concat(id.toString()));
        //更新redis中单位排序
        reOrderDepartment();
    }

    /**
     * 获取指定组织架构信息
     *
     * @param id 指定组织id
     * @return 指定组织架构信息
     * @author gengjiajia
     * @since 2018/12/14 15:15
     */
    public Map<String, Object> getDepartmentInfo(Long id) {
        Department department = super.getById(id);
        return JsonUtil.ObjectToMap(department,
                new String[]{"id", "name", "iLevel", "gradationCode", "idParent", "parentName", "parentLevel", "parentGradationCode", "notes", "gmtCreate"},
                (m, u) -> {
                    if (department.getIdParent() != null) {
                        Department parentDepartment = super.getById(department.getIdParent());
                        m.put("parentName", parentDepartment.getName());
                        m.put("parentLevel", parentDepartment.getLevel());
                        m.put("parentGradationCode", parentDepartment.getGradationCode());
                    }
                    m.put("gmtCreate", DateUtils.timeStamp2Date(department.getGmtCreate()));
                });
    }

    /**
     * 获取指定组织架构和其子机构信息
     *
     * @param id 指定组织id
     * @return 指定组织架构信息
     * @author zhangxiaogang
     * @since 2019/3/6 14:22
     */
    public List<Department> getDepartmentAndChildDepartList(Long id) {
        Department department = super.getById(id);
        List<Department> departmentList;
        if (department != null) {
            QueryWrapper<Department> qw = new QueryWrapper<>();
            qw.lambda().likeRight(Department::getGradationCode, department.getGradationCode());
            departmentList = baseMapper.selectList(qw);
        } else {
            departmentList = Lists.newArrayList();
        }
        return departmentList;
    }

    /**
     * 根据条件查询组织机构列表
     *
     * @param search 查询条件
     * @return 组织机构列表
     * @author gengjiajia
     * @since 2019/01/09 17:15
     */
    public Map findDepartmentListBySearch(SearchElementGrid search) {
        QueryWrapper<Department> wrapper = search.toEntityWrapper(Department.class);
        wrapper.lambda().orderByAsc(Department::getId);
        return JsonUtil.<Department>ObjectToMap(super.page(search.getPageable(), wrapper),
                new String[]{"id", "name", "idParent", "level", "gradationCode", "gmtCreate", "notes"},
                (m, u) ->
                        m.put("gmtCreate", DateUtils.timeStamp2Date(u.getGmtCreate()))
        );
    }

    /**
     * 批量获取机构信息
     *
     * @param departmentIds 机构id集
     * @return 机构信息
     * @author gengjiajia
     * @since 2019/01/09 19:26
     */
    public Map<String, Map<String, Object>> findDepartmentInfoByIdIn(List<Long> departmentIds) {
        Map<String, Map<String, Object>> map = Maps.newHashMap();
        List<Map<String, Object>> mapList = JsonUtil.ObjectToList(super.list(new QueryWrapper<Department>().lambda()
                        .in(Department::getId, departmentIds.toArray())),
                new String[]{"id", "name", "idParent", "level", "gradationCode", "gmtCreate", "notes", "depType"},
                (m, u) ->
                        m.put("gmtCreate", DateUtils.timeStamp2Date(u.getGmtCreate()))
        );
        mapList.forEach(m ->
                map.put(m.get("id").toString(), m)
        );
        return map;
    }

    /**
     * 批量获取党委信息
     *
     * @param departmentIds 机构id集
     * @return 机构信息
     * @author wangbin
     * @since 2019年2月28日13:40:15
     */
    public Map<String, Map<String, Object>> findPartyCommitteeMap(List<Long> departmentIds) {
        Map<String, Map<String, Object>> map = Maps.newHashMap();
        //获取到所有的公司
        List<Department> list = super.list(new QueryWrapper<Department>().lambda()
                .eq(Department::getDepType, DepartmentDepTypeEnum.COMPANY.getType()));
        //取公司id,通过公司id获取下级，即为党委
        List<Long> companyIdList = list.parallelStream().map(Department::getId).collect(toList());
        LambdaQueryWrapper<Department> ew = new QueryWrapper<Department>().lambda();
        if (CollectionUtils.isNotEmpty(departmentIds)) {
            ew.in(Department::getId, departmentIds.toArray());
            ew.in(Department::getIdParent, companyIdList.toArray());
        } else {
            //查询所有党委，公司下级即为党委，不根据类型区分
            ew.in(Department::getIdParent, companyIdList.toArray());
        }
        ew.orderByAsc(Department::getSort);
        List<Department> departmentList = super.list(ew);
        List<Map<String, Object>> mapList = JsonUtil.ObjectToList(departmentList,
                new String[]{"id", "name", "idParent", "level", "gradationCode", "notes", "depType"},
                null);
        mapList.forEach(m ->
                map.put(m.get("id").toString(), m)
        );
        return map;
    }

    /**
     * 批量获取支部信息
     *
     * @param departmentId 机构id集
     * @return 机构信息
     * @author wangbin
     * @since 2019年2月28日13:40:15
     */
    public Map<String, Map<String, Object>> findDepartmentInfoByIdInAndBranch(Long departmentId) {
        Map<String, Map<String, Object>> map = Maps.newHashMap();
        QueryWrapper<Department> ew = new QueryWrapper<>();
        ew.lambda().eq(Department::getDepType, DepartmentDepTypeEnum.BRANCH.getType());
        ew.lambda().eq(Department::getIdParent, departmentId);
        List<Department> departmentList = super.list(ew);

        List<Map<String, Object>> mapList = JsonUtil.ObjectToList(departmentList,
                new String[]{"id", "name", "idParent", "level", "gradationCode", "gmtCreate", "notes"},
                (m, u) ->
                        m.put("gmtCreate", DateUtils.timeStamp2Date(u.getGmtCreate()))
        );
        mapList.forEach(m ->
                map.put(m.get("id").toString(), m)
        );
        return map;
    }

    /**
     * 获取公司列表
     *
     * @return 公司列表
     * @author gengjiajia
     * @since 2019/01/24 10:54
     */
    public List<Map<String, Object>> getCompanyList() {
        return JsonUtil.ObjectToList(super.list(new QueryWrapper<Department>().lambda()
                        .eq(Department::getDepType, DepartmentDepTypeEnum.COMPANY.getType())),
                new String[]{"id", "name"},
                null);
    }

    /**
     * 获取党支部列表
     *
     * @return 党支部列表
     * @author gengjiajia
     * @since 2019/01/24 10:54
     */
    public List<Map<String, Object>> getPartyBranchListByCompanyId(Long companyId) {
        Department department = super.getById(companyId);
        return JsonUtil.ObjectToList(super.list(new QueryWrapper<Department>().lambda()
                        .likeRight(Department::getGradationCode, department.getGradationCode().concat("."))
                        .eq(Department::getDepType, DepartmentDepTypeEnum.BRANCH.getType())),
                new String[]{"id", "name"},
                null);
    }

    /**
     * 获取指定组织下的所有节点
     *
     * @return 党支部列表
     * @author gengjiajia
     * @since 2019/01/24 10:54
     */
    public List<Long> getDepartmentListByParentId(Long companyId) {
        Department department = super.getById(companyId);
        List<Department> departmentList = super.list(new QueryWrapper<Department>().lambda()
                .likeRight(Department::getGradationCode, department.getGradationCode().concat(".")));
        return departmentList.parallelStream().map(Department::getId).collect(toList());
    }

    /**
     * 获取党委及支部列表
     *
     * @return 党委及支部列表
     * @author gengjiajia
     * @since 2019/02/28 14:58
     */
    public List<Map<String, Object>> getPartyCommitteeBranchList() {
        List<Department> departmentList = super.list(new QueryWrapper<Department>().lambda()
                .in(Department::getDepType, Arrays.asList(DepartmentDepTypeEnum.PARTYCOMMITTEE.getType(), DepartmentDepTypeEnum.BRANCH.getType())));

        if (CollectionUtils.isEmpty(departmentList)) {
            return Lists.newArrayList();
        }
        return JsonUtil.ObjectToList(departmentList,
                new String[]{"id", "name"},
                null);
    }

    /**
     * 根据支部id获取公司列表
     *
     * @param departmentIdList 支部ID集
     * @return 公司列表
     * @author gengjiajia
     * @since 2019/03/12 13:58
     */
    public List<Map<String, Object>> getCompanyListByDepartmentIds(List<Long> departmentIdList) {
        if (CollectionUtils.isEmpty(departmentIdList)) {
            return Lists.newArrayList();
        }
        return super.listMaps(new QueryWrapper<Department>().lambda()
                .in(Department::getId, departmentIdList.toArray())
                .isNotNull(Department::getIdParent));
    }


    /**
     * 获取统计需要的组织架构列表
     *
     * @return 部门信息
     */
    public List<Department> findDepartmentListByLevel(Integer level) {
        QueryWrapper<Department> qw = new QueryWrapper<>();
        qw.lambda().eq(Department::getLevel, level);
        return baseMapper.selectList(qw);
    }


    /**
     * 返回 集团单位置顶，其他按单位名排序的，相同的单位按时间倒序 的 集合
     *
     * @param ids 主键集合
     * @return 集团单位置顶，其他按单位名排序的，相同的单位按时间倒序 的 集合
     * @author JH
     * @date 2019/7/12 16:15
     */
    public List<Department> getNameOrderdDepartmentsByIds(List<Long> ids) {
        return baseMapper.getNameOrderdDepartmentsByIds(ids);
    }

    /**
     * 根据公司名称获取对应公司列表
     *
     * @param name 公司名称
     * @return 对应公司列表
     * @author gengjiajia
     * @since 2019/07/09 11:15
     */
    public List<Map<String, Object>> findDepartmentListByName(String name) {
        List<Department> departmentList = baseMapper.selectList(
                new QueryWrapper<Department>().lambda().like(Department::getName, name)
                        .orderByAsc(Department::getLevel));
        return JsonUtil.ObjectToList(departmentList,
                new String[]{"id", "name", "level",},
                (m, entity) -> {
                });
    }

    /**
     * 根据公司id获取其集团公司和自身列表
     *
     * @param id 公司id
     * @return 对应公司列表
     * @author gengjiajia
     * @since 2019/07/09 11:15
     */
    public List<Department> thirdGroupGetDepts(Long id) {
        return list(new LambdaQueryWrapper<Department>().and(e->e.eq(Department::getLevel, UserAccountLevelEnum.GROUP.getId()).or().eq(Department::getId, id)).orderByDesc(Department::getSort));
    }

    /**
     * 三级单位根据自己的gradationCode获取其集团公司和自身列表和自己的上级
     *
     * @param gradationCode 公司的gradationCode
     * @return 对应公司列表
     * @author gengjiajia
     * @since 2019/07/09 11:15
     */
    public List<Department> thirdGroupGetDepts2(String gradationCode) {
        return list(new LambdaQueryWrapper<Department>().and(e->e.eq(Department::getLevel, UserAccountLevelEnum.GROUP.getId()).or().eq(Department::getGradationCode, gradationCode).or().eq(Department::getGradationCode, gradationCode.substring(0, gradationCode.lastIndexOf(".")))).orderByDesc(Department::getSort));
    }


    /**
     * 根据单位等级，获取单位的id和name的集合
     *
     * @param levels 单位等级
     * @return 单位的id和name集合
     * @author lifeihong
     * @date 2019/7/5 17:06
     */
    public List<Department> listDepartmentListByLevel(List<Integer> levels) {
        //查询符合条件的单位集合
        return baseMapper.selectList(new LambdaQueryWrapper<Department>().in(Department::getLevel, levels.toArray()));
    }

    public List<Department> listDepartmentListForNotice() {
        //查询符合条件的单位集合
        return baseMapper.listDepartmentListForNotice();
    }
    /**
     * 通过账号级别获取单位信息
     *
     * @param accountLevel 账号级别 1：集团、2：二级单位、3：三级单位、4：项目账号
     * @return 单位列表
     * @author gengjiajia
     * @since 2019/07/08 17:36
     */
    public List<Map<String, Object>> findDepartmentByAccountLevel(Integer accountLevel) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        //根据当前登录人的账号级别获取对应单位 此处当 当前人账号级别如为二级账号，则不能获取集团单位，只能获取当前人所属二级单位及其下级单位，三级账号以此类推
        Integer thisAccountLevel = customer.getAccountLevel();
        if (UserAccountLevelEnum.PROJECT.getId().equals(thisAccountLevel)) {
            //当前人是项目账号，不可新增账号
            return Lists.newArrayList();
        } else if (UserAccountLevelEnum.THIRD_COMPANY.getId().equals(thisAccountLevel)) {
            if (UserAccountLevelEnum.GROUP.getId().equals(accountLevel) || UserAccountLevelEnum.SECOND_COMPANY.getId().equals(accountLevel)) {
                //当前人是三级账号，不可获取集团及二级单位
                return Lists.newArrayList();
            }
        } else if (UserAccountLevelEnum.GROUP.getId().equals(accountLevel) && UserAccountLevelEnum.SECOND_COMPANY.getId().equals(thisAccountLevel)) {
            //当前是二级单位，不可获取集团账号
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (UserAccountLevelEnum.GROUP.getId().equals(thisAccountLevel)) {
            //当前人账号级别为集团，可获取所有对应的单位
            if (UserAccountLevelEnum.GROUP.getId().equals(accountLevel) || UserAccountLevelEnum.SECOND_COMPANY.getId().equals(accountLevel)
                    || UserAccountLevelEnum.THIRD_COMPANY.getId().equals(accountLevel)) {
                //账号级别为 集团 或 二级单位 或三级单位 则获取对应级别的单位
                wrapper.eq(Department::getLevel, accountLevel);
            } else if (UserAccountLevelEnum.PROJECT.getId().equals(accountLevel)) {
                //账号级别为 项目账号 获取有效的全部单位
                wrapper.isNotNull(Department::getIdParent);
            } else {
                //传入意料之外的值
                return Lists.newArrayList();
            }
        } else if (UserAccountLevelEnum.SECOND_COMPANY.getId().equals(thisAccountLevel)) {
            //当前人账号为二级单位，只能获取自己所属的二级单位，获取下属三级单位
            if (UserAccountLevelEnum.SECOND_COMPANY.getId().equals(accountLevel)) {
                //只返回当前人所属的单位
                wrapper.eq(Department::getId, customer.getIdRbacDepartment());
            } else if (UserAccountLevelEnum.THIRD_COMPANY.getId().equals(accountLevel)) {
                //返回当前人所属单位的下属单位
                wrapper.eq(Department::getIdParent, customer.getIdRbacDepartment());
            } else if (UserAccountLevelEnum.PROJECT.getId().equals(accountLevel)) {
                //账号级别为 项目账号 获取当前人所属单位及下属单位 !!! 如果出现四级单位，此处会查询出所有子孙
                wrapper.likeRight(Department::getGradationCode, customer.getGradationCodeRbacDepartment());
            } else {
                //传入意料之外的值
                return Lists.newArrayList();
            }
        } else if (UserAccountLevelEnum.THIRD_COMPANY.getId().equals(thisAccountLevel)) {
            //当前人账号为三级单位，只能获取自己所属的三级单位
            if (UserAccountLevelEnum.THIRD_COMPANY.getId().equals(accountLevel) || UserAccountLevelEnum.PROJECT.getId().equals(accountLevel)) {
                //返回当前人所属单位的下属单位  项目账号 获取当前人所属单位
                wrapper.eq(Department::getId, customer.getIdRbacDepartment());
            } else {
                //传入意料之外的值
                return Lists.newArrayList();
            }
        } else {
            //其他情况，暂不考虑，返回空值
            return Lists.newArrayList();
        }
        List<Department> departmentList = this.list(wrapper);
        if (CollectionUtils.isEmpty(departmentList)) {
            return Lists.newArrayList();
        }
        return JsonUtil.ObjectToList(departmentList, new String[]{"id", "name"}, null);
    }


    /**
     * 下载excel
     *
     * @param department 需要下载的数据
     * @return 数据流
     * @author JH
     * @date 2019/7/9 15:48
     */
    public byte[] getDepartmentTemplate(Department department) {
        //查询模板信息
        byte[] content;

        String templatePath = systemConfiguration.getUploadPath() + File.separator + "department" + File.separator;
        String templateFile = templatePath + File.separator + "department.xls";
        File dir = new File(templatePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream out = null;
        try {
            // excel对象
            HSSFWorkbook wb = new HSSFWorkbook();
            // sheet对象
            HSSFSheet sheet = wb.createSheet(log.getName());
            String[][] param = {
                    {"单位名称", department.getName()},
                    {"注册地址", department.getAddress() == null ? "" : department.getAddress()},
                    {"提交日期", department.getGmtModified() == null ? "" : DateUtils.timeStamp2Date(department.getGmtModified())}
            };
            Map<String, CellStyle> styleMap = ExcelStyleUtil.createProjectStyles(wb);
            //创建单元格
            for (int i = 0; i < HEADERS_LENGTH; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < CELL_LENGTH; j++) {
                    Cell cell = row.createCell(j);
                    if (j == 0) {
                        cell.setCellStyle(styleMap.get("title"));
                    } else {
                        cell.setCellStyle(styleMap.get("data"));
                    }
                    sheet.autoSizeColumn(j, true);
                    cell.setCellValue(param[i][j]);
                }
            }

            out = new FileOutputStream(templateFile);
            // 输出excel
            wb.write(out);
            out.close();
            File file = new File(templateFile);
            content = FileReaderUtil.getBytes(file);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.SERVER_ERROR, "下载失败");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content;
    }

    /**
     * 功能描述 条件判断获取单位名称
     *
     * @return java.util.List<com.unity.rbac.entity.Department>
     * @author gengzhiqiang
     * @date 2019/7/10 17:21
     */
    public List<Department> getAllDeptNames() {
        return baseMapper.getAllDeptNames();
    }

    /**
     * 获取单位信息
     *
     * @param id 单位id
     * @return 单位信息
     * @author gengjiajia
     * @since 2019/07/11 20:12
     */
    public Map<String, Object> getDepartmentMapById(Long id) {
        Department department = this.getById(id);
        if (department == null) {
            return null;
        }
        return GsonUtils.map(GsonUtils.format(department));
    }

    /**
     * 上移/下移
     *
     * @param department 需要移动的实体
     * @author JH
     * @date 2019/7/24 11:35
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeOrder(Department department) {
        if (department != null && department.getId() != null && department.getUpOrDown() != null) {
            long id = department.getId();
            Department old = super.getById(id);
            String msg;
            if(UserAccountLevelEnum.GROUP.getId().equals(old.getLevel())) {
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                        .message("集团不能移动").build();
            }
            int upOrDown = department.getUpOrDown();
            long sort = old.getSort();
            LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper();
            Customer customer = LoginContextHolder.getRequestAttributes();
            wrapper.likeRight(Department::getGradationCode, customer.getGradationCodeRbacDepartment());
            if (upOrDown == 1) {
                wrapper.lt(Department::getSort, sort);
                msg = "已经是最后一条数据";
                wrapper.orderByDesc(Department::getSort);
            } else {
                wrapper.gt(Department::getSort, sort);
                msg = "已经是第一条数据";
                wrapper.orderByAsc(Department::getSort);
            }
            wrapper.last("limit 1");
            //当前数据的上一条或者下一条的数据
            Department entity1 = getOne(wrapper);
            if (entity1 == null) {
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                        .message(msg).build();
            }

            if(UserAccountLevelEnum.GROUP.getId().equals(entity1.getLevel())){
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                        .message("集团不能移动").build();
            }
            baseMapper.changeOrder(old.getId(),entity1.getSort());
            baseMapper.changeOrder(entity1.getId(),sort);
            //更新redis中单位排序
            reOrderDepartment();
        } else {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("id、upOrDown不能为空").build();
        }
    }

    /**
     * 更新redis中单位排序
     *
     * @author JH
     * @date 2019/7/24 16:46
     */
    public void reOrderDepartment() {
        //将公司排序信息放入redis
        List<Department> list = super.list(new LambdaQueryWrapper<Department>().orderByDesc(Department::getSort));
        List<Long> ids = list.stream().map(Department::getId).collect(toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            String s = JSON.toJSONString(ids);
            redisTemplate.delete(RedisConstants.DEPARTMENT_ORDER_LIST);
            redisTemplate.opsForValue().set(RedisConstants.DEPARTMENT_ORDER_LIST, s);
            log.info("====== 《com.unity.rbac.service.DepartmentServiceImpl.reOrderDepartment()》 跟新单位排序到缓存数量 {}", ids.size());
        }
    }
}
