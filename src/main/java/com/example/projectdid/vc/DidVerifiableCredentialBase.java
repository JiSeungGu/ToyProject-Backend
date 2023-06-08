package com.example.projectdid.vc;

import com.example.projectdid.did.Did;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.common.collect.Lists;
import org.threeten.bp.Instant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName   : com.example.projectdid.vc
 * fileName  : DidVerifiableCredentialBase
 * author    : jiseung-gu
 * date  : 2023/01/09
 * description : 기본적으로 VC에 들어갈 형식을 표현,
 **/

public class DidVerifiableCredentialBase<T extends CredentialSubject> {

  @Expose(serialize = true, deserialize = false)
  @SerializedName(DidVerifiableCredentialJsonProperties.CONTEXT)
  protected List<String> context;

  @Expose(serialize = true, deserialize = true)
  @SerializedName(DidVerifiableCredentialJsonProperties.CREDENTIAL_SUBJECT)
  protected List<T> credentialSubject;

  @Expose(serialize = true, deserialize = true)
  @SerializedName(DidVerifiableCredentialJsonProperties.ID)
  protected String id;

  @Expose(serialize = true, deserialize = true)
  @SerializedName(DidVerifiableCredentialJsonProperties.TYPE)
  protected List<String> type;

  @Expose(serialize = true, deserialize = true)
  @SerializedName(DidVerifiableCredentialJsonProperties.ISSUER)
  protected String issuer;

  @Expose(serialize = true, deserialize = true)
  @SerializedName(DidVerifiableCredentialJsonProperties.ISSUANCE_DATE)
  public Instant issuanceDate;

  @Expose(serialize = true, deserialize = true)
  @SerializedName(DidVerifiableCredentialJsonProperties.VALID_FROM_DATE)
  public Instant validFrom;

  @Expose(serialize = true, deserialize = true)
  @SerializedName(DidVerifiableCredentialJsonProperties.VALID_UNTIL_DATE)
  public Instant validUntil;

  public DidVerifiableCredentialBase() {
    super();
    this.context = Lists.newArrayList(DidVerifiableCredentialJsonProperties.FIRST_CONTEXT_ENTRY);
    this.type = Lists.newArrayList(DidVerifiableCredentialJsonProperties.VERIFIABLE_CREDENTIAL_TYPE);

  }

  // Hashgraph에서는 issuer클래스를 만들어서 issuer의 DID와 name을 넣을 수 있는 클래스를 만들었지만 Project에서는 안넣을 예정이라 따로 클래스를 만들지 않음
  public void setIssuer(final String issuerDid) {
    this.issuer = issuerDid;
  }

  public void setIssuer(final Did issuerDid) {
    setIssuer(issuerDid.toDid());
  }

  public void setIssuanceDate(final Instant issuanceDate) {
    this.issuanceDate = issuanceDate;
  }

  public void setValidFrom(final Instant validFrom) {
    this.validFrom = validFrom;
  }

  public void setValidUntil(final Instant validUntil) {
    this.validUntil = validUntil;
  }

//    public void setExpirationDate(final Instant expirationDate) {
//        this.expirationDate = expirationDate;
//    }
  public void addCredentialSubject(final T credentialSubject) {
    if (this.credentialSubject == null) {
      this.credentialSubject = new ArrayList<>();
    }
    this.credentialSubject.add(credentialSubject);
  }

  public List<T> getCredentialSubject() {
    return credentialSubject;
  }

  public void addType(final String type) {
    System.out.println(type.toString());
    System.out.println(type.length());
    this.type.add(type);
  }
}

