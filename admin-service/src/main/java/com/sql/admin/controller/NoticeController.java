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

import com.sql.admin.dto.NoticeCreateDTO;
import com.sql.admin.dto.NoticeUpdateDTO;
import com.sql.admin.service.NoticeService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.TableDataInfo;
import com.sql.common.entity.db.Notice;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

@RestController
@RequestMapping("/admin/notice")
@LoginRequired
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

    @Log(title = "通知公告", businessType = BusinessType.INSERT, operatorType = UserTypes.ADMIN)
    @PutMapping("/publish")
    @RequiresType(UserTypes.ADMIN)
    public R<?> publishNotice(@Validated @RequestBody NoticeCreateDTO dto) {
        noticeService.publishNotice(dto);
        return R.ok("发布公告成功");
    }

    @Log(title = "通知公告", businessType = BusinessType.UPDATE, operatorType = UserTypes.ADMIN)
    @PutMapping("/edit/{noticeId}")
    @RequiresType(UserTypes.ADMIN)
    public R<?> edit(@Validated @RequestBody NoticeUpdateDTO dto, @PathVariable Long noticeId) {
        noticeService.updateNotice(dto, noticeId);
        return R.ok("编辑公告成功");
    }

    @Log(title = "通知公告", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/{noticeId}")
    @RequiresType(UserTypes.ADMIN)
    public R<?> remove(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return R.ok("删除公告成功");
    }
}
