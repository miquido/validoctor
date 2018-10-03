package com.miquido.validoctor.reducerrule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.rule.Rule;

import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Specialized kind of Rule that reduces several properties of the same type into one value and validates this
 * reduced value when testing a patient object.
 *
 * @param <PatientType>  type of validated object
 * @param <PropertyType> type of reduced properties
 */
public class ReducerRule<PatientType, PropertyType> implements Rule<PatientType> {

  private final List<String> properties;
  private final List<Function<PatientType, PropertyType>> getters;
  private final BinaryOperator<PropertyType> reducer;
  private final Rule<? super PropertyType> rule;


  /**
   * @param patientClass class of patient object
   * @param propertyClass class of properties to reduce and validate
   * @param <T> type of patient object
   * @param <P> type of properties to reduce and validate
   * @return builder for instance of ReducerRule
   */
  public static <T, P> ReducerRuleBuilder<T, P> builder(Class<T> patientClass, Class<P> propertyClass) {
    return new ReducerRuleBuilder<>(patientClass, propertyClass);
  }


  ReducerRule(List<String> properties, List<Function<PatientType, PropertyType>> getters,
              BinaryOperator<PropertyType> reducer, Rule<? super PropertyType> rule) {
    this.properties = properties;
    this.getters = getters;
    this.reducer = reducer;
    this.rule = rule;
  }

  @Override
  public boolean test(PatientType obj) {
    PropertyType sum = getters.stream().map(getter -> getter.apply(obj)).reduce(reducer).orElse(null);
    return rule.test(sum);
  }

  @Override
  public Ailment getAilment() {
    return rule.getAilment();
  }

  /**
   * @return list of names of properties that this rule reduces and validates.
   */
  public List<String> getProperties() {
    return properties;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ReducerRule)) return false;
    ReducerRule<?, ?> that = (ReducerRule<?, ?>) o;
    return Objects.equals(properties, that.properties) &&
        Objects.equals(getters, that.getters) &&
        Objects.equals(reducer, that.reducer) &&
        Objects.equals(rule, that.rule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(properties, getters, reducer, rule);
  }
}
