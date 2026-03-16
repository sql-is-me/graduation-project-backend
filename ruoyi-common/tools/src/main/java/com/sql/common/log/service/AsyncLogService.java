package com.sql.common.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sql.api.RemoteOperLogService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.OperLog;

/**
 * 异步调用日志服务
 * 
 * @author loveSport
 */
@Service
public class AsyncLogService {
    @Autowired
    private RemoteOperLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveOperLog(OperLog operLog) throws Exception {
        remoteLogService.saveOperLog(operLog, AuthConstants.INNER);
    }
}
