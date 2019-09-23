
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.client.RbacClient;
import com.unity.common.client.SystemClient;
import com.unity.common.exception.UnityRuntimeException;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.ui.tree.zTree;
import com.unity.common.ui.tree.zTreeStructure;
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

import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import com.unity.common.enums.FlagEnum;
import javax.servlet.http.HttpServletResponse;

import com.unity.innovation.service.IplDarbMainServiceImpl;
import com.unity.innovation.entity.IplDarbMain;
import com.unity.innovation.enums.*;







/**
 * darb->Development and Reform Bureau\r\n\r\n
 * @author zhang
 * 生成时间 2019-09-21 15:45:35
 */
@Controller
@RequestMapping("/ipldarbmain")
public class IplDarbMainController extends BaseWebController {
    @Autowired
    IplDarbMainServiceImpl service;
    
    @Autowired
    RbacClient rbacClient;

    @Autowired
    SystemClient systemClient;
    
     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<IplDarbMain> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    
         /**
     * 添加或修改
     * @param entity darb->Development and Reform Bureau\r\n\r\n实体
     * @return
     */
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody IplDarbMain entity) {
        Integer source = entity.getSource();
        service.saveOrUpdate(entity);
        return success(null);
    }
    
    @RequestMapping({"/export/excel"})
    public void exportExcel(HttpServletResponse res,String cond) {
        String fileName="darb->Development and Reform Bureau\r\n\r\n";
        ExportEntity<IplDarbMain> excel =  ExcelEntity.exportEntity(res);

        try {
            SearchElementGrid search = new SearchElementGrid();
            search.setCond(JSON.parseObject(cond, SearchCondition.class));
            LambdaQueryWrapper<IplDarbMain> ew = wrapper(search);
            List<IplDarbMain> list = service.list(ew);
     
            excel
                .addColumn(IplDarbMain::getId,"编号")
                .addColumn(IplDarbMain::getIdIplmMainIplMain,"编号_创新发布清单-创新发展清单管理表一对多创新发展清单表")
                .addColumn(IplDarbMain::getNotes,"备注")
                .addColumn(IplDarbMain::getEditor,"修改人")
                .addColumn(IplDarbMain::getIndustryCategory,"行业类别")
                .addColumn(IplDarbMain::getEnterpriseName,"企业名称")
                .addColumn(IplDarbMain::getDemandItem,"需求名目")
                .addColumn(IplDarbMain::getDemandCategory,"需求类别")
                .addColumn(IplDarbMain::getProjectName,"项目/产品/服务名称")
                .addColumn(IplDarbMain::getContent,"内容及规模")
                .addColumn(IplDarbMain::getTotalInvestment,"总投资")
                .addColumn(IplDarbMain::getProjectProgress,"项目形象进度")
                .addColumn(IplDarbMain::getTotalAmount,"需求总额")
                .addColumn(IplDarbMain::getBank,"银行")
                .addColumn(IplDarbMain::getBond,"债券")
                .addColumn(IplDarbMain::getIncreaseTrustType,"增信方式")
                .addColumn(IplDarbMain::getWhetherIntroduceSocialCapital,"是否引入社会资本")
                .addColumn(IplDarbMain::getConstructionCategory,"建设类别")
                .addColumn(IplDarbMain::getConstructionStage,"建设阶段")
                .addColumn(IplDarbMain::getConstructionModel,"建设模式")
                .addColumn(IplDarbMain::getContactPerson,"联系人")
                .addColumn(IplDarbMain::getContactWay,"联系方式")
                .addColumn(IplDarbMain::getAttachmentCode,"附件")
                .addColumn(IplDarbMain::getSource,"来源")
                .addColumn(IplDarbMain::getStatus,"状态")
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
    
        LambdaQueryWrapper<IplDarbMain> ew = wrapper(search);

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
    private LambdaQueryWrapper<IplDarbMain> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<IplDarbMain> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule(IplDarbMain::getGmtCreate).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule(IplDarbMain::getGmtModified).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(IplDarbMain.class);

        }
        else{
            ew = new LambdaQueryWrapper<IplDarbMain>();
        }

        ew.orderBy(true, false,IplDarbMain::getSort);
        
        return ew;
    }
    
    
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IplDarbMain> list){
       
        return JsonUtil.<IplDarbMain>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,IplDarbMain::getId,IplDarbMain::getIdIplmMainIplMain,IplDarbMain::getSort,IplDarbMain::getNotes,IplDarbMain::getIndustryCategory,IplDarbMain::getEnterpriseName,IplDarbMain::getDemandItem,IplDarbMain::getDemandCategory,IplDarbMain::getProjectName,IplDarbMain::getContent,IplDarbMain::getTotalInvestment,IplDarbMain::getProjectProgress,IplDarbMain::getTotalAmount,IplDarbMain::getBank,IplDarbMain::getBond,IplDarbMain::getIncreaseTrustType,IplDarbMain::getWhetherIntroduceSocialCapital,IplDarbMain::getConstructionCategory,IplDarbMain::getConstructionStage,IplDarbMain::getConstructionModel,IplDarbMain::getContactPerson,IplDarbMain::getContactWay,IplDarbMain::getAttachmentCode,IplDarbMain::getSource,IplDarbMain::getStatus
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IplDarbMain ent){
        return JsonUtil.<IplDarbMain>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity);
                }
                ,IplDarbMain::getId,IplDarbMain::getIdIplmMainIplMain,IplDarbMain::getIsDeleted,IplDarbMain::getSort,IplDarbMain::getNotes,IplDarbMain::getIndustryCategory,IplDarbMain::getEnterpriseName,IplDarbMain::getDemandItem,IplDarbMain::getDemandCategory,IplDarbMain::getProjectName,IplDarbMain::getContent,IplDarbMain::getTotalInvestment,IplDarbMain::getProjectProgress,IplDarbMain::getTotalAmount,IplDarbMain::getBank,IplDarbMain::getBond,IplDarbMain::getIncreaseTrustType,IplDarbMain::getWhetherIntroduceSocialCapital,IplDarbMain::getConstructionCategory,IplDarbMain::getConstructionStage,IplDarbMain::getConstructionModel,IplDarbMain::getContactPerson,IplDarbMain::getContactWay,IplDarbMain::getAttachmentCode,IplDarbMain::getSource,IplDarbMain::getStatus
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,IplDarbMain entity){
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


    /**
     * 更改排序
     * @param id
     * @param up 1 下降 0 上升
     * @return
     */
    @PostMapping("/changeOrder/{id}/{up}")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@PathVariable Integer id,@PathVariable Integer up){
        IplDarbMain entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<IplDarbMain> wrapper = new LambdaQueryWrapper();

        String msg ="";
        if(up==1) {
            wrapper.lt(IplDarbMain::getSort, sort);
            msg ="已经是最后一条数据";
            wrapper.orderByDesc(IplDarbMain::getSort);
        }
        else {
            wrapper.gt(IplDarbMain::getSort, sort);
            msg ="已经是第一条数据";
            wrapper.orderByAsc(IplDarbMain::getSort);
        }


        IplDarbMain entity1 = service.getOne(wrapper);
        if(entity1==null) throw new UnityRuntimeException(msg);

        entity.setSort(entity1.getSort());

        IplDarbMain entityA = new IplDarbMain();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        IplDarbMain entityB = new IplDarbMain();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }
}

