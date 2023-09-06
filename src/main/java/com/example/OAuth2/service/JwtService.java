package com.example.OAuth2.service;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.GetPublicKeyRequest;
import com.amazonaws.services.kms.model.GetPublicKeyResult;
import com.example.OAuth2.dto.NiceDto;
import com.example.OAuth2.entity.DeviceInfoEntity;
import com.example.OAuth2.entity.JwtEntity;
import com.example.OAuth2.entity.SnsKeysEntity;
import com.example.OAuth2.entity.UserInfoEntity;
import com.example.OAuth2.repository.DeviceInfoTableRepository;
import com.example.OAuth2.repository.JwtTableRepository;
import com.example.OAuth2.repository.SnskeysTableRepository;
import com.example.OAuth2.repository.UserInfoTableRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * packageName   : com.example.OAuth2.service
 * fileName  : JwtService
 * author    : jiseung-gu
 * date  : 2023/07/31
 * description :
 **/
@RequiredArgsConstructor
@Slf4j
@Service
public class JwtService {
  private static final String KEY_ID = "arn:aws:kms:ap-northeast-2:905126260776:key/b4a93930-1651-4ff7-9ad1-009d1a88f2e4";
  private static AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

  private final TokenProcess tokenProcess;
  private final RedisService redisService;
  private final JpaService jpaService;
  private final SnskeysTableRepository snskeysTableRepository;
  private final DeviceInfoTableRepository deviceInfoTableRepository;
  private final UserInfoTableRepository userInfoTableRepository;

  public String Onlyjwt() {
    String accessToken = tokenProcess.getToken("test", 60 * 30, "AccessToken");
    //RefreshToken 발급
    String refreshToken = tokenProcess.getToken("test", 60 * 60 * 24 * 30, "RefreshToken");
    System.out.println("accessToken : " + accessToken);
    System.out.println("refreshToken : " + refreshToken);
    return refreshToken;
  }

  public HashMap<String, Object> jwt(HttpServletRequest request) {
    log.info(request.getHeader("device-id"));
    log.info(request.getHeader("authorization"));
    log.info(request.getHeader("type"));

    String deviceId = request.getHeader("device-id");
    String platForm = request.getHeader("type");
    HashMap<String, Object> result = new HashMap<String, Object>();

    //PlatformType  인증
    HashMap<String, Object> paltForm = tokenProcess.PlatFormType(request);

    //AccessToken 발급 용 userId
    String userId = paltForm.get("data").toString();

    //기존 platfrom 가입 이력 확인
   tokenProcess.processByDeviceId(platForm, userId);

    //AccessToken 발급
    String accessToken = tokenProcess.getToken(userId, 60 * 30, "AccessToken");
    //RefreshToken 발급
    String refreshToken = tokenProcess.getToken(userId, 60 * 60 * 24 * 30, "RefreshToken");

    //Redis 저장
    redisService.setValue(userId + deviceId, refreshToken);

    result.put("accessToken", accessToken);
    result.put("refreshToken", refreshToken);

    return result;
  }

  public HashMap<String, Object> refreshToken(HttpServletRequest request) throws Exception {

    HashMap<String, Object> result;

    String DeviceId = request.getHeader("device-id");
    String accessToken = request.getHeader("authorization");
    String refreshToken = request.getHeader("refresh-token");

    PublicKey publicKey = tokenProcess.ConvertPubkey(GetPubKey());

    //SOURCE_DESC accesstoken 검증 및 payload(userId) 추출
    HashMap<String, Object> base64Payload = tokenProcess.getTokenPayload(publicKey, accessToken);

    //SOURCE_DESC refresh Token 체크
    tokenProcess.Verify(publicKey, refreshToken);

//    String payload = new String(Base64.getDecoder().decode(base64Payload.get("payload").toString()));
    String payload = base64Payload.get("payload").toString();
    log.info("payload : " + payload);

    String userId = JsonParser.parseString(payload).getAsJsonObject().get("uid").getAsString();
    log.info("userId : " + userId);

    //SOURCE_DESC Redis 있는 UserId, DeviceId 매칭 확인 후 jwt발급
    result = tokenProcess.processByUserId(userId, DeviceId, refreshToken);

    return result;
  }

  public String Nice(NiceDto niceDto) {
    Map<String, BiConsumer<Long,String>> platformActions = new HashMap<>();
    platformActions.put("Kakao", jpaService::UpdateKakao);
    platformActions.put("Google", jpaService::UpdateGoogle);
    platformActions.put("Apple", jpaService::UpdateApple);
    log.info("paylaod : " + niceDto.toString());
    //SOURCE_DESC 해당 CI가 이미 있으면  CI의 seq를 가져와서 sns 저장,
    userInfoTableRepository.findByCI(niceDto.getCI()).ifPresentOrElse(userInfoEntity -> {
        log.info(userInfoEntity.toString());

        //SOURCE_DESC PlatForm에 따른 sns 저장
        platformActions.get(niceDto.getType()).accept(userInfoEntity.getSeq(), niceDto.getToken());

      },
      () -> {

        //SOURCE_DESC nice인증으로 받은 redirect 정보 추출 후 회원가입로직
        UserInfoEntity savedEntity = userInfoTableRepository.save(UserInfoEntity.builder()
          .birthDate(niceDto.getBirthDate())
          .CI(niceDto.getCI())
          .name(niceDto.getName())
          .socialNumber(niceDto.getSocialNumber())
          .build());
        log.info("savedEntity.getSeq() : {}", savedEntity.getSeq());

        //SOURCE_DESC device, sns 정보 등록
        deviceInfoTableRepository.save(DeviceInfoEntity.builder()
          .userInfo(savedEntity)
          .uuid(niceDto.getUuid())
          .pushkeyToken(niceDto.getPushkeyToken())
          .build());

        snskeysTableRepository.save(SnsKeysEntity.builder()
          .userInfo(savedEntity)
          .kakao(niceDto.getToken())
          .build());
      });


    return "Success";
  }
  public String verify(String token) throws Exception {

    PublicKey publicKey = tokenProcess.ConvertPubkey(GetPubKey());

    tokenProcess.Verify(publicKey, token);

    return "Success";
  }

  public String GetPubKey() throws Exception {

    GetPublicKeyRequest getPublicKeyRequest = new GetPublicKeyRequest();
    getPublicKeyRequest.setKeyId(KEY_ID);
    GetPublicKeyResult getPublicKeyResult = kmsClient.getPublicKey(getPublicKeyRequest);
    ByteBuffer publicKey = getPublicKeyResult.getPublicKey();
    byte[] publicKeyBytes = new byte[publicKey.remaining()];
    publicKey.get(publicKeyBytes);

    // 공개키 객체를 생성합니다.
    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    PublicKey pubkey = kf.generatePublic(spec);
//    System.out.println("publickey :"+ Base64.getUrlEncoder().withoutPadding().encodeToString(pubkey.getEncoded()));
    System.out.println("publickey :" + Base64.getEncoder().encodeToString(pubkey.getEncoded()));
//    return pubkey;
//    return Base64.getUrlEncoder().withoutPadding().encodeToString(pubkey.getEncoded());
    return Base64.getEncoder().encodeToString(pubkey.getEncoded());
  }
}
