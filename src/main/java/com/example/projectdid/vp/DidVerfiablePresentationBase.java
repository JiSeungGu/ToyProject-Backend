package com.example.projectdid.vp;

import com.example.projectdid.vc.VCDocument;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName   : com.example.projectdid.vp
 * fileName  : DidVpBASE
 * author    : jiseung-gu
 * date  : 2023/01/11
 * description :
 **/
public class DidVerfiablePresentationBase<T extends VCDocument> {

    @Expose(serialize = true, deserialize = true)
    @SerializedName(DidVpDocumentJsonProperties.CONTEXT)
    protected String context;

    @Expose(serialize = true, deserialize = true)
    @SerializedName(DidVpDocumentJsonProperties.ID)
    protected String id;

    @Expose(serialize = true, deserialize = true)
    @SerializedName(DidVpDocumentJsonProperties.TYPE)
    protected List<String> type;

    @Expose(serialize = true, deserialize = true)
    @SerializedName(DidVpDocumentJsonProperties.VERIFIABLECREDENTIAL)
    protected List<JsonElement> verifiableCredential;

    @Expose(serialize = true, deserialize = true)
    @SerializedName(DidVpDocumentJsonProperties.CREDENTIALSALT)
    protected JsonElement credentialSalt;

    @Expose(serialize = true, deserialize = true)
    @SerializedName(DidVpDocumentJsonProperties.CREDENTIALTEXT)
    protected JsonElement credentialtext;


//    @Expose(serialize = true, deserialize = true)
//    @SerializedName(DidVpDocumentJsonProperties.PROOF)
//    protected String proof;

    public void setCredentialSalt(JsonElement credentialSalt) {
        this.credentialSalt = credentialSalt;
    }
    public void setCredentialtext(JsonElement credentialtext) {
        this.credentialtext = credentialtext;
    }
    public DidVerfiablePresentationBase() {
        super();
        this.context = DidVpDocumentJsonProperties.CONTEXT;
        this.type = Lists.newArrayList(DidVpDocumentJsonProperties.VERIFIABLE_PRESENTATION_TYPE);
    }

    public void setContext(final String context) {
        this.context = context;
    }

    public void setId(String id) {this.id = id;}

//    public void setVerifiableCredential(String verifiableCredential) {
//        this.verifiableCredential = verifiableCredential;
//    }
//    public void addVC(final HashMap<String,JsonElement> array) {
    public void addVC(final JsonElement array) {
        System.out.println(array);
        if(this.verifiableCredential == null) {
            this.verifiableCredential = new ArrayList<>();
        }
        this.verifiableCredential.add(array);
    }
    public List<JsonElement> getVerifiableCredential() {
        return verifiableCredential;
    }
    public void addType(final String type) {
        this.type.add(type);
    }
}
