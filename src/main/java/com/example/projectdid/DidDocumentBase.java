package com.example.projectdid;

import com.example.projectdid.RSA.SejongDidRsaPubKey;
import com.example.projectdid.did.DidSyntax;
import com.example.projectdid.did.SejongDidPubKey;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.Instant;
import java.util.Iterator;

public class DidDocumentBase {

    @Expose(serialize = true, deserialize = false)
    @SerializedName(DidDocumentJsonProperties.CONTEXT)
    protected String context;

    @Expose(serialize = true, deserialize = true)
    @SerializedName(DidDocumentJsonProperties.ID)
    protected String id;

    @Expose(serialize = false, deserialize = false)
    protected SejongDidPubKey SejongRootKey;

    //RSA로인한 추가
    @Expose(serialize = false, deserialize = false)
    protected SejongDidRsaPubKey SejongRSARootKey;

    public DidDocumentBase(final String did){
        this.id = did;
        this.context = DidSyntax.DID_DOCUMENT_CONTEXT;
    }
    public static DidDocumentBase fromJson(final String json) {
        Gson gson = new GsonBuilder().disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();

        DidDocumentBase result = null;

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            result = gson.fromJson(root, DidDocumentBase.class);

            if (root.has(DidDocumentJsonProperties.PUBLIC_KEY)) {
                Iterator<JsonElement> itr = root.getAsJsonArray(DidDocumentJsonProperties.PUBLIC_KEY).iterator();
                while (itr.hasNext()) {
                    JsonObject publicKeyObj = itr.next().getAsJsonObject();
                    if (publicKeyObj.has(DidDocumentJsonProperties.ID)
                            && publicKeyObj.get(DidDocumentJsonProperties.ID).getAsString()
                            .equals(result.getId() + SejongDidPubKey.DID_ROOT_KEY_NAME)) {
                        result.setDidRootKey(gson.fromJson(publicKeyObj, SejongDidPubKey.class));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Given JSON string is not a valid DID document", e);
        }

        return result;
    }

    /**
     * Converts this DID document into JSON string.
     *
     * @return The JSON representation of this document.
     */
    public String toJson() {
        Gson gson = new GsonBuilder().disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();

        JsonElement jsonElement = gson.toJsonTree(this);
        System.out.println("rootObject :"+ gson.toJsonTree(this));
        JsonObject rootObject = jsonElement.getAsJsonObject();
        addDidRootKeyToPublicKeys(rootObject);
        addDidRootKeyToAuthentication(rootObject);

        return gson.toJson(jsonElement);
    }
    //RSA를위한 추가
    public String toRSAJson() {
        Gson gson = new GsonBuilder().disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();

        JsonElement jsonElement = gson.toJsonTree(this);
        System.out.println("rootObject :"+ gson.toJsonTree(this));
        JsonObject rootObject = jsonElement.getAsJsonObject();
        //RSA를위한 추가
        addDidRsaRootKeyToPublicKeys(rootObject);
        addDidRsaRootKeyToAuthentication(rootObject);

        return gson.toJson(jsonElement);
    }
    private void addDidRsaRootKeyToAuthentication(final JsonObject rootObject) {
        if (!rootObject.has(DidDocumentJsonProperties.AUTHENTICATION)) {
            rootObject.add(DidDocumentJsonProperties.AUTHENTICATION, new JsonArray());
        }

        JsonElement authElement = rootObject.get(DidDocumentJsonProperties.AUTHENTICATION);
        if (authElement.isJsonArray() && authElement.getAsJsonArray().size() == 0) {
            authElement.getAsJsonArray().add(SejongRSARootKey.getId());
        }
    }
    protected void addDidRsaRootKeyToPublicKeys(final JsonObject rootObject) {
        JsonArray publicKeys = null;
        if (rootObject.has(DidDocumentJsonProperties.PUBLIC_KEY)) {
            publicKeys = rootObject.getAsJsonArray(DidDocumentJsonProperties.PUBLIC_KEY);
        } else {
            publicKeys = new JsonArray(1);
            rootObject.add(DidDocumentJsonProperties.PUBLIC_KEY, publicKeys);
        }

        publicKeys.add(new GsonBuilder().disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create().toJsonTree(SejongRSARootKey));
    }

    private void addDidRootKeyToAuthentication(final JsonObject rootObject) {
        if (!rootObject.has(DidDocumentJsonProperties.AUTHENTICATION)) {
            rootObject.add(DidDocumentJsonProperties.AUTHENTICATION, new JsonArray());
        }

        JsonElement authElement = rootObject.get(DidDocumentJsonProperties.AUTHENTICATION);
        if (authElement.isJsonArray() && authElement.getAsJsonArray().size() == 0) {
            authElement.getAsJsonArray().add(SejongRootKey.getId());
        }
    }

    /**
     * Adds a #did-root-key to public keys of the DID document.
     *
     * @param rootObject The root object of DID Document as JsonObject.
     */
    protected void addDidRootKeyToPublicKeys(final JsonObject rootObject) {
        JsonArray publicKeys = null;
        if (rootObject.has(DidDocumentJsonProperties.PUBLIC_KEY)) {
            publicKeys = rootObject.getAsJsonArray(DidDocumentJsonProperties.PUBLIC_KEY);
        } else {
            publicKeys = new JsonArray(1);
            rootObject.add(DidDocumentJsonProperties.PUBLIC_KEY, publicKeys);
        }

        publicKeys.add(new GsonBuilder().disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create().toJsonTree(SejongRootKey));
    }
    public String getContext() {
        return context;
    }

    public String getId() {
        return id;
    }
    public void setDidRootKey(final SejongDidPubKey SejongRootKey) {
        this.SejongRootKey = SejongRootKey;
    }

    //RSA를 위한 추가
    public void setDidRSARootKey(final SejongDidRsaPubKey SejongRootKey) {
        this.SejongRSARootKey = SejongRootKey;
    }
}
