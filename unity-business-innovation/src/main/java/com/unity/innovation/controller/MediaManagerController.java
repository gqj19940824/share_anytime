
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Dic;
import com.unity.common.ui.PageEntity;
import com.unity.common.ui.tree.TNode;
import com.unity.common.util.ValidFieldUtil;
import com.unity.common.utils.DicUtils;
import com.unity.common.constant.ParamConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.util.JsonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import com.unity.innovation.service.MediaManagerServiceImpl;
import com.unity.innovation.entity.MediaManager;


/**
 * 媒体管理表
 *
 * @author zhang
 * 生成时间 2019-10-28 13:41:56
 */
@RestController
@RequestMapping("/mediaManager")
public class MediaManagerController extends BaseWebController {

    @Resource
    MediaManagerServiceImpl service;

    @Resource
    private DicUtils dicUtils;


    /**
     * 列表查询
     *
     * @param pageEntity 统一查询条件
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity < com.unity.common.pojos.SystemResponse < java.lang.Object>>>
     * @author JH
     * @date 2019/10/28 14:17
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<MediaManager> pageEntity) {

        MediaManager entity = pageEntity.getEntity();
        Page<MediaManager> pageable = pageEntity.getPageable();
        LambdaQueryWrapper<MediaManager> ew = wrapper(entity);
        IPage<MediaManager> p = service.page(pageable, ew);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }


    /**
     * 添加或修改
     *
     * @param entity 实体
     * @return 成功 or 失败 成功：code -> 0
     * @author JH
     * @date 2019/10/28 14:24
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody MediaManager entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, MediaManager::getMediaType, MediaManager::getMediaName);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }

        if (entity.getMediaName().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "媒体名称限制20字");
        }
        if (entity.getContactPerson().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系人限制20字");
        }
        if (entity.getContactWay().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系方式限制20字");
        }
        List<MediaManager> list;
        //新增
        if (entity.getId() == null) {
            list = service.list(new LambdaUpdateWrapper<MediaManager>().eq(MediaManager::getMediaName, entity.getMediaName()));

        } else {
            list = service.list(new LambdaUpdateWrapper<MediaManager>().eq(MediaManager::getMediaName, entity.getMediaName()).ne(MediaManager::getId, entity.getId()));
        }
        if (CollectionUtils.isNotEmpty(list)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("名称已存在!").build();
        }
        service.saveOrUpdate(entity);
        return success(null);
    }


    /**
     * 修改禁用/启用状态
     *
     * @param entity 实体
     * @return 成功 or 失败
     * @author JH
     * @date 2019/9/17 16:01
     */
    @PostMapping("/changeStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeStatus(@RequestBody MediaManager entity) {
        if (entity.getId() == null || entity.getStatus() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "缺少必要参数");
        }
        service.updateById(entity);
        return success(InnovationConstant.SUCCESS);
    }

    /**
     * 查询条件转换
     *
     * @param entity 实体
     * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.MediaManager>
     * @author JH
     * @date 2019/10/28 14:17
     */
    private LambdaQueryWrapper<MediaManager> wrapper(MediaManager entity) {
        LambdaQueryWrapper<MediaManager> ew = new LambdaQueryWrapper<>();
        if (entity != null) {
            if (entity.getMediaType() != null) {
                ew.eq(MediaManager::getMediaType, entity.getMediaType());
            }
            if (entity.getStatus() != null) {
                ew.eq(MediaManager::getStatus, entity.getStatus());
            }
            if (StringUtils.isNotBlank(entity.getMediaName())) {
                ew.like(MediaManager::getMediaName, entity.getMediaName());
            }
        }
        ew.orderByDesc(MediaManager::getStatus);
        ew.last(", CONVERT(media_name USING gbk)");

        return ew;
    }


    /**
     * 参数转换
     *
     * @param list 实体集合
     * @return 转换后的数据
     * @author JH
     * @date 2019/10/28 14:23
     */
    private List<Map<String, Object>> convert2List(List<MediaManager> list) {

        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    Dic mediaType = dicUtils.getDicByCode(DicConstants.MEDIA_TYPE, entity.getMediaType().toString());
                    if (mediaType != null && StringUtils.isNotBlank(mediaType.getDicValue())) {
                        m.put("mediaTypeTitle", mediaType.getDicValue());
                    }
                    if(YesOrNoEnum.YES.getType() == entity.getStatus()) {
                        m.put("statusName","启用");
                    } else {
                        m.put("statusName","禁用");
                    }
                }
                , MediaManager::getId, MediaManager::getSort, MediaManager::getNotes, MediaManager::getMediaType, MediaManager::getMediaName, MediaManager::getContactPerson, MediaManager::getContactWay, MediaManager::getStatus
        );
    }


    /**
    * 获取媒体树
    *
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/29 10:24
    */
    @PostMapping("/getMediaTree")
    public Mono<ResponseEntity<SystemResponse<Object>>> getMediaTree() {
        List<MediaManager> list = service.list(new LambdaQueryWrapper<MediaManager>().eq(MediaManager::getStatus, YesOrNoEnum.YES.getType()));
        //根节点
        TNode root = new TNode();
        root.setId("0");
        root.setText("全部");
        root.setIsParent(true);
        List<TNode> rootChildren = Lists.newArrayList();
        root.setChildren(rootChildren);
        //按deptType分组
        Map<Long, List<MediaManager>> map = list.stream().collect(Collectors.groupingBy(MediaManager::getMediaType));
        //类型集合
        Set<Long> typeSet = map.keySet();
        for (long mediaType : typeSet) {
            TNode typeNode = new TNode();
            typeNode.setId("mediaType:"+mediaType);
            Dic dic = dicUtils.getDicByCode(DicConstants.MEDIA_TYPE, mediaType + "");
            if(dic != null){
                typeNode.setText(dic.getDicValue());
            }else {
                typeNode.setText("");
            }
            typeNode.setIsParent(true);
            List<TNode> typeChildren = Lists.newArrayList();
            typeNode.setChildren(typeChildren);
            List<MediaManager> listByType = map.get(mediaType);
            if(CollectionUtils.isNotEmpty(listByType)) {
                listByType.forEach(media -> {
                    TNode node = new TNode();
                    node.setId(media.getId().toString());
                    node.setText(media.getMediaName());
                    node.setIsParent(false);
                    typeChildren.add(node);
                });
            }
            rootChildren.add(typeNode);
        }
        return success(root);
    }




}

