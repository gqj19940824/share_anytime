
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JKDates;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.dao.IplYzgtMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplYzgtMain;
import com.unity.innovation.enums.SourceEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 亦庄国投业务处理
 * <p>
 * create by zhangxiaogang at 2019/9/27 16:05
 */
@Service
public class IplYzgtMainServiceImpl extends BaseServiceImpl<IplYzgtMainDao, IplYzgtMain> {
    @Resource
    private AttachmentServiceImpl attachmentService;


    /**
     * 功能描述 分页列表查询
     *
     * @param search 查询条件
     * @return 返回数据
     * @author zhangxiaogang
     * @date 2019/9/27 13:43
     */
    public IPage<IplYzgtMain> listByPage(PageEntity<IplYzgtMain> search) {
        return page(search.getPageable(), getSearchCond(search.getEntity()));
    }

    /**
     * 分析查询条件
     *
     * @param entity 查询条件
     * @return 组装的查询条件
     * @author zhangxiaogang
     * @since 2019/10/10 19:56
     */
    public LambdaQueryWrapper<IplYzgtMain> getSearchCond(IplYzgtMain entity) {
        LambdaQueryWrapper<IplYzgtMain> lqw = new LambdaQueryWrapper<>();
        //标题
        if (StringUtils.isNotBlank(entity.getEnterpriseName())) {
            lqw.like(IplYzgtMain::getEnterpriseName, entity.getEnterpriseName());
        }
        //行业类别
        if (entity.getIndustryCategory() != null) {
            lqw.eq(IplYzgtMain::getIndustryCategory, entity.getIndustryCategory());
        }
        //来源
        if (entity.getSource() != null) {
            lqw.eq(IplYzgtMain::getSource, entity.getSource());
        }
        //时间段 todo
        if (StringUtils.isNotBlank(entity.getCreateDate())) {
            String createTime = entity.getCreateDate();
            String[] dateArr = createTime.split("-");
            int maxDay = JKDates.getMaxDay(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]));
            Date startDate = DateUtils.parseDate(createTime.concat("-01 00:00:00"));
            Date endDate = DateUtils.parseDate(createTime.concat("-").concat(String.valueOf(maxDay)).concat(" 23:59:59"));
            lqw.between(IplYzgtMain::getGmtCreate, startDate.getTime(), endDate.getTime());
        }
        //备注
        if (StringUtils.isNotBlank(entity.getNotes())) {
            lqw.like(IplYzgtMain::getNotes, entity.getNotes());
        }
        //排序规则 创建时间倒序
        lqw.last(" ORDER BY gmt_create desc ");
        return lqw;
    }

    /**
     * 保存或更新亦庄国投信息
     *
     * @param entity 亦庄国投信息
     * @author zhangxiaogang
     * @since 2019/10/10 14:42
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateIplYzgtMain(IplYzgtMain entity) {
        if (entity.getId() == null) {
            entity.setAttachmentCode(UUIDUtil.getUUID());
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
            save(entity);
        } else {
            IplYzgtMain iym = getById(entity.getId());
            attachmentService.updateAttachments(iym.getAttachmentCode(), entity.getAttachmentList());
            updateById(entity);
        }
    }

    /**
     * 功能描述 批量删除
     *
     * @param ids ids删除id
     * @author zhangxiaogang
     * @date 2019/9/27 13:44
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeById(List<Long> ids) {
        List<IplYzgtMain> list = list(new LambdaQueryWrapper<IplYzgtMain>().in(IplYzgtMain::getId, ids));
        List<String> codes = list.stream().map(IplYzgtMain::getAttachmentCode).collect(Collectors.toList());
        //附件表
        attachmentService.remove(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codes));
        //主表
        removeByIds(ids);
    }

    /**
     * 功能描述 详情接口
     *
     * @param entity 对象
     * @return IplSuggestion 对象
     * @author zhangxiaogang
     * @date 2019/9/27 16:03
     */
    public IplYzgtMain detailById(IplYzgtMain entity) {
        IplYzgtMain iym = getById(entity.getId());
        if (iym == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //来源名称
        SourceEnum sourceEnum = SourceEnum.of(iym.getSource());
        if (sourceEnum != null) {
            iym.setSourceTitle(sourceEnum.getName());
        }
        return iym;
    }


}
