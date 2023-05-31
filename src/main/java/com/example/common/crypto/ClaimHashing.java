package com.example.common.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
/**
 * packageName   : com.example.common.crypto
 * fileName  : ClaimHashing
 * author    : jiseung-gu
 * date  : 2023/04/11
 * description :
 **/
public class ClaimHashing {

  public static void main(String[] args) {
    String salt ="QluX/rv09PSbPKWXlzXVhw==";
    String hashed = hashWithSalt("지승구", salt);

    System.out.println("hashed: " + hashed);
  }
  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  public static String hashWithSalt(String input, String salt) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      String inputWithSalt = input + salt;
      byte[] inputBytes = inputWithSalt.getBytes(StandardCharsets.UTF_8);
      byte[] hashedBytes = md.digest(inputBytes);
      return Base64.getEncoder().encodeToString(hashedBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not found", e);
    }
  }
}
