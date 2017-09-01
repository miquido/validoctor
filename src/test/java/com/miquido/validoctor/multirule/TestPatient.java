package com.miquido.validoctor.multirule;

import lombok.Data;

@Data
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
}
