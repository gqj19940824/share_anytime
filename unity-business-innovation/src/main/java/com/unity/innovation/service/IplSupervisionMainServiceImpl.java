
package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.DateUtils;
import com.unity.common.util.FileReaderUtil;
import com.unity.common.utils.ExcelStyleUtil;
import com.unity.common.utils.UUIDUtil;
import com.unity.common.constant.ParamConstants;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.entity.generated.IplmManageLog;
import com.unity.innovation.enums.IplCategoryEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unity.innovation.entity.IplSupervisionMain;
import com.unity.innovation.dao.IplSupervisionMainDao;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;


/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplSupervisionMainServiceImpl extends BaseServiceImpl<IplSupervisionMainDao, IplSupervisionMain> {

    @Resource
    private IplManageMainServiceImpl iplManageMainService;

    @Resource
    private AttachmentServiceImpl attachmentService;

    @Resource
    private IplmManageLogServiceImpl logService;
    @Resource
    private SystemConfiguration systemConfiguration;


    /**
     * 新增
     *
     * @param entity 实体
     * @author JH
     * @date 2019/10/8 16:30
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateIplManageMain(IplManageMain entity) {
        String attachmentCode;
        //保存快照数据
        setSnapShot(entity);
        //新增
        if (entity.getId() == null) {
            //提交
            if (YesOrNoEnum.YES.getType() == entity.getIsCommit()) {
                entity.setStatus(WorkStatusAuditingStatusEnum.TWENTY.getId());
                entity.setGmtSubmit(System.currentTimeMillis());
            } else {
                entity.setStatus(WorkStatusAuditingStatusEnum.TEN.getId());
                entity.setGmtSubmit(ParamConstants.GMT_SUBMIT);
            }
            attachmentCode = UUIDUtil.getUUID();
            entity.setAttachmentCode(attachmentCode);
            entity.setIdRbacDepartmentDuty(InnovationConstant.DEPARTMENT_SUGGESTION_ID);
            //保存主表
            iplManageMainService.save(entity);
        } else {
            IplManageMain old = iplManageMainService.getById(entity.getId());
            attachmentCode = old.getAttachmentCode();
            if (YesOrNoEnum.YES.getType() == entity.getIsCommit()) {
                if (ParamConstants.GMT_SUBMIT.equals(old.getGmtSubmit())) {
                    entity.setGmtSubmit(System.currentTimeMillis());
                }
                entity.setStatus(WorkStatusAuditingStatusEnum.TWENTY.getId());
            }
            //修改主表
            iplManageMainService.updateById(entity);
        }
        //保存附件表
        attachmentService.updateAttachments(attachmentCode, entity.getAttachments());
        //提交、记录日志
        if (YesOrNoEnum.YES.getType() == entity.getIsCommit()) {
            logService.saveLog(InnovationConstant.DEPARTMENT_SUGGESTION_ID, WorkStatusAuditingStatusEnum.TWENTY.getId(), "", entity.getId());
        }
    }


    /**
     * 保存快照数据
     *
     * @param entity 实体
     * @author JH
     * @date 2019/10/9 10:14
     */
    private void setSnapShot(IplManageMain entity) {
        //保存快照数据
        List<IplSupervisionMain> snapShotList = entity.getSupervisionMainList();
        snapShotList.sort(comparing(IplSupervisionMain::getCategory)
                .reversed()
                .thenComparing(IplSupervisionMain::getGmtCreate)
                .reversed());
        String snapshot = JSON.toJSONString(snapShotList);
        entity.setSnapshot(snapshot);
    }


    /**
     * 分页查询
     *
     * @param pageable 分页参数
     * @param ew       查询条件
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.unity.innovation.entity.generated.IplManageMain>
     * @author JH
     * @date 2019/10/9 16:26
     */
    public IPage<IplManageMain> pageIplManageMain(IPage<IplManageMain> pageable, Wrapper<IplManageMain> ew) {
        return iplManageMainService.page(pageable, ew);
    }





    /**
     * 查询条件转换
     *
     * @param entity 统一查询对象
     * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.IplSupervisionMain>
     * @author JH
     * @date 2019/9/26 13:54
     */
    public LambdaQueryWrapper<IplSupervisionMain> wrapper(IplSupervisionMain entity) {
        LambdaQueryWrapper<IplSupervisionMain> ew = new LambdaQueryWrapper<>();
        if (entity.getCategory() != null) {
            ew.eq(IplSupervisionMain::getCategory, entity.getCategory());
        }
        //创建时间
        if (StringUtils.isNotBlank(entity.getCreateTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), false);
            //gt 大于 lt 小于
            ew.lt(IplSupervisionMain::getGmtCreate, end);
            ew.gt(IplSupervisionMain::getGmtCreate, begin);
        }
        if (entity.getDescription() != null) {
            ew.like(IplSupervisionMain::getDescription, entity.getDescription());
        }
        ew.orderByDesc(IplSupervisionMain::getGmtCreate);
        return ew;
    }

    /**
     * 返回可选择的基础数据以及已选择的数据
     *
     * @param entity 查询条件
     * @return java.util.List<com.unity.innovation.entity.IplSupervisionMain>
     * @author JH
     * @date 2019/10/10 11:35
     */
    public Map<String, List<IplSupervisionMain>> listSupervisionToAdd(IplSupervisionMain entity) {
        Map<String, List<IplSupervisionMain>> res = new HashMap<>(16);
        LambdaQueryWrapper<IplSupervisionMain> ew = wrapper(entity);
        //基础数据
        List<IplSupervisionMain> base = super.list(ew);
        res.put("base", base);
        IplManageMain iplManageMain = iplManageMainService.getById(entity.getId());
        String s = iplManageMain.getSnapshot();
        //快照数据
        List<IplSupervisionMain> snapshot = JSON.parseArray(s, IplSupervisionMain.class);
        res.put("snapshot", snapshot);
        return res;
    }

    /**
     * 清亲政商关系清单发布管理-纪检组 删除接口
     *
     * @param id 主键
     * @author JH
     * @date 2019/10/10 14:12
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeIplManageMainById(Long id) {
        IplManageMain iplManageMain = iplManageMainService.getById(id);
        String attachmentCode = iplManageMain.getAttachmentCode();
        //删除附件
        attachmentService.updateAttachments(attachmentCode, null);
        //删除日志
        logService.remove(new LambdaUpdateWrapper<IplmManageLog>().eq(IplmManageLog::getIdIplManageMain, id).eq(IplmManageLog::getIdRbacDepartment, InnovationConstant.DEPARTMENT_SUGGESTION_ID));
        iplManageMainService.removeById(id);
    }

    /**
     * 下载接口
     *
     * @param entity 实体
     * @return byte[] 字节流
     * @author JH
     * @date 2019/10/11 11:10
     */
    public byte[] download(IplManageMain entity) {
        //查询模板信息
        byte[] content;
        String templatePath = systemConfiguration.getUploadPath() + File.separator + "清亲政商" + File.separator;
        String templateFile = templatePath + File.separator + "清亲政商.xls";
        File dir = new File(templatePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream out = null;
        try {
            // excel对象
            HSSFWorkbook wb = new HSSFWorkbook();
            // sheet对象
            HSSFSheet sheet = wb.createSheet(entity.getTitle());
            //创建导出参数
            createExcelParam(entity, wb, sheet);
            out = new FileOutputStream(templateFile);
            // 输出excel
            wb.write(out);
            out.close();
            File file = new File(templateFile);
            content = FileReaderUtil.getBytes(file);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.SERVER_ERROR, "下载失败");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content;
    }

    /**
     * 创建导出信息
     *
     * @param entity 实体
     * @param wb     HSSFWorkbook
     * @param sheet  HSSFSheet
     * @author JH
     * @date 2019/10/11 11:11
     */
    private void createExcelParam(IplManageMain entity, HSSFWorkbook wb, HSSFSheet sheet) {
        String header = entity.getTitle();
        String[] title = {"清单类别", "内容", "创建时间"};
        Map<String, CellStyle> styleMap = ExcelStyleUtil.createProjectStyles(wb);
        //header
        Row headerRow = sheet.createRow(0);
        Cell titleCell = headerRow.createCell(0);
        titleCell.setCellStyle(styleMap.get("title"));
        titleCell.setCellValue(header);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, title.length - 1));
        //表头
        Row titleRow = sheet.createRow(1);
        for (int j = 0; j < title.length; j++) {
            Cell cell = titleRow.createCell(j);
            cell.setCellStyle(styleMap.get("title"));
            sheet.setColumnWidth(j, title[j].getBytes().length * 2 * 256);
            cell.setCellValue(title[j]);
        }
        //快照数据
        List<IplSupervisionMain> iplSupervisionMains = JSON.parseArray(entity.getSnapshot(), IplSupervisionMain.class);
        //按清单类别分组
        Map<Integer, List<IplSupervisionMain>> map = iplSupervisionMains.stream().collect(Collectors.groupingBy(IplSupervisionMain::getCategory));
        List<IplSupervisionMain> zmqd = map.get(IplCategoryEnum.ZMQD.getId()) == null ? new ArrayList<>() : map.get(IplCategoryEnum.ZMQD.getId());
        List<IplSupervisionMain> fmqd = map.get(IplCategoryEnum.FMQD.getId()) == null ? new ArrayList<>() : map.get(IplCategoryEnum.FMQD.getId());
        List<IplSupervisionMain> ydqd = map.get(IplCategoryEnum.YDQD.getId()) == null ? new ArrayList<>() : map.get(IplCategoryEnum.YDQD.getId());
        int num = 2;
        //内容
        for (int i = 2; i < iplSupervisionMains.size() + 2; i++) {
            IplSupervisionMain supervisionMain = iplSupervisionMains.get(i - 2);
            String categoryName = "";
            if (CollectionUtils.isNotEmpty(zmqd) && supervisionMain.getId().equals(zmqd.get(0).getId())) {
                categoryName = "正面清单";
            } else if (CollectionUtils.isNotEmpty(fmqd) && supervisionMain.getId().equals(fmqd.get(0).getId())) {
                categoryName = "负面清单";
            } else if (CollectionUtils.isNotEmpty(ydqd) && supervisionMain.getId().equals(ydqd.get(0).getId())) {
                categoryName = "引导清单";
            }
            Map<Integer, String> params = getIndexParam(iplSupervisionMains.get(i - 2), categoryName);
            Row row = sheet.createRow(num);
            for (int j = 0; j < title.length; j++) {
                Cell cell = row.createCell(j);
                sheet.setColumnWidth(j, title[j].getBytes().length * 2 * 256);
                cell.setCellStyle(styleMap.get("data"));
                cell.setCellValue(params.get(j));
            }
            num++;
        }
        //合并清单类型的单元格
        if (zmqd.size() > 1) {
            sheet.addMergedRegion(new CellRangeAddress(2, 2 + zmqd.size() - 1, 0, 0));
        }
        if (fmqd.size() > 1) {
            sheet.addMergedRegion(new CellRangeAddress(2 + zmqd.size(), 2 + zmqd.size() + fmqd.size() - 1, 0, 0));
        }
        if (ydqd.size() > 1) {
            sheet.addMergedRegion(new CellRangeAddress(2 + zmqd.size() + fmqd.size(), 2 + zmqd.size() + fmqd.size() + ydqd.size() - 1, 0, 0));
        }
        //备注
        Row notesRow = sheet.createRow(num);
        for (int j = 0; j < title.length; j++) {
            Cell cell = notesRow.createCell(j);
            cell.setCellStyle(styleMap.get("data"));
            sheet.setColumnWidth(j, title[j].getBytes().length * 2 * 256);
            if (j == 0) {
                cell.setCellValue(entity.getTitle());
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(num, num, 0, title.length - 1));
    }

    /**
     * 获取单元格参数
     *
     * @param entity       实体
     * @param categoryName 清单类型名称
     * @return java.util.Map<java.lang.Integer, java.lang.String>
     * @author JH
     * @date 2019/10/11 11:11
     */
    private Map<Integer, String> getIndexParam(IplSupervisionMain entity, String categoryName) {
        Map<Integer, String> map = new HashMap<>(16);
        map.put(0, categoryName);
        map.put(1, entity.getDescription());
        map.put(2, DateUtils.timeStamp2Date(entity.getGmtCreate()));
        return map;
    }


}
