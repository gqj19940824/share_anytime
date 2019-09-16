
package com.unity.rbac.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.enums.UserAccountLevelEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.ui.tree.TNode;
import com.unity.common.ui.tree.zTree;
import com.unity.common.ui.tree.zTreeStructure;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.rbac.entity.Department;
import com.unity.rbac.entity.User;
import com.unity.rbac.service.DepartmentServiceImpl;
import com.unity.rbac.service.UserServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import sun.nio.cs.US_ASCII;

import java.nio.charset.StandardCharsets;
import java.text.CollationElementIterator;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 组织机构
 *
 * @author creator
 * 生成时间 2018-12-24 19:43:57
 */
@RestController
@RequestMapping("/department")
public class DepartmentController extends BaseWebController {
    private final
    DepartmentServiceImpl service;

    private final
    UserServiceImpl userService;

    /**
     * 项目地址长度最大值
     **/
    private static final Integer ADDRESS_MAX_LENGTH = 255;
    /**
     * 经营范围长度最大值
     **/
    private static final Integer BUSINESS_SCOPE_MAX_LENGTH = 500;
    /**
     * 从业人员最大值
     **/
    private static final Integer MAX_EMPLOYEE_NUM = 1000000;
    /**
     * 主管部门长度最大值
     **/
    private static final Integer DEPT_MAX_LENGTH = 50;
    /**
     * 企业负责人长度最大值
     **/
    private static final Integer DEPT_DIRECTOR_MAX_LENGTH = 20;
    /**
     * 安全负责人长度最大值
     **/
    private static final Integer SAFE_DIRECTOR_MAX_LENGTH = 20;
    /**
     * 企业负责人联系方式长度最大值
     **/
    private static final Integer DEPT_DIRECTOR_CONTACT_MAX_LENGTH = 50;
    /**
     * 安全负责人联系方式长度最大值
     **/
    private static final Integer SAFE_DIRECTOR_CONTACT_MAX_LENGTH = 50;

    public DepartmentController(DepartmentServiceImpl service, UserServiceImpl userService) {
        this.service = service;
        this.userService = userService;
    }


    /**
     * 获取树数据
     *
     * @param search 统一查询条件
     * @return 树数据
     * @author gengjiajia
     * @since 2019/07/02 10:46
     */
    @PostMapping("/tree")
    public Mono<ResponseEntity<SystemResponse<Object>>> tree(@RequestBody SearchElementGrid search) {
        QueryWrapper<Department> ew = wrapper(search);
        List<Department> list = service.list(ew);
        zTreeStructure structure = zTreeStructure.newInstance()
                .idField("id")
                .textField("name")
                .parentField("idParent")
                .kidField("gradationCode,level")
                .build();
        return success(zTree.getTree(list, structure));
    }

    /**
     * 根据父id获取树数据
     *
     * @return 树数据
     */
    @PostMapping("/moduleTreeById")
    public Mono<ResponseEntity<SystemResponse<Object>>> moduleTreeById() {
        Customer c=LoginContextHolder.getRequestAttributes();
        Department department  = service.getById(c.getIdRbacDepartment());
        List<Department> list = service.list(new QueryWrapper<Department>().lambda()
                .likeRight(Department::getGradationCode, department.getGradationCode()));
        //根节点父id赋空值
        list.forEach(d->{
            if (d != null && d.getId() != null && c.getIdRbacDepartment().equals(d.getId())) {
                d.setIdParent(null);
            }
        });
        zTreeStructure structure = zTreeStructure.newInstance()
                .idField("id")
                .textField("name")
                .parentField("idParent")
                .gradationCodeField("gradationCode")
                .kidField("gradationCode,level")
                .build();
        return success(sortNodes(zTree.getTree(list, structure)));
    }


    /**
     * 预警通知接受单位树
     *
     * @return 树数据
     */
    @PostMapping("/getDepartmentTree")
    public Mono<ResponseEntity<SystemResponse<Object>>> getDepartmentTree() {

        Customer c=LoginContextHolder.getRequestAttributes();
        Department department  = service.getById(c.getIdRbacDepartment());
        List<Department> list = service.list(new QueryWrapper<Department>().lambda()
                .likeRight(Department::getGradationCode, department.getGradationCode()));
        //根节点父id赋空值
        list.forEach(d->{
            if (d != null && d.getId() != null && c.getIdRbacDepartment().equals(d.getId())) {
                d.setIdParent(null);
            }
        });
        zTreeStructure structure = zTreeStructure.newInstance()
                .idField("id")
                .textField("name")
                .parentField("idParent")
                .gradationCodeField("gradationCode")
                .kidField("gradationCode,level")
                .build();
        List<TNode> treeList = zTree.getTree(list, structure);
        TNode root = treeList.get(0);
        List<TNode> nodeList = Lists.newArrayList();
       nodeList.addAll(root.getChildren());
        return success(sortNodes(nodeList));
    }




    /**
    * 返回包含人员的树结构
    *
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/8/14 10:33
    */
    @PostMapping("/departmentTreeWithUser")
    public Mono<ResponseEntity<SystemResponse<Object>>> departmentTreeWithUser() {
        Customer customer = LoginContextHolder.getRequestAttributes();
        int level = customer.getAccountLevel();
        long departmentId = customer.getIdRbacDepartment();
        long userId = customer.getId();
        //集团
        if(level == 1) {
            List<Department> list = service.list(new QueryWrapper<Department>().lambda()
                    .likeRight(Department::getGradationCode, customer.getGradationCodeRbacDepartment()));
            //根节点父id赋空值
            list.forEach(d->{
                if (d != null && d.getId() != null && departmentId == d.getId()) {
                    d.setIdParent(null);
                }
            });
            zTreeStructure structure = zTreeStructure.newInstance()
                    .idField("id")
                    .textField("name")
                    .parentField("idParent")
                    .gradationCodeField("gradationCode")
                    .kidField("gradationCode,level")
                    .build();
            List<TNode> departTree = zTree.getTree(list, structure);
            List<Long> departmentIds = list.stream().map(Department::getId).collect(Collectors.toList());
            //按单位id对user进行分组
            Map<Long, List<User>> collect = userService.list(new LambdaQueryWrapper<User>().in(User::getIdRbacDepartment, departmentIds).ne(User::getId,userId)).stream().collect(Collectors.groupingBy(User::getIdRbacDepartment));
            for(TNode departNode : departTree) {
                //二级子公司
                List<TNode> twoNodeList = departNode.getChildren();
                for (TNode twoNode : twoNodeList) {
                    //三级子公司
                    List<TNode> threeNodeList = twoNode.getChildren();
                    for (TNode threeNode : threeNodeList) {
                        addUserChildren(threeNode,collect);
                    }
                    addUserChildren(twoNode,collect);
                }
                addUserChildren(departNode,collect);

            }
            return success(departTree);
        }else {
            Department department = service.getById(departmentId);
            department.setIdParent(null);
            TNode departNode = new TNode();
            departNode.setId("Department:"+department.getId());
            departNode.setText(department.getName());
            departNode.setIsParent(true);
            List<User> userList = userService.list(new LambdaQueryWrapper<User>().eq(User::getIdRbacDepartment, department.getId()).ne(User::getId,userId));
            if(CollectionUtils.isNotEmpty(userList)) {
                List<TNode> userNodeList = Lists.newArrayList();
                for (User user :userList) {
                    TNode userNode = new TNode();
                    userNode.setId(user.getId().toString());
                    userNode.setText(user.getName());
                    userNode.setIsParent(false);
                    userNodeList.add(userNode);
                }
                departNode.setChildren(userNodeList);
            }
            return success(departNode);
        }

    }


    /**
     * 突发事件管理返回包含人员的树结构
     *
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author JH
     * @date 2019/8/14 10:33
     */
    @PostMapping("/burstTreeWithUser")
    public Mono<ResponseEntity<SystemResponse<Object>>> burstTreeWithUser() {
        Customer customer = LoginContextHolder.getRequestAttributes();
        int level = customer.getAccountLevel();
        long departmentId = customer.getIdRbacDepartment();
        long userId = customer.getId();
        //集团
        if(level == 1) {
            List<Department> list = service.list(new QueryWrapper<Department>().lambda()
                    .likeRight(Department::getGradationCode, customer.getGradationCodeRbacDepartment()));
            //根节点父id赋空值
            list.forEach(d->{
                if (d != null && d.getId() != null && departmentId == d.getId()) {
                    d.setIdParent(null);
                }
            });
            zTreeStructure structure = zTreeStructure.newInstance()
                    .idField("id")
                    .textField("name")
                    .parentField("idParent")
                    .gradationCodeField("gradationCode")
                    .kidField("gradationCode,level")
                    .build();
            List<TNode> departTree = zTree.getTree(list, structure);
            List<Long> departmentIds = list.stream().map(Department::getId).collect(Collectors.toList());
            //按单位id对user进行分组
            Map<Long, List<User>> collect = userService.list(new LambdaQueryWrapper<User>().in(User::getIdRbacDepartment, departmentIds).ne(User::getId,userId)).stream().collect(Collectors.groupingBy(User::getIdRbacDepartment));
            for(TNode departNode : departTree) {
                //二级子公司
                List<TNode> twoNodeList = departNode.getChildren();
                for (TNode twoNode : twoNodeList) {
                    //三级子公司
                    List<TNode> threeNodeList = twoNode.getChildren();
                    for (TNode threeNode : threeNodeList) {
                        addUserChildren(threeNode,collect);
                    }
                    addUserChildren(twoNode,collect);
                }
                addUserChildren(departNode,collect);

            }
            return success(departTree);
        }else {
            Department department = service.getById(departmentId);
            department.setIdParent(null);
            TNode departNode = new TNode();
            departNode.setId("Department:"+department.getId());
            departNode.setText(department.getName());
            departNode.setIsParent(true);
            List<User> userList = userService.list(new LambdaQueryWrapper<User>().eq(User::getIdRbacDepartment, department.getId()).ne(User::getId,userId).ne(User::getAccountLevel,UserAccountLevelEnum.PROJECT.getId()));
            if(CollectionUtils.isNotEmpty(userList)) {
                List<TNode> userNodeList = Lists.newArrayList();
                for (User user :userList) {
                    TNode userNode = new TNode();
                    userNode.setId(user.getId().toString());
                    userNode.setText(user.getName());
                    userNode.setIsParent(false);
                    userNodeList.add(userNode);
                }
                departNode.setChildren(userNodeList);
            }
            return success(departNode);
        }

    }


    /**
     * 应急值守 返回包含人员的树结构
     *
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author JH
     * @date 2019/8/14 10:33
     */
    @PostMapping("/dutyTree")
    public Mono<ResponseEntity<SystemResponse<Object>>> dutyTree() {
        Customer customer = LoginContextHolder.getRequestAttributes();
        int level = customer.getAccountLevel();
        long userId = customer.getId();
        List<Department> list = service.list(new LambdaQueryWrapper<Department>().eq(Department::getLevel, UserAccountLevelEnum.GROUP.getId()));

        //根节点父id赋空值
        list.forEach(d->{
            if (d != null && d.getId() != null &&  UserAccountLevelEnum.GROUP.getId().equals(d.getAccountLevel())) {
                d.setIdParent(null);
            }
        });

        zTreeStructure structure = zTreeStructure.newInstance()
                    .idField("id")
                    .textField("name")
                    .parentField("idParent")
                    .gradationCodeField("gradationCode")
                    .kidField("gradationCode,level")
                    .build();
            List<TNode> departTree = zTree.getTree(list, structure);
        //非集团账号需要添加自己单位
            if(UserAccountLevelEnum.GROUP.getId() != level) {
                Long id = customer.getIdRbacDepartment();
                Department current = service.getById(id);
                list.add(current);
                TNode departNode = new TNode();
                departNode.setId(current.getId().toString());
                departNode.setText(current.getName());
                departNode.setIsParent(true);
                departTree.get(0).setChildren(Lists.newArrayList(departNode));
            }
            List<Long> departmentIds = list.stream().map(Department::getId).collect(Collectors.toList());
            //按单位id对user进行分组
            Map<Long, List<User>> collect = userService.list(new LambdaQueryWrapper<User>().in(User::getIdRbacDepartment, departmentIds).ne(User::getId,userId)).stream().collect(Collectors.groupingBy(User::getIdRbacDepartment));
            for(TNode departNode : departTree) {
                //二级子公司
                List<TNode> twoNodeList = departNode.getChildren();
                for (TNode twoNode : twoNodeList) {
                    addUserChildren(twoNode,collect);
                }
                addUserChildren(departNode,collect);

            }
            return success(departTree);

    }

    /**
    * 给单位节点添加本公司的user节点
    *
    * @param departNode 单位节点
     * @param collect 按单位分组的userList
    * @author JH
    * @date 2019/8/13 11:06
    */
    private void addUserChildren(TNode departNode,Map<Long, List<User>> collect) {
        List<User> users = collect.get(Long.parseLong(departNode.getId()));
        //更改单位节点的id，避免与user节点的id重复
        departNode.setId("Department:"+departNode.getId());
        //可选
        if(CollectionUtils.isNotEmpty(users)) {
            List<TNode> userNodeList = Lists.newArrayList();
            for (User user :users) {
                TNode userNode = new TNode();
                userNode.setId(user.getId().toString());
                userNode.setText(user.getName());
                userNode.setIsParent(false);
                userNodeList.add(userNode);
            }
            if(CollectionUtils.isEmpty(departNode.getChildren())){
                departNode.setChildren(userNodeList);
            }else {
                departNode.getChildren().addAll(userNodeList);
            }
        }
    }

    /**
     * 获取包含用户关系的组织架构tree
     *
     * @return 树数据
     */
    @PostMapping("/treeByUser/{userId}")
    public Mono<ResponseEntity<SystemResponse<Object>>> treeByUser(@PathVariable("userId") String userId) {
        QueryWrapper<Department> ew = new QueryWrapper<>();
        ew.lambda().orderByAsc(Department::getSort);
        List<Department> list = service.list(ew);
        //查询用户与组织架构关系
        User user = userService.getById(userId);
        List<String> checkedList = Lists.newArrayList();
        if (user.getIdRbacDepartment() != null) {
            checkedList.add(user.getIdRbacDepartment().toString());
        }
        zTreeStructure structure = zTreeStructure.newInstance()
                .idField("id")
                .textField("name")
                .parentField("idParent")
                .kidField("gradationCode,level")
                .checkedList(checkedList)
                .build();
        return success(zTree.getTree(list, structure));
    }

    /**
     * 参数转换
     *
     * @param  entity 组织机构实体
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/07/02 10:46
     */
    @PostMapping("/save")
    public Mono<ResponseEntity<SystemResponse<Object>>> save(@RequestBody Department entity) {
        service.saveOrUpdateDepartment(entity);
        return success("操作成功");
    }


    /**
     * 参数转换
     *
     * @param  search 参数接受体
     * @return 参数封装体
     * @author gengjiajia
     * @since 2019/07/02 10:46
     */
    private QueryWrapper<Department> wrapper(SearchElementGrid search) {
        QueryWrapper<Department> ew ;
        if (search != null) {
            if (search.getCond() != null) {
                search.getCond().findRule("gmtModified").forEach(r ->
                        r.setData(DateUtils.parseDate(r.getData()).getTime())
                );
                search.getCond().findRule("gmtCreate").forEach(r ->
                    r.setData(DateUtils.parseDate(r.getData()).getTime())
                );
            }
            ew = search.toEntityWrapper(Department.class);
        } else {
            ew = new QueryWrapper<>();
        }
        ew.lambda().orderByAsc(Department::getSort);
        return ew;
    }

    /**
     * 批量删除
     *
     * @param  id 单位id
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/07/02 10:45
     */
    @PostMapping("/deleteById")
    public Mono<ResponseEntity<SystemResponse<Object>>> deleteById(@RequestBody List<Long> id) {
        if (id == null || id.get(0) == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到id")
                    .build();
        }
        service.delDepartment(id.get(0));
        return success("删除成功");
    }

    /**
     * 中文排序
     *
     * @param nodes 节点List
     * @return 排序后的list
     */
    private List<TNode> sortNodes(List<TNode> nodes){
        nodes.sort((TNode t1,TNode t2) -> {
            Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
            return com.compare(t1.getText(), t2.getText());
        });
        nodes.parallelStream().filter(tn -> CollectionUtils.isNotEmpty(tn.getChildren())).forEach(tn ->
            sortNodes(tn.getChildren())
        );
        return nodes;
    }

    /**
     * 通过账号级别获取单位列表
     *
     * @param department 包含账号级别
     * @return 单位列表
     * @author gengjiajia
     * @since 2019/07/08 17:36
     */
    @PostMapping("/findDepartmentByAccountLevel")
    public Mono<ResponseEntity<SystemResponse<Object>>> findDepartmentByAccountLevel(@RequestBody Department department) {
        if(department == null || department.getAccountLevel() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到账号级别");
        }
        return success(service.findDepartmentByAccountLevel(department.getAccountLevel()));
    }

    /**
     * 编辑方法
     *
     * @param entity 实体
     * @return 統一返回結果
     * @author JH
     * @date 2019/7/8 16:21
     */
    @PostMapping("/edit")
    public Mono<ResponseEntity<SystemResponse<Object>>> edit(@RequestBody Department entity) {
        editValidate(entity);
        service.updateById(entity);
        return success("操作成功");
    }


    /**
     * 分页接口
     *
     * @param pageEntity 分页条件
     * @return 分页数据
     * @author JH
     * @date 2019/7/8 19:30
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<Department> pageEntity) {
        Page<Department> pageable = pageEntity.getPageable();
        Department entity = pageEntity.getEntity();
        IPage<Department> page;
        if(entity != null) {

            //页面指定id
            if( entity.getId() != null) {

                long id = entity.getId();
                //查询条件为id
                page = service.page(pageable, new QueryWrapper<Department>().eq("id", id).orderByDesc("i_sort"));

                //没指定id，默认访问自己及子节点
            }else {
                //获取当前登录用户
                Customer customer = LoginContextHolder.getRequestAttributes();
                //查询条件为id 及其子节点
                page = service.page(pageable,new QueryWrapper<Department>().likeRight("gradation_code",customer.getGradationCodeRbacDepartment()).orderByDesc("i_sort"));
            }
            List<Department> list = page.getRecords();
            PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                    .total(page.getTotal())
                    .items(convert2List(list)).build();
            return success(result);

        }else {
            throw UnityRuntimeException.newInstance()
                    .message("缺少必要参数")
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .build();
        }


    }


    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return 返回转换后的列表
     */
    private List<Map<String, Object>> convert2List(List<Department> list) {

        return JsonUtil.ObjectToList(list,
                this::adapterField
                , Department::getId, Department::getGmtModified, Department::getName,
                Department::getEmployeeNum, Department::getSafeDirector ,Department::getDeptDirector,
                Department::getSafeDirectorContact,Department::getDeptDirectorContact,Department::getDept,
                Department::getAddress,Department::getBusinessScope,Department::getSort,Department::getOperationButton,Department::getLevel
        );
    }


    /**
     * 字段适配
     *
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,Department entity){
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
        Customer customer = LoginContextHolder.getRequestAttributes();
        //当前登录用户单位id
        Long currentDepartmentId = customer.getIdRbacDepartment();
        //这条数据的填报单位
        Long dbId = entity.getId();
        if (currentDepartmentId.equals(dbId)) {
            m.put("operationButton", YesOrNoEnum.YES.getType());
        }else {
            m.put("operationButton", YesOrNoEnum.NO.getType());
        }
    }



    /**
    * 下载
    *
    * @param id 需要下载数据的id
    * @return  数据流
    * @author JH
    * @date 2019/7/9 15:47
    */
    @GetMapping("/downloadDepartment/{id}")
    public  Mono<ResponseEntity<byte[]>> download(@PathVariable Long id) {

        byte[] content;
        HttpHeaders headers = new HttpHeaders();
        try {
            if(id == null) {
                throw UnityRuntimeException.newInstance()
                        .message("缺少必要参数id")
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .build();
            }
            Department department = service.getById(id);

            content = service.getDepartmentTemplate(department);
            //设置下载文件名
            String filename = department.getName()+new SimpleDateFormat("yyyyMMdd").format(new Date());
            //乱码处理
            headers.setContentDispositionFormData("attachment", new String(filename.getBytes(StandardCharsets.UTF_8), "iso8859-1")+".xls");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } catch (Exception e) {
            throw UnityRuntimeException.newInstance()
                    .message(e.getMessage())
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .build();
        }
        return Mono.just(new ResponseEntity<>(content, headers, HttpStatus.CREATED));
    }

    /***
     * 参数校验
     *
     * @param entity 实体
     * @author JH
     * @date 2019/7/8 16:18
     */
    private void editValidate(Department entity) {
        if (entity == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("没有参数").build();
        }
        if (entity.getId() == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("id不能为空").build();
        }

        //获取当前登录用户
        Customer customer = LoginContextHolder.getRequestAttributes();
        //登录用户所属公司ID
        Long departmentId = customer.getIdRbacDepartment();

        if (!departmentId.equals(entity.getId())) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.OPERATION_NO_AUTHORITY)
                    .message("登录账户只能修改自己单位的数据").build();
        }

        if (StringUtils.isEmpty(entity.getAddress())) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("注册地址不能为空").build();
        }

        if (StringUtils.isEmpty(entity.getBusinessScope())) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("企业生产经营范围").build();
        }

        if (entity.getEmployeeNum() == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("从业人数不能为空").build();
        }

        if (StringUtils.isEmpty(entity.getDept())) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("主管部门不能为空").build();
        }
        if (StringUtils.isEmpty(entity.getDeptDirector())) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("企业负责人不能为空").build();
        }
        if (StringUtils.isEmpty(entity.getSafeDirector())) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("安全负责人不能为空").build();
        }

        if (entity.getAddress().length() > ADDRESS_MAX_LENGTH) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("注册地址长度不能超过255").build();
        }

        if (entity.getBusinessScope().length() > BUSINESS_SCOPE_MAX_LENGTH) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("企业生产经营范围长度不能超过500").build();
        }

        if (entity.getEmployeeNum().toString().length() > 10) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("从业人数不能超过10个字符").build();
        }

        if (entity.getDept().length() > DEPT_MAX_LENGTH) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("主管部门长度不能超过50").build();
        }

        if (entity.getDeptDirector().length() > DEPT_DIRECTOR_MAX_LENGTH) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("企业负责人长度不能超过20").build();
        }

        if (entity.getSafeDirector().length() > SAFE_DIRECTOR_MAX_LENGTH) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("安全负责人长度不能超过20").build();
        }


        if (entity.getDeptDirectorContact() != null && entity.getDeptDirectorContact().length() > DEPT_DIRECTOR_CONTACT_MAX_LENGTH) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("企业负责人联系方式长度不能超过50").build();
        }

        if (entity.getSafeDirectorContact() != null && entity.getSafeDirectorContact().length() > SAFE_DIRECTOR_CONTACT_MAX_LENGTH) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("安全负责人联系方式长度不能超过50").build();
        }


    }

    /**
     * 账号管理列表页面获取单位下拉列表
     *
     * @return 单位下拉列表
     * @author gengjiajia
     * @since 2019/07/16 11:24
     */
    @PostMapping("/getDepartmentListToUserList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getDepartmentListToUserList() {
        Customer customer = LoginContextHolder.getRequestAttributes();
        //数据权限
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Department::getSort);
        if(customer.getIsSuperAdmin().equals(YesOrNoEnum.NO.getType())){
            wrapper.likeRight(Department::getGradationCode,customer.getGradationCodeRbacDepartment());
        }
        List<Map<String, Object>> maps = JsonUtil.ObjectToList(service.list(wrapper),
                null
                , Department::getId, Department::getName
        );
        return success(maps);
    }



    /**
     * 上移/下移
     *
     * @param department 需要移动的实体
     * @author JH
     * @date 2019/7/24 11:35
     */
    @PostMapping("/changeOrder")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@RequestBody Department department) {
        service.changeOrder(department);
        return success();
    }

    /**
     * 组织架构列表
     *
     * @return 组织架构列表
     * @author zhangxiaogang
     * @since 2019/3/21 14:20
     */
    @PostMapping("getAllDepartmentList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getAllDepartmentList() {
        return success(service.list(new LambdaQueryWrapper<Department>(null)));
    }

}

