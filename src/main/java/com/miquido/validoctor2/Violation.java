package com.miquido.validoctor2;

import java.util.HashSet;
import java.util.Set;

public class Violation {
  public String field;
  public Object value;
  public Set<String> ailments;

  public Violation(String field, Object value, String ailment) {
    this.field = field;
    this.value = value;
    ailments = new HashSet<>(1);
    ailments.add(ailment);
  }

  public Violation(String field, Object value, Set<String> ailments) {
    this.field = field;
    this.value = value;
    this.ailments = ailments;
  }
}
