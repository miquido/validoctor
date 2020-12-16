package com.miquido.validoctor2.target;

import java.util.List;

public interface RuleTarget<T, P> {
  List<P> getPatients(T object);
  List<String> getFieldNames();
}
