package com.sql.utils;

import com.github.pagehelper.PageHelper;
import com.sql.common.constants.PageConstants;
import com.sql.common.entity.vo.Page;

/**
 * 分页工具类
 */
public class PageUtils extends PageHelper {
    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        Page page = getPage();
        Integer pageNum = page.getPageNum();
        Integer pageSize = page.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(getOrderBy(page));
        Boolean reasonable = page.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }

    /**
     * 封装分页对象
     */
    public static Page getPage() {
        Page page = new Page();
        page.setPageNum(Convert.toInt(ServletUtils.getParameter(PageConstants.PAGE_NUM), 1));
        page.setPageSize(Convert.toInt(ServletUtils.getParameter(PageConstants.PAGE_SIZE), 10));
        page.setOrderByColumn(ServletUtils.getParameter(PageConstants.ORDER_BY_COLUMN));
        page.setIsAsc(ServletUtils.getParameter(PageConstants.IS_ASC));
        page.setReasonable(ServletUtils.getParameterToBool(PageConstants.REASONABLE));
        return page;
    }

    public static String getOrderBy(Page page) {
        if (StringUtils.isEmpty(page.getOrderByColumn())) {
            return "";
        }
        return StringUtils.toUnderScoreCase(page.getOrderByColumn()) + " " + page.getIsAsc();
    }
}
