package com.example.holder.controller;

import com.example.common.response.CommonResult;
import com.example.holder.dto.FileDTO;
import com.example.holder.dto.HolderDTO;
import com.example.holder.dto.VpRequestDTO;
import com.example.holder.service.HolderService;
import com.example.holder.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * fileName  : HolderController
 * author    : jiseung-gu
 * date  : 2023/01/19
 * description :
 **/
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value="/v1/toy")
public class HolderController {

    private final HolderService holderService;
    private final UserActivityService userActivityService;
  //MY_THOUGHTS 생각해보니까 사용자는 holder이므로 사용자가 did를 발급받는 것이 맞는 것 같다. issuer controller가 아닌 holder controller로
  //COMPLETED 회원가입 완료
  @PostMapping(value="/holder")
  public CommonResult createUser(@RequestBody HolderDTO holderDTO) {
    return holderService.createUserDiD(holderDTO);
  }

  //COMPLETED 로그인 완료
  @GetMapping(value="/holder")
  public CommonResult Login(
    @RequestParam("userDid") String userDid,
    @RequestParam("userId") String userId,
    @RequestParam("userPasswd") String userPasswd
  ) {
    HolderDTO holderDTO = new HolderDTO();
    holderDTO.setUserDid(userDid);
    holderDTO.setUserId(userId);
    holderDTO.setUserPasswd(userPasswd);
    return holderService.login(holderDTO);
  }

  //COMPLETED 사용자 로그인시 API호출해서 S3에 저장된 폴더 리스트 '전부' 가져오기
  @GetMapping(value="/holder/folder")
  public CommonResult getFolderList() {
    return holderService.getFolderList();
  }


  //COMPLETED VP 서명
  @PostMapping(value="/holder/sign")
//  public CommonResult signVP(@RequestBody HolderDTO holderDTO, @RequestBody List<String> claim) {
  public CommonResult signVP(@RequestBody VpRequestDTO vpRequestDTO) {
      return holderService.signVP(vpRequestDTO.getUserId(), vpRequestDTO.getVc(),vpRequestDTO.getClaim());
  }

//  @PostMapping(value="/holder/Upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  //COMPLETED 파일 업로드
  @PostMapping(value="/holder/Upload")
  public CommonResult setFileUpload(@RequestParam("file") MultipartFile file,
  @RequestParam("path") String name) {
    // 파일 정보를 FileDTO 객체로 변환
    FileDTO fileDTO = new FileDTO();
    fileDTO.setFileName(file.getOriginalFilename());
    fileDTO.setFileSize(file.getSize());
    fileDTO.setFilePath(name);
    // fileDTO.setFilePath(filePath); // 파일 경로 설정 (예: 저장된 파일의 URL 또는 경로)

    return userActivityService.setFileupload(file, fileDTO);
  }


  @GetMapping(value="/holder/Download")
  public ResponseEntity<byte[]> getFileDownLoad(@RequestParam("fileName") String fileName) throws IOException {
    return holderService.getFileDownLoad(fileName);
  }
}
