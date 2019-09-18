
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

    private final Integer NAME_MAX_LENGTH = 50;
    private final DepartmentServiceImpl service;

    public DepartmentController(DepartmentServiceImpl service) {
        this.service = service;
    }

    /**
     * 新增或编辑单位信息
     *
     * @param  entity 组织机构实体
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/07/02 10:46
     */
    @PostMapping("/save")
    public Mono<ResponseEntity<SystemResponse<Object>>> save(@RequestBody Department entity) {
        if (StringUtils.isBlank(entity.getName())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未检测到单位名称");
        }
        if (entity.getName().length() > NAME_MAX_LENGTH) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"单位名称字数限制50字");
        }
        if (entity.getId() == null && 0 < service.count(new QueryWrapper<Department>().lambda()
                .eq(Department::getName, entity.getName()))) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS,"单位名称已存在");
        } else if (entity.getId() != null && 0 < service.count(new QueryWrapper<Department>().lambda()
                .ne(Department::getId, entity.getId())
                .eq(Department::getName, entity.getName()))) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS,"单位名称已存在");
        }
        service.saveOrUpdateDepartment(entity);
        return success("操作成功");
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
     * 分页接口
     *
     * @param pageEntity 分页条件
     * @return 分页数据
     * @author JH
     * @date 2019/7/8 19:30
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<Department> pageEntity) {
        PageElementGrid<Map<String, Object>> page = service.listByPage(pageEntity);
        return success(page);
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
    * 获取适用范围下拉框数据
    *
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/9/17 15:55
    */
    @PostMapping("listAllDepartmentList")
    public Mono<ResponseEntity<SystemResponse<Object>>> listAllDepartmentList() {
        List<Department> list = service.list(new LambdaQueryWrapper<Department>(null).eq(Department::getUseStatus,YesOrNoEnum.YES.getType()));
        Department department = new Department();
        department.setName("全部");
        department.setId(0L);
        list.add(department);
        return success(JsonUtil.ObjectToList(list,
                null
                , Department::getId,Department::getName
        ));
    }

    /**
     * 变更状态
     *
     * @param  department 包含状态值
     * @return code -> 0 成功
     * @author gengjiajia
     * @since 2019/09/18 09:40
     */
    @PostMapping("/changeStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeStatus(@RequestBody Department department) {
        if(department == null || department.getId() == null || department.getUseStatus() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到要变更的状态");
        }
        service.changeStatus(department);
        return success();
    }
}

