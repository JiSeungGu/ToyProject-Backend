package com.example.projectdid.vc;

import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.Instant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.common.crypto.ClaimHashing.generateSalt;
import static com.example.common.crypto.ClaimHashing.hashWithSalt;


/**
 * packageName   : com.example.projectdid.vc
 * fileName  : CredentialSubject
 * author    : jiseung-gu
 * date  : 2023/01/09
 * description : Issuer가 holder에게 VC를 전달할때 들어가는 클레임 Subject
 * 기존 Hashgraph  hedera에서는 id값을 상속받아서 구현하였지만 이번 프로젝트에서는 통일 예정
 **/
public class CredentialSubject {
  //  소속    /    직위    /  이름 /  핸드폰 번호 /  재직 여부
//  private static final String[] JSON_PROPERTIES_ORDER = {"type", "position", "name", "phoneno", "status"};
  private static final String[] JSON_PROPERTIES_ORDER = {"userId", "userName", "userPhoneno", "userStatus", "userRegdate", "status"};
  @Expose(serialize = true, deserialize = true)
  @SerializedName(DidVerifiableCredentialJsonProperties.ID)
  protected String id;

  @Expose
  private final String userId;

  @Expose
  private final String userName;

  @Expose
  private final String userPhoneno;

  @Expose
  private final String userStatus;

  @Expose
  private final String userRegdate;


  public CredentialSubject(final String userId, final String userName, final String userPhoneno, final String userStatus, final String userRegdate) {
    this.userId = userId;
    this.userName = userName;
    this.userPhoneno = userPhoneno;
    this.userStatus = userStatus;
    this.userRegdate = userRegdate;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public JsonElement toNormalizedJsonElement() {
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
      if (root.has(property)) {
        map.put(property, root.get(property));
      }
    }
    // Turn map to JSON
    return gson.toJsonTree(map);
  }
}
