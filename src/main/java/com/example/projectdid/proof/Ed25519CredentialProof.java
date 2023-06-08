package com.example.projectdid.proof;

import com.example.projectdid.RSA.CreateKeyPair;
import com.example.projectdid.did.PrivateKey;
import com.example.projectdid.did.PublicKey;
import com.example.projectdid.did.DidPubKey;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.example.projectdid.vc.CredentialSubject;
import com.example.projectdid.vc.DidVerifiableCredentialBase;
import com.example.projectdid.vc.DidVerifiableCredentialJsonProperties;
import com.example.projectdid.vc.VCDocument;
import com.google.common.hash.Hashing;
import com.google.gson.*;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.threeten.bp.Instant;
import org.web3j.abi.datatypes.Bool;
import org.web3j.crypto.Sign;

import javax.json.Json;
import java.lang.annotation.ElementType;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * packageName   : com.example.projectdid.vc
 * fileName  : Ed25519CredentialProof
 * author    : jiseung-gu
 * date  : 2023/01/09
 * description :
 **/
public class Ed25519CredentialProof extends proofBase{
    public static final String PROOF_TYPE = "Ed25519Signature2018";
    public static final String VC_VERIFICATION_METHOD = "Ed25519Signature2018";
    public static final String VC_PROOF_PURPOSE = "authentication";
    private static final String[] JSON_PROPERTIES_ORDER = {"type", "creator", "created", "domain", "nonce",
            "proofPurpose", "verificationMethod", "proofValue"};
    private static final String JSON_PROPERTY_JWS = "proofValue";
    private static final CreateKeyPair key = new CreateKeyPair();

    /**
     * Constructs a new proof document - without signature.
     *
     * @param issuerDid DID of a credential issuer.
     */
    public Ed25519CredentialProof(final String issuerDid) {
        this(issuerDid, null, null);
    }

    /**
     * Constructs a new proof document - without signature.
     *
     * @param issuerDid DID of a credential issuer.
     * @param nonce     The variable nonce.
     */
    public Ed25519CredentialProof(final String issuerDid, final String nonce) {
        this(issuerDid, null, nonce);
    }

    /**
     * Constructs a new proof document - without signature.
     *
     * @param issuerDid DID of a credential issuer.
     * @param domain    The domain.
     * @param nonce     The variable nonce.
     */
    public Ed25519CredentialProof(final String issuerDid, final String domain, final String nonce) {
        setType(PROOF_TYPE);
        setProofPurpose(VC_PROOF_PURPOSE);
        setVerificationMethod(issuerDid + DidPubKey.DID_ROOT_KEY_NAME);
        setCreator(issuerDid);
        setCreated(Instant.now());
        setDomain(domain);
        setNonce(nonce);
    }

    /**
     * Creates a signing input in JWS form.
     *
     * @param header       The JWS header.
     * @param signingInput The signing input.
     * @return The singing input in JWS form.
     */
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

    /**
     * Note: this is a manual implementation of ordered JSON items.
     * In a real-world application it is recommended to use a JSON-LD compatible library to handle normalization.
     * However at this point the only available one in Java support JSON-LD version 1.0, but 1.1 is required by W3C
     * Verifiable Credentials.
     *
     * @param withoutSignature Will skip signature value ('jwk' attribute) if True.
     * @return A normalized JSON string representation of this proof.
     */
    public JsonElement toNormalizedJsonElement(final boolean withoutSignature) {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();

        // First turn to normal JSON
        JsonObject root = gson.toJsonTree(this).getAsJsonObject();
        // Then put JSON properties in ordered map
        LinkedHashMap<String, JsonElement> map = new LinkedHashMap<>();

        for (String property : JSON_PROPERTIES_ORDER) {
            if (JSON_PROPERTY_JWS.equals(property) && withoutSignature) {
                continue;
            } else if (root.has(property)) {
                map.put(property, root.get(property));
            }
        }
        // Turn map to JSON
        return gson.toJsonTree(map);
    }

    /**
     * Creates a linked data proof of type
     * Implementation is a simplified version for this example based on
     * https://github.com/WebOfTrustInfo/ld-signatures-java/.
     *
     * @param signingKey     Private key of the signing subject.
     * @param documentToSign The canonicalized JSON string of a verifiable credential document.
     */
    public void sign(final PrivateKey signingKey, final java.security.PrivateKey signingRSAKey, final String documentToSign) throws Exception {
        // normalizedProof = proof에 들어가는 json 형태  type,creator,created,proofPurPose, VerificationMethod, proofValue
        String normalizedProof = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create().toJson(toNormalizedJsonElement(true));

        //        byte[] inputForSigning = new byte[64];
        byte[] inputForSigning = inputForSigning(documentToSign,normalizedProof);

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.EdDSA).customParam("b64", Boolean.FALSE)
                .criticalParams(Collections.singleton("b64")).build();
        byte[] jwsSigningInput = getJwsSigningInput(jwsHeader, inputForSigning);
        if (signingKey==null) {
            System.out.println("signingKey==null");
            System.out.println(Base64.getEncoder().encodeToString(jwsSigningInput));
            System.out.println(String.valueOf(jwsSigningInput));
            String signature = key.sign((Base64.getEncoder().encodeToString(jwsSigningInput)),signingRSAKey);
            System.out.println("signature"+ signature);
            setJws(jwsHeader.toBase64URL().toString() + '.' + '.' + signature.toString());
        }else {
            Base64URL signature = Base64URL.encode(signingKey.sign(jwsSigningInput));
            setJws(jwsHeader.toBase64URL().toString() + '.' + '.' + signature.toString());

        }

//        unsign(signingKey.getPublicKey(),signature,jwsSigningInput);

    }
    public static JsonElement getPropertyJson(String VcText, String property) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(VcText);

        if(property !=null ) {
            JsonElement propertyData = element.getAsJsonObject().get(property);
            return propertyData;
        }
//        if(property.equals(DidVerifiableCredentialJsonProperties.PROOF)) {
//            propertyData.getAsJsonObject().remove(JSON_PROPERTY_JWS);
//        }
        return element;
    }
    // signTextd와 message는 VC문서 통으로 받아서 분리해서 만들기로 함 주석 처리
//    public void unsign(PublicKey publicKey, Base64URL signText,byte[] message) {

      public Boolean unsign(PublicKey publicKey, java.security.PublicKey RsaPublicKey, String VcText) throws Exception {

          System.out.println("====unsign Start====");
          System.out.println(VcText);
          Gson gson = new GsonBuilder()
                  .disableHtmlEscaping()
                  .excludeFieldsWithoutExposeAnnotation()
                  .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                  .create();

          //일단 서명되어있는 VC를 둘로 나누어야함
          JsonElement halfOfVC = getPropertyJson(VcText,DidVerifiableCredentialJsonProperties.PROOF);
          JsonElement SignText = getPropertyJson(halfOfVC.toString(),JSON_PROPERTY_JWS);
          halfOfVC.getAsJsonObject().remove("proofValue");
          JsonElement VCAll = getPropertyJson(VcText,null);

//          JsonObject root = gson.toJsonTree(VcText).getAsJsonObject();
          LinkedHashMap<String, JsonElement> map = new LinkedHashMap<>();
//          JsonArray credentialSubjectsArray = new JsonArray();

          for(String property : VCDocument.JSON_PROPERTIES_ORDER) {
                if(DidVerifiableCredentialJsonProperties.PROOF.equals(property)) {
                    System.out.println(DidVerifiableCredentialJsonProperties.PROOF+" EXCEPTION");
                    continue;
                }
                else if (VCAll.getAsJsonObject().get(property) != null){
                    map.put(property,VCAll.getAsJsonObject().get(property));
                }
                else {
                    System.out.println(property);
                    System.out.println("null value exist");
                }
          }
          // 전달 받은 VC문서를 Sign과 비교하기 위해서 만듬

          byte[] inputForSigning = inputForSigning(gson.toJson(map),halfOfVC.toString());
          JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.EdDSA).customParam("b64", Boolean.FALSE)
                  .criticalParams(Collections.singleton("b64")).build();
          byte[] jwsSigningInput = getJwsSigningInput(jwsHeader, inputForSigning);

          // 서명 비교
          Base64URL signTobyte = new Base64URL(SignSubString(SignText.toString()));
          if(publicKey == null) {
              return key.verify(Base64.getEncoder().encodeToString(jwsSigningInput) ,Base64.getEncoder().encodeToString(signTobyte.decode()),RsaPublicKey);
          }else{
              return publicKey.verify(jwsSigningInput,signTobyte.decode());
          }

//          System.out.println(Ed25519.verify(signTobyte.decode(), 0, publicKey.toBytes(), 0, jwsSigningInput, 0, jwsSigningInput.length));
    }
    //앞에는 JWSheader가 붙어있고 뒤에가 실제 Sign 값이다. | 가운데 .. 까지 제외 한 이후 값을 리턴
    public String SignSubString(String VcText) {

        return VcText.substring(VcText.lastIndexOf('.')+1,VcText.length()-1);
    }

    //문자열을 나눠서 하나로 만드는 (sign)할때 사용하는 부분을 공통함수로 뺐음
    public byte[] inputForSigning(String documentToSign, String normalizedProof) {
        byte[] inputForSigning = new byte[64];

        byte[] normalizedDocHash = Hashing.sha256().hashBytes(documentToSign.getBytes(StandardCharsets.UTF_8))
                .asBytes();
        byte[] normalizedProofHash = Hashing.sha256().hashBytes(normalizedProof.getBytes(StandardCharsets.UTF_8))
                .asBytes();
        System.arraycopy(normalizedProofHash, 0, inputForSigning, 0, 32);
        System.arraycopy(normalizedDocHash, 0, inputForSigning, 32, 32);

        return inputForSigning;
    }
}
