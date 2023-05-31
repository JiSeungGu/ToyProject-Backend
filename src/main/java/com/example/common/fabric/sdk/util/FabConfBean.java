package com.example.common.fabric.sdk.util;

import com.example.common.fabric.sdk.user.UserContext;
import lombok.Synchronized;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.HashMap;

public class FabConfBean {

  private HashMap<Object, Object> DMAP = new HashMap<Object, Object>();

  private UserContext userContext = null;

  private GenericObjectPool genericObjectPool = null;

  private static final FabConfBean instance = new FabConfBean();

  public static FabConfBean getInstance() {
    return instance;
  }

  public void putMap(String key, String val) {
    DMAP.put(key, val);
  }

  public String getMap(String key) {
    String retVal = DMAP.get(key).toString();
    return retVal;
  }

  @Synchronized
  public void putMapObject(String key, Object val) {
    DMAP.put(key, val);
  }

  @Synchronized
  public void removeMapObject(String key) {
    DMAP.remove(key);
  }

  @Synchronized
  public Object getMapObject(String key) {
    return DMAP.get(key);
  }

  public void putUserContext(UserContext userContext) {
    this.userContext = userContext;
  }

  public UserContext getUserContext() {
    return this.userContext;
  }

  public GenericObjectPool getGenericObjectPool() {
    return genericObjectPool;
  }

  public void setGenericObjectPool(GenericObjectPool genericObjectPool) {
    this.genericObjectPool = genericObjectPool;
  }
}
