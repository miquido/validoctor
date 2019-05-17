package com.miquido.validoctor.reducerrule;

import com.miquido.validoctor.rule.Rule;
import com.miquido.validoctor.util.NameUtil;
import com.miquido.validoctor.util.PropertyAccessException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builder for {@link ReducerRule}s. Call to all methods is mandatory before ReducerRule can be built.
 * Uses reflection to find getters of properties.
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
  private boolean nullIgnoring = false;

  ReducerRuleBuilder(Class<T> subjectClass, Class<P> propertiesClass) {
    this.subjectClass = subjectClass;
    this.propertiesClass = propertiesClass;
  }

  /**
   * Reflexively reads specified properties using getters. For property named foo, looks for getFoo method,
   * except for the case when foo is boolean when it looks for isFoo instead.
   * @param properties names of properties that are to be reduced and validated by the rule
   * @return this builder for chaining
   */
  public ReducerRuleBuilder<T, P> properties(String... properties) {
    propertyNames = Arrays.asList(properties);
    propertyGetters = Arrays.stream(properties)
        .map(property ->
            (Function<T, P>) o -> {
              String methodName = "getter of " + property;
              try {
                Method method = subjectClass.getDeclaredMethod(
                    (propertiesClass.isAssignableFrom(boolean.class) ? "is" : "get") + NameUtil.capitalize(property));
                methodName = method.getName();
                return (P) method.invoke(o);
              } catch (NoSuchMethodException e) {
                throw PropertyAccessException.noGetter(property, e);
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw PropertyAccessException.noGetter(methodName, o, e);
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
   * @param reducer reduction operator used to reduce the properties into one value. If null values are encountered,
   *                it is by default expected that the reducer will handle them, or NullPointerException will be
   *                thrown otherwise. To lift that responsibility from reducer and make the rule ignore nulls use
   *                {@link ReducerRuleBuilder#nullIgnoring()}.
   * @return this builder for chaining
   */
  public ReducerRuleBuilder<T, P> reducer(BinaryOperator<P> reducer) {
    this.reducer = reducer;
    return this;
  }

  /**
   * Makes the resulting rule ignore nulls encountered during reduction, so they are never sent to the reducer function.
   * @return this builder for chaining
   */
  public ReducerRuleBuilder<T, P> nullIgnoring() {
    nullIgnoring = true;
    return this;
  }

  /**
   * Builds the ReducerRule. Properties, rule and reducer must have all been set for this method to succeed.
   * @return new ReducerRule
   * @throws IllegalStateException if properties, rule or reducer were not set
   */
  public ReducerRule<T, P> build() {
    if (!buildable()) {
      throw new IllegalStateException("Properties, rule and reducer must all be set before building");
    }
    return new ReducerRule<>(propertyNames, propertyGetters, reducer, rule, nullIgnoring);
  }


  private boolean buildable() {
    return !propertyGetters.isEmpty() && rule != null && reducer != null;
  }

}
