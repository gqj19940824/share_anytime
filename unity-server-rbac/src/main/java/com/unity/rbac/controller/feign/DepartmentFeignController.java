package com.unity.rbac.controller.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;
import com.unity.common.enums.UserAccountLevelEnum;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.JsonUtil;
import com.unity.rbac.entity.Department;
import com.unity.rbac.entity.User;
import com.unity.rbac.service.DepartmentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织架构信息后台控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 */
@Slf4j
@RestController
@RequestMapping("feign/department")
public class DepartmentFeignController {

    private final DepartmentServiceImpl departmentService;

    public DepartmentFeignController(DepartmentServiceImpl departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * @desc: 根据层级编码查询子单位名称
     * @param: [code]
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author: 郭振洋
     * @date: 2019/7/17 11:51
     **/
    @GetMapping("getDepartmentChild/{idDepartment}")
    public List<Department> getDepartmentChild(@PathVariable("idDepartment") Long idDepartment){
        return departmentService.getDepartmentAndChildDepartList(idDepartment);
    }

    /**
     * 组织架构列表
     *
     * @return code 0 表示成功
     * @author gengjiajia
     * @since 2018/12/11 15:00
     */
    @GetMapping("findDepartmentList")
    public List<Map<String, Object>> findDepartmentList() {
        List<Map<String, Object>> maps = JsonUtil.ObjectToList(departmentService.list(new QueryWrapper<Department>().lambda().orderByAsc(Department::getId)),
                new String[]{"id", "name", "gradationCode"},
                null);
        return maps;
    }

    /**
     * 组织架构列表
     *
     * @return 组织架构列表
     * @author zhangxiaogang
     * @since 2019/3/21 14:20
     */
    @GetMapping("findAllDepartmentList")
    public List<Department> findAllDepartmentList() {
        return departmentService.list(new QueryWrapper<Department>().lambda().orderByAsc(Department::getId));
    }

    /**
     * 根据条件查询机构列表
     *
     * @return code 0 表示成功
     * @author gengjiajia
     * @since 2018/12/11 15:00
     */
    @PostMapping("findDepartmentListBySearch")
    public Map findDepartmentListBySearch(@RequestBody SearchElementGrid search) {
        log.info("===根据条件查询机构列表===《findDepartmentListBySearch》===入参 {}", GsonUtils.format(search));
        return departmentService.findDepartmentListBySearch(search);
    }

    /**
     * 批量获取机构信息
     *
     * @return code 0 表示成功
     * @author gengjiajia
     * @since 2018/12/11 15:00
     */
    @PostMapping("findDepartmentInfoByIdIn")
    public Map<String, Map<String, Object>> findDepartmentInfoByIdIn(@RequestBody List<Long> departmentIds) {
        log.info("===批量获取机构信息===《findDepartmentInfoByIdIn》===入参 {}", GsonUtils.format(departmentIds));
        if (CollectionUtils.isEmpty(departmentIds)) {
            return Maps.newHashMap();
        }
        return departmentService.findDepartmentInfoByIdIn(departmentIds);
    }

    /**
     * 批量获取党委信息
     *
     * @return code 0 表示成功
     * @author wangbin
     * @since 2019年2月28日13:44:59
     */
    @PostMapping("findPartyCommitteeMap")
    public Map<String, Map<String, Object>> findPartyCommitteeMap(@RequestBody List<Long> departmentIds) {
        log.info("===批量获取党委信息===《findPartyCommitteeMap》===入参 {}", GsonUtils.format(departmentIds));
        return departmentService.findPartyCommitteeMap(departmentIds);
    }

    /**
     * 通过党委获取支部信息
     *
     * @return code 0 表示成功
     * @author wangbin
     * @since 2019年2月28日13:44:59
     */
    @PostMapping("findDepartmentInfoByIdInAndBranch/{id}")
    public Map<String, Map<String, Object>> findDepartmentInfoByIdInAndBranch(@PathVariable("id") Long id) {
        log.info("===批量获取党委信息===《findDepartmentInfoByIdInAndDepType》===入参 {}", id);
        if (id == null) {
            return Maps.newHashMap();
        }
        return departmentService.findDepartmentInfoByIdInAndBranch(id);
    }


    /**
     * 获取组织架构所属人员ID
     *
     * @param departmentIds 组织ID列表
     * @author jiaww
     * @since 2019/02/20 11:14
     */
    @PostMapping("getUserIdsByDepartmentIds")
    public List<Long> getUserIdsByDepartmentIds(@RequestBody List<Long> departmentIds) {
        return departmentService.getUserIdsByDepartmentIds(departmentIds);
    }

    /**
     * 通过ID获取组织机构信息
     *
     * @param id
     * @return map
     * @author zhaozesheng
     * @since 2019/1/10 14:42
     */
    @GetMapping("getDepartmentById/{id}")
    public Map<String, Object> getDepartmentById(@PathVariable("id") Long id) {
        return departmentService.getDepartmentMapById(id);
    }

    /**
     * 通过ID获取组织机构信息
     *
     * @param id
     * @return map
     * @author zhaozesheng
     * @since 2019/1/10 14:42
     */
    @GetMapping("getParentDepartment")
    public Department getParentDepartment(@RequestBody Long id) {
        Department department = departmentService.getById(id);
        if (department == null){
            return null;
        }else {
            Long idParent = department.getIdParent();
            return departmentService.getById(idParent);
        }
    }




    /**
     * 通过ID获取组织机构信息
     *
     * @param id
     * @return map
     */
    @PostMapping("getById/{id}")
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        return departmentService.getMap(new QueryWrapper<Department>().lambda().eq(Department::getId, id));
    }


    /**
     * 通过ID获取组织机构详细信息
     *
     * @param id
     * @return map
     * @author zhaozesheng
     * @since 2019/1/10 14:42
     */
    @GetMapping("getDepartment/{id}")
    public Map<String, Object> findDepartmentInfoById(@PathVariable("id") Long id) {
        return departmentService.getDepartmentInfo(id);
    }


    /**
     * 获取指定组织下的所有节点
     *
     * @param id
     * @return map
     * @author zhaozesheng
     * @since 2019/1/10 14:42
     */
    @GetMapping("getDepartmentListByParentId/{id}")
    public List<Long> getDepartmentListByParentId(@PathVariable("id") Long id) {
        return departmentService.getDepartmentListByParentId(id);
    }


    /**
     * 获取指定组织架构和其子机构信息
     *
     * @param id 指定组织id
     * @return 指定组织架构信息
     */
    @GetMapping("getDepartmentAndChildDepartList/{id}")
    public List<Department> getDepartmentAndChildDepartList(@PathVariable("id") Long id) {
        return departmentService.getDepartmentAndChildDepartList(id);
    }

    /**
     * 获取指定组织架构和其子机构信息
     *
     * @param id 指定组织id
     * @return 指定组织架构信息
     */
    @PostMapping("postDepartmentAndChildDepartList/{id}")
    public List<Department> postDepartmentAndChildDepartList(@PathVariable("id") Long id) {
        return departmentService.getDepartmentAndChildDepartList(id);
    }

    /**
     * 获取上级公司和子公司的公司列表
     *
     * @param gradationList 级次编码列表
     * @return 指定组织架构信息
     */
    @PostMapping("getParentAndChildrenDeptList")
    public List<Department> postDepartmentAndChildDepartList(@RequestBody List<String> gradationList) {
        return departmentService.postDepartmentAndChildDepartList(gradationList);
    }

    /**
     * 获取公司列表
     *
     * @return 公司列表
     * @author gengjiajia
     * @since 2019/01/24 10:54
     */
    @GetMapping("/getCompanyList")
    public List<Map<String, Object>> getCompanyList() {
        return departmentService.getCompanyList();
    }

    /**
     * 获取党支部列表
     *
     * @return 支部列表
     * @author gengjiajia
     * @since 2019/01/24 10:54
     */
    @GetMapping("/getPartyBranchListByCompanyId/{companyId}")
    public List<Map<String, Object>> getPartyBranchListByCompanyId(@PathVariable("companyId") Long companyId) {
        return departmentService.getPartyBranchListByCompanyId(companyId);
    }

    /**
     * 根据公司名称获取党支部列表
     *
     * @return 支部列表
     * @author gengjiajia
     * @since 2019/01/24 10:54
     */
    /*@PostMapping("/getPartyBranchListByCompanyName")
    public List<Map<String,Object>> getPartyBranchListByCompanyName(@RequestParam("companyName") String companyName){
        return departmentService.getPartyBranchListByCompanyName(companyName);
    }*/

    /**
     * 通过ID列表获取组织名称
     *
     * @param id ID列表
     * @return maps
     * @author jiaww
     * @since 2019/01/07 09:50
     */
    @PostMapping("getDeptName/{id}")
    public String getDeptName(@PathVariable("id") Long id) {
        Department department = departmentService.getById(id);
        return department == null ? " " : department.getName();
    }

    /**
     * 通过ID获取组织名称
     *
     * @param ids ID列表
     * @return maps
     * @author jiaww
     * @since 2019/01/07 09:50
     */
    @PostMapping("getDeptNames")
    public List<Map<String, Object>> getDeptNames(@RequestBody List<Long> ids) {
        QueryWrapper<Department> ew = new QueryWrapper();
        if (CollectionUtils.isEmpty(ids)) {
            ew.lambda().isNull(Department::getId);
        } else {
            ew.lambda().in(Department::getId, ids);
        }
        List<Department> resources = departmentService.list(ew);
        List<Map<String, Object>> maps = JsonUtil.<Department>ObjectToList(resources,
                new String[]{"id", "name"}, null
        );
        return maps;
    }

    /**
     * 通过id获取相应的用户列表
     *
     * @param ids ID列表
     * @return maps
     */
    @PostMapping("listUserInDept")
    public Map<Long, Map<Long, String>> listUserInDept(@RequestBody List<Long> ids) {
        List<Department> list = departmentService.listUserInDept(ids);
        Map<Long, List<User>> map = list.stream().collect(Collectors.toMap(Department::getId, Department::getUsers));
        Map<Long, Map<Long, String>> result = new HashMap<>();
        for (Map.Entry<Long, List<User>> entry : map.entrySet())
            result.put(entry.getKey(), entry.getValue().stream().collect(Collectors.toMap(User::getId, User::getName)));
        return result;
    }
    /**
     * 功能描述: <br>>获取所有的单位附带用户列表
     * @Param: []
     * @Return: java.util.Map<java.lang.Long,java.util.Map<java.lang.Long,java.lang.String>>
     * @Author: gengzhiqiang
     * @Date: 2019/7/2 16:29
     */
    @PostMapping("listUserInDeptAll")
    public Map<Long, Map<Long, String>> listUserInDeptAll() {
        List<Department> list = departmentService.listUserInDeptAll();
        Map<Long, List<User>> map = list.stream().collect(Collectors.toMap(Department::getId, Department::getUsers));
        Map<Long, List<User>> map1=new LinkedTreeMap<>();
        list.stream().forEach(department -> {
            map1.put(department.getId(),department.getUsers());
        });
        Map<Long, Map<Long, String>> result = new LinkedTreeMap<>();
        for (Map.Entry<Long, List<User>> entry : map1.entrySet()) {
            Map<Long, String> innerMap = new LinkedTreeMap<>();
            entry.getValue().stream().forEach(user -> {
                innerMap.put(user.getId(), user.getName());
            });
            result.put(entry.getKey(), innerMap);
        }
        return result;
    }
    /**
     * 通过ID获取组织名称
     *
     * @param ids ID列表
     * @return maps
     */
    @PostMapping("getDeptNamesByIds")
    public Map<Long, String> getDeptNamesByIds(@RequestBody List<Long> ids) {
        QueryWrapper<Department> ew = new QueryWrapper();
        if (CollectionUtils.isEmpty(ids)) {
            ew.lambda().isNull(Department::getId);
        } else {
            ew.lambda().in(Department::getId, ids);
        }
        List<Department> resources = departmentService.list(ew);

        Map<Long, String> map = resources.stream().collect(Collectors.toMap(Department::getId, Department::getName));

        return map;
    }





    /**
     * 获取所有的党委及支部列表
     *
     * @return 党委及支部列表findDepartmentInfoByIdInAndDepType
     * @author gengjiajia
     * @since 2019/02/28 14:55
     */
    @GetMapping("/getPartyCommitteeBranchList")
    public List<Map<String, Object>> getPartyCommitteeBranchList() {
        return departmentService.getPartyCommitteeBranchList();
    }


    /**
     * 获取所有的党委的ID
     *
     * @param
     * @return
     * @author zhaozesheng
     * @since 2019/3/11 15:11
     */
    @GetMapping("getAllPartyCommitteesIds/{depType}")
    public List<Long> getAllPartyCommitteesIds(@PathVariable String depType) {

        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Department::getDepType, Long.valueOf(depType));
        List<Department> departmentList = departmentService.list(queryWrapper);
        List<Long> longList = new ArrayList<Long>() {{
            departmentList.forEach(item -> add(item.getId()));
        }};

        return longList;
    }

    /**
     * 根据支部id获取公司列表
     *
     * @param ids 支部ID集
     * @return 公司列表
     * @author gengjiajia
     * @since 2019/03/12 13:58
     */
    @PostMapping("getCompanyListByDepartmentIds")
    public List<Map<String, Object>> getCompanyListByDepartmentIds(@RequestBody List<Long> ids) {
        return departmentService.getCompanyListByDepartmentIds(ids);
    }

    /*
     * 获取统计需要的组织架构列表
     *
     * @return 组织架构列表
     * @author zhangxiaogang
     * @since 2019/3/23 15:46
    @GetMapping("findChooseDepartmentList")
    public List<Department> findChooseDepartmentList() {
        return departmentService.findUserChooseDepartList();
    }
    */

    @PostMapping("/listDepartmentListByLevel")
    public List<Department> listDepartmentListByLevel(@RequestBody List<Integer> levels){
        if(CollectionUtils.isEmpty(levels)){
            return new ArrayList<>();
        }
        return departmentService.listDepartmentListByLevel(levels);
    }

    @PostMapping("/listDepartmentListForNotice")
    public List<Department> listDepartmentListForNotice(){
        return departmentService.listDepartmentListForNotice();
    }
    /**
     * 根据级别获取需要的组织架构列表
     *
     * @return 组织架构列表
     */
    @PostMapping("findDepartmentListByLevel")
    public List<Department> findDepartmentListByLevel(@RequestBody Map<String, Integer> map) {
        if (map.get("level") == null) {
            return Lists.newArrayList();
        }
        return departmentService.findDepartmentListByLevel(map.get("level"));
    }

    /**
     * 功能描述 根据用户级别查询单位ID集合
     *
     * @param level
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO> 单位集合
     * @author gengzhiqiang
     * @date 2019/7/9 17:10
     */
    @PostMapping("findDepartmentIdsListByLevel")
    public List<Long> findDepartmentIdsListByLevel(@RequestParam("level") Integer level){
        return departmentService.getDepartmentIdsByLevel(level);
    }

    /**
     * 功能描述: <br>根据公司名称 模糊查询单位信息
     * @Param: [map]
     * @Return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author: gengzhiqiang
     * @Date: 2019/7/4 11:27
     */
    @PostMapping("findDepartmentListByName")
    public List<Map<String, Object>>  findDepartmentListByName(@RequestBody Map<String, String> map) {
        return departmentService.findDepartmentListByName(map.get("name"));
    }

    /**
     * 功能描述: <br>三级单位获取公司列表(集团+自身)
     * @Param: [map]
     * @Return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author: gengzhiqiang
     * @Date: 2019/7/4 11:27
     */
    @PostMapping("thirdGroupGetDepts")
    public List<Department>  thirdGroupGetDepts(@RequestBody Long id) {
        return departmentService.thirdGroupGetDepts(id);
    }

    /**
     * 三级单位根据自己的gradationCode获取其集团公司和自身列表和自己的上级
     *
     * @param gradationCode 公司的gradationCode
     * @return 对应公司列表
     * @author gengjiajia
     * @since 2019/07/09 11:15
     */
    @PostMapping("thirdGroupGetDepts2")
    public List<Department> thirdGroupGetDepts2(String gradationCode) {
        return departmentService.thirdGroupGetDepts2(gradationCode);
    }

    /**
     * 功能描述 集团账号获取单位列表（全部单位列表）
     *
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO> 单位集合
     * @author 秦欢
     * @date 2019/7/9 17:10
     */
    @PostMapping("firstGroupGetDepts")
    List<Department>  firstGroupGetDepts(){
        return departmentService.list(new LambdaQueryWrapper<Department>().orderByDesc(Department::getSort));
    }

    /**
     * 功能描述 集团账号获取单位列表（全部单位列表）
     *
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO> 单位集合
     * @author 秦欢
     * @date 2019/7/9 17:10
     */
    @PostMapping("secondGroupGetDepts")
    List<Department>  secondGroupGetDepts(@RequestBody String gradationCode){
        return departmentService.list(new LambdaQueryWrapper<Department>().and(e->e.eq(Department::getLevel, UserAccountLevelEnum.GROUP.getId()).or().likeRight(Department::getGradationCode, gradationCode)).orderByDesc(Department::getSort));
    }

    /**
     * 根据ids 返回 集团单位置顶，其他按单位名排序的，相同的单位按时间倒序 的 集合
     * @param ids
     * @return
     */
    @PostMapping("getNameOrderdDepartmentsByIds")
    public List<Department> getNameOrderdDepartmentsByIds(@RequestBody List<Long> ids) {
        return departmentService.getNameOrderdDepartmentsByIds(ids);
    }

    /**
     * 功能描述 获取所有单位名称
     * @return Map<id,name>
     * @author gengzhiqiang
     * @date 2019/7/10 16:32
     */
    @PostMapping("getAllDeptNames")
    public Map<Long, String> getAllDeptNames() {
        List<Department> list = departmentService.getAllDeptNames();
        return list.stream().collect(Collectors.toMap(Department::getId, Department::getName));
    }

    /**
     * 功能描述 获取所有单位名称
     * @return Map<id,name>
     * @author gengzhiqiang
     * @date 2019/7/10 16:32
     */
    @PostMapping("getAllDeptIds")
    public List<Long> getAllDeptIds() {
        List<Department> list = departmentService.list(new LambdaQueryWrapper<Department>().orderByAsc(Department::getSort));
        departmentService.reOrderDepartment();
        return list.stream().map(Department::getId).collect(Collectors.toList());
    }


    /**
    * 根据主键集合返回单位集合
    *
    * @param ids 主键集合
    * @return 单位集合
    * @author JH
    * @date 2019/7/17 17:06
    */
    @PostMapping("getDepartmentListByIds")
    public List<Department> getDepartmentListByIds(@RequestBody List<Long> ids){
        return (List<Department>) departmentService.listByIds(ids);
    }

}