package com.miquido.validoctor.reducerrule;

import com.miquido.validoctor.rule.Rule;
import com.miquido.validoctor.util.NameUtil;
import com.miquido.validoctor.util.PropertyAccessException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builder for {@link ReducerRule}s. Uses reflection to find getter of properties.
 * @param <T> type of patient object
 * @param <P> type of reduced and validated properties
 */
public class ReducerRuleBuilder<T, P> {

  private final Class<T> subjectClass;
  private final Class<P> propertiesClass;

  private List<String> propertyNames = new ArrayList<>(0);
  private List<Function<T, P>> propertyGetters = new ArrayList<>(0);
  private Rule<? super P> rule;
  private BinaryOperator<P> reducer;

  ReducerRuleBuilder(Class<T> subjectClass, Class<P> propertiesClass) {
    this.subjectClass = subjectClass;
    this.propertiesClass = propertiesClass;
  }

  /**
   * @param properties names of properties that are to be reduced and validated by the rule
   * @return this builder for chaining
   */
  public ReducerRuleBuilder<T, P> properties(String... properties) {
    propertyNames = Arrays.asList(properties);
    propertyGetters = Arrays.stream(properties)
        .map(property -> {
          try {
            return subjectClass.getDeclaredMethod(
                (propertiesClass.isAssignableFrom(boolean.class) ? "is" : "get") + NameUtil.capitalize(property));
          } catch (NoSuchMethodException e) {
            throw PropertyAccessException.noGetter(property, e);
          }
        })
        .filter(Objects::nonNull)
        .map(method ->
            (Function<T, P>) o -> {
              try {
                return (P) method.invoke(o);
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw PropertyAccessException.noGetter(method.getName(), o, e);
              }
            }
          )
        .collect(Collectors.toList());
    return this;
  }

  /**
   * @param rule rule to apply to reduced properties
   * @return this builder for chaining
   */
  public ReducerRuleBuilder<T, P> rule(Rule<? super P> rule) {
    this.rule = rule;
    return this;
  }

  /**
   * @param reducer reduction operator used reduce the properties into one value
   * @return this builder for chaining
   */
  public ReducerRuleBuilder<T, P> reducer(BinaryOperator<P> reducer) {
    this.reducer = reducer;
    return this;
  }

  public ReducerRule<T, P> build() {
    return new ReducerRule<>(propertyNames, propertyGetters, reducer, rule);
  }

}
