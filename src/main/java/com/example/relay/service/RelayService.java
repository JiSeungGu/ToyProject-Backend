package com.example.relay.service;

import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.contract.SmartContractABI;
import com.example.contract.NewSmartContractABI;
import com.example.entity.UserEntity;
import com.example.projectdid.did.PrivateKey;
import com.example.projectdid.did.PublicKey;
import com.example.projectdid.proof.Ed25519CredentialProof;
import com.example.projectdid.proof.Ed25519PresentationProof;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.example.projectdid.vc.CredentialSubject;
import com.example.projectdid.vc.CredentialSubjectData;
import com.example.projectdid.vc.VCDocument;
import com.example.relay.dto.RelayDTO;
import com.example.repository.UserTableRepository;
import com.google.bitcoin.core.Base58;
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
import org.json.simple.parser.JSONParser;
import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.example.common.crypto.ClaimHashing.hashWithSalt;
import static com.example.common.crypto.PasswordHashing.generateSalt;

/**
 * packageName   : com.example.issuer.service
 * fileName  : IssuerService
 * author    : jiseung-gu
 * date  : 2023/03/27
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class RelayService {

  private final ResponseService responseService;
  private final UserTableRepository userTableRepository;
  private final RelayhardhatService relayhardhatService;

  @Transactional(rollbackOn = Exception.class)
  public CommonResult hashCompare(RelayDTO relayDTO) throws Exception, PrivateKey.BadKeyException {
    log.info(relayDTO.getHolderid());
    log.info(relayDTO.getVp());
    HashMap resultMap = new HashMap<>();
    relayhardhatService.verifyCredential(relayDTO.getVp());
    return responseService.getSingleResult(resultMap);
  }

  public CommonResult verify(RelayDTO relayDTO) throws Exception, PrivateKey.BadKeyException {
    log.info(relayDTO.toString());
    JsonParser parser = new JsonParser();
    //SOURCE_DESC Credential 검증
    relayhardhatService.verifyCredential(relayDTO.getVp());

    //SOURCE_DESC VC Hash 검증 - 1. VC Hash를 가져온다.
    String hash = relayhardhatService.getHashToContract(relayDTO.getHolderid());

    //SOURCE_DESC VC Hash 검증 - 2. 전달 받은 VC Hash를 가져온다.
    JsonObject vcDocument = relayhardhatService.extractData(
      relayhardhatService.convertToJson(relayDTO.getVp())
      , "verifiableCredential");
    String vcHash = relayhardhatService.hash(vcDocument.toString());

    //SOURCE_DESC VC Hash 검증 - 3. VC Hash를 비교한다.
    if (hash.equals(vcHash)) {
      log.info("vcHash True");
    } else log.info("vcHash False");

    //SOURCE_DESC 서명 검증 - 1.holder의 DID를 가져온다
    String holderDid = relayDTO.getHolderid();
    UserEntity user = userTableRepository.findByuserId(holderDid).orElseThrow(() -> new Exception("해당 DID가 존재하지 않습니다."));
    log.info("userDID : "+user.getUserDid());

    //SOURCE_DESC 서명 검증 - 2.holder의 DID로 Hardhat에 접근하여 해당 DID의 PublicKey를 가져온다.
    String publickeyBase58 = relayhardhatService.getPubFromContract(user.getUserDid());
    byte[] publicKeyBytes = Base58.decode(publickeyBase58);
    PublicKey publicKey = PublicKey.fromBytes(publicKeyBytes);

    //SOURCE_DESC 서명 검증 - 3.holder Pubkey로 검증
    Ed25519PresentationProof VPproof = new Ed25519PresentationProof(holderDid,"99612b24-63d9-11ea-b99f-4f66f3e4f81a");
    boolean verify = VPproof.unsign(publicKey,relayDTO.getVp());
    log.info("검증 결과 "+verify);
    return responseService.getSingleResult(verify);
  }
}
