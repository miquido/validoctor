package com.miquido.validoctor2.rule;

import com.miquido.validoctor2.result.Ailment2;

import java.util.Set;

public interface Rule2<T> {
  Set<Ailment2> apply(T patient);
}
