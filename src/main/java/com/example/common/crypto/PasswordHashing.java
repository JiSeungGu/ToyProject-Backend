package com.example.common.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * packageName   : com.example.common.crypto
 * fileName  : PasswordHashing
 * author    : jiseung-gu
 * date  : 2023/03/31
 * description :
 **/
public class PasswordHashing {
  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);

    return toHex(salt);
  }

  public static String hashPassword(String password, String userId,  String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
    // Get the first character of the user ID
    char firstChar = userId.charAt(0);
    // Convert the first character to a hexadecimal string
    String firstCharHex = Integer.toHexString(firstChar);

    // Combine the salt with the first character's hexadecimal representation
    String combinedSalt = firstCharHex + salt;
    KeySpec spec = new PBEKeySpec(password.toCharArray(), fromHex(combinedSalt), 65536, 128);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] hash = factory.generateSecret(spec).getEncoded();
    return toHex(hash) + salt;
  }

  public static String extractSalt(String hashedPassword) {
    int saltLength = 32; // 16바이트의 솔트는 16진수로 표현했을 때 32자리입니다.
    return hashedPassword.substring(hashedPassword.length() - saltLength);
  }
  public static String toHex(byte[] array) {
    BigInteger bi = new BigInteger(1, array);
    return String.format("%0" + (array.length << 1) + "x", bi);
  }

  public static byte[] fromHex(String hex) {
    byte[] bytes = new byte[hex.length() / 2];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return bytes;
  }

}
