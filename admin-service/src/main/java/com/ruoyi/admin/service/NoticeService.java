package com.ruoyi.admin.service;

import java.util.List;

import com.ruoyi.common.entity.Notice;

public interface NoticeService {
    Notice getNoticeById(Long noticeId);

    List<Notice> listNotice(Notice notice);

    int addNotice(Notice notice);

    int updateNotice(Notice notice);

    int deleteNoticeById(Long noticeId);

    int deleteNoticeByIds(Long[] noticeIds);
}
