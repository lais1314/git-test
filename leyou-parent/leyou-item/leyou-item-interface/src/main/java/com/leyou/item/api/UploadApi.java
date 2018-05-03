package com.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传
 * @author: HuYi.Zhang
 * @create: 2018-04-05 10:18
 **/
@RequestMapping("upload")
public interface UploadApi {

    @PostMapping
    ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file);
}
