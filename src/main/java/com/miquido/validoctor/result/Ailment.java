package com.miquido.validoctor.result;

import java.util.HashSet;
import java.util.Objects;
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


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Ailment ailment = (Ailment) o;
    return Objects.equals(field, ailment.field) && Objects.equals(ailments, ailment.ailments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, ailments);
  }
}
