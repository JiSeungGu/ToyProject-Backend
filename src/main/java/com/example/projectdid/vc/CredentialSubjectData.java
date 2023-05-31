package com.example.projectdid.vc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * packageName   : com.example.projectdid.vc
 * fileName  : HashedCredentialSalt
 * author    : jiseung-gu
 * date  : 2023/04/10
 * description :
 **/
@Setter
@Getter
public class CredentialSubjectData {
  private JsonElement encryptedMap;
  private JsonElement textMap;
  private JsonElement saltMap;

}
