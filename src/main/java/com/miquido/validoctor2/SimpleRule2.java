package com.miquido.validoctor2;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class SimpleRule2<T> implements Rule2<T> {

  private final String violationMessage;
  private final Predicate<T> predicate;

  public SimpleRule2(String violationMessage, Predicate<T> predicate) {
    this.violationMessage = violationMessage;
    this.predicate = predicate;
  }

  @Override
  public Set<Ailment2> apply(T patient) {
    if (!predicate.test(patient)) {
      //TODO 1. this is always a singleton set, can we avoid creating those sets somehow?
      //TODO 2. is returning the patient value useful?
      return Collections.singleton(new Ailment2(null, patient, violationMessage));
    } else {
      return Collections.emptySet();
    }
  }
}
