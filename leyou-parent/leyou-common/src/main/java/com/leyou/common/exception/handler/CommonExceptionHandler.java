package com.leyou.common.exception.handler;

import com.leyou.common.exception.InvalidArgumentsException;
import com.leyou.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-30 15:06
 **/
@ControllerAdvice
public class CommonExceptionHandler {

    /**
     * 处理404异常
     * @param e
     * @return
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * 处理400异常
     * @param e
     * @return
     */
    @ExceptionHandler(InvalidArgumentsException.class)
    public ResponseEntity<String> handleInvalidArguments(InvalidArgumentsException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
