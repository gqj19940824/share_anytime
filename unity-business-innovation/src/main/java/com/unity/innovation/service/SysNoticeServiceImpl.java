
package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.constant.RedisConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.Customer;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.SysNoticeDepartment;
import com.unity.innovation.entity.SysNoticeUser;
import com.unity.innovation.enums.IsReadEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.SysNotice;
import com.unity.innovation.dao.SysNoticeDao;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: SysNoticeService
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class SysNoticeServiceImpl extends BaseServiceImpl<SysNoticeDao, SysNotice> {

    @Resource
    private AttachmentServiceImpl attachmentService;
    @Resource
    private RbacClient rbacClient;
    @Resource
    private SysNoticeUserServiceImpl noticeUserService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SysNoticeDepartmentServiceImpl noticeDepartmentService;
    @Resource
    private SysMessageReadLogServiceImpl readLogService;


    /**
     * 修改
     *
     * @param entity 实体
     * @author JH
     * @date 2019/9/23 16:51
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(SysNotice entity) {
        long id = entity.getId();
        SysNotice old = baseMapper.selectById(id);
        //原先未发送、现在已发送
        if (YesOrNoEnum.NO.getType() == old.getIsSend() && YesOrNoEnum.YES.getType() == entity.getIsSend()) {
            entity.setGmtSend(System.currentTimeMillis());
        }
        //修改主表
        baseMapper.updateById(entity);
        String attachmentCode = old.getAttachmentCode();
        //处理附件
        attachmentService.updateAttachments(attachmentCode, entity.getAttachmentList());
        //修改单位关联表
        updateNoticeDepartment(entity);
        if (YesOrNoEnum.YES.getType() == entity.getIsSend()) {
            //保存人员关联表数据
            saveNoticeUser(entity);
        }

    }

    /**
     * 新增
     *
     * @param entity 实体
     * @author JH
     * @date 2019/9/23 16:51
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveNotice(SysNotice entity) {
        String attachmentCode = UUIDUtil.getUUID();
        entity.setAttachmentCode(attachmentCode);
        if (YesOrNoEnum.YES.getType() == entity.getIsSend()) {
            entity.setGmtSend(System.currentTimeMillis());
        }
        //保存主表数据
        super.save(entity);
        //处理附件
        attachmentService.updateAttachments(attachmentCode, entity.getAttachmentList());
        //保存单位关联表
        updateNoticeDepartment(entity);
        if (YesOrNoEnum.YES.getType() == entity.getIsSend()) {
            //保存人员关联表数据
            saveNoticeUser(entity);
        }

    }

    /**
     * 修改关联表数据
     *
     * @param entity 实体
     * @author JH
     * @date 2019/9/23 17:18
     */
    private void saveNoticeUser(SysNotice entity) {

        List<Long> departmentIds = entity.getDepartmentIds();
        //返回单位包含的用户集合
        Map<Long, List<Long>> map = rbacClient.listUserInDepartment(departmentIds);
        List<SysNoticeUser> noticeUserList = Lists.newArrayList();
        departmentIds.forEach(departmentId -> {
            List<Long> userIds = map.get(departmentId);
            if (CollectionUtils.isNotEmpty(userIds)) {
                userIds.forEach(userId -> {
                    SysNoticeUser noticeUser = new SysNoticeUser();
                    noticeUser.setIsRead(YesOrNoEnum.NO.getType());
                    noticeUser.setIdSysNotice(entity.getId());
                    noticeUser.setIdRbacDepartment(departmentId);
                    noticeUser.setIdRbacUser(userId);
                    noticeUser.setIsShow(YesOrNoEnum.YES.getType());
                    noticeUserList.add(noticeUser);
                });
            }
        });
        //保存关联表数据
        noticeUserService.saveBatch(noticeUserList);
        //发送系统消息
        List<Long> userList = noticeUserList.stream().map(SysNoticeUser::getIdRbacUser).collect(Collectors.toList());
        readLogService.updateMessageNumToUserIdList(2,userList,YesOrNoEnum.YES.getType());
    }


    /**
     * 修改单位关联表数据
     *
     * @param entity 实体
     * @author JH
     * @date 2019/9/24 20:20
     */
    private void updateNoticeDepartment(SysNotice entity) {
        Long id = entity.getId();
        if (id != null) {
            noticeDepartmentService.remove(new LambdaUpdateWrapper<SysNoticeDepartment>().eq(SysNoticeDepartment::getIdSysNotice, id));
        }
        List<Long> departmentIds = entity.getDepartmentIds();
        List<SysNoticeDepartment> noticeDepartmentList = Lists.newArrayList();
        departmentIds.forEach(departmentId -> {
            SysNoticeDepartment noticeDepartment = new SysNoticeDepartment();
            noticeDepartment.setIdSysNotice(entity.getId());
            noticeDepartment.setIdRbacDepartment(departmentId);
            noticeDepartmentList.add(noticeDepartment);
        });
        //保存单位关联表数据
        noticeDepartmentService.saveBatch(noticeDepartmentList);
    }

    /**
     * 详情接口
     *
     * @param id 主键
     * @return com.unity.innovation.entity.SysNotice
     * @author JH
     * @date 2019/9/24 14:02
     */
    public SysNotice detailById(Long id) {
        SysNotice entity = super.getById(id);
        String attachmentCode = entity.getAttachmentCode();
        //附件集合
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, attachmentCode));
        //接收单位集合
        List<SysNoticeDepartment> list = noticeDepartmentService.list(new LambdaQueryWrapper<SysNoticeDepartment>().eq(SysNoticeDepartment::getIdSysNotice, id));
        List<Long> departmentIds = list.stream().map(SysNoticeDepartment::getIdRbacDepartment).collect(Collectors.toList());
        List<DepartmentVO> departmentList = InnovationUtil.getDepartmentListByIds(departmentIds);
        entity.setAttachmentList(attachmentList);
        entity.setDepartmentList(departmentList);
        return entity;
    }

    /**
     * 根据主表id获取附件集合
     *
     * @param id 主表id
     * @return java.util.List<com.unity.innovation.entity.Attachment>
     * @author JH
     * @date 2019/9/24 14:19
     */
    public List<Attachment> getAttachmentListById(Long id) {
        SysNotice entity = super.getById(id);
        String attachmentCode = entity.getAttachmentCode();
        //附件集合
        return attachmentService.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, attachmentCode));
    }

    /**
     * 返回浏览情况
     *
     * @param entity 实体
     * @return java.util.List<com.unity.innovation.entity.SysNoticeUser>
     * @author JH
     * @date 2019/9/24 14:56
     */
    public List<SysNoticeUser> getReadInfo(SysNoticeUser entity) {


        Long noticeId = entity.getIdSysNotice();
        SysNotice sysNotice = baseMapper.selectById(noticeId);
        List<SysNoticeUser> res = Lists.newArrayList();
        //未发送
        if (YesOrNoEnum.NO.getType() == sysNotice.getIsSend()) {
            //接收单位集合
            List<SysNoticeDepartment> list = noticeDepartmentService.list(new LambdaQueryWrapper<SysNoticeDepartment>().eq(SysNoticeDepartment::getIdSysNotice, noticeId));
            List<Long> departmentIds = list.stream().map(SysNoticeDepartment::getIdRbacDepartment).collect(Collectors.toList());
            //返回单位包含的用户集合
            Map<Long, List<Long>> map = rbacClient.listUserInDepartment(departmentIds);
            departmentIds.forEach(departmentId -> {
                List<Long> userIds = map.get(departmentId);
                if (CollectionUtils.isNotEmpty(userIds)) {
                    userIds.forEach(userId -> {
                        SysNoticeUser noticeUser = new SysNoticeUser();
                        noticeUser.setIdRbacDepartment(departmentId);
                        noticeUser.setIdRbacUser(userId);
                        res.add(noticeUser);
                    });
                }
            });
        } else {
            Integer isRead = entity.getIsRead();

            //已浏览
            LambdaQueryWrapper<SysNoticeUser> isReadWrapper = new LambdaQueryWrapper<>();
            isReadWrapper.eq(SysNoticeUser::getIdSysNotice, entity.getIdSysNotice());
            isReadWrapper.eq(SysNoticeUser::getIsRead, YesOrNoEnum.YES.getType());
            if (entity.getIdRbacDepartment() != null) {
                isReadWrapper.eq(SysNoticeUser::getIdRbacDepartment, entity.getIdRbacDepartment());
            }
            isReadWrapper.orderByDesc(SysNoticeUser::getGmtRead);
            List<SysNoticeUser> isReadList = noticeUserService.list(isReadWrapper);


            //未浏览
            String s = (String) redisTemplate.opsForValue().get(RedisConstants.DEPARTMENT_ORDER_LIST);
            List<Long> orderByList = JSON.parseArray(s, Long.class);
            //单位排序
            LambdaQueryWrapper<SysNoticeUser> noReadWrapper = new LambdaQueryWrapper<>();
            noReadWrapper.eq(SysNoticeUser::getIsRead, YesOrNoEnum.NO.getType());
            noReadWrapper.eq(SysNoticeUser::getIdSysNotice, entity.getIdSysNotice());
            if (entity.getIdRbacDepartment() != null) {
                noReadWrapper.eq(SysNoticeUser::getIdRbacDepartment, entity.getIdRbacDepartment());
            }
            if (orderByList != null && orderByList.size() > 0) {
                StringBuffer sb = new StringBuffer();
                sb.append("  order by field ( id_rbac_department ");
                orderByList.forEach(o -> sb.append(",").append(o.toString()));
                sb.append(") ");
                noReadWrapper.last(sb.toString());
            }
            List<SysNoticeUser> noReadList = noticeUserService.list(noReadWrapper);


            if (isRead != null) {
                //已浏览
                if (YesOrNoEnum.YES.getType() == isRead) {
                    res.addAll(isReadList);
                    //未浏览
                } else {
                    res.addAll(noReadList);
                }
            } else {
                res.addAll(isReadList);
                res.addAll(noReadList);
            }
        }

        //设置单位名称、用户名称、浏览名称
        res.forEach(sysNoticeUser -> {
            sysNoticeUser.setDepartmentName(InnovationUtil.getDeptNameById(sysNoticeUser.getIdRbacDepartment()));
            sysNoticeUser.setUserName(InnovationUtil.getUserNameById(sysNoticeUser.getIdRbacUser()));
            if (sysNoticeUser.getIsRead() != null) {
                sysNoticeUser.setIsReadName(IsReadEnum.of(sysNoticeUser.getIsRead()).getName());
            }
        });
        return res;

    }

    /**
     * 删除接口
     *
     * @param ids 删除
     * @author JH
     * @date 2019/9/24 15:59
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeByIds(List<Long> ids) {
        //附件码集合
        List<SysNotice> sysNotices = baseMapper.selectList(new LambdaQueryWrapper<SysNotice>().in(SysNotice::getId, ids));
        List<String> codeList = sysNotices.stream().map(SysNotice::getAttachmentCode).collect(Collectors.toList());
        //删除主表
        baseMapper.deleteBatchIds(ids);
        //删除单位关联表
        noticeDepartmentService.remove(new LambdaUpdateWrapper<SysNoticeDepartment>().in(SysNoticeDepartment::getIdSysNotice, ids));
        //删除附件表
        attachmentService.remove(new LambdaUpdateWrapper<Attachment>().in(Attachment::getAttachmentCode, codeList));
        //无需删除用户关联表，因为此时还未发送
    }


    /**
     * 非宣传部查看收到通知公告列表接口
     *
     * @param page   分页参数
     * @param entity 查询条件
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.unity.innovation.entity.SysNotice>
     * @author JH
     * @date 2019/9/25 9:59
     */
    public IPage<SysNotice> listByPageOther(Page<SysNotice> page, SysNotice entity) {
        return baseMapper.listByPageOther(page, entity);
    }


    /**
    * 非宣传部查看详情接口
    *
    * @param id 主表id
    * @return com.unity.innovation.entity.SysNotice
    * @author JH
    * @date 2019/9/25 10:52
    */
    @Transactional(rollbackFor = Exception.class)
    public SysNotice findById(Long id) {
        SysNotice notice = super.getById(id);
        String attachmentCode = notice.getAttachmentCode();
        //附件集合
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, attachmentCode));
        notice.setAttachmentList(attachmentList);
        Customer customer = LoginContextHolder.getRequestAttributes();
        Long userId = customer.getId();
        SysNoticeUser noticeUser = noticeUserService.getOne(new LambdaQueryWrapper<SysNoticeUser>().eq(SysNoticeUser::getIdSysNotice,id).eq(SysNoticeUser::getIdRbacUser,userId));
        //原先未浏览，改为已浏览，同时设置浏览时间,减少相应用户的系统消息
        if(YesOrNoEnum.NO.getType() == noticeUser.getIsRead()) {
            noticeUser.setIsRead(YesOrNoEnum.YES.getType());
            noticeUser.setGmtRead(System.currentTimeMillis());
            noticeUserService.updateById(noticeUser);
            readLogService.updateMessageNumToUserIdList(2,Lists.newArrayList(userId),YesOrNoEnum.NO.getType());
        }
        return notice;
    }

    /**
    * 逻辑删除
    *
    * @param ids 主表id集合
    * @author JH
    * @date 2019/9/25 11:13
    */
    public void deleteByIds(List<Long> ids) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        Long userId = customer.getId();
        List<SysNoticeUser> list = noticeUserService.list(new LambdaQueryWrapper<SysNoticeUser>().in(SysNoticeUser::getIdSysNotice, ids).eq(SysNoticeUser::getIdRbacUser, userId));
        //逻辑删除，将isShow字段改为0
        list.forEach(n -> n.setIsShow(YesOrNoEnum.NO.getType()));
        noticeUserService.updateBatchById(list);
    }
}
