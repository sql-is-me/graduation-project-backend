package com.sql.user.service;

public interface CourseService {

    /**
     * 教练上传课程签到照片（一个课程一张）
     */
    void uploadSignIn(Long courseId, String photoUrl);

    /**
     * 教练上传课程签退照片（一个课程一张）
     */
    void uploadSignOut(Long courseId, String photoUrl);

    // TODO:教练的课程列表，会员课程列表，课程详细信息
}
