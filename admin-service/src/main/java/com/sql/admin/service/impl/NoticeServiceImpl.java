package com.sql.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.dto.NoticeCreateDTO;
import com.sql.admin.dto.NoticeUpdateDTO;
import com.sql.admin.mapper.NoticeMapper;
import com.sql.admin.service.NoticeService;
import com.sql.common.entity.db.Notice;
import com.sql.common.header.ContextHolder;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public List<Notice> listNotice(Notice notice) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(Notice::getStatus, 1)
                .orderByDesc(Notice::getNoticeId);
        return noticeMapper.selectList(wrapper);
    }

    @Override
    public Notice getNoticeById(Long noticeId) {
        return noticeMapper.selectById(noticeId);
    }

    @Override
    public void publishNotice(NoticeCreateDTO dto) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setCreateBy(ContextHolder.getUsername());

        noticeMapper.insert(notice);
    }

    @Override
    public int updateNotice(NoticeUpdateDTO dto, Long noticeId) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new IllegalArgumentException("公告不存在");
        }

        if (dto.getTitle() != null) {
            notice.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            notice.setContent(dto.getContent());
        }
        if (dto.getStatus() != null) {
            notice.setStatus(dto.getStatus());
        }

        notice.setUpdateBy(ContextHolder.getUsername());

        return noticeMapper.updateById(notice);
    }

    @Override
    public int deleteNotice(Long noticeId) {
        return noticeMapper.deleteById(noticeId);
    }
}
