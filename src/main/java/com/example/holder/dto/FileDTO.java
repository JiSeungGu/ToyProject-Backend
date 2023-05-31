package com.example.holder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * packageName   : com.example.holder.dto
 * fileName  : FileDTO
 * author    : jiseung-gu
 * date  : 2023/04/28
 * description :
 **/
@Getter
@Setter
public class FileDTO {

  @JsonProperty(value = "file_name")
  private String fileName;

  @JsonProperty(value = "file_path")
  private String filePath;

  @JsonProperty(value = "file_size")
  private long fileSize;


  @Override
  public String toString() {
    return "FileDTO{" +
      "fileName='" + fileName + '\'' +
      ", filePath='" + filePath + '\'' +
      ", fileSize=" + fileSize +
      '}';
  }
}
