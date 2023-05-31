package com.example.common.fabric.sdk.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RevObjectPool {

  private final int OBJECTNUM = 5;
  private List<Object> objectList = new ArrayList<Object>();

  private static final RevObjectPool instance = new RevObjectPool();

  public static RevObjectPool getInstance() {
    return instance;
  }

  public int getObjectSize() {
    return this.objectList.size();
  }

  public void initObject() {
    for (int i = 0; i < OBJECTNUM; i++) {
      RevFabObject RFO = createObject(i);
      this.objectList.add(RFO);
    }
  }

  private RevFabObject createObject(int numId) {
    RevFabObject RFO = new RevFabObject(true, numId);
    return RFO;
  }

  public synchronized RevFabObject getObject() {

    int objectsize = this.objectList.size();
    //log.info("====================== Pool Size = " + objectsize + "===========================");

    if (objectsize > 0) {
      RevFabObject revFabObject = (RevFabObject) objectList.get(0);

      // double TStartTime = System.currentTimeMillis();
      // revFabObject.setTStartTime(TStartTime);
      this.objectList.remove(0);
      return revFabObject;

    } else {
      log.info("[NEW FABRIC CONNECT]");
      RevFabObject RFOW = new RevFabObject(false, 0);
      return RFOW;
    }
  }

  public synchronized void retObject(RevFabObject revFabObject) {
    // int objectsize = this.objectList.size();
    // revFabObject.getTime();
    // int numid = revFabObject.getNumId();

    // log.info("============================");
    // log.info("RET OBJ:" + numid + ":" + objectsize + ":" + revFabObject.isInit());
    // log.info("============================");

    if (!revFabObject.isInit()) {
      revFabObject.shutdown();
    } else {
      this.objectList.add(revFabObject);
    }
  }
}
