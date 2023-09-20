package com.example.holder.service;


import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.entity.UserEntity;
import com.example.holder.dto.HolderDTO;
import com.example.issuer.service.IssuerService;
import com.example.projectdid.DidDocumentBase;
import com.example.projectdid.did.PrivateKey;
import com.example.projectdid.did.Did;
import com.example.projectdid.proof.Ed25519PresentationProof;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.example.projectdid.vp.VPDocument;
import com.example.repository.UserTableRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.threeten.bp.Instant;

import java.io.IOException;
import java.net.URLConnection;
import java.util.*;

import static com.example.aws.AwsServiceConnect.*;
import static com.example.common.crypto.PasswordHashing.*;
import static java.lang.Boolean.FALSE;

/**
 * fileName  : HolderService
 * author    : jiseung-gu
 * date  : 2023/01/19
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class HolderService {

  private final ResponseService responseService;
  private final UserTableRepository userTableRepository;
  private final IssuerService issuerService;
  public CommonResult mainbucket() {
    listFolders();
    return responseService.getSuccessResult();

  }
  public CommonResult createUserDiD(HolderDTO holderDTO) {

    return responseService.getSingleResult("SECRET SOURCE CODE");
  }

  @SneakyThrows
  public String CompareHash(String passWd, String userId,String salt)  {
    return hashPassword(passWd,userId,salt);
  }


  public CommonResult login(HolderDTO holderDTO) {
    HashMap<Object,Object> resultMap = new HashMap<>();
    HashMap<Object,Object> DataMap = new HashMap<>();

    userTableRepository.findByuserId(holderDTO.getUserId()) // 해당 아이디가 존재하는지 확인
      .ifPresentOrElse(
        UserEntity -> {
          // 해당 아이디가 존재할 때
          log.info("userTable : " + UserEntity.toString());
          log.info("extractSalt : " + extractSalt(UserEntity.getUserPass()));
          if(UserEntity.getUserPass().equals(CompareHash(holderDTO.getUserPasswd(),holderDTO.getUserId(),extractSalt(UserEntity.getUserPass())))){
            // 로그인 성공
            resultMap.put("Result","SUCCESS");
            if (folderExists(holderDTO.getUserId())) {
              createFolder(holderDTO.getUserId());
            } else {
              log.info("해당 유저 존재하는 폴더입니다.");
            }

          }else{
            // 비밀번호가 틀렸을 때
            resultMap.put("Result","FAILE");
          }
        },
        () -> {
          // 해당 아이디가 존재하지 않을 때
          resultMap.put("Result","FALSE");
        });

    return responseService.getSingleResult(resultMap);
  }
  //SOURCE_DESC AWS S3에 있는 버킷 리스트를 가져온다.
  public CommonResult getFolderList() {
    log.info("METHOD getFolderList CALL");
    HashMap<Object,Object> resultMap = new HashMap<>();
    HashMap<Object,Object> DataMap = new HashMap<>();

    List<String>  FolderList = listFolders();
    for (String folder: FolderList) {
        DataMap.put(folder,listFiles(folder));
    }
//    DataMap.put("data",FolderList);
    resultMap.put("Result",DataMap);
    return responseService.getSingleResult(resultMap);
  }

  public CommonResult signVP(String Holderid,JsonElement vc,List<String> claim) {

    return responseService.getSingleResult("SECRET SOURCE CODE");
  }

  public Map<String, String> extractClaim(JsonElement vc, List<String> claimList, String credentialName ){
    Map<String, String> resultMap = new HashMap<>();
    //SECRET SOURCE CODE
    return resultMap;
  }

  public JsonElement Proof(String did,String privateKey,JsonElement vc,List<String> claim) throws PrivateKey.BadKeyException {
  //SECRET SOURCE CODE
    return null;

  }

  public ResponseEntity<byte[]> getFileDownLoad(String folderFileName) throws IOException {
    String[] parts = folderFileName.split("/");

    String folderName = parts[0]; // 버킷 명
    String fileName = parts[1]; // 파일 명
    byte[] fileContent = null;
    try {
      fileContent = downloadFile(folderName, fileName);
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    String mimeType = determineMimeType(fileName); // 사용자 정의 함수를 호출하여 파일의 MIME 타입을 결정합니다.

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(mimeType)); // Content-Type 헤더를 설정합니다.
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

    return ResponseEntity.ok()
      .headers(headers)
      .body(fileContent);
  }
  public String determineMimeType(String fileName) {
    String mimeType = URLConnection.guessContentTypeFromName(fileName);
    if (mimeType == null) {:
      mimeType = "application/octet-stream";
    }
    return mimeType;
  }
}
