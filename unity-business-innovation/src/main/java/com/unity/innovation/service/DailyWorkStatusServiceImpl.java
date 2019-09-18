
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.dao.DailyWorkStatusDao;
import com.unity.innovation.entity.DailyWorkKeyword;
import com.unity.innovation.entity.DailyWorkStatus;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: DailyWorkStatusService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 11:17:01
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DailyWorkStatusServiceImpl extends BaseServiceImpl<DailyWorkStatusDao, DailyWorkStatus> {

    @Resource
    private DailyWorkKeywordServiceImpl keywordService;

    /**
     * 功能描述 分页列表查询
     *
     * @param search 查询条件
     * @return 返回数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:43
     */
    public IPage<DailyWorkStatus> listByPage(PageEntity<DailyWorkStatus> search) {
        LambdaQueryWrapper<DailyWorkStatus> lqw = new LambdaQueryWrapper<>();
        //标题
        if (StringUtils.isNotBlank(search.getEntity().getTitle())) {
            lqw.like(DailyWorkStatus::getTitle, search.getEntity().getTitle());
        }
        //主题
        if (StringUtils.isNotBlank(search.getEntity().getTheme())) {
            lqw.like(DailyWorkStatus::getTheme, search.getEntity().getTheme());
        }
        //工作类别
        if (search.getEntity().getType() != null) {
            lqw.eq(DailyWorkStatus::getType, search.getEntity().getType());
        }
        //关键字
        if (search.getEntity().getKeyWord() != null) {
            //关键词中间表查询工作动态的数据
            List<DailyWorkKeyword> keyList = keywordService.list(new LambdaQueryWrapper<DailyWorkKeyword>()
                    .eq(DailyWorkKeyword::getIdKeyword, search.getEntity().getKeyWord()));
            List<Long> ids = keyList.stream().map(DailyWorkKeyword::getIdDailyWorkStatus).collect(Collectors.toList());
            lqw.in(DailyWorkStatus::getId, ids);
        }
        //状态
        if (search.getEntity().getState() != null) {
            lqw.eq(DailyWorkStatus::getState, search.getEntity().getState());
        }
        //创建时间
        if (StringUtils.isNotBlank(search.getEntity().getCreateTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), false);
            //gt 大于 lt 小于
            lqw.gt(DailyWorkStatus::getGmtCreate, begin);
            lqw.lt(DailyWorkStatus::getGmtCreate, end);
        }
        //创建时间
        if (StringUtils.isNotBlank(search.getEntity().getSubmitTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getSubmitTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getSubmitTime(), false);
            lqw.gt(DailyWorkStatus::getGmtSubmit, begin);
            lqw.lt(DailyWorkStatus::getGmtSubmit, end);
        }
        IPage<DailyWorkStatus> list = page(search.getPageable(), lqw);
        List<Long> ids = list.getRecords().stream().map(DailyWorkStatus::getId).collect(Collectors.toList());
        //工作类别
        Map<Long, String> typeList = getTypeList();

        //关键字
        List<DailyWorkKeyword> keys = keywordService.list(new LambdaQueryWrapper<DailyWorkKeyword>()
                .eq(DailyWorkKeyword::getIdDailyWorkStatus, ids));
        Map<Long, List<DailyWorkKeyword>> keyStr = keys.stream().collect(Collectors.groupingBy(DailyWorkKeyword::getIdDailyWorkStatus));

        return list;
    }

    Map<Long, String> getTypeList() {
        Map<Long, String> m = new HashMap<>();
        m.put(1L, "类型1");
        m.put(2L, "类型2");
        m.put(3L, "类型3");
        return m;
    }
}
