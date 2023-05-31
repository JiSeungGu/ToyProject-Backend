package com.example.common.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfBean {

  private static final ConfBean instance = new ConfBean();

  public static ConfBean getInstance() {
    return instance;
  }

  private String privateseed;

}
