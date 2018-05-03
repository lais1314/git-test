package com.leyou.common.exception;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-30 15:04
 **/
public class InvalidArgumentsException extends RuntimeException {
    public InvalidArgumentsException() {
        super("你传入的参数有误。");
    }

    public InvalidArgumentsException(String message) {
        super(message);
    }
}
