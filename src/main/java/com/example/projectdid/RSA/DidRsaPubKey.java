package com.example.projectdid.RSA;

import com.google.bitcoin.core.Base58;
import com.google.gson.annotations.Expose;
import lombok.Setter;

import java.security.PublicKey;

/**
 * packageName   : com.example.projectdid.RSA
 * fileName  : DidRsaPubKey
 * author    : jiseung-gu
 * date  : 2023/01/25
 * description :
 **/

/*
    Document에 publikey안에 들어가는 세부 내용이라 가정
    pubkey,
    did공개키위치,
    controller,
    인증 알고리즘 타입
 */
@Setter
public class DidRsaPubKey {

    public static final String DID_ROOT_KEY_NAME = "#did-owner-key";
    public static final String DID_ROOT_KEY_TYPE = "Ed25519VerificationKey2018";

    //@Expose
    //  object 중 해당 값이 null일 경우, json으로 만들 필드를 자동 생략해 준다.
    //@Expose(serialize = true ) 일때 객체가 Json 문자열로 변환될 때, 해당 필드가 포함됩니다.
    //@Expose(deserialize = true ) 일때 Json 문자열이 객체로 변환될 때, 해당 필드가 포함됩니다.
    @Expose(serialize = true, deserialize = true)
    private String id;

    @Expose(serialize = true, deserialize = true )
    private String type;

    @Expose(serialize = true, deserialize = true )
    private String controller;

    @Expose(serialize = true, deserialize = true )
    private String publicKeyBase58;


    public String getId() {
        return id;
    }

    public static DidRsaPubKey fromIdentity(final RsaDid did, final PublicKey didRootKey) {
        if (did == null) {
            throw new IllegalArgumentException("DID cannot be null");
        }

        if (didRootKey == null) {
            throw new IllegalArgumentException("DID root key cannot be null");
        }

        // Validate if hcsIdentity is derived from the given root key
//        System.out.println("did.getIdString()) :"+did.getIdString());
        if (!RsaDid.publicKeyToIdString(didRootKey).equals(did.getIdString())) {
            throw new IllegalArgumentException("The specified DID does not correspond to the given DID root key");
        }
//        System.out.println("Base58.encode(didRootKey.toBytes()) :"+Base58.encode(didRootKey.toBytes()));
//        System.out.println("didRootKey.toBytes() :"+didRootKey.toBytes());

        DidRsaPubKey result = new DidRsaPubKey();
        result.controller = did.toDid();
        System.out.println(result.getId());
        result.id = result.controller + DID_ROOT_KEY_NAME;
        System.out.println("Base58.encode(didRootKey.getEncoded()) RSA Working");
        System.out.println(didRootKey.getEncoded().toString());
        System.out.println(Base58.encode(didRootKey.getEncoded()));
        result.publicKeyBase58 = Base58.encode(didRootKey.getEncoded());
        result.type = DID_ROOT_KEY_TYPE;

        return result;
    }
}
