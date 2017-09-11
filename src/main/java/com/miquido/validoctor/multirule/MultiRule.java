package com.miquido.validoctor.multirule;

import com.miquido.validoctor.ailment.Ailment;

import java.util.Map;
import java.util.Set;

/**
 * Aggregation of rules grouped by property they deal with.
 * Useful for defining validation rules of several or all properties of an object in one declaration.
 * @param <T> type of object tested
 */
public interface MultiRule<T> {

  /**
   * @param obj object to validate
   * @return map of property name -> set of Ailments stated during validation
   */
  Map<String, Set<Ailment>> test(T obj);

  /**
   * @return map of property name -> set of Ailments that are possible to be stated in case of violation
   */
  Map<String, Set<Ailment>> getAilments();

}
