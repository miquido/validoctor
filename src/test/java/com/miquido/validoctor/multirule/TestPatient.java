package com.miquido.validoctor.multirule;

public class TestPatient {

  private boolean idSet;
  private Long id;
  private boolean nameSet;
  private String name;
  private boolean phoneSet;
  private String phone;
  private boolean registered;


  public void setId(Long id) {
    idSet = true;
    this.id = id;
  }

  public void setName(String name) {
    nameSet = true;
    this.name = name;
  }

  public void setPhone(String phone) {
    phoneSet = true;
    this.phone = phone;
  }

  public void setRegistered(boolean registered) {
    this.registered = registered;
  }

  public boolean isIdSet() {
    return idSet;
  }

  public Long getId() {
    return id;
  }

  public boolean isNameSet() {
    return nameSet;
  }

  public String getName() {
    return name;
  }

  public boolean isPhoneSet() {
    return phoneSet;
  }

  public String getPhone() {
    return phone;
  }

  public boolean isRegistered() {
    return registered;
  }
}
