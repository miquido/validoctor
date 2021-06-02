package com.miquido.validoctor2.result;

import java.util.HashSet;
import java.util.Set;

public class Ailment2 {
  public String field;
  public Set<String> ailments;

  public Ailment2(String field, String ailment) {
    this.field = field;
    ailments = new HashSet<>(1);
    ailments.add(ailment);
  }

  public Ailment2(String field, Set<String> ailments) {
    this.field = field;
    this.ailments = ailments;
  }
}
