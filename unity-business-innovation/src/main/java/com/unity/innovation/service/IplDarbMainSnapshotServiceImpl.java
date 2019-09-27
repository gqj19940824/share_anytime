package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.dao.IplDarbMainDao;
import com.unity.innovation.dao.IplDarbMainSnapshotDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.entity.generated.IplDarbMainSnapshot;
import com.unity.innovation.entity.generated.IplLog;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: IplDarbMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:36
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplDarbMainSnapshotServiceImpl extends BaseServiceImpl<IplDarbMainSnapshotDao, IplDarbMainSnapshot> {
}