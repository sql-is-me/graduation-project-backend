package com.ruoyi.common.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ruoyi.common.Constants.AuthConstants;
import com.ruoyi.common.entity.OperLog;
import com.ruoyi.system.api.RemoteLogService;

/**
 * 异步调用日志服务
 * 
 * @author ruoyi
 */
@Service
public class AsyncLogService {
    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveOperLog(OperLog operLog) throws Exception {
        remoteLogService.saveLog(operLog, AuthConstants.INNER);
    }
}
