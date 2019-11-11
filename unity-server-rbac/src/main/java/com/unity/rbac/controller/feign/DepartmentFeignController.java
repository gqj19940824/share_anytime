package com.unity.rbac.controller.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.rbac.entity.Department;
import com.unity.rbac.service.DepartmentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhqgeng
 * @create 2019-09-26 15:58
 */
@Slf4j
@RestController
@RequestMapping("feign/dept")
public class DepartmentFeignController {

    private final DepartmentServiceImpl departmentService;

    public DepartmentFeignController(DepartmentServiceImpl departmentService) {
        this.departmentService = departmentService;
    }


    /**
     * 功能描述 获取单位信息
     * @return 所有的单位信息
     * @author gengzhiqiang
     * @date 2019/9/26 16:03
     */
    @PostMapping("/getAllDepartment")
    public List<Department> getAllDepartment() {
        return departmentService.list(new LambdaQueryWrapper<Department>().eq(Department::getUseStatus, YesOrNoEnum.YES.getType()));
    }

}
