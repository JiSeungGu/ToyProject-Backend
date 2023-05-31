package com.example.projectdid.proof;

import com.google.gson.annotations.Expose;
import org.threeten.bp.Instant;

/**
 * packageName   : com.example.projectdid.proof
 * fileName  : proofBase
 * author    : jiseung-gu
 * date  : 2023/01/09
 * description :
 **/
public class proofBase {
    @Expose
    private String type;
    @Expose
    private String creator;
    @Expose
    private Instant created;
    @Expose
    private String domain;

    //nonce 값은 왜들어가는거지
    @Expose
    private String nonce;
    @Expose
    private String proofPurpose;
    @Expose
    private String verificationMethod;
    @Expose
    private String proofValue;

    public String getCreator() {
        return creator;
    }

    public void setCreator(final String creator) {
        this.creator = creator;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(final Instant created) {
        this.created = created;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(final String nonce) {
        this.nonce = nonce;
    }

    public String getProofPurpose() {
        return proofPurpose;
    }

    public void setProofPurpose(final String proofPurpose) {
        this.proofPurpose = proofPurpose;
    }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(final String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getProofValue() {
        return proofValue;
    }

    public void setJws(final String proofValue) {
        this.proofValue = proofValue;
    }
}
