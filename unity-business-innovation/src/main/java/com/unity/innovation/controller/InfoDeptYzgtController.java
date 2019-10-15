
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.util.*;
import com.unity.innovation.entity.SysNotice;
import org.apache.commons.lang3.StringUtils;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.constants.ConstString;
import java.util.Map;
import java.util.List;
import com.unity.innovation.service.InfoDeptYzgtServiceImpl;
import com.unity.innovation.entity.InfoDeptYzgt;

import javax.annotation.Resource;


/**
 * 入区企业信息管理-亦庄国投-基础数据表
 * @author zhang
 * 生成时间 2019-10-15 15:33:00
 */
@RestController
@RequestMapping("/infoDeptYZGT")
public class InfoDeptYzgtController extends BaseWebController {

    @Resource
    private InfoDeptYzgtServiceImpl service;

     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<InfoDeptYzgt> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    

     /**
     * 添加或修改
     *
     * @param entity 亦庄国投
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author JH
     * @date 2019/10/15 16:10
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody InfoDeptYzgt entity) {
        validate(entity);
        service.saveOrUpdateYzgt(entity);
        return success(null);
    }

    private void validate(InfoDeptYzgt entity) {
        if (entity == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("缺少参数").build();
        }
        //非空校验
        String result = ValidFieldUtil.checkEmptyStr(entity
                , ValidFieldFactory.emptyReg("行业类别不能为空! ", InfoDeptYzgt::getIndustryCategory)
                , ValidFieldFactory.emptyReg("企业规模不能为空! ", InfoDeptYzgt::getEnterpriseScale)
                , ValidFieldFactory.emptyReg("企业性质不能为空! ", InfoDeptYzgt::getEnterpriseNature));
        if (StringUtils.isNotEmpty(result)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message(result).build();
        }
        ValidFieldUtil.checkReg(entity, ValidFieldFactory.lengthReg(1, 20, "标题不能超过50字符!", SysNotice::getTitle));
    }
    


    /**
     * 查询条件转换
     * @param search 统一查询对象
     * @return
     */
    private LambdaQueryWrapper<InfoDeptYzgt> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<InfoDeptYzgt> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule(InfoDeptYzgt::getGmtCreate).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule(InfoDeptYzgt::getGmtModified).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(InfoDeptYzgt.class);

        }
        else{
            ew = new LambdaQueryWrapper<InfoDeptYzgt>();
        }

        ew.orderBy(true, false,InfoDeptYzgt::getSort);
        
        return ew;
    }
    
    
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<InfoDeptYzgt> list){
       
        return JsonUtil.<InfoDeptYzgt>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,InfoDeptYzgt::getId,InfoDeptYzgt::getSort,InfoDeptYzgt::getNotes,InfoDeptYzgt::getEnterpriseName,InfoDeptYzgt::getIndustryCategory,InfoDeptYzgt::getEnterpriseScale,InfoDeptYzgt::getEnterpriseNature,InfoDeptYzgt::getContactPerson,InfoDeptYzgt::getContactWay,InfoDeptYzgt::getEnterpriseIntroduction,InfoDeptYzgt::getAttachmentCode,InfoDeptYzgt::getIdPmInfoDept,InfoDeptYzgt::getStatus
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(InfoDeptYzgt ent){
        return JsonUtil.<InfoDeptYzgt>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity);
                }
                ,InfoDeptYzgt::getId,InfoDeptYzgt::getIsDeleted,InfoDeptYzgt::getSort,InfoDeptYzgt::getNotes,InfoDeptYzgt::getEnterpriseName,InfoDeptYzgt::getIndustryCategory,InfoDeptYzgt::getEnterpriseScale,InfoDeptYzgt::getEnterpriseNature,InfoDeptYzgt::getContactPerson,InfoDeptYzgt::getContactWay,InfoDeptYzgt::getEnterpriseIntroduction,InfoDeptYzgt::getAttachmentCode,InfoDeptYzgt::getIdPmInfoDept,InfoDeptYzgt::getStatus
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,InfoDeptYzgt entity){
        if(!StringUtils.isEmpty(entity.getCreator())) {
            if(entity.getCreator().indexOf(ConstString.SEPARATOR_POINT)>-1) {
                m.put("creator", entity.getCreator().split(ConstString.SPLIT_POINT)[1]);
            }
            else {
                m.put("creator", entity.getCreator());
            }
        }
        if(!StringUtils.isEmpty(entity.getEditor())) {
            if(entity.getEditor().indexOf(ConstString.SEPARATOR_POINT)>-1) {
                m.put("editor", entity.getEditor().split(ConstString.SPLIT_POINT)[1]);
            }
            else {
                m.put("editor", entity.getEditor());
            }
        }
        
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
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

