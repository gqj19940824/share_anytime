package com.unity.common.client;

import com.unity.common.client.vo.DepartmentVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * rbac feign 接口调用客户端
 *
 * @author gengjiajia
 * @since 2019/07/12 10:33
 */
@FeignClient(name = "unity-server-rbac", fallback = RbacClient.HystrixClientFallback.class)
public interface RbacClient {

    /**
     * 获取上级公司和子公司的公司列表
     *
     * @param gradationList 级次编码列表
     * @return 指定组织架构信息
     */
    @PostMapping("/feign/department/getParentAndChildrenDeptList")
    List<DepartmentVO> getParentAndChildrenDeptList(@RequestBody List<String> gradationList);


        /**
         * @desc: 获取到本单位下的子单位
         * @param: [code]
         * @return: java.util.List<com.unity.safety.entity.Project>
         * @author: 郭振洋
         * @date: 2019/7/16 19:52
         **/
    @GetMapping("/feign/department/getDepartmentChild/{idDepartment}")
     List<DepartmentVO> getDepartmentChild(@PathVariable("idDepartment") Long idDepartment);

    /**
     * @desc: 查询所有用户
     * @param:
     * @return:
     * @author: 郭振洋
     * @date: 2019/7/18 9:27
    **/
    @PostMapping("/feign/user/listUser")
    List<Map<String,Object>> listUser();

    /**
     * 通过ID获取组织机构信息
     *
     * @param id
     * @return map
     * @author zhaozesheng
     * @since 2019/1/10 14:42
     */
    @GetMapping("/feign/department/getParentDepartment")
    DepartmentVO getParentDepartment(@RequestBody Long id);

    /**
     * 查询属于某几个单位的用户，得到{用户id，单位id}集合
     *
     * @param ids 单位id的集合
     * @return java.util.Map<java.lang.Long ,   java.lang.Long>返回符合条件的用户的{用户id，单位id}集合
     * @author lifeihong
     * @date 2019/7/10 17:27
     */
    @PostMapping("/feign/user/listUserInDepartment")
    Map<Long, Long> listUserInDepartment(@RequestBody List<Long> ids);

    /**
     * 查询某几个单位的用户，包括is_deleted为1的，不进行分组
     *
     * @param ids 单位id的集合
     * @return java.util.Map<java.lang.Long ,   java.lang.String> 返回符合条件的用户集合
     * @author lifeihong
     * @date 2019/7/10 17:11
     */
    @PostMapping("/feign/user/listAllInDepartment")
    Map<Long, String> listAllInDepartment(@RequestBody List<Long> ids);

    /**
     * 根据单位级别获取对应级别的单位的集合
     *
     * @param levels 单位级别的集合
     * @return 符合条件的单位的id和name的集合
     * @author lifeihong
     * @date 2019/7/5 17:16
     */
    @PostMapping("/feign/department/listDepartmentListByLevel")
    List<DepartmentVO> listDepartmentListByLevel(@RequestBody List<Integer> levels);

    /**
     * 功能描述 通知公告 二三级单位列表
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO>
     * @author gengzhiqiang
     * @date 2019/8/21 10:04
     */
    @PostMapping("/feign/department/listDepartmentListForNotice")
    List<DepartmentVO> listDepartmentListForNotice();

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     * @author gengjiajia
     * @since 2019/07/12 13:57
     */
    @GetMapping("/feign/user/getUser/{userId}")
    Map<String,Object> getUserById(@PathVariable("userId") Long userId);

    /**
     * 获取单位id对应的用户信息
     *
     * @param ids 单位id集
     * @return 单位id对应的用户信息
     * @author gengjiajia
     * @since 2019/07/12 13:58
     */
    @PostMapping("/feign/department/listUserInDept")
    Map<Long, Map<Long, String>> listUserIndept(@RequestBody List<Long> ids);

    /**
     * 获取菜单列表拥有的按钮列表
     *
     * @param code 菜单编码
     * @return 按钮列表
     * @author gengjiajia
     * @since 2019/07/12 14:00
     */
    @GetMapping("/feign/resource/getMenuButton/{code}")
    List<Integer> getMenuButton(@PathVariable("code") String code);


    /**
     * 根据id返回单位map key对应department的属性
     *
     * @param id 单位id
     * @return Department map
     * @author JH
     * @date 2019/7/9 16:34
     */
    @GetMapping("/feign/department/getDepartmentById/{id}")
    Map<String, Object> getDepartmentById(@PathVariable("id") Long id);

    /**
     *  根据用户ID集合获取用户信息
     *
     * @param ids 用户id的集合
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 返回符合条件的用户集合
     * @author vv
     * @date 2019/7/10 17:09
     */
    @PostMapping("/feign/user/getUserListByIds")
    List<Map<String, Object>> getUserListByIds(@RequestBody Set<Long> ids);

    /**
     * 根据ids 获取单位的名称
     *
     * @param ids 主鍵集合
     * @return map<id ,   name>
     * @author JH
     * @date 2019/7/9 16:35
     */
    @PostMapping("/feign/department/getDeptNamesByIds")
    Map<Long, String> getDeptNamesByIds(@RequestBody List<Long> ids);



    /**
     * 根据id 获取指定组织架构和其子机构信息
     *
     * @param id 主键
     * @return 指定组织架构和其子机构信息
     * @author JH
     * @date 2019/7/9 16:36
     */
    @PostMapping("/feign/department/postDepartmentAndChildDepartList/{id}")
    List<DepartmentVO> postDepartmentAndChildDepartList(@PathVariable("id") Long id);


    /**
     * 根据主键返回单位名称
     *
     * @param id 主键
     * @return 单位名称
     * @author JH
     * @date 2019/7/9 16:38
     */
    @PostMapping("/feign/department/getDeptName/{id}")
    String getDeptName(@PathVariable("id") Long id);

    /**
     * 根据单位id，返回子单位id及自己单位id的集合
     *
     * @param id 父单位id
     * @return 返回子单位id及自己单位id的集合
     * @author JH
     * @date 2019/7/9 16:41
     */
    @GetMapping("/feign/department/getDepartmentListByParentId/{id}")
    List<Long> getDepartmentListByParentId(@PathVariable("id") Long id);

    /**
     * 功能描述 根据用户级别查询单位集合
     *
     * @param map <level,1>键值对
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO> 单位集合
     * @author gengzhiqiang
     * @date 2019/7/9 17:10
     */
    @PostMapping("/feign/department/findDepartmentListByLevel")
    List<DepartmentVO> findDepartmentListByLevel(@RequestBody Map<String, Integer> map);

    /**
     * 功能描述 三级单位获取公司列表(集团+自身)
     *
     * @param id 公司id
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO> 单位集合
     * @author 秦欢
     * @date 2019/7/9 17:10
     */
    @PostMapping("/feign/department/thirdGroupGetDepts")
    List<DepartmentVO>  thirdGroupGetDepts(@RequestBody Long id);

    /**
     * 功能描述 三级单位获取公司列表(集团+自身)
     *
     * @param gradationCode 公司gradationCode
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO> 单位集合
     * @author 秦欢
     * @date 2019/7/9 17:10
     */
    @PostMapping("/feign/department/thirdGroupGetDepts2")
    List<DepartmentVO>  thirdGroupGetDepts2(@RequestBody String gradationCode);

    /**
     * 功能描述 集团账号获取单位列表（全部单位列表）
     *
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO> 单位集合
     * @author 秦欢
     * @date 2019/7/9 17:10
     */
    @PostMapping("/feign/department/firstGroupGetDepts")
    List<DepartmentVO>  firstGroupGetDepts();

    /**
     * 功能描述 二级单位账号获取单位列表（自身+子公司+集团）
     *
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO> 单位集合
     * @author 秦欢
     * @date 2019/7/9 17:10
     */
    @PostMapping("/feign/department/secondGroupGetDepts")
    List<DepartmentVO>  secondGroupGetDepts(@RequestBody String gradationCode);

    /**
     * 功能描述 根据用户级别查询单位ID集合
     *
     * @param level
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO> 单位集合
     * @author gengzhiqiang
     * @date 2019/7/9 17:10
     */
    @PostMapping("/feign/department/findDepartmentIdsListByLevel")
    List<Long> findDepartmentIdsListByLevel(@RequestParam("level")Integer level);

    /**
     * 功能描述 获取所有单位及其用户的id和id和name
     *
     * @return Map<Long ,   Map   <   Long ,   String>>  <deptId ,<userId,userName>>
     * @author gengzhiqiang
     * @date 2019/7/9 17:12
     */
    @PostMapping("feign/department/listUserInDeptAll")
    Map<Long, Map<Long, String>> listUserInDeptAll();

    /**
     * 功能描述 根据单位名称 模糊查询出单位的id及其用户信息
     *
     * @param map
     * @return Map<Long ,   Map   <   Long ,   String>>  <deptId ,<userId,userName>>
     * @author gengzhiqiang
     * @date 2019/7/9 17:14
     */
    @PostMapping("feign/department/findDepartmentListByName")
    List<Map<String, Object>> findDepartmentListByName(Map<String, String> map);

    /**
     * 返回集团置顶，其余按单位名称排序的集合
     *
     * @param ids 单位id集合
     * @return 集团置顶，其余按单位名称排序的集合
     * @author JH
     * @date 2019/7/9 16:42
     */
    @PostMapping("/feign/department/getNameOrderdDepartmentsByIds")
    List<DepartmentVO> getNameOrderdDepartmentsByIds(@RequestBody List<Long> ids);


    /**
    * 根据主键集合返回单位集合
    *
    * @param ids 主键集合
    * @return 单位集合
    * @author JH
    * @date 2019/7/17 17:07
    */
    @PostMapping("/feign/department/getDepartmentListByIds")
    List<DepartmentVO> getDepartmentListByIds(@RequestBody List<Long> ids);


    /**
     * 功能描述 获取所有单位名称
     *
     * @return Map<id   ,   name>
     * @author gengzhiqiang
     * @date 2019/7/10 16:32
     */
    @PostMapping("/feign/department/getAllDeptNames")
    Map<Long, String> getAllDeptNames();


    /**
     * @desc: 根据角色ID查询该角色下所有的用户ID
     * @param: [roleId]
     * @return: java.util.List<java.lang.Long>
     * @author: vv
     * @date: 2019/7/15 14:59
     **/
    @GetMapping("/feign/role/getUserIdsByRoleId/{roleId}")
    Set<Long> getUserIdsByRoleId(@PathVariable("roleId") Long roleId);

    /**
     * 根据用户id返回项目账户的项目id
     *
     * @param id 主键
     * @return 项目id
     * @author JH
     * @date 2019/7/18 18:24
     */
    @PostMapping("feign/user/getIdProjectById/{id}")
    Long getIdProjectById(@PathVariable("id") Long id);

    /**
     * 根据项目id返回项目账户个数
     *
     * @param projectIds 项目id集合
     * @return 项目账户的个数
     * @author JH
     * @date 2019/7/23 14:57
     */
    @PostMapping("feign/user/getCountByIdInfoProject")
    Integer getCountByIdInfoProject(@RequestBody List<Long> projectIds);

    /**
     * 返回当前登录账号，发送时可选择的账号
     *
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author JH
     * @date 2019/8/8 9:00
     */
    @PostMapping("feign/user/getUserList")
    List<Map<String, Object>> getUserList();


    /**
    * 根据角色、单位集合获取用户id集合
    *
    * @param map 参数map
    * @return java.util.List<java.lang.Long>
    * @author JH
    * @date 2019/8/8 18:11
    */
    @PostMapping("feign/user/getUserIdsByRoleIdAndDepartmentIds")
    Map<Long, List<Long>> getUserIdsByRoleIdAndDepartmentIds(@RequestBody Map<String,Object> map);

    /**
     * 获取某单位某角色的用户
     *
     * @param map 参数map
     * @return java.util.List<java.lang.Long>
     * @author JH
     * @date 2019/8/8 18:11
     */
    @PostMapping("feign/user/getUserIdsByRoleIdAndDepartmentId")
    List<Long> getUserIdsByRoleIdAndDepartmentId(@RequestBody Map<String,Object> map);

    /**
    * 根据单位id获取这个单位下的所有员工
    *
    * @param id 单位id
    * @return java.util.List<java.lang.Long>
    * @author JH
    * @date 2019/8/12 16:07
    */
    @PostMapping("feign/user/getUserIdsByDepartmentId/{id}")
    List<Long> getUserIdsByDepartmentId(@PathVariable("id") Long id);

    /**
     * 功能描述 获取所有单位名称
     *
     * @return List<Long>
     * @author gengzhiqiang
     * @date 2019/8/29 14:14
     */
    @PostMapping("/feign/department/getAllDeptIds")
    List<Long> getAllDeptIds();
    
    @Component
    class HystrixClientFallback implements RbacClient {


        @Override
        public List<DepartmentVO> getParentAndChildrenDeptList(List<String> gradationList) {
            return null;
        }

        @Override
        public List<DepartmentVO> getDepartmentChild(Long idDepartment) {
            return null;
        }

        @Override
        public List<Map<String, Object>> listUser() {
            return null;
        }

        @Override
        public DepartmentVO getParentDepartment(Long id) {
            return null;
        }


        @Override
        public Map<Long, Long> listUserInDepartment(List<Long> ids) {
            return null;
        }

        @Override
        public Map<Long, String> listAllInDepartment(List<Long> ids) {
            return null;
        }

        @Override
        public List<DepartmentVO> listDepartmentListByLevel(List<Integer> levels) {
            return null;
        }

        @Override
        public List<DepartmentVO> listDepartmentListForNotice() {
            return null;
        }

        @Override
        public Map getUserById(Long userId) {
            return null;
        }

        @Override
        public Map<Long, Map<Long, String>> listUserIndept(List<Long> ids) {
            return null;
        }

        @Override
        public List<Integer> getMenuButton(String code) {
            return null;
        }

        @Override
        public Map<String, Object> getDepartmentById(Long id) {
            return null;
        }

        @Override
        public List<Map<String, Object>> getUserListByIds(Set<Long> ids) {
            return null;
        }

        @Override
        public Map<Long, String> getDeptNamesByIds(List<Long> ids) {
            return null;
        }

        @Override
        public List<DepartmentVO> postDepartmentAndChildDepartList(Long id) {
            return null;
        }

        @Override
        public String getDeptName(Long id) {
            return null;
        }

        @Override
        public List<Long> getDepartmentListByParentId(Long id) {
            return null;
        }

        @Override
        public List<DepartmentVO> findDepartmentListByLevel(Map<String, Integer> map) {
            return null;
        }

        @Override
        public List<DepartmentVO> thirdGroupGetDepts(Long id) {
            return null;
        }

        @Override
        public List<DepartmentVO> thirdGroupGetDepts2(String gradationCode) {
            return null;
        }

        @Override
        public List<DepartmentVO> firstGroupGetDepts() {
            return null;
        }

        @Override
        public List<DepartmentVO> secondGroupGetDepts(String gradationCode) {
            return null;
        }

        @Override
        public List<Long> findDepartmentIdsListByLevel(Integer level) {
            return null;
        }

        @Override
        public Map<Long, Map<Long, String>> listUserInDeptAll() {
            return null;
        }

        @Override
        public List<Map<String, Object>> findDepartmentListByName(Map<String, String> map) {
            return null;
        }

        @Override
        public List<DepartmentVO> getNameOrderdDepartmentsByIds(List<Long> ids) {
            return null;
        }

        @Override
        public List<DepartmentVO> getDepartmentListByIds(List<Long> ids) {
            return null;
        }

        @Override
        public Map<Long, String> getAllDeptNames() {
            return null;
        }

        @Override
        public Set<Long> getUserIdsByRoleId(Long roleId) {
            return null;
        }

        @Override
        public Long getIdProjectById(Long id) {
            return null;
        }

        @Override
        public Integer getCountByIdInfoProject(List<Long> projectIds) {
            return null;
        }

        @Override
        public List<Map<String, Object>> getUserList() {
            return null;
        }

        @Override
        public  Map<Long, List<Long>> getUserIdsByRoleIdAndDepartmentIds(Map<String, Object> map) {
            return null;
        }

        @Override
        public List<Long> getUserIdsByRoleIdAndDepartmentId(Map<String, Object> map) {
            return null;
        }

        @Override
        public List<Long> getUserIdsByDepartmentId(Long id) {
            return null;
        }

        @Override
        public List<Long> getAllDeptIds() {
            return null;
        }


    }


}
