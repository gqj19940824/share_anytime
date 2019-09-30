
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JKDates;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.dao.IplPdMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplPdMain;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: IplPdMainService
 * date: 2019-09-29 15:50:28
 *
 * @author G
 * @since JDK 1.8
 */
@Service
public class IplPdMainServiceImpl extends BaseServiceImpl<IplPdMainDao, IplPdMain> {

    @Resource
    private DicUtils dicUtils;
    @Resource
    private AttachmentServiceImpl attachmentService;

    /**
     * 发布会 列表数据
     *
     * @param pageEntity 分页及参数
     * @return 列表数据
     * @author gengjiajia
     * @since 2019/09/29 16:24
     */
    public PageElementGrid<Map<String, Object>> listByPage(PageEntity<IplPdMain> pageEntity) {
        LambdaQueryWrapper<IplPdMain> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(IplPdMain::getSort);
        IplPdMain entity = pageEntity.getEntity();
        if (entity != null) {
            if (entity.getIndustryCategory() != null) {
                wrapper.eq(IplPdMain::getIndustryCategory, entity.getIndustryCategory());
            }
            if (StringUtils.isNotBlank(entity.getEnterpriseName())) {
                wrapper.like(IplPdMain::getEnterpriseName, entity.getEnterpriseName());
            }
            if (entity.getSource() != null) {
                wrapper.eq(IplPdMain::getSource, entity.getSource());
            }
            if (StringUtils.isNotBlank(entity.getNotes())) {
                wrapper.like(IplPdMain::getNotes, entity.getNotes());
            }
            if (StringUtils.isNotBlank(entity.getCreateDate())) {
                String createTime = entity.getCreateDate();
                String[] dateArr = createTime.split("-");
                int maxDay = JKDates.getMaxDay(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]));
                Date startDate = DateUtils.parseDate(createTime.concat("-01 00:00:00"));
                Date endDate = DateUtils.parseDate(createTime.concat("-").concat(String.valueOf(maxDay)).concat(" 23:59:59"));
                wrapper.between(IplPdMain::getGmtCreate, startDate.getTime(), endDate.getTime());
            }
        }
        IPage<IplPdMain> iPage = this.page(pageEntity.getPageable(), wrapper);
        return PageElementGrid.<Map<String, Object>>newInstance()
                .total(iPage.getTotal())
                .items(convert2List(iPage.getRecords()))
                .build();
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return List Map
     */
    private List<Map<String, Object>> convert2List(List<IplPdMain> list) {
        //批量获取附件
        List<String> codeList = list.stream().map(IplPdMain::getAttachmentCode).collect(Collectors.toList());
        List<Attachment> allAttachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codeList.toArray()));
        Map<String, List<Attachment>> listMap = allAttachmentList.stream().collect(Collectors.groupingBy(Attachment::getAttachmentCode));
        return JsonUtil.<IplPdMain>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                    m.put("attachments",listMap.get(entity.getAttachmentCode()));
                }
                , IplPdMain::getId, IplPdMain::getIndustryCategory, IplPdMain::getEnterpriseName, IplPdMain::getEnterpriseIntroduction,
                IplPdMain::getSpecificCause, IplPdMain::getIdCard, IplPdMain::getContactPerson, IplPdMain::getContactWay,
                IplPdMain::getAttachmentCode, IplPdMain::getSource, IplPdMain::getStatus, IplPdMain::getPost, IplPdMain::getNotes,
                IplPdMain::getSort
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, IplPdMain entity) {
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
        m.put("sourceTitle","");
        dicUtils.getDicByCode("",entity.getIndustryCategory().toString());
    }

    /**
     * 保存或更新发布会
     *
     * @param entity 包含发布会信息
     * @author gengjiajia
     * @since 2019/09/30 10:39
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateIplPdMain(IplPdMain entity) {
        if(entity.getId() == null){
            entity.setStatus(YesOrNoEnum.NO.getType());
            this.save(entity);
        } else {
            this.updateById(entity);
        }
        //附件处理
        String uuid = UUIDUtil.getUUID();
        attachmentService.updateAttachments(uuid,entity.getAttachments());
    }

    /**
     * 删除发布会
     *
     * @param  id 发布会id
     * @author gengjiajia
     * @since 2019/09/30 11:11
     */
    public void deleteById(Long id) {
        IplPdMain pdMain = this.getById(id);
        if(StringUtils.isNotEmpty(pdMain.getAttachmentCode())){
            attachmentService.remove(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode,pdMain.getAttachmentCode()));
        }
        this.removeById(id);
    }

    /**
     * 查询详情
     *
     * @param id 发布会id
     * @return 发布会详情信息
     * @author gengjiajia
     * @since 2019/09/30 11:32
     */
    public Map<String,Object> detailById(Long id){
        IplPdMain pdMain = this.getById(id);
        //获取附件详情
        if(StringUtils.isNotEmpty(pdMain.getAttachmentCode())){
            List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, pdMain.getAttachmentCode()));
            pdMain.setAttachments(attachmentList);
        }
        return convert2Map(pdMain);
    }

    /**
     * 将实体列表 转换为Map
     *
     * @param ent 实体对象
     * @return Map
     */
    private Map<String, Object> convert2Map(IplPdMain ent){
        return JsonUtil.ObjectToMap(ent,
                (m, entity) -> adapterField(m,entity)
                ,IplPdMain::getId,IplPdMain::getIdIplmMainIplMain,IplPdMain::getIndustryCategory,IplPdMain::getEnterpriseName,
                IplPdMain::getEnterpriseIntroduction,IplPdMain::getSpecificCause,IplPdMain::getIdCard,IplPdMain::getContactPerson,
                IplPdMain::getContactWay,IplPdMain::getAttachmentCode,IplPdMain::getSource,IplPdMain::getStatus,IplPdMain::getPost,
                IplPdMain::getNotes,IplPdMain::getAttachments
        );
    }
}
