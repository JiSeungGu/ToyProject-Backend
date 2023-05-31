package com.example.issuer;

import com.example.projectdid.did.PrivateKey;
import com.example.projectdid.proof.Ed25519CredentialProof;
import com.example.projectdid.vc.CredentialSubject;
import com.example.projectdid.vc.VCDocument;
import com.google.gson.JsonElement;
import org.threeten.bp.Instant;

/**
 * packageName   : com.example.issuer
 * fileName  : DIDService
 * author    : jiseung-gu
 * date  : 2023/04/10
 * description :
 **/
public class DIDService {

  public JsonElement VcCreate() throws Exception, PrivateKey.BadKeyException {

    //TODO VC 생성
    //일단 issuer 하드코딩 추후 디비에서 값 가져오기
    /*
        공개키  :mN5R54z5vPEHQgjOXpUhpCPyuHHVuKlSavSYZT6Qu9I=
        DID   :3dMT3qwyrubqubDvDDF8CH7xTLtSEEV74N31CBM3Nq1i
        개인키  :302e020100300506032b65700422042047f1a92ec0191e0dfb1603618ee23a955074a940c74cd00a5452999742595877
        publicKeyBase58 : BR5czSwfAmzbt8Hafz9PnKJ9SFUmuAF5pTEXdu12zZUC
     */
    String issuerDid = "3dMT3qwyrubqubDvDDF8CH7xTLtSEEV74N31CBM3Nq1i";
    String issuerpk = "302e020100300506032b65700422042047f1a92ec0191e0dfb1603618ee23a955074a940c74cd00a5452999742595877";

    VCDocument vcDocument = new VCDocument();
    vcDocument.setIssuer(issuerDid);

    Instant instant = Instant.now().plusSeconds(32400);
    vcDocument.setIssuanceDate(instant);
    vcDocument.setValidFrom(instant);
    vcDocument.setValidUntil(instant.plusSeconds(2592000));
    vcDocument.addCredentialSubject(
      //3. VC Subject 정의
      new CredentialSubject("BlockChainDevelopment","JM","JiSeungGu"
        ,"01047628287","Y")
    );

    PrivateKey VCPrivateKey = PrivateKey.fromString(issuerpk);

    Ed25519CredentialProof proof = new Ed25519CredentialProof(issuerDid);
    proof.sign(VCPrivateKey,null, vcDocument.toNormalizedJson(true));

    vcDocument.setProof(proof);

    return null;
  }
}
