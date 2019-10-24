
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.InfoDeptSatb;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.enums.IsCommitEnum;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unity.innovation.entity.InfoDeptYzgt;
import com.unity.innovation.dao.InfoDeptYzgtDao;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class InfoDeptYzgtServiceImpl extends BaseServiceImpl<InfoDeptYzgtDao, InfoDeptYzgt> {


    @Resource
    private AttachmentServiceImpl attachmentService;
    @Resource
    private SysCfgServiceImpl cfgService;
    @Resource
    private DicUtils dicUtils;

    /**
    * 新增/修改
    *
    * @param entity 实体
    * @author JH
    * @date 2019/10/15 16:09
    */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateYzgt(InfoDeptYzgt entity) {
        if(entity.getId() == null) {
            //默认为提请发布
            entity.setStatus(YesOrNoEnum.NO.getType());
            String attachmentCode = UUIDUtil.getUUID();
            entity.setAttachmentCode(attachmentCode);
            super.save(entity);
            //保存附件
            attachmentService.updateAttachments(attachmentCode,entity.getAttachmentList());
        }else {
            InfoDeptYzgt old = super.getById(entity.getId());
            String attachmentCode = old.getAttachmentCode();
            super.updateById(entity);
            //保存附件
            attachmentService.updateAttachments(attachmentCode,entity.getAttachmentList());
        }
    }

    /**
    * 详情接口
    *
    * @param id 主键
    * @return com.unity.innovation.entity.InfoDeptYzgt
    * @author JH
    * @date 2019/10/16 9:26
    */
    public InfoDeptYzgt detailById(Long id) {
        InfoDeptYzgt entity = super.getById(id);
        if(entity == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("数据不存在").build();
        }
        //行业类别
        SysCfg industryCategory = cfgService.getById(entity.getIndustryCategory());
        entity.setIndustryCategoryName(industryCategory == null ? "" : industryCategory.getCfgVal());
        //企业规模
        entity.setEnterpriseScaleName(dicUtils.getDicValueByCode(DicConstants.ENTERPRISE_SCALE,entity.getEnterpriseScale().toString()));
        //企业性质
        SysCfg enterpriseNature = cfgService.getById(entity.getEnterpriseNature());
        entity.setEnterpriseNatureName(enterpriseNature == null ? "" : enterpriseNature.getCfgVal());
        return entity;
    }


    /**
    * 批量删除接口
    *
    * @param ids 主键集合
    * @author JH
    * @date 2019/10/16 10:03
    */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(@RequestBody List<Long> ids) {
        List<InfoDeptYzgt> list = super.list(new LambdaQueryWrapper<InfoDeptYzgt>().in(InfoDeptYzgt::getId, ids));
        //状态为已提请发布的数据
        List<InfoDeptYzgt> collect = list.stream().filter(n -> n.getStatus().equals(YesOrNoEnum.YES.getType())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(collect)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("已提请发布的数据无法删除").build();
        }
        //附件集合
        List<String> attachmentCodeList = list.stream().map(InfoDeptYzgt::getAttachmentCode).collect(Collectors.toList());
        //删除主表
        super.removeByIds(ids);
        //删除附件表
        attachmentService.remove(new LambdaUpdateWrapper<Attachment>().in(Attachment::getAttachmentCode,attachmentCodeList));
    }


    /**
    * 将实体列表 转换为List Map
    *
    * @param list 实体列表
    * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
    * @author JH
    * @date 2019/10/16 11:10
    */
    @SuppressWarnings("unchecked")
    public void convert2List(List<InfoDeptYzgt> list){
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        List<SysCfg> sysList = cfgService.list(new LambdaQueryWrapper<SysCfg>().in(SysCfg::getCfgType,  Lists.newArrayList(SysCfgEnum.THREE.getId(), SysCfgEnum.SIX.getId())));
        Map<Long, String> map = sysList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        List<String> attachmentCodeList = list.stream().map(InfoDeptYzgt::getAttachmentCode).collect(Collectors.toList());
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, attachmentCodeList));
        Map<String, List<Attachment>> attatchmentMap = attachmentList.stream().collect(Collectors.groupingBy(Attachment::getAttachmentCode));
        list.forEach(n -> {
            //行业类别
            n.setIndustryCategoryName(map.get(n.getIndustryCategory()));
            //企业规模
            n.setEnterpriseScaleName(dicUtils.getDicValueByCode(DicConstants.ENTERPRISE_SCALE,n.getEnterpriseScale().toString()));
            //企业性质
            n.setEnterpriseNatureName(map.get(n.getEnterpriseNature()));
            //状态名
            n.setStatusName(IsCommitEnum.of(n.getStatus()).getName());
            n.setAttachmentList(attatchmentMap.get(n.getAttachmentCode()));
        });
    }


    public IPage<InfoDeptYzgt> listForYzgt(PageEntity<InfoDeptYzgt> search) {
        LambdaQueryWrapper<InfoDeptYzgt> ew = new LambdaQueryWrapper<>();
        Page<InfoDeptYzgt> pageable = search.getPageable();
        InfoDeptYzgt entity = search.getEntity();
        if (entity != null) {
            //企业名称
            if (StringUtils.isNotBlank(entity.getEnterpriseName())) {
                ew.like(InfoDeptYzgt::getEnterpriseName, entity.getEnterpriseName());
            }
            //企业规模
            if (entity.getEnterpriseScale() != null) {
                ew.eq(InfoDeptYzgt::getEnterpriseScale, entity.getEnterpriseScale());
            }
            //行业类型
            if (entity.getIndustryCategory() != null) {
                ew.eq(InfoDeptYzgt::getIndustryCategory, entity.getIndustryCategory());
            }
            //企业性质
            if (entity.getEnterpriseNature() != null) {
                ew.eq(InfoDeptYzgt::getEnterpriseNature, entity.getEnterpriseNature());
            }
            //状态
            if (entity.getStatus() != null) {
                ew.eq(InfoDeptYzgt::getStatus, entity.getStatus());
            }
            //创建时间
            if (StringUtils.isNotBlank(entity.getCreateTime())) {
                long end = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), false);
                ew.lt(InfoDeptYzgt::getGmtCreate, end);
                long begin = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), true);
                ew.gt(InfoDeptYzgt::getGmtCreate, begin);
            }
            //包内的和未提请的数据
            if (entity.getIdPmInfoDept() != null) {
                List<InfoDeptYzgt> list = list(new LambdaQueryWrapper<InfoDeptYzgt>()
                        .eq(InfoDeptYzgt::getIdPmInfoDept, entity.getIdPmInfoDept()));
                List<Long> ids = list.stream().map(InfoDeptYzgt::getId).collect(Collectors.toList());
                ew.and(w -> w
                        .in(InfoDeptYzgt::getId, ids)
                        .or()
                        .eq(InfoDeptYzgt::getStatus, YesOrNoEnum.NO.getType()));
            } else {
                ew.eq(InfoDeptYzgt::getStatus, YesOrNoEnum.NO.getType());
            }
        }
        //排序规则      未提请发布在前，已提请发布在后；各自按创建时间倒序
        ew.last(" ORDER BY status ASC , gmt_create desc ");
        return page(pageable, ew);
    }
}
