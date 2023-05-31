package com.example.projectdid.did;


import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.encoders.Hex;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;


public final class PrivateKey {
    private final byte[] keyData;
    static final ASN1ObjectIdentifier ID_ED25519 = new ASN1ObjectIdentifier("1.3.101.112");

    @Nullable
    private final KeyParameter chainCode;

    // Cache the derivation of the public key
    @Nullable
    private PublicKey publicKey;
    public byte[] toBytes() {
      return keyData;
    }
    public PrivateKey(byte[] keyData, @Nullable KeyParameter chainCode) {
        this.keyData = keyData;
        this.chainCode = chainCode;
    }
    public PublicKey getPublicKey() {
        if (publicKey != null) {
            return publicKey;
        }

        byte[] publicKeyData = new byte[Ed25519.PUBLIC_KEY_SIZE];
        Ed25519.generatePublicKey(keyData, 0, publicKeyData, 0);

        publicKey = new PublicKey(publicKeyData);
        return publicKey;
    }
    public String toString() {
        return Hex.toHexString(toDER());
    }

    private byte[] toDER() {
        try {
            return new PrivateKeyInfo(
                    new AlgorithmIdentifier(ID_ED25519),
                    new DEROctetString(keyData)
            ).getEncoded("DER");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static PrivateKey fromString(String privateKey) throws BadKeyException {
        return fromBytes(Hex.decode(privateKey));
    }

    public static PrivateKey fromBytes(byte[] privateKey) throws BadKeyException {
        if ((privateKey.length == Ed25519.SECRET_KEY_SIZE)
                || (privateKey.length == Ed25519.SECRET_KEY_SIZE + Ed25519.PUBLIC_KEY_SIZE)) {
            // If this is a 32 or 64 byte string, assume an Ed25519 private key
            return new PrivateKey(Arrays.copyOfRange(privateKey, 0, Ed25519.SECRET_KEY_SIZE), null);
        }

        // Assume a DER-encoded private key descriptor
        return PrivateKey.fromPrivateKeyInfo(PrivateKeyInfo.getInstance(privateKey));
    }
    private static PrivateKey fromPrivateKeyInfo(PrivateKeyInfo privateKeyInfo) throws BadKeyException {
        try {
            var privateKey = (ASN1OctetString) privateKeyInfo.parsePrivateKey();

            return new PrivateKey(privateKey.getOctets(), null);
        } catch (IOException e) {
            throw new BadKeyException(e);
        }
    }

    //예외 처리도 따로 만들어서 사용하는구나 여기는 어휴
    public static class BadKeyException extends Throwable {
        public BadKeyException(String message) { super(message); }

        BadKeyException(Throwable cause) {
            super(cause);
        }
    }
    public byte[] sign(byte[] message) {
        byte[] signature = new byte[Ed25519.SIGNATURE_SIZE];
        Ed25519.sign(keyData, 0, message, 0, message.length, signature, 0);

        return signature;
    }
}
