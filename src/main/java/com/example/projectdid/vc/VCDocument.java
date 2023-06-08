package com.example.projectdid.vc;

import com.example.projectdid.proof.Ed25519CredentialProof;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.threeten.bp.Instant;

import java.util.*;

/**
 * packageName   : com.example.projectdid.vc
 * fileName  : VCDocument
 * author    : jiseung-gu
 * date  : 2023/01/09
 * description :    VC 문서 양식
 **/
@Getter
@Setter
public class VCDocument extends  DidVerifiableCredentialBase<CredentialSubject>{
    public static final String CREDENTIAL_SCHEMA_TYPE = "JsonSchemaValidator2018";
    private static final String DOCUMENT_TYPE = "AccessCredential";
    private static final String EXAMPLE_ID_PREFIX = "https://example.appnet.com/Access/";
    private static final String JSON_PROPERTY_CREDENTIAL_SUBJECT = "credentialSubject";
    private static final String JSON_PROPERTY_PROOF = "proof";
    public static final String[] JSON_PROPERTIES_ORDER = {"@context", "id", "type", "credentialSchema",
            "credentialSubject", "issuer", "issuanceDate","expirationDate","validFrom","validUntil", "proof"};
//    @Expose
//    private CredentialSchema credentialSchema;


//   Proof 작성 클래스 VC에 마지막으로 proof 적용
    @Expose
    private Ed25519CredentialProof proof;

    @Expose
    private JsonArray  textMap;
    @Expose
    private JsonArray  saltMap;


//  VCDocument의 id 와 type 즉, VC 식별자와 무슨 유형의 VC인지 알수 있어야하므로 정의 되어 있어야함 .
    public VCDocument() {
        super();
        // VC 타입
        addType(DOCUMENT_TYPE);

        // VC 설정
        this.id= EXAMPLE_ID_PREFIX + UUID.randomUUID();
    }


    public void setProof(final Ed25519CredentialProof proof) {
        this.proof = proof;
    }
    public String toNormalizedJson(final boolean withoutProof) {
        //Gson 설명 :
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();

        // First turn to normal JSON
        // toJonTree형태로 만들때 @SerializedName에 정해진 이름으로 만든다.
        // 즉 클래스안에 @SerializedName 중복으로 사용하면 되어있으면 오류
        JsonObject root = gson.toJsonTree(this).getAsJsonObject();
        // Then put JSON properties in ordered map
        LinkedHashMap<String, JsonElement> map = new LinkedHashMap<>();

        JsonArray credentialSubjectsArray = new JsonArray();
        //Properties에 들어가는 문자를 JSON_PROPERTIES_ORDER 배열로 만든 이유
        for (String property : JSON_PROPERTIES_ORDER) {
          if (JSON_PROPERTY_CREDENTIAL_SUBJECT.equals(property) && getCredentialSubject() != null) {
            for (CredentialSubject dl : getCredentialSubject()) {
              //toNormalizedJsonElement 설명 :
              credentialSubjectsArray.add(dl.toNormalizedJsonElement());
            }
            map.put(property, credentialSubjectsArray);
          } else if (JSON_PROPERTY_PROOF.equals(property) && withoutProof) {
            continue;
          } else if (root.has(property)) {
            map.put(property, root.get(property));
          }
        }
        // Turn map to JSON
        return gson.toJson(map);
    }
    public HashMap<String,JsonElement> toNormalizedJson2(final boolean withoutProof) {
        //Gson 설명 :
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();

        // First turn to normal JSON
        // toJonTree형태로 만들때 @SerializedName에 정해진 이름으로 만든다.
        // 즉 클래스안에 @SerializedName 중복으로 사용하면 되어있으면 오류
        JsonObject root = gson.toJsonTree(this).getAsJsonObject();
        // Then put JSON properties in ordered map
        LinkedHashMap<String, JsonElement> map = new LinkedHashMap<>();

        JsonArray credentialSubjectsArray = new JsonArray();
        //Properties에 들어가는 문자를 JSON_PROPERTIES_ORDER 배열로 만든 이유
        for (String property : JSON_PROPERTIES_ORDER) {
            if (JSON_PROPERTY_CREDENTIAL_SUBJECT.equals(property) && getCredentialSubject() != null) {
                for (CredentialSubject dl : getCredentialSubject()) {
                    //toNormalizedJsonElement 설명 :
//                    credentialSubjectsArray.add(dl.toNormalizedJsonElement());
                  credentialSubjectsArray.add(dl.toNormalizedJsonElement());
                }
              map.put(property, credentialSubjectsArray);

            } else if (JSON_PROPERTY_PROOF.equals(property) && withoutProof) {
                continue;
            } else if (root.has(property)) {
//                System.out.println("property :"+property);

                map.put(property, root.get(property));
            }
        }
        // Turn map to JSON
        return map;
    }
}
