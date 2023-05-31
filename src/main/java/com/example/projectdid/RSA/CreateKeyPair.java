package com.example.projectdid.RSA;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CreateKeyPair {

    private static final int DEFAULT_KEY_SIZE = 2048;

    private static final String KEY_FACTORY_ALGORITHM = "RSA";
//    private static final String KEY_FACTORY_ALGORITHM = "SHA256withRSA";

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private static final String CHARSET = "UTF-8";
    public static void main(String[] args) throws Exception {
        CreateKeyPair key = new CreateKeyPair();


        KeyPair keyPair = key.genRSAKeyPair();
        java.security.PublicKey publicKey = keyPair.getPublic();
        java.security.PrivateKey privateKey = keyPair.getPrivate();

        String base64PublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("Base64 Public Key : " + base64PublicKey);

        byte[] bytePrivateKey = privateKey.getEncoded();
        String base64PrivateKey = Base64.getEncoder().encodeToString(bytePrivateKey);
        System.out.println("Base64 Private Key : " + base64PrivateKey);

        System.out.println(publicKey.getFormat());
        System.out.println(privateKey.getFormat());
        java.security.PrivateKey prKey = key.getPrivateKeyFromBase64String(base64PrivateKey);
        java.security.PublicKey puKey = key.getPublicKeyFromBase64String(base64PublicKey);

        String signMessage = key.sign("VPSignature",privateKey);
        System.out.println(signMessage);

        System.out.println(
                key.verify("VPSignature",signMessage,publicKey)
        );
    }
    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        System.out.println(encryptCipher.getBlockSize());
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String sign(String plainText, java.security.PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        System.out.println("plainText.getBytes(length)"+plainText.getBytes(UTF_8).length);
        privateSignature.update(plainText.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }
    public boolean verify(String plainText, String signature, java.security.PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }
    /**
     * Base64 엔코딩된 개인키 문자열로부터 PrivateKey객체를 얻는다.
     * @param keyString
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public PrivateKey getPrivateKeyFromBase64String(final String keyString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        final String privateKeyString =
                keyString.replaceAll("\\n",  "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecPKCS8 =
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));

        return keyFactory.generatePrivate(keySpecPKCS8);
    }


    /**
     * Base64 엔코딩된 공용키키 문자열로부터 PublicKey객체를 얻는다.
     * @param keyString
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public PublicKey getPublicKeyFromBase64String(final String keyString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        final String publicKeyString =
                keyString.replaceAll("\\n",  "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec keySpecX509 =
                new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));

        return keyFactory.generatePublic(keySpecX509);
    }
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);
        generator.initialize(DEFAULT_KEY_SIZE);
        KeyPair pair = generator.generateKeyPair();
        return pair;
    }
    public KeyPair genRSAKeyPair() throws NoSuchAlgorithmException {

        SecureRandom secureRandom = new SecureRandom();
        System.out.println("secureRandom :"+ secureRandom.toString());
        KeyPairGenerator gen;
        gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(1024, secureRandom);
        KeyPair keyPair = gen.genKeyPair();
        return keyPair;
    }

}