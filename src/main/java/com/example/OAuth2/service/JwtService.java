package com.example.OAuth2.service;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.GetPublicKeyRequest;
import com.amazonaws.services.kms.model.GetPublicKeyResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

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
  public String jwt() {

    String header = tokenProcess.Header();

    String payload = tokenProcess.Payload();

    String signature = tokenProcess.GetSign(tokenProcess.Combine(header, payload));

    String jwt = header+"."+ payload +"."+ signature;

    return jwt;
  }

  public Boolean verify(String token) throws Exception {

    PublicKey publicKey = tokenProcess.ConvertPubkey(GetPubKey());
    return tokenProcess.Verify(publicKey,token);
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
    System.out.println("publickey :"+ Base64.getUrlEncoder().withoutPadding().encodeToString(pubkey.getEncoded()));
//    return pubkey;
    return Base64.getUrlEncoder().withoutPadding().encodeToString(pubkey.getEncoded());
  }
}
