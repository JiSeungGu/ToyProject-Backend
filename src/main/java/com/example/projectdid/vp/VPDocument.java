package com.example.projectdid.vp;

import com.example.projectdid.proof.Ed25519PresentationProof;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.threeten.bp.Instant;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * packageName   : com.example.projectdid.vp
 * fileName  : DidVpDocument
 * author    : jiseung-gu
 * date  : 2023/01/12
 * description :
 **/
public class VPDocument extends DidVerfiablePresentationBase{
    public static final String PRESENTATION_SIGNATURE_TYPE = "Ed25519VerificationKey2018";
    private static final String DOCUMENT_TYPE_PV = "SejongAccessPresentation";
    private static final String PRESENTATION_PROOFPURPOSE = "authentication";
    private static final String VP_EXAMPLE_ID_PREFIX = "https://example.appnet.com/SejongAccess/";
    public static final String[] JSON_PROPERTIES_ORDER = {"@context","id","type",
            "verifiableCredential","proof","credentialText","credentialSalt"};
    private static final String JSON_PROPERTY_PROOF = "proof";

    // Proof 작성 클래스
    // 기존 Ed25519CredentialProof는 생성자에  presentation과 다른 변수 값 주입, 객체 등의 상이함 이유로 새로 만들었음
    @Expose
    private Ed25519PresentationProof proof;

    public void setProof(final Ed25519PresentationProof proof) {
        this.proof = proof;
    }

    public VPDocument() {
        super();
        addType(DOCUMENT_TYPE_PV);
        this.id = VP_EXAMPLE_ID_PREFIX+UUID.randomUUID(); //식별자
    }
    public String toNormalizedJson(final boolean withoutProof) {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();

        JsonObject root = gson.toJsonTree(this).getAsJsonObject();

        LinkedHashMap<String, JsonElement> map = new LinkedHashMap<>();

        JsonArray presentationSubjectArray = new JsonArray();

        for (String Presenproperty : JSON_PROPERTIES_ORDER) {
            //
            if (JSON_PROPERTY_PROOF.equals(Presenproperty) && withoutProof) {
                continue;
            }
            //실수 List<JsonElement>에 들어가있는게 여러개  있을수 있으니 등록을 해줘야햠.
            else if (verifiableCredential.equals(Presenproperty) && getVerifiableCredential() !=null) {
                for(Object element : getVerifiableCredential()) {
                    presentationSubjectArray.add((JsonElement) element);
                }
            }
            else if (root.has(Presenproperty)) {
                map.put(Presenproperty, root.get(Presenproperty));
            }
        }
        return gson.toJson(map);
    }
}
