package com.ruoyi.admin.service.Impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.admin.mapper.NoticeMapper;
import com.ruoyi.admin.service.NoticeService;
import com.ruoyi.common.entity.Notice;
import com.ruoyi.common.header.ContextHolder;
import com.ruoyi.utils.StringUtils;

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
                .eq(StringUtils.isNotEmpty(notice.getType()), Notice::getType, notice.getType())
                .like(StringUtils.isNotEmpty(notice.getCreateBy()), Notice::getCreateBy, notice.getCreateBy())
                .orderByDesc(Notice::getNid);
        return noticeMapper.selectList(wrapper);
    }

    @Override
    public int addNotice(Notice notice) {
        notice.setCreateBy(ContextHolder.getUsername());
        notice.setCreateTime(new Date());
        return noticeMapper.insert(notice);
    }

    @Override
    public int updateNotice(Notice notice) {
        notice.setUpdateBy(ContextHolder.getUsername());
        notice.setUpdateTime(new Date());
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
