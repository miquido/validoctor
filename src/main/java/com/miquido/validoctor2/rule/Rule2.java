package com.miquido.validoctor2.rule;

import com.miquido.validoctor2.result.Ailment2;

import java.util.Set;

/**
 * Rule ready to be passed to
 * {@link com.miquido.validoctor2.Validoctor2#examine(Object, Rule2[]) Validoctor's examine method}.
 * @param <T> type of patient
 */
public interface Rule2<T> {
  Set<Ailment2> apply(T patient);
}
