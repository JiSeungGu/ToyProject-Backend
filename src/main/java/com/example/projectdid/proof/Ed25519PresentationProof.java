package com.example.projectdid.proof;

import com.example.projectdid.did.PrivateKey;
import com.example.projectdid.did.PublicKey;
import com.example.projectdid.did.DidPubKey;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.example.projectdid.vc.DidVerifiableCredentialJsonProperties;
import com.example.projectdid.vc.VCDocument;
import com.example.projectdid.vp.DidVpDocumentJsonProperties;
import com.example.projectdid.vp.VPDocument;
import com.google.common.hash.Hashing;
import com.google.gson.*;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;
import org.threeten.bp.Instant;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * packageName   : com.example.projectdid.proof
 * fileName  : Ed25519PresentationProof
 * author    : jiseung-gu
 * date  : 2023/01/12
 * description :
 **/
public class Ed25519PresentationProof extends VPproofBase {
    public static final String PROOF_TYPE = "Ed25519Signature2018";
    private static final String VP_PROOF_PURPOSE = "authentication";
    private static final String[] JSON_PROPERTIES_ORDER = {"type", "created", "proofPurpose",
            "verifiableMethod", "challenge", "domain", "proofValue"};
    private static final String JSON_PROPERTY_JWS = "proofValue";
    public Ed25519PresentationProof(final String holderDid,final String challenge) {
//        this(challenge, null);
        setType(PROOF_TYPE);
        setCreated(Instant.now());
        setProofPurpose(VP_PROOF_PURPOSE);
        setVerifiableMethod(holderDid+ DidPubKey.DID_ROOT_KEY_NAME);
        setChallenge(challenge);
        setDomain("No idear");
    }

    /**
     * Creates a signing input in JWS form.
     *
     * @param header       The JWS header.
     * @param signingInput The signing input.
     * @return The singing input in JWS form.
     */
    // proofValue에 들어가는 Jws header를 구현하기 위한 메소드
    private static byte[] getJwsSigningInput(final JWSHeader header, final byte[] signingInput) {
        byte[] encodedHeader;

        if (header.getParsedBase64URL() != null) {
            encodedHeader = header.getParsedBase64URL().toString().getBytes(StandardCharsets.UTF_8);
        } else {
            encodedHeader = header.toBase64URL().toString().getBytes(StandardCharsets.UTF_8);
        }

        byte[] jwsSigningInput = new byte[encodedHeader.length + 1 + signingInput.length];
        System.arraycopy(encodedHeader, 0, jwsSigningInput, 0, encodedHeader.length);
        jwsSigningInput[encodedHeader.length] = (byte) '.';
        System.arraycopy(signingInput, 0, jwsSigningInput, encodedHeader.length + 1, signingInput.length);

        return jwsSigningInput;
    }
    // Proof값 문자열 변환
    public JsonElement toNormalizedJsonElement(final boolean withoutSignature) {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();

        JsonObject root = gson.toJsonTree(this).getAsJsonObject();

        LinkedHashMap<String, JsonElement> map = new LinkedHashMap<>();

        for(String property : JSON_PROPERTIES_ORDER) {
            if(JSON_PROPERTY_JWS.equals(property) && withoutSignature) {
                continue;
            }else if (root.has(property)) {
                map.put(property, root.get(property));
            }
        }

        return gson.toJsonTree(map);
    }
    //서명
    public void sign(final PrivateKey signingKey, final String documentToSign) {
        //이 부분이 VP의 해당하는 proof 부분만 / proofValue는 같이 껴있지 않음
        String normalizedProof = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create().toJson(toNormalizedJsonElement(false));

        //VC에 존재하는 속성, proof까지 포함한 데이터 내용이 VP sign 에서는 documentToSign이 된다.
        System.out.println("documentToSign : "+documentToSign);
        System.out.println("normalizedProof : "+normalizedProof);
        byte[] inputForSigning = inputForSigning(documentToSign.toString(),normalizedProof);

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.EdDSA).customParam("b64", Boolean.FALSE)
                .criticalParams(Collections.singleton("b64")).build();
        byte[] jwsSigningInput = getJwsSigningInput(jwsHeader, inputForSigning);

        Base64URL signature = Base64URL.encode(signingKey.sign(jwsSigningInput));
        setProofValue(jwsHeader.toBase64URL().toString() + '.' + '.' + signature.toString());
    }
    public static JsonElement getPropertyJson(String VcText, String property) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(VcText);

        if(property !=null ) {
            JsonElement propertyData = element.getAsJsonObject().get(property);
            return propertyData;
        }

        return element;
    }
    public Boolean unsign(PublicKey publicKey, String VcText) {
        System.out.println("**********unsign Start**********");
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();

        //일단 서명되어있는 VC를 둘로 나누어야함
        JsonElement halfOfVC = getPropertyJson(VcText, DidVpDocumentJsonProperties.PROOF);
        JsonElement SignText = getPropertyJson(halfOfVC.toString(),JSON_PROPERTY_JWS);
        halfOfVC.getAsJsonObject().remove("proofValue");
        JsonElement VCAll = getPropertyJson(VcText,null);
        System.out.println("halfOfVC  :"+halfOfVC); //verifiableMethod ***이게 여기에는 있고 위에 sig에는 없음
        System.out.println("SignText  :"+SignText);
        System.out.println("Substring :"+SignSubString(SignText.toString()));
        System.out.println("VPALL     :"+VCAll);

        LinkedHashMap<String, JsonElement> map = new LinkedHashMap<>();
        //Proof를 제외한 데이터만 사용
        for(String property : VPDocument.JSON_PROPERTIES_ORDER) {
            if(DidVerifiableCredentialJsonProperties.PROOF.equals(property)) {
                System.out.println(DidVerifiableCredentialJsonProperties.PROOF+" EXCEPTION");
                continue;
            }
            else if (VCAll.getAsJsonObject().get(property) != null){
                map.put(property,VCAll.getAsJsonObject().get(property));
            }
            else {
                System.out.println("null value exist" +property);
            }
        }
        System.out.println("map : "+ gson.toJson(map));
        // 전달 받은 VC문서를 Sign과 비교하기 위해서 만듬
        byte[] inputForSigning = inputForSigning(gson.toJson(map),halfOfVC.toString());
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.EdDSA).customParam("b64", Boolean.FALSE)
                .criticalParams(Collections.singleton("b64")).build();
        byte[] jwsSigningInput = getJwsSigningInput(jwsHeader, inputForSigning);

        // 서명 비교
        Base64URL signTobyte = new Base64URL(SignSubString(SignText.toString()));
        return publicKey.verify(jwsSigningInput,signTobyte.decode());
//          System.out.println(Ed25519.verify(signTobyte.decode(), 0, publicKey.toBytes(), 0, jwsSigningInput, 0, jwsSigningInput.length));
    }
    //앞에는 JWSheader가 붙어있고 뒤에가 실제 Sign 값이다. | 가운데 .. 까지 제외 한 이후 값을 리턴
    public static String SignSubString(String VcText) {

        return VcText.substring(VcText.lastIndexOf('.')+1,VcText.length()-1);
    }
    //문자열을 나눠서 하나로 만드는 (sign)할때 사용하는 부분을 공통함수로 뺐음
    public byte[] inputForSigning(String documentToSign, String normalizedProof) {
        byte[] inputForSigning = new byte[64];

        byte[] normalizedDocHash = Hashing.sha256().hashBytes(documentToSign.getBytes(StandardCharsets.UTF_8))
                .asBytes();

        System.out.println("normalizedDocHash   :" + Base64.getEncoder().encodeToString(normalizedDocHash));
        byte[] normalizedProofHash = Hashing.sha256().hashBytes(normalizedProof.getBytes(StandardCharsets.UTF_8))
                .asBytes();
        System.out.println("normalizedProofHash :"+Base64.getEncoder().encodeToString(normalizedProofHash));
        System.arraycopy(normalizedProofHash, 0, inputForSigning, 0, 32);
        System.arraycopy(normalizedDocHash, 0, inputForSigning, 32, 32);

        return inputForSigning;
    }
}
