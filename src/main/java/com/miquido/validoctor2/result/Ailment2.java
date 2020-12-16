package com.miquido.validoctor2.result;

import java.util.HashSet;
import java.util.Set;

public class Ailment2 {
  public String field;
  public Object value;
  public Set<String> ailments;

  public Ailment2(String field, Object value, String ailment) {
    this.field = field;
    this.value = value;
    ailments = new HashSet<>(1);
    ailments.add(ailment);
  }

  public Ailment2(String field, Object value, Set<String> ailments) {
    this.field = field;
    this.value = value;
    this.ailments = ailments;
  }
}
