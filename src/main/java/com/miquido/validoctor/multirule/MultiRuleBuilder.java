package com.miquido.validoctor.multirule;

import com.miquido.validoctor.rule.Rule;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Allows building specialized {@link Rule} lists for conditional validation of multiple properties of a single object.
 *
 * @param <T> type of validated object
 */
public class MultiRuleBuilder<T> {

  private final MultiRule<T> rules;


  MultiRuleBuilder() {
    rules = new MultiRule<>();
  }

  /**
   * Will use reflection to get properties. Failure to find or call property getters will cause validation fail.
   * <br/><b></b>Contract:</b><br/>
   * - Subject class must contain a getter for each property that is to be validated.<br/>
   * - Subject class may contain getters for boolean or Boolean properties that start with "is".<br/>
   */
  public ReflexiveMultiRuleBuilder reflexiveProperties(Class<T> subjectClass) {
    return new ReflexiveMultiRuleBuilder(subjectClass);
  }

  /**
   * @param propertyName   name of the property the rules apply to
   * @param propertyGetter getter of the property the rules apply to
   * @param rules          rules to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  @SafeVarargs
  public final <PropertyType> MultiRuleBuilder<T> withRules(String propertyName,
                                                            Function<T, PropertyType> propertyGetter,
                                                            Rule<PropertyType>... rules) {
    return withConditionalRules(t -> true, propertyName, propertyGetter, rules);
  }

  /**
   * @param <PropertyType> type of property
   * @param condition      conditional predicate determining whether to run validation on the property
   * @param propertyName   name of the property the rules apply to
   * @param propertyGetter getter of the property the rules apply to
   * @param rules          rules to apply
   * @return builder for chaining
   */
  @SafeVarargs
  public final <PropertyType> MultiRuleBuilder<T> withConditionalRules(Predicate<T> condition,
                                                                       String propertyName,
                                                                       Function<T, PropertyType> propertyGetter,
                                                                       Rule<PropertyType>... rules) {
    for (Rule<PropertyType> rule : rules) {
      this.rules.add(new ConditionalPropertyRule<>(propertyName, propertyGetter, condition, rule));
    }
    return this;
  }

  public MultiRule<T> build() {
    return rules;
  }


  public class ReflexiveMultiRuleBuilder {

    private final Class<T> subjectClass;

    private ReflexiveMultiRuleBuilder(Class<T> subjectClass) {
      this.subjectClass = subjectClass;
    }

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
     * @param <PropertyType> type of property
     * @param condition      conditional predicate determining whether to run validation on the property
     * @param propertyName   name of property to apply the rules to
     * @param rules          rules to apply
     * @return builder for chaining
     */
    @SafeVarargs
    public final <PropertyType> ReflexiveMultiRuleBuilder withConditionalRules(Predicate<T> condition,
                                                                               String propertyName,
                                                                               Rule<PropertyType>... rules) {
      MultiRuleBuilder.this.withConditionalRules(condition, propertyName, getterFunction(propertyName), rules);
      return this;
    }

    /**
     * Applies given rules to all properties of given type found in the subject class.
     * Every method returning a given type and starting with "get" or "is" is treated as a property getter.
     * @param propertyClass  class of properties to apply the rules to
     * @param rules          rules to apply
     * @param <PropertyType> type of property
     * @return builder for chaining
     */
    @SafeVarargs
    public final <PropertyType> ReflexiveMultiRuleBuilder withRulesForAll(Class<? extends PropertyType> propertyClass,
                                                                          Rule<PropertyType>... rules) {
      Arrays.stream(subjectClass.getMethods())
          .filter(method -> method.getReturnType().equals(propertyClass) &&
              (method.getName().startsWith("get") || method.getName().startsWith("is")))
          .forEach(getter -> {
            boolean isIs = getter.getName().startsWith("is");
            String propertyName = StringUtils.uncapitalize(getter.getName().substring(isIs ? 2 : 3, getter.getName().length()));
            MultiRuleBuilder.this.withRules(propertyName, getterFunction(propertyName, getter), rules);
          });
      return this;
    }

    public MultiRule<T> build() {
      return MultiRuleBuilder.this.build();
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

  }
}
