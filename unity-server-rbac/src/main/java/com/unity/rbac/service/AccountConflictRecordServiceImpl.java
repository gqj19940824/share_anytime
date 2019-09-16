
package com.unity.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.vo.UcsUser;
import com.unity.common.constant.SysReminderConstants;
import com.unity.common.enums.SysReminderDataSourceEnum;
import com.unity.common.enums.UserAccountLevelEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.dao.AccountConflictRecordDao;
import com.unity.rbac.entity.AccountConflictRecord;
import com.unity.rbac.entity.Department;
import com.unity.rbac.entity.User;
import com.unity.rbac.entity.UserIdentity;
import com.unity.rbac.enums.AccountDataStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * ClassName: AccountConflictRecordService
 * date: 2019-07-25 18:51:37
 *
 * @author zhang
 * @since JDK 1.8
 */
@Slf4j
@Service
public class AccountConflictRecordServiceImpl extends BaseServiceImpl<AccountConflictRecordDao, AccountConflictRecord> {

    @Resource
    private UserHelpServiceImpl userHelpService;
    @Resource
    private UserIdentityServiceImpl userIdentityService;
    @Resource
    private DepartmentServiceImpl departmentService;

    /**
     * 查询冲突账号列表
     *
     * @param pageEntity 分页查询条件
     * @return 冲突账号列表
     * @author gengjiajia
     * @since 2019/07/26 14:50
     */
    public PageElementGrid<Map<String, Object>> listByPage(PageEntity<AccountConflictRecord> pageEntity) {
        LambdaQueryWrapper<AccountConflictRecord> wrapper = new LambdaQueryWrapper<>();
        AccountConflictRecord record = pageEntity.getEntity();
        if (record != null && StringUtils.isNotBlank(record.getLoginName())) {
            wrapper.like(AccountConflictRecord::getLoginName, record.getLoginName());
        }
        if (record != null && record.getUcsSource() != null) {
            wrapper.eq(AccountConflictRecord::getUcsSource, record.getUcsSource());
        }
        int count = this.count(wrapper);
        if (count > 0) {
            //查询列表
            Page<AccountConflictRecord> page = pageEntity.getPageable();
            page.setCurrent((page.getCurrent() - 1) * page.getSize());
            List<AccountConflictRecord> recordList = baseMapper.findAccountConflictRecordListByPage(pageEntity.getEntity(), page);
            return PageElementGrid.<Map<String, Object>>newInstance()
                    .total((long) count)
                    .items(convert2List(recordList))
                    .build();
        }
        return PageElementGrid.<Map<String, Object>>newInstance()
                .total(0L)
                .items(Lists.newArrayList())
                .build();
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return 列表
     * @author gengjiajia
     * @since 2019/07/26 15:37
     */
    private List<Map<String, Object>> convert2List(List<AccountConflictRecord> list) {
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
                    m.put("userGmtCreate", DateUtils.timeStamp2Date(entity.getUserGmtCreate()));
                    /*m.put("accountLevelTitle", UserAccountLevelEnum.of(entity.getAccountLevel()).getName());
                    m.put("sourceTitle", UcsSourceEnum.of(entity.getSource()).getName());
                    m.put("dataStatus", AccountDataStatusEnum.of(entity.getDataStatus()).getName());
                    m.put("ucsSourceTitle", UcsSourceEnum.of(entity.getUcsSource()).getName());
                    m.put("perfectStatusTitle", UserPerfectStatusEnum.of(entity.getPerfectStatus()).getName());*/
                }
                , AccountConflictRecord::getId, AccountConflictRecord::getLoginName, AccountConflictRecord::getLocalId,
                AccountConflictRecord::getUcsId, AccountConflictRecord::getUcsSource, AccountConflictRecord::getDataStatus,
                AccountConflictRecord::getAccountLevel, AccountConflictRecord::getDepartment, AccountConflictRecord::getName,
                AccountConflictRecord::getPerfectStatus, AccountConflictRecord::getPhone, AccountConflictRecord::getSource,
                AccountConflictRecord::getDepartNameUcsUser
        );
    }


    /**
     * 解决冲突
     *
     * @param dto 冲突参数
     * @author gengjiajia
     * @since 2019/07/26 17:26
     */
    @Transactional(rollbackFor = Exception.class)
    public void conflictResolution(AccountConflictRecord dto) {
        AccountConflictRecord record = this.getById(dto.getId());
        if (record == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("冲突记录不存在")
                    .build();
        }
        //已处理的不再做处理
        if (AccountDataStatusEnum.END.getId().equals(record.getDataStatus())) {
            return;
        }
        User user = userHelpService.getById(record.getLocalId());
        user.setIdUcsUser(record.getUcsId());
        if (dto.getConflictFlag().equals(YesOrNoEnum.YES.getType())) {
            //保留用户中心账号
            user.setSource(record.getUcsSource());
            user.setPwd(record.getUcsPwd());
        }
        userHelpService.updateById(user);
        //保留本地账号，当前记录置为历史记录
        record.setDataStatus(AccountDataStatusEnum.END.getId());
        this.updateById(record);
    }

    /**
     * 用户中心推送信息到本系统
     *
     * @param dto 包含用户信息
     * @author gengjiajia
     * @since 2019/07/25 19:40
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized void pushUcsUserToSecurity(UcsUser dto) {
        //判断本地是否存在该账号
        User user = userHelpService.getOne(new LambdaQueryWrapper<User>().eq(User::getLoginName, dto.getLoginName()));
        if (user == null) {
            //账号未冲突 保存至用户信息表 但信息待完善
            user = new User();
            user.setIdUcsUser(dto.getIdUcsUser());
            user.setPerfectStatus(YesOrNoEnum.NO.getType());
            user.setLoginName(dto.getLoginName());
            user.setPwd(dto.getPwd());
            user.setSource(dto.getSource());
            user.setDepartNameUcsUser(dto.getDepartName());
            user.setNotes(dto.getDepartName());
            user.setName(dto.getName());
            user.setIsAdmin(YesOrNoEnum.NO.getType());
            user.setIsLock(YesOrNoEnum.NO.getType());
            userHelpService.save(user);
            //分配默认身份
            UserIdentity userIdentity = new UserIdentity();
            userIdentity.setIdRbacUser(user.getId());
            userIdentity.setIdRbacIdentity(UserConstants.DEFAULT_IDENTITY_ID);
            userIdentityService.save(userIdentity);
            //账号同步，需加入系统提醒中 由一级管理员进行分配单位
            Department department = departmentService.getOne(new LambdaQueryWrapper<Department>().eq(Department::getLevel, UserAccountLevelEnum.GROUP.getId()));
            String title = SysReminderConstants.ACCOUNT_INITIAL_SYNC_TITLE.replace(SysReminderConstants.ACCOUNT_KEY, user.getLoginName());
            if(StringUtils.isBlank(dto.getName())){
                title = title.replace(SysReminderConstants.NAME_OF_RE,"");
            } else {
                title = title.replace(SysReminderConstants.NAME,dto.getName());
            }
            userHelpService.saveSysReminder(user.getId(), title, SysReminderDataSourceEnum.ACCOUNT_SYNC_DEP.getId(), department.getId());
        } else if (!user.getIdUcsUser().equals(dto.getIdUcsUser())) {
            //账号一致，用户中心id不一致 认为是不同账号 记录冲突
            AccountConflictRecord record = new AccountConflictRecord();
            record.setLocalId(user.getId());
            record.setLoginName(user.getLoginName());
            record.setUcsId(dto.getIdUcsUser());
            record.setDepartNameUcsUser(dto.getDepartName());
            record.setUcsPwd(dto.getPwd());
            record.setUcsSource(dto.getSource());
            record.setDepartNameUcsUser(dto.getDepartName());
            record.setName(dto.getName());
            this.save(record);
            //增加系统提醒
            Department department = departmentService.getOne(new LambdaQueryWrapper<Department>().eq(Department::getLevel, UserAccountLevelEnum.GROUP.getId()));
            String title = SysReminderConstants.ACCOUNT_CONFLICT_TITLE.replace(SysReminderConstants.ACCOUNT_KEY, user.getLoginName());
            if(StringUtils.isNotBlank(dto.getName())){
                title = title.replace(SysReminderConstants.NAME,dto.getName());
            } else {
                title = title.replace(SysReminderConstants.NAME_OF_RE,"");
            }
            userHelpService.saveSysReminder(user.getId(), title, SysReminderDataSourceEnum.ACCOUNT_CONFLICT.getId(), department.getId());
        }
        //账号一致，用户中心id一致 认为是同一账号 不处理
    }
}
