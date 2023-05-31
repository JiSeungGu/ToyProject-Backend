package com.example.common.fabric.sdk.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class RevFabObjectPool extends BasePooledObjectFactory<RevFabObject> {

  @Override
  public RevFabObject create() throws Exception {
    return new RevFabObject(true, 0);
  }

  @Override
  public PooledObject<RevFabObject> wrap(RevFabObject revFabObject) {
    return new DefaultPooledObject<RevFabObject>(revFabObject);
  }
}
