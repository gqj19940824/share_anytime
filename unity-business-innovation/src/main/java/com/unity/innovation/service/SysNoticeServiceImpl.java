
package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.constant.RedisConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.SysNoticeUser;
import com.unity.innovation.enums.IsReadEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.type.YesNoType;
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
        if(YesOrNoEnum.NO.getType() == old.getIsSend() && YesOrNoEnum.YES.getType() == entity.getIsSend()) {
            entity.setGmtSend(System.currentTimeMillis());
        }
        //修改主表
        baseMapper.updateById(entity);
        String attachmentCode = old.getAttachmentCode();
        //处理附件
        attachmentService.updateAttachments(attachmentCode,entity.getAttachmentList());
        //修改关联表
        updateNoticeUser(entity);

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
        if(YesOrNoEnum.YES.getType() == entity.getIsSend()) {
            entity.setGmtSend(System.currentTimeMillis());
        }
        //保存主表数据
        super.save(entity);
        //处理附件
        attachmentService.updateAttachments(attachmentCode,entity.getAttachmentList());
        //保存关联表数据
        updateNoticeUser(entity);
    }

    /**
    * 修改关联表数据
    *
    * @param entity 实体
    * @author JH
    * @date 2019/9/23 17:18
    */
    private void updateNoticeUser(SysNotice entity) {
        Long id = entity.getId();
        if (id!= null) {
            noticeUserService.remove(new LambdaUpdateWrapper<SysNoticeUser>().eq(SysNoticeUser::getIdSysNotice,id));
        }
        List<Long> departmentIds = entity.getDepartmentIds();
        //返回单位包含的用户集合
        Map<Long, List<Long>> map = rbacClient.listUserInDepartment(departmentIds);
        List<SysNoticeUser> noticeUserList = Lists.newArrayList();
        departmentIds.forEach(departmentId ->{
            List<Long> userIds = map.get(departmentId);
            if(CollectionUtils.isNotEmpty(userIds)) {
                userIds.forEach( userId -> {
                    SysNoticeUser noticeUser = new SysNoticeUser();
                    noticeUser.setIsRead(YesOrNoEnum.NO.getType());
                    noticeUser.setIdSysNotice(entity.getId());
                    noticeUser.setIdRbacDepartment(departmentId);
                    noticeUser.setIdRbacUser(userId);
                    noticeUserList.add(noticeUser);
                });
            }
        });
        //保存关联表数据
        noticeUserService.saveBatch(noticeUserList);
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
        List<SysNoticeUser> noticeUserList = noticeUserService.list(new LambdaQueryWrapper<SysNoticeUser>().eq(SysNoticeUser::getIdSysNotice, id));
        //接收单位集合
        List<Long> departmentIds = noticeUserList.stream().map(SysNoticeUser::getIdRbacDepartment).collect(Collectors.toList());
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

        Integer isRead = entity.getIsRead();
        List<SysNoticeUser> res = Lists.newArrayList();

        //已浏览
        LambdaQueryWrapper<SysNoticeUser> isReadWrapper = new LambdaQueryWrapper<>();
        isReadWrapper.eq(SysNoticeUser::getIdSysNotice, entity.getIdSysNotice());
        isReadWrapper.eq(SysNoticeUser::getIsRead, YesOrNoEnum.YES.getType());
        if(entity.getIdRbacDepartment() != null) {
            isReadWrapper.eq(SysNoticeUser::getIdRbacDepartment,entity.getIdRbacDepartment());
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
        if(entity.getIdRbacDepartment() != null) {
            noReadWrapper.eq(SysNoticeUser::getIdRbacDepartment,entity.getIdRbacDepartment());
        }
        if (orderByList != null && orderByList.size() > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("  order by field ( id_rbac_department ");
            orderByList.forEach(o -> sb.append(",").append(o.toString()));
            sb.append(") ");
            noReadWrapper.last(sb.toString());
        }
        List<SysNoticeUser> noReadList = noticeUserService.list(noReadWrapper);


        if(isRead != null) {
            //已浏览
            if(YesOrNoEnum.YES.getType() == isRead) {
                res.addAll(isReadList);
            //未浏览
            } else {
                res.addAll(noReadList);
            }
        } else {
            res.addAll(isReadList);
            res.addAll(noReadList);
        }
        //设置单位名称、用户名称、浏览名称
        res.forEach(sysNoticeUser -> {
            sysNoticeUser.setDepartmentName(InnovationUtil.getDeptNameById(sysNoticeUser.getIdRbacDepartment()));
            sysNoticeUser.setUserName(InnovationUtil.getUserNameById(sysNoticeUser.getIdRbacUser()));
            sysNoticeUser.setIsReadName(IsReadEnum.of(sysNoticeUser.getIsRead()).getName());
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
        //删除关联表
        noticeUserService.remove(new LambdaUpdateWrapper<SysNoticeUser>().in(SysNoticeUser::getIdSysNotice,ids));
        //删除附件表
        attachmentService.remove(new LambdaUpdateWrapper<Attachment>().in(Attachment::getAttachmentCode,codeList));
    }



}
