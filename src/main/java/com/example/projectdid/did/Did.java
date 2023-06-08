package com.example.projectdid.did;

import com.example.projectdid.DidDocumentBase;
import com.google.bitcoin.core.Base58;
import com.google.common.hash.Hashing;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.rfc8032.Ed25519;

import java.util.Arrays;

public class Did {

    private String idString;
    private String did;
    private PublicKey didRootKey;
    private PrivateKey privateDidRootKey;


    public String toString() {
        return did;
    }

    public String getIdString() {
        return idString;
    }

    public String toDid() {
        return toString();
    }
    public Did(final String network,final PublicKey did,PrivateKey pri) {
        this.didRootKey = did;
        this.idString = Did.publicKeyToIdString(did);
        this.did = buildDid();
        this.privateDidRootKey = pri;

    }
    public static PrivateKey generateDidRootKey() {
        byte[] data = new byte[Ed25519.SECRET_KEY_SIZE + 32];
        ThreadLocalSecureRandom.current().nextBytes(data);
        return derivableKey(data);
    }
    private static PrivateKey derivableKey(byte[] deriveData) {
        var keyData = Arrays.copyOfRange(deriveData, 0, 32);
        var chainCode = new KeyParameter(deriveData, 32, 32);
        return new PrivateKey(keyData, chainCode);
    }
    public DidDocumentBase generateDidDocument() {
        DidDocumentBase result = new DidDocumentBase(this.toDid());

        if (didRootKey != null) {
            DidPubKey rootKey = DidPubKey.fromIdentity(this, didRootKey);
            result.setDidRootKey(rootKey);
        }

        return result;
    }
    public static String publicKeyToIdString(final PublicKey didRootKey) {
        return Base58.encode(Hashing.sha256().hashBytes(didRootKey.toBytes()).asBytes());
    }
    public DidSyntax.Method getMethod() {
        return DidSyntax.Method.TOYPROJECT;
    }

    private String buildDid() {
        String methodNetwork = String.join(DidSyntax.DID_METHOD_SEPARATOR, getMethod().toString());

        StringBuilder sb = new StringBuilder()
                .append(DidSyntax.DID_PREFIX)
                .append(DidSyntax.DID_METHOD_SEPARATOR)
                .append(methodNetwork)
                .append(DidSyntax.DID_METHOD_SEPARATOR)
                .append(idString)
                .append(DidSyntax.DID_PARAMETER_SEPARATOR);
//                .append(methodNetwork)
//                .append(DidSyntax.DID_METHOD_SEPARATOR)
//                .append(MethodSpecificParameter.ADDRESS_BOOK_FILE_ID)
//                .append(DidSyntax.DID_PARAMETER_VALUE_SEPARATOR);
//                .append(addressBookFileId.toString());

        return sb.toString();
    }
}
