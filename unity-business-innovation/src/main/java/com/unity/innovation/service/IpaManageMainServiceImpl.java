
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.dao.IpaManageMainDao;
import com.unity.innovation.entity.DailyWorkStatusPackage;
import com.unity.innovation.entity.PmInfoDept;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.entity.generated.IplManageMain;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ClassName: IpaManageMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:33
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IpaManageMainServiceImpl extends BaseServiceImpl<IpaManageMainDao, IpaManageMain> {

    @Resource
    private IplManageMainServiceImpl iplManageMainService;

    @Resource
    private DailyWorkStatusPackageServiceImpl dailyWorkStatusPackageService;

    @Resource
    private PmInfoDeptServiceImpl pmInfoDeptService;

    public void edit(IpaManageMain entity){
        List<Long> idDwspList = entity.getIdDwspList();
        List<Long> idIplpList = entity.getIdIplpList();
        List<Long> idPmpList = entity.getIdPmpList();

        if (CollectionUtils.isEmpty(idDwspList) && CollectionUtils.isEmpty(idIplpList) && CollectionUtils.isEmpty(idPmpList)){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message(
                    SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName()).build();
        }

        updateById(entity);
    }

    /**
     * 新增活动管理
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/17 2:37 下午
     */
    public void add(IpaManageMain entity) {
        List<Long> idDwspList = entity.getIdDwspList();
        List<Long> idIplpList = entity.getIdIplpList();
        List<Long> idPmpList = entity.getIdPmpList();

        if (CollectionUtils.isEmpty(idDwspList) && CollectionUtils.isEmpty(idIplpList) && CollectionUtils.isEmpty(idPmpList)){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message(
                    SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName()).build();
        }

        save(entity);

        // 工作动态
        if (CollectionUtils.isNotEmpty(idDwspList)){
            int count = dailyWorkStatusPackageService.count(new LambdaQueryWrapper<DailyWorkStatusPackage>().in(DailyWorkStatusPackage::getId, idDwspList).isNotNull(DailyWorkStatusPackage::getIdIpaMain));
            checkUnique(count);

            iplManageMainService.update(IplManageMain.newInstance().idIpaMain(entity.getId()).build(), new LambdaQueryWrapper<IplManageMain>().in(IplManageMain::getId, idDwspList));
        }
        // 与会企业信息
        if (CollectionUtils.isNotEmpty(idDwspList)){
            int count = iplManageMainService.count(new LambdaQueryWrapper<IplManageMain>().in(IplManageMain::getId, idDwspList).isNotNull(IplManageMain::getIdIpaMain));
            checkUnique(count);
            PmInfoDept pmInfoDept = new PmInfoDept();
            pmInfoDept.setIdIpaMain(entity.getId());
            pmInfoDeptService.update(pmInfoDept, new LambdaQueryWrapper<PmInfoDept>().in(PmInfoDept::getId, idDwspList));
        }
        // 创新发布清单
        if (CollectionUtils.isNotEmpty(idDwspList)){
            int count = iplManageMainService.count(new LambdaQueryWrapper<IplManageMain>().in(IplManageMain::getId, idDwspList).isNotNull(IplManageMain::getIdIpaMain));
            checkUnique(count);

            iplManageMainService.update(IplManageMain.newInstance().idIpaMain(entity.getId()).build(), new LambdaQueryWrapper<IplManageMain>().in(IplManageMain::getId, idDwspList));
        }
    }

    private void checkUnique(int count) {
        if (count > 0) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                    .message("所添加的数据中存在已添加至其他创新发布活动的数据，请重新添加！").build();
        }
    }
}
