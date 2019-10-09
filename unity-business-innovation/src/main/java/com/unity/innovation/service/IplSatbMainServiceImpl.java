
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JKDates;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.dao.IplSatbMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.enums.SysCfgEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: IplSatbMainService
 * date: 2019-10-08 17:03:09
 *
 * @author G
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IplSatbMainServiceImpl extends BaseServiceImpl<IplSatbMainDao, IplSatbMain> {

    @Resource
    AttachmentServiceImpl attachmentService;
    @Resource
    SysCfgServiceImpl sysCfgService;

    /**
     * 获取清单列表
     *
     * @param pageEntity 包含分页及检索条件
     * @return 清单列表
     * @author gengjiajia
     * @since 2019/10/08 17:35
     */
    public PageElementGrid<Map<String, Object>> listByPage(PageEntity<IplSatbMain> pageEntity) {
        LambdaQueryWrapper<IplSatbMain> ew = new LambdaQueryWrapper<>();
        IplSatbMain entity = pageEntity.getEntity();
        if (entity != null) {
            wrapper(entity, ew);
        }
        IPage<IplSatbMain> page = this.page(pageEntity.getPageable(), ew);
        return PageElementGrid.<Map<String, Object>>newInstance()
                .total(page.getTotal())
                .items(convert2List(page.getRecords()))
                .build();
    }

    /**
     * 查询条件转换
     *
     * @param entity 检索条件
     * @param ew     检索条件组装器
     * @author gengjiajia
     * @since 2019/10/08 17:52
     */
    private void wrapper(IplSatbMain entity, LambdaQueryWrapper<IplSatbMain> ew) {
        ew.orderByAsc(IplSatbMain::getSort);
        if (entity.getIndustryCategory() != null) {
            ew.eq(IplSatbMain::getIndustryCategory, entity.getIndustryCategory());
        }
        if (entity.getDemandCategory() != null) {
            ew.eq(IplSatbMain::getDemandCategory, entity.getDemandCategory());
        }
        if (StringUtils.isNotBlank(entity.getEnterpriseName())) {
            ew.like(IplSatbMain::getEnterpriseName, entity.getEnterpriseName());
        }
        if (StringUtils.isNotBlank(entity.getProjectName())) {
            ew.like(IplSatbMain::getProjectName, entity.getProjectName());
        }
        if (StringUtils.isNotEmpty(entity.getCreateDate())) {
            String createTime = entity.getCreateDate();
            String[] dateArr = createTime.split("-");
            int maxDay = JKDates.getMaxDay(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]));
            ew.between(IplSatbMain::getGmtCreate,
                    DateUtils.parseDate(createTime.concat("-01 00:00:00")).getTime(),
                    DateUtils.parseDate(createTime.concat("-").concat(String.valueOf(maxDay)).concat(" 23:59:59")).getTime());
        }
        if (entity.getSource() != null) {
            ew.eq(IplSatbMain::getSource, entity.getSource());
        }
        if (entity.getStatus() != null) {
            ew.eq(IplSatbMain::getStatus, entity.getStatus());
        }
        if (StringUtils.isNotBlank(entity.getNotes())) {
            ew.like(IplSatbMain::getNotes, entity.getNotes());
        }
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return map列表
     * @author gengjiajia
     * @since 2019/10/08 17:58
     */
    private List<Map<String, Object>> convert2List(List<IplSatbMain> list) {
        //查询附件
        List<String> codeList = list.stream().map(IplSatbMain::getAttachmentCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(codeList)) {
            codeList.add("0");
        }
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codeList.toArray()));
        Map<String, List<Attachment>> attachmentMap = attachmentList.stream().collect(Collectors.groupingBy(Attachment::getAttachmentCode));
        //获取行业类别
        Map<Long, String> industryCategoryMap = sysCfgService.getSysCfgMap(SysCfgEnum.ONE.getId());
        //需求类别
        Map<Long, String> demandCategoryMap = sysCfgService.getSysCfgMap(SysCfgEnum.FOUR.getId());
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                    List<Attachment> attachments = attachmentMap.get(entity.getAttachmentCode());
                    m.put("attachmentList", CollectionUtils.isEmpty(attachments) ? Lists.newArrayList() : convertList2MapByAttachment(attachments));
                    m.put("industryCategoryTitle", industryCategoryMap.get(entity.getIndustryCategory()));
                    m.put("demandCategoryTitle", demandCategoryMap.get(entity.getDemandCategory()));
                }
                , IplSatbMain::getId, IplSatbMain::getNotes, IplSatbMain::getIndustryCategory, IplSatbMain::getEnterpriseName, IplSatbMain::getDemandCategory,
                IplSatbMain::getProjectName, IplSatbMain::getProjectAddress, IplSatbMain::getProjectIntroduce, IplSatbMain::getTotalAmount, IplSatbMain::getBank,
                IplSatbMain::getBond, IplSatbMain::getRaise, IplSatbMain::getTechDemondInfo, IplSatbMain::getContactPerson, IplSatbMain::getContactWay,
                IplSatbMain::getSource, IplSatbMain::getStatus
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, IplSatbMain entity) {
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
    }

    /**
     * 将实体列表 转换为Map
     *
     * @param list 实体对象
     * @return Map
     */
    private List<Map<String, Object>> convertList2MapByAttachment(List<Attachment> list) {
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    // adapterField(m, entity);
                }
                , Attachment::getSize, Attachment::getUrl, Attachment::getName
        );
    }

    /**
     * 新增or修改清单
     *
     * @param  entity 清单信息
     * @author gengjiajia
     * @since 2019/10/08 20:46
     */
    public void saveOrUpdateIplSatbMain(IplSatbMain entity) {

    }

    /**
     * 删除清单信息
     *
     * @param  id 清单id
     * @author gengjiajia
     * @since 2019/10/08 20:47
     */
    public void deleteById(Long id) {
        //关联删除附件

    }

    /**
     * 获取清单详情
     *
     * @param  id 清单id
     * @return 清单详情
     * @author gengjiajia
     * @since 2019/10/08 20:48
     */
    public Map<String, Object> detailById(Long id) {

        return null;
    }
}