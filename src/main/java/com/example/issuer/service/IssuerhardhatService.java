package com.example.issuer.service;

import com.example.contract.NewSmartContractABI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * packageName   : com.example.issuer.service
 * fileName  : hardhatService
 * author    : jiseung-gu
 * date  : 2023/04/27
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class IssuerhardhatService {

  Web3j web3 = Web3j.build(new HttpService("http://www.fufuanfox.com:8545"));
  private final String contractAddress = "0x850EC3780CeDfdb116E38B009d0bf7a1ef1b8b38";
  private final String NewcontractAddress = "0x1ACcBD355245AbA93CE46D33ab1D0152CE33Fd00";
  // privateKey를 사용하여 Credentials 객체 생성
  @Value("${contract.key}")
  private String privateKey;


  //SOURCE_DESC VC hash값 가져오기
  public void setHashToContract(String holderDid,String vcHash) throws Exception {
    try {
      Credentials credentials = Credentials.create(privateKey);

      TransactionManager transactionManager = new RawTransactionManager(web3, credentials);

      NewSmartContractABI contract = NewSmartContractABI.load(
        NewcontractAddress,
        web3,
        transactionManager,
        new DefaultGasProvider()
      );
      contract.setVcHash(holderDid,vcHash).send();

      log.info("setHashToContract Success");

    } catch (Exception e) {
      log.info("setHashToContract Fail");
    }
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
