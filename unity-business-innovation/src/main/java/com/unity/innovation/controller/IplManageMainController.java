package com.unity.innovation.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constants.ConstString;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchCondition;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.service.IplManageMainServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 创新发布清单-发布管理主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:37
 */
@Controller
@RequestMapping("/iplmanagemain")
public class IplManageMainController extends BaseWebController {
    @Autowired
    IplManageMainServiceImpl service;

     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<IplManageMain> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    
         /**
     * 添加或修改
     * @param entity 创新发布清单-发布管理主表实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody IplManageMain entity) {
        
        service.saveOrUpdate(entity);
        return success(null);
    }
    
    @RequestMapping({"/export/excel"})
    public void exportExcel(HttpServletResponse res, String cond) {
        String fileName="创新发布清单-发布管理主表";
        ExportEntity<IplManageMain> excel =  ExcelEntity.exportEntity(res);

        try {
            SearchElementGrid search = new SearchElementGrid();
            search.setCond(JSON.parseObject(cond, SearchCondition.class));
            LambdaQueryWrapper<IplManageMain> ew = wrapper(search);
            List<IplManageMain> list = service.list(ew);
     
            excel
                .addColumn(IplManageMain::getId,"编号")
                .addColumn(IplManageMain::getNotes,"备注")
                .addColumn(IplManageMain::getEditor,"修改人")
                .addColumn(IplManageMain::getTitle,"标题")
                .addColumn(IplManageMain::getStatus,"状态")
                .addColumn(IplManageMain::getAttachmentCode,"附件")
                .addColumn(IplManageMain::getIdRbacDepartmentDuty,"单位id")
                .addColumn(IplManageMain::getPublishResult,"发布结果")
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
    
        LambdaQueryWrapper<IplManageMain> ew = wrapper(search);

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
    private LambdaQueryWrapper<IplManageMain> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<IplManageMain> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule(IplManageMain::getGmtCreate).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule(IplManageMain::getGmtModified).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(IplManageMain.class);

        }
        else{
            ew = new LambdaQueryWrapper<IplManageMain>();
        }

        ew.orderBy(true, false,IplManageMain::getSort);
        
        return ew;
    }
    
    
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IplManageMain> list){
       
        return JsonUtil.<IplManageMain>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,IplManageMain::getId,IplManageMain::getSort,IplManageMain::getNotes,IplManageMain::getTitle,IplManageMain::getStatus,IplManageMain::getAttachmentCode,IplManageMain::getIdRbacDepartmentDuty,IplManageMain::getPublishResult
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IplManageMain ent){
        return JsonUtil.<IplManageMain>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity);
                }
                , IplManageMain::getId,IplManageMain::getIsDeleted,IplManageMain::getSort,IplManageMain::getNotes,IplManageMain::getTitle,IplManageMain::getStatus,IplManageMain::getAttachmentCode,IplManageMain::getIdRbacDepartmentDuty,IplManageMain::getPublishResult
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,IplManageMain entity){
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

