package com.example.common.fabric.sdk.user;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.Serializable;
import java.util.Set;

public class UserContext implements User, Serializable {

  private static final long serialVersionUID = 1L;
  protected String name; // ID
  protected Set<String> roles;
  protected String account;
  protected String affiliation; // 소속(org)
  protected Enrollment enrollment;
  protected String mspId;

  public UserContext(String name, String affiliation, String mspId) {
    this.name = name;
    this.affiliation = affiliation;
    this.mspId = mspId;
  }

  public UserContext(String name, String mspId, Enrollment enrollment) {
    this.name = name;
    this.mspId = mspId;
    this.enrollment = enrollment;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public void setAffiliation(String affiliation) {
    this.affiliation = affiliation;
  }

  public void setEnrollment(Enrollment enrollment) {
    this.enrollment = enrollment;
  }

  public void setMspId(String mspId) {
    this.mspId = mspId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Set<String> getRoles() {
    return roles;
  }

  @Override
  public String getAccount() {
    return account;
  }

  @Override
  public String getAffiliation() {
    return affiliation;
  }

  @Override
  public Enrollment getEnrollment() {
    return enrollment;
  }

  @Override
  public String getMspId() {
    return mspId;
  }
}
