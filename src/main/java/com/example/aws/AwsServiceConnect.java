package com.example.aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class AwsServiceConnect {
  private static String Seoul_Region = "ap-northeast-2";
  private static String KEYARN = "arn:aws:kms:ap-northeast-2:905126260776:key/2af69298-b2ea-476a-8636-a9c039f16159";
  private static AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();
  private static AmazonS3 s3 = AmazonS3ClientBuilder.standard()
    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
      "https://s3.ap-northeast-2.amazonaws.com",
      Seoul_Region))
    .withCredentials(new ProfileCredentialsProvider())
    .build();
  private static final byte[] EXAMPLE_DATA = "Ji Seung Gu - AWS Encryption SDK".getBytes(StandardCharsets.UTF_8);
  private static final String KEY_ID = "alias/privateproject";
  private static final String KEY_SPEC = "AES_256";
  public static final AwsCrypto crypto = AwsCrypto.standard();

  //ToyProject 버킷 이름
  private static final String BUCKET_NAME = "pupuproject";


  public static void main(String[] args) throws IOException, ParseException {
    KMS_create_DataKEY();
    // * S3에 저장된 암호화된 private 가져오기 (byte형태로 가져오기 때문에 new String(...) 형태
    byte[] jsonStringData = GetAwsS3Data("0x0C94A0E9E4a5A3e2829389b1572b8176209670c1");
    System.out.println("jsonStringData :" + jsonStringData);
    System.out.println("jsonStringData :" + new String(jsonStringData, StandardCharsets.UTF_8));
    System.out.println("jsonStringData :" + Base64.getEncoder().encodeToString(jsonStringData));
    String temp = new String(jsonStringData, StandardCharsets.UTF_8);
    JSONObject jsonObject = StringToJson(temp);
    System.out.println("jsObject1 :" + jsonObject.get("ciphertext"));

    // * String형태를 byte형태로 만들어서 복호화
    byte[] bytes = StringToByte(jsonObject.get("ciphertext"));

    // String temp2 = jsonObject.get("ETH").toString();
    // encryptAndDecrypt_DecryptionSDK(temp.getBytes(StandardCharsets.UTF_8),"ETH",temp2);
    encryptAndDecrypt_DecryptionSDK(bytes, "ETH", "wallet");
  }


  //toyproject - 해당 유저가 폴더 없을시 폴더 생성
  public static void createFolder(String folderName) {
    // Ensure the folder name ends with a '/'
    if (!folderName.endsWith("/")) {
      folderName += "/";
    }

    // Create a zero-byte object with the folder name as the key
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(0);

    InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
    PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, folderName, emptyContent, metadata);

    s3.putObject(putObjectRequest);
    log.info("Folder '" + folderName + "' created in bucket '" + BUCKET_NAME + "'.");
  }
  //toyproject - 해당 유저의 폴더가 있는지 없는지 검사
  // 폴더가 있으면 False , 없으면 True
  public static boolean folderExists(String folderName) {
    ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(BUCKET_NAME).withPrefix(folderName));

    return objectListing.getObjectSummaries().isEmpty();
  }

  //폴더 리스트 가져오기
  public static List<String> listFolders() {
    ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(BUCKET_NAME).withDelimiter("/");
    ListObjectsV2Result result;

    do {
      result = s3.listObjectsV2(request);

      for (String folder : result.getCommonPrefixes()) {
        System.out.println("Folder: " + folder);
      }

      request.setContinuationToken(result.getNextContinuationToken());
    } while (result.isTruncated());

    return result.getCommonPrefixes();
  }

  //폴더속 파일명 가져오기
public static List<String> listFiles(String folderName) {
    ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(BUCKET_NAME).withPrefix(folderName).withDelimiter("/");
    ListObjectsV2Result result;
    List<String> list = new ArrayList<>();
    do {
      result = s3.listObjectsV2(request);

      for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
        if(objectSummary.getKey().equals(folderName)) continue;

        System.out.println("File: " + objectSummary.getKey());
        list.add(objectSummary.getKey());
      }

      request.setContinuationToken(result.getNextContinuationToken());
    } while (result.isTruncated());


    return list;
  }
  //파일 다운로드
  public static byte[] downloadFile(String folderName, String fileName ) throws IOException {
    log.info("Downloading an object");
    log.info("fileName : " + fileName + " folderName : " + folderName);
    S3Object fullObject = s3.getObject(new GetObjectRequest(BUCKET_NAME, folderName+"/"+fileName));
    InputStream inputStream = fullObject.getObjectContent();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    int bytesRead;
    byte[] buffer = new byte[1024];
      while ((bytesRead = inputStream.read(buffer)) > 0) {
        byteArrayOutputStream.write(buffer, 0, bytesRead);
      }
    return byteArrayOutputStream.toByteArray();
  }
  //파일 업로드
  public static void uploadFile(String folderName, String fileName, File file) {
    s3.putObject(BUCKET_NAME, folderName +"/"+ fileName, file);
  }
  public static byte[] StringToByte(Object before) {
    String response = before.toString();
    String[] byteValues = response.substring(1, response.length() - 1).split(",");
    byte[] bytes = new byte[byteValues.length];

    for (int i = 0, len = bytes.length; i < len; i++) {
      bytes[i] = Byte.parseByte(byteValues[i].trim());
    }

    return bytes;
  }

  public static void test() {
//         Instantiate the SDK
//        final AwsCrypto crypto = AwsCrypto.standard();

    // Set up the master key provider
    final KmsMasterKeyProvider prov = KmsMasterKeyProvider.builder().buildStrict(KEYARN);

    // Set up the encryption context
    // NOTE: Encrypted data should have associated encryption context
    // to protect its integrity. This example uses placeholder values.
    // For more information about the encryption context, see
    // https://docs.aws.amazon.com/encryption-sdk/latest/developer-guide/concepts.html#encryption-context
    final Map<String, String> context = Collections.singletonMap("ExampleContextKey", "ExampleContextValue");

    // Encrypt the data
    //
    final CryptoResult<byte[], KmsMasterKey> encryptResult = crypto.encryptData(prov, EXAMPLE_DATA, context);
    final byte[] ciphertext = encryptResult.getResult();
    System.out.println("Ciphertext: " + Arrays.toString(ciphertext));
    System.out.println("ciphertext: " + ciphertext);

//        String response = "[-47, 1, 16, 84, 2, 101, 110, 83, 111, 109, 101, 32, 78, 70, 67, 32, 68, 97, 116, 97]";      // response from the Python script
    String response2 = Arrays.toString(ciphertext);      // response from the Python script
    String[] byteValues = response2.substring(1, response2.length() - 1).split(",");
    byte[] bytes = new byte[byteValues.length];

    for (int i = 0, len = bytes.length; i < len; i++) {
      bytes[i] = Byte.parseByte(byteValues[i].trim());
    }
    System.out.println("제발:" + Arrays.toString(bytes));
    System.out.println(bytes);

    // Decrypt the data
    final CryptoResult<byte[], KmsMasterKey> decryptResult = crypto.decryptData(prov, bytes);
    // Your application should verify the encryption context and the KMS key to
    // ensure this is the expected ciphertext before returning the plaintext
    if (!decryptResult.getMasterKeyIds().get(0).equals(KEYARN)) {
      throw new IllegalStateException("Wrong key id!");
    }

    // The AWS Encryption SDK may add information to the encryption context, so check to
    // ensure all of the values that you specified when encrypting are *included* in the returned encryption context.
    if (!context.entrySet().stream()
      .allMatch(e -> e.getValue().equals(decryptResult.getEncryptionContext().get(e.getKey())))) {
      throw new IllegalStateException("Wrong Encryption Context!");
    }

    // The data is correct, so return it.
    System.out.println("Decrypted: " + new String(decryptResult.getResult(), StandardCharsets.UTF_8));

  }

  public static JSONObject StringToJson(String data) throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(data);
    JSONArray jsonArray = (JSONArray) jsonObject.get("data");
    JSONObject temp = (JSONObject) jsonArray.get(0);

    System.out.println("jsonData  " + jsonObject.get("data"));

    return temp;
  }

  public static String encryptAndDecrypt_EncryptionSDK(byte[] PLAINTEXT, String Key, String Value) {

//        final AwsCrypto crypto = AwsCrypto.builder()
//                .withCommitmentPolicy(CommitmentPolicy.RequireEncryptAllowDecrypt)
//                .build();
    // 래핑키 지정
    final KmsMasterKeyProvider keyProvider = KmsMasterKeyProvider.builder().buildStrict(KEYARN);

    // 암호화 컨텍스트 = 로그 확인용, 올바른 암호화된 메시지의 암호를 해독하고 있는지 확인
    Map<String, String> encryptionContext = Collections.singletonMap(Key, Value);


    final CryptoResult<byte[], KmsMasterKey> encryptResult = crypto.encryptData(keyProvider,
      PLAINTEXT, encryptionContext);
    final byte[] ciphertext_byte = encryptResult.getResult();
    System.out.println("encryptResult.getMasterKeyIds() :" + encryptResult.getMasterKeyIds());
    System.out.println("encryptResult.getMasterKeys() :" + encryptResult.getMasterKeys());
    System.out.println("encryptResult.getEncryptionContext() :" + encryptResult.getEncryptionContext());
    System.out.println("encryptResult.getHeaders().getEncryptionContext() :" + encryptResult.getHeaders().getEncryptionContext());
    System.out.println("encryptResult.getHeaders().getEncryptionContextMap() :" + encryptResult.getHeaders().getEncryptionContextMap());
    //암호화된 텍스트 바이트 값
    System.out.println("2. CIPHERTEXT :" + ciphertext_byte);
    //암호화 컨텍스트
    System.out.println("3. ENCRYPT_CONTEXT :" + encryptResult.getEncryptionContext());

    String test = Base64.getEncoder().encodeToString(ciphertext_byte);
    System.out.println("Base64 : " + test);
    String test2 = new String(test.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    System.out.println("Base64 -> new String :" + test2);

    String ciphertext = Arrays.toString(ciphertext_byte);
    System.out.println(ciphertext);
    return ciphertext;
  }

  public static String encryptAndDecrypt_DecryptionSDK(byte[] CIPHERTEXT, String Key, String Value) {
    System.out.println("CIPHERTEXT :" + CIPHERTEXT);
    // 래핑키 지정
    final KmsMasterKeyProvider keyProvider = KmsMasterKeyProvider.builder().buildStrict(KEYARN);
    System.out.println("1");
    //암호화 컨텍스트
    Map<String, String> encryptionContext = Collections.singletonMap(Key, Value);
    System.out.println("2");
    //복호화
    final CryptoResult<byte[], KmsMasterKey> decryptResult = crypto.decryptData(keyProvider, CIPHERTEXT);
    System.out.println("3");
    //복호화 바이트 값
    System.out.println("4. DECRYPTRESULT :" + decryptResult.getResult());

    if (!encryptionContext.entrySet().stream()
      .allMatch(
        e -> e.getValue().equals(decryptResult.getEncryptionContext().get(e.getKey())))) {
      throw new IllegalStateException("Wrong Encryption Context!");
    }

    // 7. Verify that the decrypted plaintext matches the original plaintext
    assert Arrays.equals(decryptResult.getResult(), EXAMPLE_DATA);

    System.out.println("5. PLAIN_TEXT: " + new String(decryptResult.getResult(), StandardCharsets.UTF_8));

    return new String(decryptResult.getResult(), StandardCharsets.UTF_8);
  }

  public static byte[] GetAwsS3Data(String waddress) throws IOException {
    S3Object o = s3.getObject("privateproject-s3", waddress + ".json");
    S3ObjectInputStream s3io = o.getObjectContent();

    return s3io.readAllBytes();
  }

  public static void Aws_UsingS3(String waddress, String CIPHERTEXT, String Key, String Value) {
    List<Bucket> buckets = s3.listBuckets();
    System.out.println("Your {S3} buckets are:");
    for (Bucket b : buckets) {
      System.out.println("* " + b.getName());
    }
    JSONObject jsonObject = new JSONObject();
    JSONObject ResultJson = new JSONObject();
    jsonObject.put(Key, Value);
    jsonObject.put("ciphertext", CIPHERTEXT);
    JSONArray jsonArray = new JSONArray();
    jsonArray.add(jsonObject);
    ResultJson.put("data", jsonArray);

    s3.putObject("privateproject-s3", waddress + ".json", ResultJson.toString());
  }

  public static void KMS_create_DataKEY() {
    GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
    dataKeyRequest.setKeyId(KEY_ID);
    dataKeyRequest.setKeySpec(KEY_SPEC);

    GenerateDataKeyResult dataKeyResult = kmsClient.generateDataKey(dataKeyRequest);

    ByteBuffer plaintextKey = dataKeyResult.getPlaintext();
    //KMS CMK로 생성한 데이터 키
    System.out.printf(
      "Successfully generated an plaintextKey: %s%n",
      Base64.getEncoder().encodeToString(plaintextKey.array())
    );
    ByteBuffer encryptedKey = dataKeyResult.getCiphertextBlob();

    //KMS CMK로 생성한 암호화된 데이터 키
    System.out.printf(
      "Successfully generated an encrypted data key: %s%n",
      Base64.getEncoder().encodeToString(encryptedKey.array())
    );
  }

  public void KMS_Data_Key_Dec(ByteBuffer encriptedText) {
    System.out.println("KMS_Data_Key_Dec Start");
    System.out.println();

    DecryptRequest request = new DecryptRequest()
      .withKeyId(KEY_ID)
      .withCiphertextBlob(encriptedText);

    ByteBuffer plainText = kmsClient
      .decrypt(request)
      .getPlaintext();

    System.out.println(new String(plainText.array()));
  }

  public void KMS_Data_Key_enc() {
    System.out.println("KMS_Data_Key_enc Start");
    System.out.println();
    ByteBuffer plaintext = ByteBuffer.wrap(EXAMPLE_DATA);

    EncryptRequest req = new EncryptRequest()
      .withKeyId(KEY_ID)
      .withPlaintext(plaintext);

    ByteBuffer ciphertext = kmsClient
      .encrypt(req)
      .getCiphertextBlob();

    ByteBuffer ciphertextBlob = ciphertext;

    System.out.println("========================");
    System.out.println("encode Base 64 CipherTextBlob :" + new String(Base64.getEncoder().encodeToString(ciphertext.array())));
    System.out.println("========================");

    KMS_Data_Key_Dec(ciphertext);

  }

  // KMS 별칭 만들기
  public void KmsTest_MakeName() {

    // Create an alias for a KMS key
    //arn:aws:kms:ap-northeast-2:905126260776:key/2af69298-b2ea-476a-8636-a9c039f16159
    String aliasName = "alias/privateproject2";
    // Replace the following example key ARN with a valid key ID or key ARN
    String targetKeyId = "2af69298-b2ea-476a-8636-a9c039f16159";

    CreateAliasRequest req = new CreateAliasRequest().withAliasName(aliasName).withTargetKeyId(targetKeyId);
    kmsClient.createAlias(req);
  }
}
