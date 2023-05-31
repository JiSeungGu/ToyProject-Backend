package com.example.holder.service;


import com.example.common.fabric.FabApiConnect;
import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.entity.UserEntity;
import com.example.holder.dto.HolderDTO;
import com.example.issuer.service.IssuerService;
import com.example.projectdid.DidDocumentBase;
import com.example.projectdid.did.PrivateKey;
import com.example.projectdid.did.SejongDid;
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
 * packageName   : net.sejongtelecom.demo.domain.holder.service
 * fileName  : HolderService
 * author    : jiseung-gu
 * date  : 2023/01/19
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class HolderService {

  private final FabApiConnect fabApiConnect;
  private final ResponseService responseService;
  private final UserTableRepository userTableRepository;
  private final IssuerService issuerService;
  public CommonResult mainbucket() {
    listFolders();
    return responseService.getSuccessResult();

  }
  public CommonResult createUserDiD(HolderDTO holderDTO) {
    JsonParser parser = new JsonParser();
    log.info(holderDTO.toString());
    HashMap<Object,Object> resultMap = new HashMap<>();
    HashMap<Object,Object> DataMap = new HashMap<>();

    userTableRepository.findByuserId(holderDTO.getUserId()).ifPresentOrElse(
      UserEntity -> {
        // 해당 아이디가 이미 존재할 때
        log.info("userTable : " + UserEntity.toString());
        resultMap.put("Result",FALSE + "해당 아이디는 이미 존재하는 아이디입니다.");
      },
      () -> {
        //패스워드 Hash 암호화
        holderDTO.setUserPasswd(toHash(holderDTO.getUserPasswd(),holderDTO.getUserId()));

        //DID 생성 및 데이터 베이스 추가 로직
        PrivateKey privateKey = SejongDid.generateDidRootKey();
        SejongDid did = new SejongDid("TOYPROJECT",privateKey.getPublicKey(),privateKey);
        DidDocumentBase doc = did.generateDidDocument();
        JsonElement element = parser.parse(doc.toJson());

        DataMap.put("DID",did.getIdString());
        DataMap.put("Document",element);
        resultMap.put("Result",DataMap);

        log.info("DID : "+did.getIdString());
        log.info("Document : "+element);
        //회원 가입 가능
        userTableRepository.save(UserEntity.builder(
          ).userId(holderDTO.getUserId())
          .userPass(holderDTO.getUserPasswd())
          .userName(holderDTO.getUserName())
          .userPhoneNo(holderDTO.getUserPhoneNo())
          .privateKey(privateKey.toString())
          .userDid(did.getIdString())
          .build());
      });

//    resultMap.put("Result",issuerDto.toString());
    return responseService.getSingleResult(resultMap);
  }

  @SneakyThrows
  public String toHash(String passWd, String userId)  {
    return hashPassword(passWd,userId,generateSalt());
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
    log.info("METHOD signVP CALL");
    log.info("Holderid : "+Holderid);
    log.info("vc : "+vc);
    log.info("claim : "+claim);
    Gson gson = new GsonBuilder()
      .disableHtmlEscaping()
      .excludeFieldsWithoutExposeAnnotation()
      .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
      .create();
    log.info("Gson.toJsonTree :"+gson.toJsonTree(vc));
    Map<String,JsonElement> resultMap = new HashMap<>();

    userTableRepository.findByuserId(Holderid).ifPresentOrElse(
      UserEntity -> {
        // 해당 아이디가 존재할 때
        log.info("userTable : " + UserEntity.toString());
        try {

          resultMap.put("data",
            Proof(UserEntity.getUserDid(),UserEntity.getPrivateKey(),vc,claim)
          );
        } catch (PrivateKey.BadKeyException e) {
          log.info("키가 잘못되었습니다."+e);
          e.printStackTrace();
        }
      },
      () -> {
        log.info("해당 아이디가 존재하지 않습니다.");
      });
    JsonElement jsonElement = gson.toJsonTree(resultMap.get("data"));
    log.info(jsonElement.getAsString());
    return responseService.getSingleResult(jsonElement.getAsString());
  }
  //MY_THOUGHTS 선택적 Claim에 대한 CredentialText, Salt제거
  public Map<String, String> extractClaim(JsonElement vc, List<String> claimList, String credentialName ){
    Map<String, String> resultMap = new HashMap<>();
    claimList.forEach(claim -> {
    resultMap.put(claim,vc.getAsJsonObject().get(credentialName).getAsJsonArray().get(0).getAsJsonObject().get(claim).getAsString());
    log.info("claim : "+claim);
    });

    return resultMap;
  }

  public JsonElement Proof(String did,String privateKey,JsonElement vc,List<String> claim) throws PrivateKey.BadKeyException {

    Gson gson = new GsonBuilder()
      .disableHtmlEscaping()
      .excludeFieldsWithoutExposeAnnotation()
      .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
      .create();
    VPDocument vp = new VPDocument();
    JsonParser parser = new JsonParser();

    //MY_THOUGHTS React에서 전달되는 VC는 JSON형태로 오기 때문에 JsonElement로 형 변환이 필요없음
//    JsonElement element = parser.parse(vc);
//    gson.toJson(element);

    //SOURCE_DESC VC를 VP에 추가하기전에 CredentialText , CredentialSalt 추출
    Map<String,String> saltMap = extractClaim(vc,claim,"credentialSalt");
    Map<String,String> textMap = extractClaim(vc,claim,"credentialText");

    //SOURCE_DESC VC를 VP에 추가하기전에 CredentialText , CredentialSalt 제거
    vc.getAsJsonObject().remove("credentialText");
    vc.getAsJsonObject().remove("credentialSalt");
    vp.addVC(vc);

    List<Object> credentialTextList = new ArrayList<>();
    List<Object> credentialSaltList = new ArrayList<>();
    credentialTextList.add(textMap);
    credentialSaltList.add(saltMap);

    vp.setCredentialSalt(gson.toJsonTree(credentialSaltList));
    vp.setCredentialtext(gson.toJsonTree(credentialTextList));

    Ed25519PresentationProof VPProof = new Ed25519PresentationProof(did,"99612b24-63d9-11ea-b99f-4f66f3e4f81a");

    PrivateKey HolderKey = PrivateKey.fromString(privateKey);

    //SOURCE_DESC 서명을 위해 CredentialText, CredentialSalt를 합침
    //SOURCE_DESC 1. Document의 Proof를 제외한 모든 형태, 나머지를 제외한 Proof 형태 두 가지를 inputForSigning 으로 암호화 후 개인 키 서명
    VPProof.sign(HolderKey, gson.toJson(issuerService.combineTextAndSalt(parser.parse(vp.toNormalizedJson(true)),saltMap,textMap)));
    vp.setProof(VPProof);

    return gson.toJsonTree(vp.toNormalizedJson(false));
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
    if (mimeType == null) {
      mimeType = "application/octet-stream";
    }
    return mimeType;
  }
}
