package com.example.projectdid.proof;

import com.google.gson.annotations.Expose;
import org.threeten.bp.Instant;

/**
 * packageName   : com.example.projectdid.proof
 * fileName  : VPproofBase
 * author    : jiseung-gu
 * date  : 2023/01/12
 * description :
 **/
public class VPproofBase {

    @Expose
    private String type;

    @Expose
    private Instant created;

    @Expose
    private String proofPurpose;

    @Expose
    private String verifiableMethod;

    @Expose
    private String challenge;

    @Expose
    private String domain;

    @Expose
    private String proofValue;

    public String getType() {return type;}

    public Instant getCreated() {return created;}

    public String getProofPurpose() {return proofPurpose;}

    public String getVerifiableMethod() {return verifiableMethod;}

    public String getChallenge() {return challenge;}

    public String getDomain() {return domain;}

    public String getProofValue() {return proofValue;}

    public void setType(String type) {this.type = type;}

    public void setCreated(Instant created) {this.created = created;}

    public void setProofPurpose(String proofPurpose) {this.proofPurpose = proofPurpose;}

    public void setVerifiableMethod(String verifiableMethod) {this.verifiableMethod = verifiableMethod;}

    public void setChallenge(String challenge) {this.challenge = challenge;}

    public void setDomain(String domain) {this.domain = domain;}

    public void setProofValue(String proofValue) {this.proofValue = proofValue;}

}
