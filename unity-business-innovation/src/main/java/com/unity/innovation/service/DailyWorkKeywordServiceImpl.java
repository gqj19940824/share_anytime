
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.innovation.dao.DailyWorkKeywordDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.DailyWorkKeyword;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClassName: DailyWorkKeywordService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 11:17:02
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class DailyWorkKeywordServiceImpl extends BaseServiceImpl<DailyWorkKeywordDao, DailyWorkKeyword> {

    /**
     * 功能描述
     *
     * @param id 工作动态主键
     * @param keys 前台传来的 关键字ids
     * @author gengzhiqiang
     * @date 2019/9/17 15:42
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateKeyWord(Long id, List<Long> keys) {
        if (keys == null) {
            keys = Lists.newArrayList();
        }
        //数据库里存的数据
        List<DailyWorkKeyword> dbList = this.list(new LambdaQueryWrapper<DailyWorkKeyword>()
                .eq(DailyWorkKeyword::getIdDailyWorkStatus, id));
        //数据库里没有数据 全部新增
        if (CollectionUtils.isEmpty(dbList)) {
            List<DailyWorkKeyword> list = Lists.newArrayList();
            keys.forEach(i -> {
                DailyWorkKeyword dwk = DailyWorkKeyword.newInstance().build();
                dwk.setIdKeyword(i);
                dwk.setIdDailyWorkStatus(id);
                list.add(dwk);
            });
            if (CollectionUtils.isNotEmpty(list)) {
                this.saveBatch(list);
            }
            return;
        }
        //keys:前台传过来的关键字   dbkeys:库里存的关键字id
        Set<Long> dbkeys = dbList.stream().map(DailyWorkKeyword::getIdKeyword).collect(Collectors.toSet());
        //新增   前台传来的  数据库里没有
        List<Long> add = keys.stream().filter(key -> !dbkeys.contains(key)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(add)) {
            List<DailyWorkKeyword> list = Lists.newArrayList();
            add.forEach(i -> {
                DailyWorkKeyword dwk = DailyWorkKeyword.newInstance().build();
                dwk.setIdDailyWorkStatus(id);
                dwk.setIdKeyword(i);
                list.add(dwk);
            });
            if (CollectionUtils.isNotEmpty(list)) {
                this.saveBatch(list);
            }
        }
        //删除  数据库里有 但是前台没传的
        List<Long> finalKeys = keys;
        List<Long> delete = dbkeys.stream().filter(key -> !finalKeys.contains(key)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(delete)) {
            baseMapper.delete(new LambdaQueryWrapper<DailyWorkKeyword>()
                    .eq(DailyWorkKeyword::getIdDailyWorkStatus, id)
                    .in(DailyWorkKeyword::getIdKeyword, delete));
        }
    }
}
