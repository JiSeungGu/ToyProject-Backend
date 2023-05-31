package com.example.holder.service;

import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.holder.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.aws.AwsServiceConnect.uploadFile;

/**
 * packageName   : com.example.holder.service
 * fileName  : UserActivityService
 * author    : jiseung-gu
 * date  : 2023/04/28
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityService {

  private final ResponseService responseService;

  public CommonResult setFileupload(MultipartFile file,FileDTO fileDTO) {
    log.info("fileDTO : {}", fileDTO.toString());

    try{
      File uploadFile = convertMultiPartToFile(file);
      log.info(uploadFile.getName());
      uploadFile(fileDTO.getFilePath(),fileDTO.getFileName(),uploadFile);
    }catch (Exception e) {
      log.error("Exception : {}", e.getMessage());
    }


    return responseService.getSuccessResult();
  }

  public File convertMultiPartToFile(MultipartFile file) throws Exception {
    File uploadFile = File.createTempFile("temp", file.getOriginalFilename());
    try (FileOutputStream fos = new FileOutputStream(uploadFile)) {
      fos.write(file.getBytes());
    }
    return uploadFile;
  }
}
