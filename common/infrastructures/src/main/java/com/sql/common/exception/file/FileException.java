package com.sql.common.exception.file;

import com.sql.common.exception.BaseException;

/**
 * 文件信息异常类
 */
public class FileException extends BaseException {
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args, String msg) {
        super("file", code, args, msg);
    }

}
