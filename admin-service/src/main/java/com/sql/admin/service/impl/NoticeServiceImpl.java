package com.sql.admin.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.mapper.NoticeMapper;
import com.sql.admin.service.NoticeService;
import com.sql.common.entity.db.Notice;
import com.sql.common.header.ContextHolder;
import com.sql.utils.StringUtils;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public Notice getNoticeById(Long noticeId) {
        return noticeMapper.selectById(noticeId);
    }

    @Override
    public List<Notice> listNotice(Notice notice) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(notice.getTitle()), Notice::getTitle, notice.getTitle())
                .like(StringUtils.isNotEmpty(notice.getCreateBy()), Notice::getCreateBy, notice.getCreateBy())
                .orderByDesc(Notice::getNoticeId);
        return noticeMapper.selectList(wrapper);
    }

    @Override
    public int addNotice(Notice notice) {
        notice.setCreateBy(ContextHolder.getUsername());
        notice.setCreateTime(LocalDateTime.now());
        return noticeMapper.insert(notice);
    }

    @Override
    public int updateNotice(Notice notice) {
        notice.setUpdateBy(ContextHolder.getUsername());
        notice.setUpdateTime(LocalDateTime.now());
        return noticeMapper.updateById(notice);
    }

    @Override
    public int deleteNoticeById(Long noticeId) {
        return noticeMapper.deleteById(noticeId);
    }

    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        if (noticeIds == null || noticeIds.length == 0) {
            return 0;
        }
        return noticeMapper.deleteByIds(Arrays.asList(noticeIds));
    }
}
