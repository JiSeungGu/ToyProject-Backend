//package com.example.common.crypto;
//
//
////import com.example.projectdid.did.PrivateKey;
////import com.example.projectdid.did.PublicKey;
//import org.abstractj.kalium.crypto.Box;
//import org.abstractj.kalium.crypto.Random;
//import org.abstractj.kalium.keys.PrivateKey;
//import org.abstractj.kalium.keys.PublicKey;
//import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
//import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
//import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
//import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
//
///**
// * packageName   : com.example.common.crypto
// * fileName  : Ed25519Encryption
// * author    : jiseung-gu
// * date  : 2023/04/11
// * description :
// **/
//public class Ed25519Encryption {
//  public static void main(String[] args) {
//    // Generate an Ed25519 key pair for Alice
//    Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
//    keyPairGenerator.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
//    org.bouncycastle.crypto.AsymmetricCipherKeyPair aliceKeyPair = keyPairGenerator.generateKeyPair();
//    Ed25519PublicKeyParameters aliceEdPublicKey = (Ed25519PublicKeyParameters) aliceKeyPair.getPublic();
//    Ed25519PrivateKeyParameters aliceEdPrivateKey = (Ed25519PrivateKeyParameters) aliceKeyPair.getPrivate();
//
//    // Convert Alice's Ed25519 keys to Curve25519 keys
//    PublicKey aliceCurvePublicKey = new PublicKey(aliceEdPublicKey.convertToEncodedPoint(false));
//    PrivateKey aliceCurvePrivateKey = new PrivateKey(aliceEdPrivateKey.convertToSecret().getEncoded());
//
//    // Generate an Ed25519 key pair for Bob
//    keyPairGenerator.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
//    org.bouncycastle.crypto.AsymmetricCipherKeyPair bobKeyPair = keyPairGenerator.generateKeyPair();
//    Ed25519PublicKeyParameters bobEdPublicKey = (Ed25519PublicKeyParameters) bobKeyPair.getPublic();
//    Ed25519PrivateKeyParameters bobEdPrivateKey = (Ed25519PrivateKeyParameters) bobKeyPair.getPrivate();
//
//    // Convert Bob's Ed25519 keys to Curve25519 keys
//    PublicKey bobCurvePublicKey = new PublicKey(bobEdPublicKey.convertToEncodedPoint(false));
//    PrivateKey bobCurvePrivateKey = new PrivateKey(bobEdPrivateKey.convertToSecret().getEncoded());
//
//    // Alice wants to send an encrypted message to Bob
//    String message = "Hello, World!";
//    byte[] encryptedMessage = encrypt(aliceCurvePrivateKey, bobCurvePublicKey, message.getBytes());
//
//    // Bob decrypts the message
//    byte[] decryptedMessage = decrypt(bobCurvePrivateKey, aliceCurvePublicKey, encryptedMessage);
//
//    System.out.println("Decrypted message: " + new String(decryptedMessage));
//  }
//
//  public static byte[] encrypt(PrivateKey privateKey, PublicKey publicKey, byte[] message) {
//    Box box = new Box(publicKey, privateKey);
//    byte[] nonce = new Random().randomBytes(Box.NONCE_SIZE);
//    return box.encrypt(nonce, message);
//  }
//  public static byte[] decrypt(PrivateKey privateKey, PublicKey publicKey, byte[] encryptedMessage) {
//    Box box = new Box(publicKey, privateKey);
//    byte[] nonce = new byte[Box.NONCE_SIZE];
//    System.arraycopy(encryptedMessage, 0, nonce, 0, Box.NONCE_SIZE);
//    byte[] cipherText = new byte[encryptedMessage.length - Box.NONCE_SIZE];
//    System.arraycopy(encryptedMessage, Box.NONCE_SIZE, cipherText, 0, cipherText.length);
//    return box.decrypt(nonce, cipherText);
//  }
//}
