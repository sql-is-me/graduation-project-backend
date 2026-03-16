package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.NoticeService;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.Notice;
import com.sql.common.entity.TableDataInfo;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

@RestController
@RequestMapping("/admin/notice")
@RequiresType(UserTypes.ADMIN)
public class NoticeController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    public TableDataInfo list(Notice notice) {
        startPage();
        List<Notice> list = noticeService.listNotice(notice);
        return getDataTable(list);
    }

    @GetMapping("/{noticeId}")
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    public R<?> getNoticeById(@PathVariable Long noticeId) {
        return R.ok(noticeService.getNoticeById(noticeId));
    }

    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PutMapping("/add")
    @RequiresType(UserTypes.ADMIN)
    public R<?> addNotice(@Validated @RequestBody Notice notice) {
        return R.ok(noticeService.addNotice(notice));
    }

    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    @RequiresType(UserTypes.ADMIN)
    public R<?> edit(@Validated @RequestBody Notice notice) {
        return R.ok(noticeService.updateNotice(notice));
    }

    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    @RequiresType(UserTypes.ADMIN)
    public R<?> remove(@PathVariable Long[] noticeIds) {
        return R.ok(noticeService.deleteNoticeByIds(noticeIds));
    }
}
