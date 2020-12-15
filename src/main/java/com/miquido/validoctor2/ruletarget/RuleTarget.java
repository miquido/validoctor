package com.miquido.validoctor2.ruletarget;

import java.util.List;

public interface RuleTarget<T, P> {
  List<P> getPatients(T object);
  List<String> getFieldNames();
}
