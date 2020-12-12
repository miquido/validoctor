package com.miquido.validoctor.result;

import java.util.HashSet;
import java.util.Set;

public class Ailment {
  public String field;
  public Set<String> ailments;

  public Ailment(String field, String ailment) {
    this.field = field;
    ailments = new HashSet<>(1);
    ailments.add(ailment);
  }

  public Ailment(String field, Set<String> ailments) {
    this.field = field;
    this.ailments = ailments;
  }
}
