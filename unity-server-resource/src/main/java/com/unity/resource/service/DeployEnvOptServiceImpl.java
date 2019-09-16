
package com.unity.resource.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.resource.dao.DeployEnvOptDao;
import com.unity.resource.entity.DeployEnvOpt;
import com.unity.resource.enums.OptTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * ClassName: DeployEnvOptService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-03 10:12:42
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Slf4j
public class DeployEnvOptServiceImpl extends BaseServiceImpl<DeployEnvOptDao, DeployEnvOpt> {

    private final SystemConfiguration systemConfiguration;

    public DeployEnvOptServiceImpl(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }


    /**
     * 条件分页查询数据
     *
     * @param pageEntity 查询参数
     * @return 查询结果
     * @author zhangxiaogang
     * @since 2019/9/3 10:36
     */
    public PageElementGrid findByPage(PageEntity<DeployEnvOpt> pageEntity) {
        DeployEnvOpt entity = pageEntity.getEntity();
        LambdaQueryWrapper<DeployEnvOpt> ew = new LambdaQueryWrapper<DeployEnvOpt>();
        if (entity != null) {
            if (StringUtils.isNotEmpty(entity.getServerName())) {
                ew.like(DeployEnvOpt::getServerName, entity.getServerName());
            }
            if (entity.getOptType() != null) {
                ew.eq(DeployEnvOpt::getOptType, entity.getOptType());
            }
        }
        ew.orderByDesc(DeployEnvOpt::getGmtModified);
        IPage p = this.page(pageEntity.getPageable(), ew);
        return PageElementGrid.<Map<String, Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
    }

    /**
     * 根据id查询详情信息
     *
     * @param id 数据主键
     * @return 查询结果
     * @author zhangxiaogang
     * @since 2019/9/6 13:29
     */
    public Map<String, Object> getDetailById(Long id) {
        DeployEnvOpt deployEnvOpt = this.getById(id);
        if (deployEnvOpt != null) {
            return convert2Map(deployEnvOpt);
        } else {
            return null;
        }
    }

    /**
     * 执行脚本
     *
     * @param id 执行参数
     * @return 执行结果 0成功 1失败
     * @author zhangxiaogang
     * @since 2019/9/3 14:30
     */
    public int doShellWork(Long id) {
        DeployEnvOpt deployEnvOpt = this.getById(id);
        //执行脚本部署
        if (deployEnvOpt != null && deployEnvOpt.getIsImpl() == YesOrNoEnum.NO.getType()) {
            String shellPath = systemConfiguration.getUploadPath() + "shellList/";
            if (OptTypeEnum.JAR_TYPE.getId().equals(deployEnvOpt.getOptType())) {
                //添加参数
                String cmd = shellPath + "default.sh " + deployEnvOpt.getServerName() + " " + deployEnvOpt.getFilePath();
                log.info("---cmd---" + cmd);
                return doShellFile(cmd, shellPath, deployEnvOpt);
                //执行脚本
            } else if (OptTypeEnum.SH_TYPE.getId().equals(deployEnvOpt.getOptType())) {
                int runningStatus = 1;
                if (StringUtils.isNotEmpty(deployEnvOpt.getFilePath())) {
                    //执行上传sh文件
                    runningStatus = doShellFile(deployEnvOpt.getFilePath(), shellPath, deployEnvOpt);
                } else if (StringUtils.isEmpty(deployEnvOpt.getFilePath()) && StringUtils.isNotEmpty(deployEnvOpt.getShCommand())) {
                    //执行命令sh
                    runningStatus = doShellFile(deployEnvOpt.getShCommand(), shellPath, deployEnvOpt);
                }
                return runningStatus;
            } else if (OptTypeEnum.HTML_TYPE.getId().equals(deployEnvOpt.getOptType())) {
                //添加参数
                log.info("----文件路径---" + deployEnvOpt.getFilePath());
                if (deployEnvOpt.getFilePath().endsWith(".zip")) {
                    String[] pathArray = deployEnvOpt.getFilePath().split("\\.")[0].split("\\/");
                    if (pathArray.length > 0) {
                        log.info("文件名称：" + pathArray[pathArray.length - 1]);
                        String cmd = shellPath + "default_html.sh " + deployEnvOpt.getFilePath() + " " + pathArray[pathArray.length - 1];
                        log.info("执行命令：=======" + cmd);
                        return doShellFile(cmd, shellPath, deployEnvOpt);
                    }
                }
            }
        }
        return 1;
    }

    /**
     * 执行shell脚本
     *
     * @param cmd          脚本命令
     * @param shellPath    脚本地址
     * @param deployEnvOpt 部署内容
     * @author zhangxiaogang
     * @since 2019/9/9 11:29
     */
    private int doShellFile(String cmd, String shellPath, DeployEnvOpt deployEnvOpt) {
        int runningStatus;
        ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", cmd);
        builder.directory(new File(shellPath));
        StringBuffer sb = new StringBuffer();
        try {
            Process p = builder.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = stdInput.readLine()) != null) {
                sb.append(line).append("\n");
            }
            while ((line = stdError.readLine()) != null) {
                sb.append(line).append("\n");
            }
            log.info("-------执行结果-------" + sb.toString());
            try {
                runningStatus = p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
                runningStatus = 1;
                log.info("等待shell脚本执行状态时，报错...", e);
                sb.append(e.getMessage());
            }
            closeStream(stdInput);
            closeStream(stdError);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("执行shell脚本出错...", e);
            sb.append(e.getMessage());
            runningStatus = 1;
        }
        log.info("runningStatus = " + runningStatus);
        log.info("sb.toString() = " + sb.toString().length());
        deployEnvOpt.setIsImpl(YesOrNoEnum.YES.getType());
        if (StringUtils.isNotEmpty(sb.toString())) {
            if (sb.toString().length() < 60000) {
                deployEnvOpt.setNotes(sb.toString());
            } else {
                deployEnvOpt.setNotes(sb.toString().substring(0, 60000));
            }
        }
        this.updateById(deployEnvOpt);
        return runningStatus;
    }

    /**
     * 关闭数据流
     *
     * @param reader 数据流
     * @author zhangxiaogang
     * @since 2019/9/3 13:56
     */
    private void closeStream(BufferedReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception e) {
            log.error("关闭流数据异常");
        }
    }


    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return 转化结果
     * @author zhangxiaogang
     * @since 2019/9/3 10:29
     */
    private List<Map<String, Object>> convert2List(List<DeployEnvOpt> list) {
        return JsonUtil.<DeployEnvOpt>ObjectToList(list,
                (m, entity) -> adapterField(m, entity)
                , DeployEnvOpt::getId, DeployEnvOpt::getSort, DeployEnvOpt::getNotes, DeployEnvOpt::getName, DeployEnvOpt::getOptType, DeployEnvOpt::getServerName, DeployEnvOpt::getFilePath, DeployEnvOpt::getIsImpl, DeployEnvOpt::getShCommand, DeployEnvOpt::getFileInfo
        );
    }

    /**
     * 将实体 转换为 Map
     *
     * @param ent 实体
     * @return 转化结果
     * @author zhangxiaogang
     * @since 2019/9/3 10:32
     */
    private Map<String, Object> convert2Map(DeployEnvOpt ent) {
        return JsonUtil.<DeployEnvOpt>ObjectToMap(ent,
                (m, entity) -> adapterField(m, entity)
                , DeployEnvOpt::getId, DeployEnvOpt::getIsDeleted, DeployEnvOpt::getSort, DeployEnvOpt::getNotes, DeployEnvOpt::getName, DeployEnvOpt::getOptType, DeployEnvOpt::getServerName, DeployEnvOpt::getFilePath, DeployEnvOpt::getIsImpl, DeployEnvOpt::getShCommand, DeployEnvOpt::getFileInfo
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     * @author zhangxiaogang
     * @since 2019/9/3 10:32
     */
    private void adapterField(Map<String, Object> m, DeployEnvOpt entity) {
        if (!StringUtils.isEmpty(entity.getCreator())) {
            if (entity.getCreator().indexOf(ConstString.SEPARATOR_POINT) > -1) {
                m.put("creator", entity.getCreator().split(ConstString.SPLIT_POINT)[1]);
            } else {
                m.put("creator", entity.getCreator());
            }
        }
        if (!StringUtils.isEmpty(entity.getEditor())) {
            if (entity.getEditor().indexOf(ConstString.SEPARATOR_POINT) > -1) {
                m.put("editor", entity.getEditor().split(ConstString.SPLIT_POINT)[1]);
            } else {
                m.put("editor", entity.getEditor());
            }
        }
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
        if (entity.getOptType() != null) {
            m.put("optTypeTitle", OptTypeEnum.of(entity.getOptType()).getName());
        }
    }

    /**
     * 删除上传服务器上的文件信息
     *
     * @param idList id集合
     * @author zhangxiaogang
     * @since 2019/9/10 14:27
     */
    public int deleteFiles(List<Long> idList) {
        LambdaQueryWrapper<DeployEnvOpt> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DeployEnvOpt::getId, idList);
        List<DeployEnvOpt> list = this.list(lambdaQueryWrapper);
        int resultInt = 1;
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(deployEnvOpt -> {
                File file = new File(deployEnvOpt.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
            });
            resultInt = 0;
        }
        return resultInt;
    }
}
