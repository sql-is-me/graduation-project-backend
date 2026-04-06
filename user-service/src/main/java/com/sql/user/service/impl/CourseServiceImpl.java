package com.sql.user.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import com.sql.common.entity.po.Course;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.user.mapper.CourseMapper;
import com.sql.user.service.CourseService;

public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public void uploadSignIn(Long courseId, String photoUrl) {
        validateCourseOwnership(courseId);

        Course course = courseMapper.selectById(courseId);
        course.setSignInPhoto(photoUrl);
        course.setSignInTime(LocalDateTime.now());
        courseMapper.updateById(course);
    }

    @Override
    public void uploadSignOut(Long courseId, String photoUrl) {
        validateCourseOwnership(courseId);

        Course course = courseMapper.selectById(courseId);
        course.setSignOutPhoto(photoUrl);
        course.setSignOutTime(LocalDateTime.now());
        courseMapper.updateById(course);
    }

    private Long getCoachStoreId() {
        Long storeId = ContextHolder.getUO().getUserInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前教练未绑定店铺");
        }
        return storeId;
    }

    /**
     * 验证当前教练是否属于课程所属店铺
     * 
     * @param courseId
     */
    private void validateCourseOwnership(Long courseId) {
        Long storeId = getCoachStoreId();
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("课程不存在");
        }
        if (!course.getStoreId().equals(storeId)) {
            throw new ServiceException("无权操作其他店铺的课程");
        }
    }
}
