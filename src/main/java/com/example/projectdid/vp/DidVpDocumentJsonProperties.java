package com.example.projectdid.vp;

/**
 * packageName   : com.example.projectdid.vp
 * fileName  : DidVpDocumentJsonProperties
 * author    : jiseung-gu
 * date  : 2023/01/12
 * description :
 **/
public class DidVpDocumentJsonProperties {
    public static final String FIRST_CONTEXT_ENTRY = "https://www.w3.org/2018/credentials/v1";

    public static final String CONTEXT = "@context";

    public static final String ID = "id";

    public static final String TYPE = "type";
    public static final String VERIFIABLE_PRESENTATION_TYPE =  "VerifiablePresentation";

    public static final String VERIFIABLECREDENTIAL = "verifiableCredential";

    public static final String PROOF = "proof";

    //SOURCE_DESC  CredentialText , CredentialSalt 추가.
    public static final String CREDENTIALTEXT = "credentialText";
    public static final String CREDENTIALSALT = "credentialSalt";
}
