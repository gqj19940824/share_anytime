
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;

import com.unity.common.ui.SearchCondition;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.constants.ConstString;


import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.unity.innovation.service.InfoDeptSatbServiceImpl;
import com.unity.innovation.entity.InfoDeptSatb;








/**
 * 路演企业信息管理-科技局-基础数据表
 * @author zhang
 * 生成时间 2019-10-15 15:33:00
 */
@Controller
@RequestMapping("/infodeptsatb")
public class InfoDeptSatbController extends BaseWebController {
    @Autowired
    InfoDeptSatbServiceImpl service;
    


    
     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<InfoDeptSatb> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    
         /**
     * 添加或修改
     * @param entity 路演企业信息管理-科技局-基础数据表实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody InfoDeptSatb entity) {
        
        service.saveOrUpdate(entity);
        return success(null);
    }
    
    @RequestMapping({"/export/excel"})
    public void exportExcel(HttpServletResponse res,String cond) {
        String fileName="路演企业信息管理-科技局-基础数据表";
        ExportEntity<InfoDeptSatb> excel =  ExcelEntity.exportEntity(res);

        try {
            SearchElementGrid search = new SearchElementGrid();
            search.setCond(JSON.parseObject(cond, SearchCondition.class));
            LambdaQueryWrapper<InfoDeptSatb> ew = wrapper(search);
            List<InfoDeptSatb> list = service.list(ew);
     
            excel
                .addColumn(InfoDeptSatb::getId,"编号")
                .addColumn(InfoDeptSatb::getNotes,"备注")
                .addColumn(InfoDeptSatb::getEditor,"修改人")
                .addColumn(InfoDeptSatb::getEnterpriseName,"企业名称")
                .addColumn(InfoDeptSatb::getIndustryCategory,"行业类别")
                .addColumn(InfoDeptSatb::getEnterpriseScale,"企业规模")
                .addColumn(InfoDeptSatb::getEnterpriseNature,"企业性质")
                .addColumn(InfoDeptSatb::getContactPerson,"联系人")
                .addColumn(InfoDeptSatb::getContactWay,"联系方式")
                .addColumn(InfoDeptSatb::getEnterpriseIntroduction,"企业简介")
                .addColumn(InfoDeptSatb::getAttachmentCode,"附件码")
                .addColumn(InfoDeptSatb::getIdPmInfoDept,"入区企业信息发布管理id")
                .addColumn(InfoDeptSatb::getInGeneralSituation,"创新成果概况")
                .addColumn(InfoDeptSatb::getInDetail,"创新成功详情")
                .addColumn(InfoDeptSatb::getAchievementLevel,"创新成功水平")
                .addColumn(InfoDeptSatb::getIsPublishFirst,"是否对外发布")
                 .export(fileName,convert2List(list));
        }
        catch (Exception ex){
            excel.exportError(fileName,ex);
        }
    }

    
     /**
     * 获取数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<InfoDeptSatb> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2List(list)).build();
        return success(result);

    }

    /**
     * 查询条件转换
     * @param search 统一查询对象
     * @return
     */
    private LambdaQueryWrapper<InfoDeptSatb> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<InfoDeptSatb> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule(InfoDeptSatb::getGmtCreate).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule(InfoDeptSatb::getGmtModified).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(InfoDeptSatb.class);

        }
        else{
            ew = new LambdaQueryWrapper<InfoDeptSatb>();
        }

        ew.orderBy(true, false,InfoDeptSatb::getSort);
        
        return ew;
    }
    
    
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<InfoDeptSatb> list){
       
        return JsonUtil.<InfoDeptSatb>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,InfoDeptSatb::getId,InfoDeptSatb::getSort,InfoDeptSatb::getNotes,InfoDeptSatb::getEnterpriseName,InfoDeptSatb::getIndustryCategory,InfoDeptSatb::getEnterpriseScale,InfoDeptSatb::getEnterpriseNature,InfoDeptSatb::getContactPerson,InfoDeptSatb::getContactWay,InfoDeptSatb::getEnterpriseIntroduction,InfoDeptSatb::getAttachmentCode,InfoDeptSatb::getIdPmInfoDept,InfoDeptSatb::getInGeneralSituation,InfoDeptSatb::getInDetail,InfoDeptSatb::getAchievementLevel,InfoDeptSatb::getIsPublishFirst
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(InfoDeptSatb ent){
        return JsonUtil.<InfoDeptSatb>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity);
                }
                ,InfoDeptSatb::getId,InfoDeptSatb::getIsDeleted,InfoDeptSatb::getSort,InfoDeptSatb::getNotes,InfoDeptSatb::getEnterpriseName,InfoDeptSatb::getIndustryCategory,InfoDeptSatb::getEnterpriseScale,InfoDeptSatb::getEnterpriseNature,InfoDeptSatb::getContactPerson,InfoDeptSatb::getContactWay,InfoDeptSatb::getEnterpriseIntroduction,InfoDeptSatb::getAttachmentCode,InfoDeptSatb::getIdPmInfoDept,InfoDeptSatb::getInGeneralSituation,InfoDeptSatb::getInDetail,InfoDeptSatb::getAchievementLevel,InfoDeptSatb::getIsPublishFirst
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,InfoDeptSatb entity){
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

