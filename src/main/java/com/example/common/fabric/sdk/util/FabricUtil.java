/*
 * SejongTelecom Core DEV Team
 */

package com.example.common.fabric.sdk.util;

import com.example.common.fabric.sdk.user.CAEnrollment;
import com.example.common.fabric.sdk.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

@Slf4j
public class FabricUtil {

  /** Serialize user(WRITE) */
  public static void writeUserContext(UserContext userContext) throws Exception {
    String directoryPath = "users/" + userContext.getAffiliation();
    String filePath = directoryPath + "/" + userContext.getName() + ".ser";
    File directory = new File(directoryPath);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    FileOutputStream file = new FileOutputStream(filePath);
    ObjectOutputStream out = new ObjectOutputStream(file);

    // Method for serialization of object
    out.writeObject(userContext);

    out.close();
    file.close();
  }

  /** Deserialize user (READ) */
  public static UserContext readUserContext(String affiliation, String username) throws Exception {
    String filePath = "users/" + affiliation + "/" + username + ".ser";
    File file = new File(filePath);
    if (file.exists()) {
      // Reading the object from a file
      FileInputStream fileStream = new FileInputStream(filePath);
      ObjectInputStream in = new ObjectInputStream(fileStream);

      // Method for deserialization of object
      UserContext uContext = (UserContext) in.readObject();

      in.close();
      fileStream.close();
      return uContext;
    }

    return null;
  }

  /** 키 및 인증서 파일에 등록 */
  public static Enrollment getEnrollment(
      String pkPath, String pkName, String certPath, String certName) {
    try {

      Enrollment enrollOrg1Admin = getEnrollment2(pkPath, pkName, certPath, certName);

      return enrollOrg1Admin;

    } catch (Exception e) {
      System.out.println("Enroll Err:" + e.toString());
    }
    return null;
  }

  /**
   * Create enrollment from key and certificate files.(키 및 인증서 파일에서 등록 생성)
   *
   * @param keyFolderPath
   * @param keyFileName
   * @param certFileName
   * @return
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws CryptoException
   */
  public static CAEnrollment getEnrollment2(
      String keyFolderPath, String keyFileName, String certFolderPath, String certFileName)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CryptoException {
    PrivateKey key = null;
    String certificate = null;
    InputStream isKey = null;
    BufferedReader brKey = null;

    //    System.out.println(keyFolderPath + keyFileName);
    //    System.out.println(certFolderPath + certFileName);
    try {

      isKey = new FileInputStream(keyFolderPath + File.separator + keyFileName);
      brKey = new BufferedReader(new InputStreamReader(isKey));
      StringBuilder keyBuilder = new StringBuilder();

      for (String line = brKey.readLine(); line != null; line = brKey.readLine()) {
        if (line.indexOf("PRIVATE") == -1) {
          keyBuilder.append(line);
        }
      }

      certificate = new String(Files.readAllBytes(Paths.get(certFolderPath, certFileName)));

      byte[] encoded = DatatypeConverter.parseBase64Binary(keyBuilder.toString());
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
      KeyFactory kf = KeyFactory.getInstance("EC");
      key = kf.generatePrivate(keySpec);

    } finally {
      isKey.close();
      brKey.close();
    }

    CAEnrollment enrollment = new CAEnrollment(key, certificate);
    return enrollment;
  }

  /** */
  public static void cleanUp() {
    String directoryPath = "users";
    File directory = new File(directoryPath);
    deleteDirectory(directory);
  }

  /** 폴더삭제 */
  public static boolean deleteDirectory(File dir) {
    if (dir.isDirectory()) {
      File[] children = dir.listFiles();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDirectory(children[i]);
        if (!success) {
          return false;
        }
      }
    }

    return dir.delete();
  }
}
