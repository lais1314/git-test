package com.leyou.common.exception;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-30 15:02
 **/
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException() {
        super("您查找的资源未找到！");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
