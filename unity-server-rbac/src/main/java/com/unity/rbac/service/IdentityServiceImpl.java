
package com.unity.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.Rule;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.rbac.dao.IdentityDao;
import com.unity.rbac.entity.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: IdentityService
 * date: 2018-12-12 20:21:06
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
public class IdentityServiceImpl extends BaseServiceImpl<IdentityDao, Identity> implements IService<Identity> {

    private final UserIdentityServiceImpl userIdentityService;
    private final ResourceIdentityServiceImpl resourceIdentityService;
    private final DefaultRoleServiceImpl defaultRoleService;
    private final ResourceServiceImpl resourceService;

    public IdentityServiceImpl(UserIdentityServiceImpl userIdentityService, ResourceIdentityServiceImpl resourceIdentityService, DefaultRoleServiceImpl defaultRoleService, ResourceServiceImpl resourceService) {
        this.userIdentityService = userIdentityService;
        this.resourceIdentityService = resourceIdentityService;
        this.defaultRoleService = defaultRoleService;
        this.resourceService = resourceService;
    }

    /**
     * 新增 or 修改 -->身份
     *
     * @param dto 包含身份信息
     * @author gengjiajia
     * @since 2018/12/12 10:37
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateIdentity(Identity dto) {
        if (dto == null || StringUtils.isEmpty(dto.getName())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到身份名称")
                    .build();
        }
        //Customer customer = LoginContextHolder.getRequestAttributes();
        if (dto.getId() == null) {
            if (0 < super.count(new QueryWrapper<Identity>().lambda()
                    .eq(Identity::getName, dto.getName()))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                        .message("身份名称已存在")
                        .build();
            }
            Identity identity = new Identity();
            //identity.setGmtCreate(XyDates.getTime(new Date()));
            identity.setName(dto.getName());
            //identity.setCreator(customer.getId().toString());
            identity.setIsDeleted(YesOrNoEnum.NO.getType());
            identity.setNotes(dto.getNotes());
            identity.setPlatform(dto.getPlatform());
            super.save(identity);
        } else {
            if (0 < super.count(new QueryWrapper<Identity>().lambda()
                    .ne(Identity::getId, dto.getId())
                    .eq(Identity::getName, dto.getName()))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                        .message("身份名称已存在")
                        .build();
            }
            Identity identity = super.getById(dto.getId());
            if (identity == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                        .message("未获取到可用的身份信息")
                        .build();
            }
            //identity.setEditor(customer.getId().toString());
            //identity.setGmtModified(XyDates.getTime(new Date()));
            identity.setName(dto.getName());
            identity.setNotes(dto.getNotes());
            identity.setPlatform(dto.getPlatform());
            super.updateById(identity);
        }
    }

    /**
     * 查询指定用户已有的身份列表
     *
     * @param search 包含指定用户id
     * @return 身份列表
     * @author gengjiajia
     * @since 2018/12/25 17:18
     */
    public Map userIdentityListByPage(SearchElementGrid search) {
        Rule rule = search.getCond().findRuleOne("userId");
        String userId = rule.getData().toString();
        List<Long> identityIds = userIdentityService.selectIdentityIdsByUserId(Long.parseLong(userId));
        if (CollectionUtils.isEmpty(identityIds)) {
            return Maps.newHashMap();
        }
        QueryWrapper<Identity> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(Identity::getId, identityIds.toArray());
        return JsonUtil.ObjectToMap(super.page(search.getPageable(), wrapper),
                new String[]{"id", "notes", "creator", "editor", "name", "platform", "gmtCreate"},
                (m, u) -> {
                    Identity entity = (Identity) u;
                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
                });
    }

    /**
     * 删除身份（逻辑删除）
     *
     * @param id 指定身份id
     * @author gengjiajia
     * @since 2018/12/12 11:17
     */
    @Transactional(rollbackFor = Exception.class)
    public void delIdentity(Long id) {
        Identity identity = super.getById(id);
        if (identity != null) {
            //判断是否关联用户
            if (0 < defaultRoleService.count(new QueryWrapper<DefaultRole>().lambda()
                    .eq(DefaultRole::getIdRbacIdentity, id))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("当前身份为系统默认身份，不允许删除！")
                        .build();
            }
            if (0 < userIdentityService.count(new QueryWrapper<UserIdentity>().lambda()
                    .eq(UserIdentity::getIdRbacUser, id))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("当前身份下存在于用户的关联，请先解除！")
                        .build();
            }
        }
        //删除与资源的关系
        resourceIdentityService.remove(new QueryWrapper<ResourceIdentity>().lambda()
                .eq(ResourceIdentity::getIdRbacIdentity, id));
        super.removeById(id);
    }

    /**
     * 获取指定身份信息
     *
     * @param id 指定身份id
     * @return 指定身份信息
     * @author gengjiajia
     * @since 2018/12/12 13:56
     */
    public Map<String, Object> getInentity(Long id) {
        QueryWrapper<Identity> wrapper = new QueryWrapper<>();
        wrapper.select("id", "name", "notes");
        wrapper.eq("id", id);
        Map<String, Object> allInfo = Maps.newHashMap();
        Map<String, Object> identityMap = super.getMap(wrapper);
        List<Map<String, Object>> resource = resourceIdentityService.selectResourceByIdentity(id);
        allInfo.put("identity", identityMap);
        allInfo.put("resource", resource);
        return allInfo;
    }


    /**
     * 批量更新用户身份id和所有资源绑定
     *
     * @param id 身份id
     * @author zhangxiaogang
     * @since 2019/7/25 15:45
     */
    public void saveUserIdentityResourceList(Long id) {
        List<Resource> resourceList = resourceService.list();
        LambdaQueryWrapper<ResourceIdentity> queryWrapper = new LambdaQueryWrapper<ResourceIdentity>();
        queryWrapper.eq(ResourceIdentity::getIdRbacIdentity, id);
        resourceIdentityService.remove(queryWrapper);
        List<ResourceIdentity> resourceIdentities = resourceList.stream()
                .map(resource -> {
                    ResourceIdentity resourceIdentity = new ResourceIdentity();
                    resourceIdentity.setIdRbacIdentity(id);
                    resourceIdentity.setIdRbacResource(resource.getId());
                    resourceIdentity.setIsDeleted(YesOrNoEnum.NO.getType());
                    return resourceIdentity;
                }).collect(Collectors.toList());
        resourceIdentityService.insertBatch(resourceIdentities);

    }
}
