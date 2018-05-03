package com.leyou.item.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.exception.InvalidArgumentsException;
import com.leyou.item.api.UploadApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-05 10:27
 **/
@RestController
public class UploadController implements UploadApi {

    @Autowired
    private FastFileStorageClient storageClient;

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");

    @Value("${leyou.imageUrl}")
    private String imageUrl;

    @Override
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 图片信息校验
            // 1)校验后缀名
            String type = file.getContentType();
            if (!suffixes.contains(type)) {
                logger.info("上传失败，文件类型不匹配：{}", type);
                throw new InvalidArgumentsException();
            }
            // 2)校验图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                logger.info("上传失败，文件内容不符合要求");
                throw new InvalidArgumentsException();
            }
            // 上传图片
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(),
                    StringUtils.substringAfter(type, "/"), null);
            return ResponseEntity.ok(this.imageUrl + "/" + storePath.getFullPath());
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
