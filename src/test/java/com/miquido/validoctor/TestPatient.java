package com.miquido.validoctor;

import java.util.Objects;

public class TestPatient {

  private boolean idSet;
  private Long id;
  private boolean nameSet;
  private String name;
  private boolean phoneSet;
  private String phone;
  private boolean ordinalSet;
  private Long ordinal;
  private boolean registered;


  public TestPatient() {}

  public TestPatient(long id, String name, String phone, boolean registered) {
    setId(id);
    setName(name);
    setPhone(phone);
    setRegistered(registered);
  }

  public TestPatient(long id, String name, String phone, long ordinal, boolean registered) {
    setId(id);
    setName(name);
    setPhone(phone);
    setOrdinal(ordinal);
    setRegistered(registered);
  }

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

  public void setOrdinal(Long ordinal) {
    ordinalSet = true;
    this.ordinal = ordinal;
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

  public boolean isOrdinalSet() {
    return ordinalSet;
  }

  public Long getOrdinal() {
    return ordinal;
  }

  public boolean isRegistered() {
    return registered;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TestPatient)) return false;
    TestPatient that = (TestPatient) o;
    return registered == that.registered &&
        Objects.equals(id, that.id) &&
        Objects.equals(name, that.name) &&
        Objects.equals(phone, that.phone) &&
        Objects.equals(ordinal, that.ordinal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, phone, ordinal, registered);
  }
}
