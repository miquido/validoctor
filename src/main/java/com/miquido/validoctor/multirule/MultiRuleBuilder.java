package com.miquido.validoctor.multirule;

import com.miquido.validoctor.rule.Rule;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Allows building rules for conditional validation of multiple properties of a single object.
 *
 * @param <T> type of validated object
 */
public class MultiRuleBuilder<T> {

  private final Class<T> subjectClass;
  private final Map<String, Function<T, ?>> propertyGettersMap = new HashMap<>();
  private final Map<String, Set<Rule>> propertyRulesMap = new HashMap<>();
  private final Map<String, Predicate<T>> propertyConditionMap = new HashMap<>();

  private MultiRuleBuilder(Class<T> subjectClass) {
    this.subjectClass = subjectClass;
  }

  public static <T> MultiRuleBuilder<T> forClass(Class<T> subjectClass) {
    return new MultiRuleBuilder<>(subjectClass);
  }

  /**
   * Will use reflection to get properties. Failure to find or call property getters will cause validation fail.
   * <br/><b></b>Contract:</b><br/>
   * - Subject class must contain a getter for each property that is to be validated.<br/>
   * - Subject class may contain getters for boolean or Boolean properties that start with "is".<br/>
   */
  public ReflexiveMultiRuleBuilder reflexiveProperties() {
    return new ReflexiveMultiRuleBuilder();
  }

  /**
   * @param propertyName   name of property to apply the rules to
   * @param propertyGetter getter of the property to apply the rules to
   * @param rules          rules to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  @SafeVarargs
  public final <PropertyType> MultiRuleBuilder<T> withRules(String propertyName,
                                                            Function<T, PropertyType> propertyGetter,
                                                            Rule<PropertyType>... rules) {
    return withConditionalRules(propertyName, null, propertyGetter, rules);
  }

  /**
   * @param propertyName   name of property to apply the rules to
   * @param condition      conditional predicate determining whether to run validation on the property
   * @param propertyGetter getter of the property to apply the rules to
   * @param rules          rules to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  @SafeVarargs
  public final <PropertyType> MultiRuleBuilder<T> withConditionalRules(String propertyName,
                                                                       Predicate<T> condition,
                                                                       Function<T, PropertyType> propertyGetter,
                                                                       Rule<PropertyType>... rules) {
    propertyGettersMap.put(propertyName, propertyGetter);
    propertyRulesMap.put(propertyName, new HashSet<>(Arrays.asList(rules)));
    if (condition != null) {
      propertyConditionMap.put(propertyName, condition);
    }
    return this;
  }


  public MultiRule<T> build() {
    return new ConditionalMultiRule<>(propertyGettersMap, propertyRulesMap, propertyConditionMap);
  }


  public class ReflexiveMultiRuleBuilder {

    /**
     * If property getter is not found within the object or can not be called at runtime,
     * {@link PropertyAccessException} is thrown.
     *
     * @param propertyName   name of property to apply the rules to
     * @param rules          rules to apply
     * @param <PropertyType> type of property
     * @return builder for chaining
     */
    @SafeVarargs
    public final <PropertyType> ReflexiveMultiRuleBuilder withRules(String propertyName,
                                                                    Rule<PropertyType>... rules) {
      MultiRuleBuilder.this.withRules(propertyName, getterFunction(propertyName), rules);
      return this;
    }

    /**
     * Will look for boolean returning is[property_name]Set methods to use as conditional predicates.
     * If such method is not found for a property, validation will fail.
     *
     * @param propertyName   name of property to apply the rules to
     * @param rules          rules to apply
     * @param <PropertyType> type of property
     * @return builder for chaining
     */
    @SafeVarargs
    public final <PropertyType> ReflexiveMultiRuleBuilder withIsSetDependentRules(String propertyName,
                                                                                  Rule<PropertyType>... rules) {
      return withConditionalRules(propertyName, isSetPredicate(propertyName), rules);
    }

    /**
     * @param propertyName   name of property to apply the rules to
     * @param condition      conditional predicate determining whether to run validation on the property
     * @param rules          rules to apply
     * @param <PropertyType> type of property
     * @return builder for chaining
     */
    @SafeVarargs
    public final <PropertyType> ReflexiveMultiRuleBuilder withConditionalRules(String propertyName,
                                                                               Predicate<T> condition,
                                                                               Rule<PropertyType>... rules) {
      MultiRuleBuilder.this.withConditionalRules(propertyName, condition, getterFunction(propertyName), rules);
      return this;
    }

    /**
     * Applies given rules to all properties found in the subject class. Every method starting with "get" is treated
     * as a property getter.
     *
     * @param rules          rules to apply
     * @param <PropertyType> type of property
     * @return builder for chaining
     */
    @SafeVarargs
    public final <PropertyType> ReflexiveMultiRuleBuilder withSameRulesForAllProperties(Rule<PropertyType>... rules) {
      withSameRulesForAllProperties(false, rules);
      return this;
    }

    /**
     * Conditionally applies given rules to all properties found in the subject class. Every method starting with "get"
     * is treated as a property getter. Conditional predicate is an is[property_name]Set method. If such method is not
     * found for a property, validation will fail.
     *
     * @param rules          rules to apply
     * @param <PropertyType> type of property
     * @return builder for chaining
     */
    @SafeVarargs
    public final <PropertyType> ReflexiveMultiRuleBuilder withSameRulesForAllSetProperties(Rule<PropertyType>... rules) {
      withSameRulesForAllProperties(true, rules);
      return this;
    }

    public MultiRule<T> build() {
      return MultiRuleBuilder.this.build();
    }


    @SafeVarargs
    private final <PropertyType> ReflexiveMultiRuleBuilder withSameRulesForAllProperties(boolean withIsSetProperties,
                                                                                         Rule<PropertyType>... rules) {
      Arrays.stream(subjectClass.getMethods())
          .filter(method -> (method.getName().startsWith("get") && !method.getName().equals("getClass"))
              || (method.getName().startsWith("is") && !method.getName().endsWith("Set")))
          .forEach(getter -> {
            boolean isIs = getter.getName().startsWith("is");
            String propertyName = StringUtils.uncapitalize(getter.getName().substring(isIs ? 2 : 3, getter.getName().length()));
            MultiRuleBuilder.this.withConditionalRules(
                propertyName, withIsSetProperties ? isSetPredicate(propertyName) : null, getterFunction(propertyName, getter), rules);
          });
      return this;
    }

    private <PropertyType> Function<T, PropertyType> getterFunction(String propertyName) {
      try {
        Class<?> type = subjectClass.getDeclaredField(propertyName).getType();
        String verb = type.equals(boolean.class) ? "is" : "get";
        String methodName = verb + StringUtils.capitalize(propertyName);
        Method method = subjectClass.getMethod(methodName);
        return getterFunction(propertyName, method);
      } catch (NoSuchMethodException | NoSuchFieldException e) {
        throw PropertyAccessException.noGetter(propertyName, e);
      }
    }

    private <PropertyType> Function<T, PropertyType> getterFunction(String propertyName, Method method) {
      return object -> {
        try {
          return (PropertyType) method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw PropertyAccessException.noGetter(propertyName, object, e);
        }
      };
    }

    private Predicate<T> isSetPredicate(String propertyName) {
      String isSetMethodName = "is" + StringUtils.capitalize(propertyName) + "Set";
      try {
        Method isSetMethod = subjectClass.getMethod(isSetMethodName);
        if (!isSetMethod.getReturnType().equals(boolean.class)) {
          throw PropertyAccessException.noIsSetMethod(propertyName, null);
        }
        return object -> {
          try {
            return (Boolean) isSetMethod.invoke(object);
          } catch (InvocationTargetException | IllegalAccessException e) {
            throw PropertyAccessException.noIsSetMethod(propertyName, object, e);
          }
        };
      } catch (NoSuchMethodException e) {
        throw PropertyAccessException.noIsSetMethod(propertyName, e);
      }
    }
  }

}
