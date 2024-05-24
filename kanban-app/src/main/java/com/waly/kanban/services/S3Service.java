package com.waly.kanban.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;

@Slf4j
@Service
public class S3Service {

    @Autowired
    private AmazonS3 s3client;

    @Value("${s3.bucket}")
    private String bucketName;

    public URL uploadFile(MultipartFile file) {
        try {
            log.info("Uploading file to S3");
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileName = Instant.now().getEpochSecond() + "." + ext;

            InputStream is = file.getInputStream();
            String contentType = file.getContentType();
            return uploadFIle(is, fileName, contentType);

        }catch (IOException e) {
            log.error("Error uploading file to S3", e);
            return null;
        }
    }

    private URL uploadFIle(InputStream is, String fileName, String contentType) {
        log.info("Uploading file to S3 start");
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(contentType);
        s3client.putObject(bucketName, fileName, is, meta);
        log.info("Uploading file to S3 end");
        return s3client.getUrl(bucketName, fileName);
    }
}
