
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.lang3.StringUtils;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.constants.ConstString;
import java.util.Map;
import java.util.List;
import com.unity.innovation.service.PmInfoDeptServiceImpl;
import com.unity.innovation.entity.PmInfoDept;

import javax.annotation.Resource;


/**
 * 企业信息发布管理
 * @author zhang
 * 生成时间 2019-10-15 15:33:01
 */
@RestController
@RequestMapping("/pmInfoDept")
public class PmInfoDeptController extends BaseWebController {

    @Resource
    PmInfoDeptServiceImpl service;
    


    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<PmInfoDept> pageEntity) {
        Page<PmInfoDept> pageable = pageEntity.getPageable();
        PmInfoDept entity = pageEntity.getEntity();
        LambdaQueryWrapper<PmInfoDept> ew = service.wrapper(entity);

        IPage<PmInfoDept> p = service.page(pageable, ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    

    @PostMapping("/save")
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody PmInfoDept entity) {
        
        service.saveOrUpdate(entity);
        return success(null);
    }
    



    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<PmInfoDept> list){
       
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    if(entity.getIdRbacDepartment().equals(InnovationConstant.DEPARTMENT_YZGT_ID)) {
                        m.put("infoTypeName","入区企业信息");
                    }else {
                        m.put("infoTypeName","路演企业信息");
                    }
                    m.put("departmentName",InnovationUtil.getDeptNameById(entity.getIdRbacDepartment()));

                }
                ,PmInfoDept::getId,PmInfoDept::getSort,PmInfoDept::getNotes,PmInfoDept::getTitle,PmInfoDept::getGmtSubmit,PmInfoDept::getStatus,PmInfoDept::getAttachmentCode,PmInfoDept::getIdRbacDepartment,PmInfoDept::getInfoType
        );
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

}

