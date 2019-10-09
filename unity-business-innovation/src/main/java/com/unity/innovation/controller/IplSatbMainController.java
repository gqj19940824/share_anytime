
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.service.IplSatbMainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;


/**
 * 创新发布清单-科技局-主表
 *
 * @author G
 * 生成时间 2019-10-08 17:03:09
 */
@RestController
@RequestMapping("/iplsatbmain")
public class IplSatbMainController extends BaseWebController {
    @Autowired
    IplSatbMainServiceImpl service;

    /**
     * 成长目标投资实时清单-科技局
     *
     * @param  pageEntity 包含分页及检索条件
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/08 17:23
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplSatbMain> pageEntity) {
        PageElementGrid result = service.listByPage(pageEntity);
        return success(result);

    }

    /**
     * 新增or修改成长目标投资实时清单
     *
     * @param  entity 实时清单信息
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/08 17:26
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IplSatbMain entity) {
        //校验

        service.saveOrUpdateIplSatbMain(entity);
        return success(null);
    }

    /*
     * 将实体 转换为 Map
     *
     * @param ent 实体
     * @return
     */
    /*private Map<String, Object> convert2Map(IplSatbMain ent) {
        return JsonUtil.<IplSatbMain>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                , IplSatbMain::getId, IplSatbMain::getIdIplmMainIplMain, IplSatbMain::getIsDeleted, IplSatbMain::getSort, IplSatbMain::getNotes, IplSatbMain::getIndustryCategory, IplSatbMain::getEnterpriseName, IplSatbMain::getDemandCategory, IplSatbMain::getProjectName, IplSatbMain::getProjectAddress, IplSatbMain::getProjectIntroduce, IplSatbMain::getTotalAmount, IplSatbMain::getBank, IplSatbMain::getBond, IplSatbMain::getRaise, IplSatbMain::getTechDemondInfo, IplSatbMain::getContactPerson, IplSatbMain::getContactWay, IplSatbMain::getAttachmentCode, IplSatbMain::getSource, IplSatbMain::getStatus
        );
    }*/

    /**
     * 删除实时清单
     *
     * @param  entity 包含清单id
     * @return code -> 0 表示删除成功
     * @author gengjiajia
     * @since 2019/10/08 17:30
     */
    @PostMapping("/deleteById")
    public Mono<ResponseEntity<SystemResponse<Object>>> deleteById(@RequestBody IplSatbMain entity) {
        service.deleteById(entity.getId());
        return success("删除成功");
    }

    /**
     * 实时清单详情
     *
     * @param  entity 包含清单id
     * @return 清单详情
     * @author gengjiajia
     * @since 2019/10/08 17:32
     */
    @PostMapping("/detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody IplSatbMain entity) {
        Map<String,Object> map = service.detailById(entity.getId());
        return success(map);
    }
}

