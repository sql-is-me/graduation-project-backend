package com.sql.admin.service;

import java.util.List;

import com.sql.common.entity.db.Notice;

public interface NoticeService {
    Notice getNoticeById(Long noticeId);

    List<Notice> listNotice(Notice notice);

    int addNotice(Notice notice);

    int updateNotice(Notice notice);

    int deleteNoticeById(Long noticeId);

    int deleteNoticeByIds(Long[] noticeIds);
}
