package com.example.projectdid.vc;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class DidVerifiableCredentialJsonProperties {
    // HashGraph 쪽 VC
    public static final String CONTEXT = "@context";
    public static final String FIRST_CONTEXT_ENTRY = "https://www.w3.org/2018/credentials/v1";

    public static final String ID = "id";
    public static final String CREDENTIAL_SUBJECT = "credentialSubject";
    public static final String TYPE = "type";
    public static final String VERIFIABLE_CREDENTIAL_TYPE = "VerifiableCredential";

    public static final String ISSUER = "issuer";

    public static final String ISSUANCE_DATE = "issuanceDate";
    //    public static final String EXPIRATION_DATE = "expirationDate";
    public static final String VALID_FROM_DATE = "validFrom";
    public static final String VALID_UNTIL_DATE = "validUntil";

//    public static final String CREDENTIAL_STATUS = "credentialStatus";
    public static final String PROOF = "proof";

}
