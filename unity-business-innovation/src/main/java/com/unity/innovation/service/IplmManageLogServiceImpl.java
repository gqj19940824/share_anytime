
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import com.unity.innovation.entity.generated.IplmManageLog;
import org.springframework.stereotype.Service;
import com.unity.innovation.dao.IplmManageLogDao;

/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplmManageLogServiceImpl extends BaseServiceImpl<IplmManageLogDao, IplmManageLog> {


    /**
     * 记录操作日志
     *
     * @param idRbacDepartment 单位id
     * @param status           状态
     * @param content          审批意见
     * @param idIplManageMain  创新发布清单id
     * @author JH
     * @date 2019/10/9 14:19
     */
    public void saveLog(Long idRbacDepartment, Integer status, String content, Long idIplManageMain) {
        IplmManageLog log = IplmManageLog.newInstance()
                .idRbacDepartment(idRbacDepartment)
                .status(status)
                .content(content)
                .idIplManageMain(idIplManageMain)
                .build();
        save(log);
    }
}
