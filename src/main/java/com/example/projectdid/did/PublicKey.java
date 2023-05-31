package com.example.projectdid.did;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.math.ec.rfc8032.Ed25519;

public final class PublicKey {
    private final byte[] keyData;

    PublicKey(byte[] keyData) {
        this.keyData = keyData;
    }
    public byte[] toBytes() {
        return keyData;
    }
    public boolean verify(byte[] message, byte[] signature) {
        return Ed25519.verify(signature,0, keyData, 0, message, 0, message.length);
    }
    public static PublicKey fromBytes(byte[] publicKey) {
        if (publicKey.length == Ed25519.PUBLIC_KEY_SIZE) {
            // If this is a 32 byte string, assume an Ed25519 public key
            return new PublicKey(publicKey);
        }

        // Assume a DER-encoded private key descriptor
        return PublicKey.fromSubjectKeyInfo(SubjectPublicKeyInfo.getInstance(publicKey));
    }
    private static PublicKey fromSubjectKeyInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        return new PublicKey(subjectPublicKeyInfo.getPublicKeyData().getBytes());
    }
}
