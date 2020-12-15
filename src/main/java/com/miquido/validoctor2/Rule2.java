package com.miquido.validoctor2;

import java.util.Set;

public interface Rule2<T> {
  Set<Ailment2> apply(T patient);
}
