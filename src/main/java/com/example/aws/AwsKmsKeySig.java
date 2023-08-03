package com.example.aws;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.*;
import com.amazonaws.util.Base64;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * packageName   : com.example.aws
 * fileName  : AwsKmsKeySig
 * author    : jiseung-gu
 * date  : 2023/07/27
 * description :
 **/
public class AwsKmsKeySig {
  private static final String KEY_ID = "arn:aws:kms:ap-northeast-2:905126260776:key/b4a93930-1651-4ff7-9ad1-009d1a88f2e4";
  private static final String KEY_SPEC = "RSA_4096";
  private static AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

  public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
//    GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
//    dataKeyRequest.setKeyId(KEY_ID);
//    dataKeyRequest.setKeySpec(KEY_SPEC);
//    GenerateDataKeyResult dataKeyResult = kmsClient.generateDataKey(dataKeyRequest);

    String message = "Message to be signed";
    ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));

    SignRequest signRequest = new SignRequest();
    signRequest.setKeyId(KEY_ID);
    signRequest.setMessage(messageBuffer);
    signRequest.setSigningAlgorithm("RSASSA_PKCS1_V1_5_SHA_256");
    signRequest.setMessageType("RAW"); // RAW or DIGEST, depending on whether you want AWS to hash the message before signing

    SignResult signResult = kmsClient.sign(signRequest);
    String signature = Base64.encodeAsString(signResult.getSignature().array());

    System.out.println("Signature: " + signature);


    verify(signature);
    System.out.println("검증 후 ");
    getPublicKey(signature);
  }
  public static void verify(String signature) {
    try {
      String message = "Message to be signed";
      ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));

//    String signature = "Base64 encoded signature here"; // replace with the actual signature
      ByteBuffer signatureBuffer = ByteBuffer.wrap(Base64.decode(signature));

      VerifyRequest verifyRequest = new VerifyRequest();
      verifyRequest.setKeyId(KEY_ID);
      verifyRequest.setMessage(messageBuffer);
      verifyRequest.setSignature(signatureBuffer);
      verifyRequest.setMessageType("RAW");
      verifyRequest.setSigningAlgorithm("RSASSA_PKCS1_V1_5_SHA_256");


      VerifyResult verifyResult = kmsClient.verify(verifyRequest);

      System.out.println("Signature valid: " + verifyResult.isSignatureValid());
    }catch (Exception e){
      throw new SecurityException("Signature validation failed.");
    }
  }
  public static void getPublicKey(String signature) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
    GetPublicKeyRequest getPublicKeyRequest = new GetPublicKeyRequest();
    getPublicKeyRequest.setKeyId(KEY_ID);
    GetPublicKeyResult getPublicKeyResult = kmsClient.getPublicKey(getPublicKeyRequest);
    ByteBuffer publicKey = getPublicKeyResult.getPublicKey();
    byte[] publicKeyBytes = new byte[publicKey.remaining()];
    publicKey.get(publicKeyBytes);
    System.out.println("Public key: " + publicKey);

    // 공개키 객체를 생성합니다.
    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    PublicKey pubkey = kf.generatePublic(spec);
    System.out.println("pubkey : " + Base64.encodeAsString(pubkey.getEncoded()));
// 서명을 검증합니다.
    Signature publicSignature = Signature.getInstance("SHA256withRSA");
    publicSignature.initVerify(pubkey);
    publicSignature.update("Message to be signed".getBytes(StandardCharsets.UTF_8));
    ByteBuffer signatureBuffer = ByteBuffer.wrap(Base64.decode(signature));

    boolean isVerified = publicSignature.verify(signatureBuffer.array());
    System.out.println("Signature verified: " + isVerified);
  }
}

