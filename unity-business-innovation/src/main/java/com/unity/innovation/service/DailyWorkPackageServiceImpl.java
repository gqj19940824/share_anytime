
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.dao.DailyWorkPackageDao;
import com.unity.innovation.entity.DailyWorkPackage;
import com.unity.innovation.entity.DailyWorkStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * ClassName: DailyWorkPackageService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 11:17:02
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class DailyWorkPackageServiceImpl extends BaseServiceImpl<DailyWorkPackageDao,DailyWorkPackage>{

    @Resource
    private DailyWorkStatusServiceImpl workStatusService;
    @Resource
    private DailyWorkStatusServiceImpl  dailyWorkStatus;
    /**
     * 功能描述
     *
     * @param id 工作动态包主键
     * @param works 前台传来的 ids
     * @author gengzhiqiang
     * @date 2019/9/17 15:42
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkPackage(Long id, List<Long> works) {
        if (works == null) {
            works = Lists.newArrayList();
        }
        //数据库里存的数据
        List<DailyWorkPackage> dbList = this.list(new LambdaQueryWrapper<DailyWorkPackage>()
                .eq(DailyWorkPackage::getIdPackage, id));
        //数据库里没有数据 全部新增
        if (CollectionUtils.isEmpty(dbList)) {
            List<DailyWorkStatus> exist = dailyWorkStatus.list(new LambdaQueryWrapper<DailyWorkStatus>()
                    .in(DailyWorkStatus::getId, works));
            if (works.size() > exist.size()) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("添加数据存在已删除的数据").build();
            }
            List<DailyWorkPackage> list = Lists.newArrayList();
            works.forEach(i -> {
                DailyWorkPackage dwp = DailyWorkPackage.newInstance().build();
                dwp.setIdDailyWorkStatus(i);
                dwp.setIdPackage(id);
                list.add(dwp);
            });
            if (CollectionUtils.isNotEmpty(list)) {
                this.saveBatch(list);
                //新增后在基础数据表里 状态置为 1
                DailyWorkStatus update=DailyWorkStatus.newInstance().build();
                update.setState(YesOrNoEnum.YES.getType());
                workStatusService.update(update, new LambdaQueryWrapper<DailyWorkStatus>().in(DailyWorkStatus::getId,works));
            }
            return;
        }
        //keys:前台传过来的关键字   dbkeys:库里存的关键字id
        Set<Long> dbkeys = dbList.stream().map(DailyWorkPackage::getIdDailyWorkStatus).collect(Collectors.toSet());
        //新增   前台传来的  数据库里没有
        List<Long> add = works.stream().filter(i -> !dbkeys.contains(i)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(add)) {
            List<DailyWorkStatus> existAdd = dailyWorkStatus.list(new LambdaQueryWrapper<DailyWorkStatus>()
                    .in(DailyWorkStatus::getId, add));
            if (add.size() > existAdd.size()) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("添加数据存在已删除的数据").build();
            }
            List<DailyWorkPackage> list = Lists.newArrayList();
            add.forEach(i -> {
                DailyWorkPackage dwp = DailyWorkPackage.newInstance().build();
                dwp.setIdPackage(id);
                dwp.setIdDailyWorkStatus(i);
                list.add(dwp);
            });
            if (CollectionUtils.isNotEmpty(list)) {
                this.saveBatch(list);
                DailyWorkStatus update= new DailyWorkStatus();
                update.setState(YesOrNoEnum.YES.getType());
                workStatusService.update(update, new LambdaQueryWrapper<DailyWorkStatus>().in(DailyWorkStatus::getId,add));
            }
        }
        //删除  数据库里有 但是前台没传的
        List<Long> finalWorks = works;
        List<Long> delete = dbkeys.stream().filter(i -> !finalWorks.contains(i)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(delete)) {
            baseMapper.delete(new LambdaQueryWrapper<DailyWorkPackage>()
                    .eq(DailyWorkPackage::getIdPackage, id)
                    .in(DailyWorkPackage::getIdDailyWorkStatus, delete));
            DailyWorkStatus update=DailyWorkStatus.newInstance().build();
            update.setState(YesOrNoEnum.NO.getType());
            workStatusService.update(update, new LambdaQueryWrapper<DailyWorkStatus>().in(DailyWorkStatus::getId,delete));
        }
    }
}
