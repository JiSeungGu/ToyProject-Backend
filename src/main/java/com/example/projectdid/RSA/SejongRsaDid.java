package com.example.projectdid.RSA;

import com.example.projectdid.DidDocumentBase;

import com.example.projectdid.did.DidSyntax;
import com.example.projectdid.did.SejongDidPubKey;

import com.google.bitcoin.core.Base58;
import com.google.common.hash.Hashing;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.rfc8032.Ed25519;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class SejongRsaDid {

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
    public SejongRsaDid(final String network,final PublicKey did,PrivateKey pri) {
        this.didRootKey = did;
        this.idString = SejongRsaDid.publicKeyToIdString(did);
        this.did = buildDid();
        this.privateDidRootKey = pri;

    }

    public DidDocumentBase generateDidDocument() {
        DidDocumentBase result = new DidDocumentBase(this.toDid());

        if (didRootKey != null) {
            SejongDidRsaPubKey rootKey = SejongDidRsaPubKey.fromSejongIdentity(this, didRootKey);
            result.setDidRSARootKey(rootKey);
        }

        return result;
    }
    public static String publicKeyToIdString(final PublicKey didRootKey) {
        return Base58.encode(Hashing.sha256().hashBytes(didRootKey.getEncoded()).asBytes());
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
