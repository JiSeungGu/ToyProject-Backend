package com.example.OAuth2.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.repository.UserTableRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static java.lang.Boolean.FALSE;

/**
 * packageName   : com.example.OAuth2.service
 * fileName  : JwtUsingFileService
 * author    : jiseung-gu
 * date  : 2023/08/02
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUsingFileService {
  private final ResponseService responseService;
  private final UserTableRepository userTableRepository;



  public CommonResult Verify(String token) throws Exception {
//    Jws<Claims> jws;
    // Load the public key
    byte[] keyBytes = Files.readAllBytes(Paths.get("public_key.pem"));
    PemReader pemReader = new PemReader(new StringReader(new String(keyBytes)));
    X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(pemReader.readPemObject().getContent());
    PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(x509Spec);

    DecodedJWT decodedJWT;
    try {
//      byte[] secret = TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=");
//      Algorithm algorithm = Algorithm.HMAC256(secret);
      Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null); // Here we don't need a private key
      JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer("TOYPROJECT")
        .build();
      decodedJWT = verifier.verify(token);

      boolean isExpired = decodedJWT.getExpiresAt().before(decodedJWT.getExpiresAt());
      //토큰 발급시간 체크'
      boolean isIssuedAt = decodedJWT.getIssuedAt().before(decodedJWT.getIssuedAt());
      if (isExpired && isIssuedAt) {
        return responseService.getFailResult(500, "Expired or unIssued Token");
      }

      return responseService.getSingleResult(decodedJWT);
    } catch (JWTVerificationException exception) {
      log.info(exception.getMessage(), (Object) exception.getStackTrace());

      return responseService.getFailResult(500, "Invalid signature/claims");
      // Invalid signature/claims
    }
  }

  public CommonResult getPubKey() throws IOException {
    byte[] keyBytes = Files.readAllBytes(Paths.get("public_key.pem"));
    String publicKey = new String(keyBytes, StandardCharsets.UTF_8);
    return responseService.getSingleResult(publicKey);
  }


//
//    public CommonResult login(UserDTO userDto) {
//      HashMap<Object, Object> resultMap = new HashMap<>();
//      userTableRepository.findByuserId(userDto.getUserId()).ifPresentOrElse(userEntity -> {
//          log.info("userEntity: {}", userEntity.toString());
//          if (userEntity.getUserPass().equals(userDto.getUserPasswd())) {
//  //          resultMap.put("Result", TRUE + "로그인 성공");
//            try {
//              String token = createJWT(userEntity.getUserId(), userEntity.getAuth());
//              resultMap.put("Result", token);
//            } catch (Exception e) {
//              e.printStackTrace();
//            }
//          } else {
//            resultMap.put("Result", FALSE + "비밀번호가 일치하지 않습니다.");
//          }
//        },
//        () -> {
//          resultMap.put("Result", FALSE + "해당 아이디는 존재하지 않습니다.");
//        });
//
//      return responseService.getSingleResult(resultMap);
//    }
//
//    public CommonResult create(UserDTO userDto) {
//      HashMap<Object, Object> resultMap = new HashMap<>();
//
//      userTableRepository.findByuserId(userDto.getUserId()).ifPresentOrElse(userEntity -> {
//          log.info("userEntity: {}", userEntity.toString());
//          resultMap.put("Result", FALSE + "해당 아이디는 이미 존재하는 아이디입니다.");
//        },
//        () -> {
//          userTableRepository.save(UserEntity.builder()
//            .userId(userDto.getUserId())
//            //TODO 비밀번호 암호화
//            .userPass(userDto.getUserPasswd())
//            .build());
//          resultMap.put("Result", "회원가입 완료");
//        });
//
//      return responseService.getSingleResult(resultMap);
//    }
//  //jwt 토큰 생성
//  public String createJWT(String userId,String auth) throws Exception {
//
//  // Load the private key
//    byte[] keyBytes = Files.readAllBytes(Paths.get("private_key.pem"));
//    PemReader pemReader = new PemReader(new StringReader(new String(keyBytes)));
//    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pemReader.readPemObject().getContent());
//    PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
//    String tokenId = UUID.randomUUID().toString();
//    String jws = Jwts.builder()
//      .setId(tokenId)
//      .setIssuer("Toy")
//      .setSubject("jwtAccess")
//      .claim("userID", userId)
//      .claim("auth",auth)
//  //    .claim("scope", "admins")
//      //현재 시간
//      .setIssuedAt(Date.from(Instant.now()))
//      // 만료시간은 한달
//      .setExpiration(Date.from(Instant.now().plusSeconds(60*60*24)))
//  //      .setExpiration(Date.from(Instant.now().plusSeconds(60 * 60 * 24 * 30)))
//      .signWith(SignatureAlgorithm.RS256, privateKey)
//      .compact();
//    //TODO
//    // tokenId를 redis에 저장
//    log.info("jws: {}", jws);
//    return jws;
//  }
}

