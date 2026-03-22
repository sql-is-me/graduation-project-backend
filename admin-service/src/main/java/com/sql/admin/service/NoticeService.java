package com.sql.admin.service;

import java.util.List;

import com.sql.admin.dto.NoticeCreateDTO;
import com.sql.admin.dto.NoticeUpdateDTO;
import com.sql.common.entity.db.Notice;

public interface NoticeService {
    List<Notice> listNotice(Notice notice);

    Notice getNoticeById(Long noticeId);

    void publishNotice(NoticeCreateDTO dto);

    void updateNotice(NoticeUpdateDTO dto, Long noticeId);

    void deleteNotice(Long noticeId);
}
