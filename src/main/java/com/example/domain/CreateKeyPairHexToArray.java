package com.example.domain;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * packageName   : com.example.domain
 * fileName  : CreateKeyPairHexToArray
 * author    : jiseung-gu
 * date  : 2023/01/25
 * description :
 **/
public class CreateKeyPairHexToArray {

    private static final int DEFAULT_KEY_SIZE = 2048;

    private static final String KEY_FACTORY_ALGORITHM = "RSA";

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private static final String CHARSET = "UTF-8";
    public static void main(String[] args) throws Exception {

//        for (Map.Entry<String, String> entrySet : rsaKeyPair.entrySet()) {
//            System.out.println(entrySet.getKey() + " : " + entrySet.getValue());
//        }


        KeyPair keyPair = genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String publicKey1 = new String(byteArrayToHex(publicKey.getEncoded()));
        String privateKey1 = new String(byteArrayToHex(privateKey.getEncoded()));

        System.out.println(publicKey1);
        System.out.println(privateKey1);
        System.out.println(publicKey.getFormat());
        System.out.println(privateKey.getFormat());
        System.out.println("--");
        System.out.println(encode(publicKey,"HI"));
        String decode = encode(publicKey,"HI");
        System.out.println(decode(privateKey,decode));
    }

    public static String encode(PublicKey publicKey,String toEncode) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] bytes = cipher.doFinal(toEncode.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(bytes));
    }
    public static String decode(PrivateKey privateKey,String toDecode) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(toDecode));
        return new String(bytes);

    }

    public void String2PublicKey(String publicKeyStr) {

        KeyFactory keyFactory = null;
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(hexToByteArray(publicKeyStr));
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(ukeySpec);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void String2PrivateKey(String privateKeyStr){

        try{
            PKCS8EncodedKeySpec rkeySpec = new PKCS8EncodedKeySpec(hexToByteArray(privateKeyStr));
            KeyFactory rkeyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = null;
            privateKey = rkeyFactory.generatePrivate(rkeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // hex string to byte[]
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }
        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }
    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {

            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);

        String hexNumber = "";

        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));

        }
        return sb.toString();
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);
        generator.initialize(DEFAULT_KEY_SIZE);
        KeyPair pair = generator.generateKeyPair();
        return pair;
    }
    public static KeyPair genRSAKeyPair() throws NoSuchAlgorithmException {

        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator gen;
        gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(1024, secureRandom);
        KeyPair keyPair = gen.genKeyPair();
        return keyPair;
    }
}
