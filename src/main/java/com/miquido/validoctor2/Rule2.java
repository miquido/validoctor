package com.miquido.validoctor2;

import java.util.Set;

public interface Rule2<T> {
  Set<Violation> test(T patient);
  String getViolationMessage();
}
