
package com.unity.innovation.dao;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.SysNotice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 通知公告
 *
 * @author zhang
 * 生成时间 2019-09-23 15:00:35
 */
public interface SysNoticeDao extends BaseDao<SysNotice> {


    /**
     * 非宣传部查看收到通知公告列表接口
     *
     * @param page   分页参数
     * @param notice 查询条件
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.unity.innovation.entity.SysNotice>
     * @author JH
     * @date 2019/9/25 9:58
     */
    @Select("  <script>" +
            "  SELECT a.id,a.title,a.gmt_send " +
            "  from sys_notice as a   " +
            "  INNER JOIN sys_m_notice_user b " +
            "  ON a.id = b.id_sys_notice   " +
            "  WHERE a.is_deleted = 0  " +
            "  AND b.is_deleted = 0  " +
            "  AND a.is_send = 1  " +
            "  AND b.is_show = 1  " +
            "  AND b.id_rbac_user = #{notice.userId}" +
            " <if test='notice.gmtStart != null  and notice.gmtStart != \"\" '> " +
            "   AND a.gmt_send &gt; #{notice.gmtStart} " +
            " </if> " +
            " <if test='notice.gmtEnd != null  and notice.gmtEnd != \"\" '> " +
            "   AND a.gmt_send &lt; #{notice.gmtEnd} " +
            " </if> " +
            " <if test='notice.title != null and notice.title != \"\" '> " +
            "   AND a.title  LIKE CONCAT('%', #{notice.title}, '%') " +
            " </if> " +
            " ORDER BY a.gmt_send DESC " +
            "  </script> ")
    IPage<SysNotice> listByPageOther(IPage<SysNotice> page, @Param("notice") SysNotice notice);

}

