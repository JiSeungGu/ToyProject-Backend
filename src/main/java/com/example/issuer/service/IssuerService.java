package com.example.issuer.service;

import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.entity.UserEntity;
import com.example.issuer.dto.IssuerDTO;
import com.example.projectdid.DidDocumentBase;
import com.example.projectdid.did.PrivateKey;
import com.example.projectdid.did.Did;
import com.example.projectdid.proof.Ed25519CredentialProof;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.example.projectdid.vc.*;
import com.example.repository.IssuerTableRepository;
import com.example.repository.UserTableRepository;
import com.google.bitcoin.core.Base58;
import com.google.common.hash.Hashing;
import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.threeten.bp.Instant;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static com.example.common.crypto.ClaimHashing.hashWithSalt;
import static com.example.common.crypto.PasswordHashing.*;
import static java.lang.Boolean.FALSE;

/**
 * packageName   : com.example.issuer.service
 * fileName  : IssuerService
 * author    : jiseung-gu
 * date  : 2023/03/27
 * description :
 **/
//SOURCE_DESC
//MY_THOUGHTS
//COMPLETED
//PLANNED
//TODO
@Slf4j
@Service
@RequiredArgsConstructor
public class IssuerService {

  private final ResponseService responseService;
  private final UserTableRepository userTableRepository;
  private final IssuerTableRepository issuerTableRepository;
  private final IssuerhardhatService issuerhardhatService;
  @Value("${issuer.name}")
  private String issuerName;


  public CommonResult VcCreate(String holderid) throws Exception, PrivateKey.BadKeyException {
    Gson gson = new GsonBuilder()
      .disableHtmlEscaping()
      .excludeFieldsWithoutExposeAnnotation()
      .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
      .create();
    Map<String,String> issuerinfo = new HashMap<>();
//    String issuerDid = "3dMT3qwyrubqubDvDDF8CH7xTLtSEEV74N31CBM3Nq1i";
//    String issuerpk = "302e020100300506032b65700422042047f1a92ec0191e0dfb1603618ee23a955074a940c74cd00a5452999742595877";

    issuerTableRepository.findByissuerName(issuerName).ifPresent(IssuerEntity -> {
      issuerinfo.put("issuerpk",IssuerEntity.getIssuerPk());
      issuerinfo.put("issuerDid",IssuerEntity.getIssuerDid());
    });

    VCDocument vcDocument = new VCDocument();
    vcDocument.setIssuer(issuerinfo.get("issuerDid"));

    Instant instant = Instant.now().plusSeconds(32400);
    vcDocument.setIssuanceDate(instant);
    vcDocument.setValidFrom(instant);
    vcDocument.setValidUntil(instant.plusSeconds(2592000));

    //CredentialSubject Hash 으로 데이터를 가져올 까 했지만 결국 Map에 담아서 내보내기로 결정해서 안씀
    CredentialSubjectData credentialSubjectData = new CredentialSubjectData();

    //SOURCE_DESC : Holder ID로 회원정보를 가져온다.
    log.info("Holder ID : {}", holderid);
    UserEntity user = getUserInfo(holderid);


    Map<String, String> hashedValues = new HashMap<>();
    Map<String, String> inputs = new HashMap<>();
    inputs.put("userId", user.getUserId());
    inputs.put("userName", user.getUserName());
    inputs.put("userPhoneno", user.getUserPhoneNo());
    inputs.put("userStatus", user.getStatus());
    inputs.put("userRegdate", user.getRegDate().toString());

    Map<String, String> saltMap = new HashMap<>();

    for (Map.Entry<String, String> entry : inputs.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      String salt = generateSalt();
      String hashedValue = hashWithSalt(value, salt);
      hashedValues.put(key, hashedValue);
      saltMap.put(key, salt);
    }

    vcDocument.addCredentialSubject(
      new CredentialSubject(
        hashedValues.get("userId"), hashedValues.get("userName"), hashedValues.get("userPhoneno"), hashedValues.get("userStatus"), hashedValues.get("userRegdate")
    ));

    PrivateKey VCPrivateKey = PrivateKey.fromString(issuerinfo.get("issuerpk"));

    Ed25519CredentialProof proof = new Ed25519CredentialProof(issuerinfo.get("issuerDid"));
    proof.sign(VCPrivateKey,null, vcDocument.toNormalizedJson(true));

    vcDocument.setProof(proof);
    JsonParser parser = new JsonParser();

    //SOURCE_DESC : VC Document를 Hashing 후 블록체인 저장.
    String vcHash = issuerhardhatService.hash(vcDocument.toNormalizedJson(false));
    issuerhardhatService.setHashToContract(holderid,vcHash);

    //SOURCE_DESC : VC Document 생성 후 SaltMap, TextMap을 합쳐서 출력.
    JsonElement result = combineTextAndSalt(parser.parse(vcDocument.toNormalizedJson(false)),saltMap,inputs);

    log.info("Final VC Document :"+result.toString());

    return responseService.getSingleResult(result);
  }
  public JsonElement combineTextAndSalt(JsonElement document, Map<String, String> saltMap, Map<String, String> textMap) {
    Gson gson = new GsonBuilder()
      .disableHtmlEscaping()
      .excludeFieldsWithoutExposeAnnotation()
      .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
      .create();

    JsonParser parser = new JsonParser();
    JsonElement element = parser.parse(document.toString());
    JsonObject test = element.getAsJsonObject();

    List<Object> credentialTextList = new ArrayList<>();
    List<Object> credentialSaltList = new ArrayList<>();
    credentialTextList.add(textMap);
    credentialSaltList.add(saltMap);

    test.add("credentialText",gson.toJsonTree(credentialTextList));
    test.add("credentialSalt",gson.toJsonTree(credentialSaltList));
    log.info(test.toString());
    return test;
  }

  public UserEntity getUserInfo(String userId) {
    return userTableRepository.findByuserId(userId).orElseThrow(
      () -> new IllegalArgumentException("해당 사용자가 없습니다. id=" + userId));
  }
}
