
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.enums.IsCommitEnum;
import com.unity.innovation.enums.SysCfgEnum;
import org.apache.commons.collections4.CollectionUtils;
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
        entity.setIndustryCategoryName(cfgService.getById(entity.getIndustryCategory()).getCfgVal());
        //企业规模
        entity.setEnterpriseScaleName(dicUtils.getDicValueByCode(DicConstants.ENTERPRISE_SCALE,entity.getEnterpriseScale().toString()));
        //企业性质
        entity.setEnterpriseNatureName(cfgService.getById(entity.getEnterpriseNature()).getCfgVal());
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
        List<InfoDeptYzgt> list = super.list(new LambdaQueryWrapper<InfoDeptYzgt>().eq(InfoDeptYzgt::getStatus, YesOrNoEnum.YES.getType()));
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
    public List<Map<String, Object>> convert2List(List<InfoDeptYzgt> list){
        List<SysCfg> sysList = cfgService.list(new LambdaQueryWrapper<SysCfg>().in(SysCfg::getCfgType,  Lists.newArrayList(SysCfgEnum.THREE.getId(), SysCfgEnum.SIX.getId())));
        Map<Long, String> map = sysList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    //行业类别
                    m.put("industryCategoryName",map.get(entity.getIndustryCategory()));
                    //企业规模
                    m.put("enterpriseScaleName",dicUtils.getDicValueByCode(DicConstants.ENTERPRISE_SCALE,entity.getEnterpriseScale().toString()));
                    //企业性质
                    m.put("enterpriseNatureName",map.get(entity.getEnterpriseNature()));
                    //状态名
                    m.put("statusName", IsCommitEnum.of(entity.getStatus()).getName());
                }
                ,InfoDeptYzgt::getId,InfoDeptYzgt::getSort,InfoDeptYzgt::getNotes,InfoDeptYzgt::getEnterpriseName,InfoDeptYzgt::getIndustryCategory,InfoDeptYzgt::getEnterpriseScale,InfoDeptYzgt::getEnterpriseNature,InfoDeptYzgt::getContactPerson,InfoDeptYzgt::getContactWay,InfoDeptYzgt::getEnterpriseIntroduction,InfoDeptYzgt::getAttachmentCode,InfoDeptYzgt::getIdPmInfoDept,InfoDeptYzgt::getStatus
        );
    }
}
