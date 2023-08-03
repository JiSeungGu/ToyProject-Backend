package com.example.relay.service;

import com.example.contract.NewSmartContractABI;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.threeten.bp.Instant;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static com.example.common.crypto.ClaimHashing.hashWithSalt;

/**
 * packageName   : com.example.relay.service
 * fileName  : VerifyService
 * author    : jiseung-gu
 * date  : 2023/04/25
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class RelayhardhatService {

//  Web3j web3 = Web3j.build(new HttpService("http://www.fufuanfox.com:8545"));
  Web3j web3 = Web3j.build(new HttpService("http://www.fufuanfox.com"));
  private final String contractAddress = "0x850EC3780CeDfdb116E38B009d0bf7a1ef1b8b38";
  private final String NewcontractAddress = "0xC5888275e0a1ca13a26463318105957aa4d1feD7";
  @Value("${contract.key}")
  private String privateKey;

  public JsonElement getPropertyJson(String VcText, String property) {
    JsonParser parser = new JsonParser();
    JsonElement element = parser.parse(VcText);

    if(property !=null ) {
      JsonElement propertyData = element.getAsJsonObject().get(property);
      return propertyData;
    }

    return element;
  }


  //SOURCE_DESC VC hash값 가져오기
  public String getHashToContract(String holderDid) throws Exception {
    try{
      System.out.println("privateKey"+privateKey);
      Credentials credentials = Credentials.create(privateKey);

      TransactionManager transactionManager = new RawTransactionManager(web3, credentials);

      NewSmartContractABI contract = NewSmartContractABI.load(
        NewcontractAddress,
        web3,
        transactionManager,
        new DefaultGasProvider()
      );
      String hash = contract.getVcHash(holderDid).send();

      log.info("getHashToContract Success");
      return hash;
    }catch (Exception e) {
      log.info("getHashToContract Fail"+e.getMessage());
      return null;
    }

  }
    // 스마트 컨트랙트 자동 생성 클래스의 인스턴스 생성
  //SOURCE_DESC Document조회 후 Publickey 가져오기
  public String getPubFromContract(String holderdid) throws Exception {
    System.out.println("privateKey"+privateKey);

    //MY_THOUGHTS  Credentials 객체를 사용하여 TransactionManager 객체 생성  객체 빈을 생성하자마자 전역변수로 넣어주고싶었지만 application.yaml파일에서 빈을 생성할때 privateKey를 전역변수로 값을 가져오면 null값을가져오게 됨
    Credentials credentials = Credentials.create(privateKey);

    TransactionManager transactionManager = new RawTransactionManager(web3, credentials);

    // 스마트 컨트랙트 자동 생성 클래스의 인스턴스 생성
//    SmartContractABI contract = SmartContractABI.load(
    NewSmartContractABI contract = NewSmartContractABI.load(
      NewcontractAddress,
      web3,
      transactionManager,
      DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT
    );
    // getDid 메서드 호출
    String Document  = contract.getDID(holderdid).send();
    log.info("Document : " + Document);

    //SOURCE_DESC Document에서 pubKey만 추출
    JsonElement pubKey = getPropertyJson(Document, "publicKey");
    log.info("publickey Test : "+pubKey.getAsJsonArray().get(0).getAsJsonObject().get("publicKeyBase58").getAsString());

    String pubKeyBase58 = pubKey.getAsJsonArray().get(0).getAsJsonObject().get("publicKeyBase58").getAsString();
    return pubKeyBase58;
  }
  public boolean verifyCredential(String credential) throws NoSuchAlgorithmException {
    boolean result = false;
    JsonObject jsonObject = JsonParser.parseString(credential).getAsJsonObject();

    JsonObject verifiableCredential = extractData(jsonObject,"verifiableCredential");
    JsonObject credentialSubject = extractData(verifiableCredential,"credentialSubject");
    JsonObject credentialText = extractData(jsonObject,"credentialText");
    JsonObject credentialSalt = extractData(jsonObject,"credentialSalt");

    Type type = new TypeToken<Map<String, String>>(){}.getType();

    Map<String, String> credentialSaltMap = new Gson().fromJson(credentialSalt,type );
    Map<String, String> credentialTextMap = new Gson().fromJson(credentialText,type );
    Map<String, String> credentialSubjectMap = new Gson().fromJson(credentialSubject,type );

    for (Map.Entry<String, String> entry : credentialTextMap.entrySet()) {
      String saltValue = credentialSaltMap.get(entry.getKey());
      String hashedValue = hashWithSalt(entry.getValue(), saltValue);

      if (credentialSubjectMap.get(entry.getKey() ).equals(hashedValue)) {
        log.info(entry.getKey()  + " is true");
      } else {
        log.info(entry.getKey()  + " is false");
      }
    }
    return result;
  }
  public JsonObject extractData(JsonObject object, String purpose) {
    return object.getAsJsonArray(purpose).get(0).getAsJsonObject();
  }

  public JsonObject convertToJson(String string) {

    Gson gson = new GsonBuilder()
      .disableHtmlEscaping()
      .excludeFieldsWithoutExposeAnnotation()
      .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
      .create();
    return gson.fromJson(string,JsonObject.class);
  }

  public String hash(String input) {
    try {
      // SHA-256 메시지 다이제스트 인스턴스 생성
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      // 입력 문자열을 바이트 배열로 변환
      byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

      // 바이트 배열을 해싱
      byte[] hashBytes = digest.digest(inputBytes);

      // 해싱된 바이트 배열을 16진수 문자열로 변환
      StringBuilder stringBuilder = new StringBuilder();
      for (byte b : hashBytes) {
        stringBuilder.append(String.format("%02x", b));
      }

      return stringBuilder.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 알고리즘이 지원되지 않습니다.", e);
    }
  }
}
