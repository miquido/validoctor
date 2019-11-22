package com.miquido.validoctor.multirule;

import com.miquido.validoctor.rule.Rule;
import com.miquido.validoctor.rule.Rules;
import com.miquido.validoctor.util.NameUtil;
import com.miquido.validoctor.util.PropertyAccessException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class ReflexiveMultiRuleBuilder<PatientType> {

  private MultiRuleBuilder<PatientType> multiRuleBuilder;
  private final Class<PatientType> subjectClass;

  ReflexiveMultiRuleBuilder(MultiRuleBuilder<PatientType> multiRuleBuilder, Class<PatientType> subjectClass) {
    this.multiRuleBuilder = multiRuleBuilder;
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
  public final <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addRules(String propertyName,
                                                                              Rule<PropertyType>... rules) {
    multiRuleBuilder.addRules(propertyName, getterFunction(propertyName), rules);
    return this;
  }

  /**
   * Adds a multiRule for validating an object that is a property of patient.</br>
   * If property getter is not found within the object or can not be called at runtime,
   * {@link PropertyAccessException} is thrown.
   *
   * @param propertyName   name of property to apply the multiRule to
   * @param multiRule      multiRule to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  public <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addMultiRule(String propertyName,
                                                                            MultiRule<PropertyType> multiRule) {
    return addMultiRule(t -> true, propertyName, multiRule);
  }

  /**
   * Adds a multiRule for validating a collection of objects that is a property of patient.</br>
   * If property getter is not found within the object or can not be called at runtime,
   * {@link PropertyAccessException} is thrown.
   *
   * @param propertyName   name of property to apply the multiRule to
   * @param multiRule      multiRule to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  public <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addMultiRuleForElements(String propertyName,
                                                                                       MultiRule<PropertyType> multiRule) {
    return addMultiRuleForElements(t -> true, propertyName, multiRule);
  }

  /**
   * If property getter is not found within the object or can not be called at runtime,
   * {@link PropertyAccessException} is thrown.
   *
   * @param condition      conditional predicate determining whether to run validation on the property
   * @param propertyName   name of property to apply the rules to
   * @param rules          rules to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  @SafeVarargs
  public final <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addRules(Predicate<PatientType> condition,
                                                                              String propertyName,
                                                                              Rule<PropertyType>... rules) {
    multiRuleBuilder.addRules(condition, propertyName, getterFunction(propertyName), rules);
    return this;
  }

  /**
   * Adds a multiRule for validating an object that is a property of patient.</br>
   * If property getter is not found within the object or can not be called at runtime,
   * {@link PropertyAccessException} is thrown.
   *
   * @param condition      conditional predicate determining whether to run validation on the property
   * @param propertyName   name of property to apply the multiRule to
   * @param multiRule      multiRule to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  public <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addMultiRule(Predicate<PatientType> condition,
                                                                            String propertyName,
                                                                            MultiRule<PropertyType> multiRule) {
    for (PropertyRule<PropertyType> rule : multiRule) {
      multiRuleBuilder.addRules(condition, propertyName + "." + rule.getProperty(), getterFunction(propertyName), rule);
    }
    return this;
  }

  /**
   * Adds a multiRule for validating a collection of objects that is a property of patient.</br>
   * If property getter is not found within the object or can not be called at runtime,
   * {@link PropertyAccessException} is thrown.
   *
   * @param condition      conditional predicate determining whether to run validation on the property
   * @param propertyName   name of property to apply the multiRule to
   * @param multiRule      multiRule to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  public <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addMultiRuleForElements(Predicate<PatientType> condition,
                                                                                       String propertyName,
                                                                                       MultiRule<PropertyType> multiRule) {
    for (PropertyRule<PropertyType> rule : multiRule) {
      multiRuleBuilder.addRules(condition, propertyName + "_element." + rule.getProperty(), getterFunction(propertyName), Rules.each(rule));
    }
    return this;
  }

  /**
   * Applies given MultiRules to all properties of given type found in the subject class.
   * For use with complex types.
   * Every public method returning a given type and starting with "get" is treated as a property getter.
   *
   * @param propertyClass    class of properties to apply the rules to
   * @param strictClassMatch if true, only properties with the exact specified class will have the rules applied.
   *                         If false, rules will be applied to properties of subtypes as well.
   * @param rules            MultiRules to apply
   * @param <PropertyType>   type of property
   * @return builder for chaining
   */
  @SafeVarargs
  public final <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addRulesForAll(Class<? extends PropertyType> propertyClass,
                                                                                    boolean strictClassMatch,
                                                                                    MultiRule<PropertyType>... rules) {
    Arrays.stream(subjectClass.getMethods())
        .filter(method -> Modifier.isPublic(method.getModifiers())
            && classMatches(method.getReturnType(), propertyClass, strictClassMatch)
            && (method.getName().startsWith("get")))
        .forEach(getter -> {
          String propertyName = NameUtil.uncapitalize(getter.getName().substring(3));
          multiRuleBuilder.addMultiRule(propertyName, getterFunction(getter), MultiRule.flatten(rules));
        });
    return this;
  }

  /**
   * Applies given rules to all properties of given type found in the subject class.
   * For use with primitive types.
   * Every public method returning a given type and starting with "get" (or "is" for boolean) is treated as a property getter.
   *
   * @param propertyClass    class of properties to apply the rules to
   * @param strictClassMatch if true, only properties with the exact specified class will have the rules applied.
   *                         If false, rules will be applied to properties of subtypes as well.
   * @param rules            rules to apply
   * @param <PropertyType>   type of property
   * @return builder for chaining
   */
  @SafeVarargs
  public final <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addRulesForAll(Class<? extends PropertyType> propertyClass,
                                                                                    boolean strictClassMatch,
                                                                                    Rule<PropertyType>... rules) {
    Arrays.stream(subjectClass.getMethods())
        .filter(method -> Modifier.isPublic(method.getModifiers())
            && classMatches(method.getReturnType(), propertyClass, strictClassMatch)
            && (method.getName().startsWith("get") || method.getName().startsWith("is")))
        .forEach(getter -> {
          boolean isIs = getter.getName().startsWith("is");
          String propertyName = NameUtil.uncapitalize(getter.getName().substring(isIs ? 2 : 3));
          multiRuleBuilder.addRules(propertyName, getterFunction(getter), rules);
        });
    return this;
  }

  /**
   * Short version of {@link ReflexiveMultiRuleBuilder#addRulesForAll(Class, boolean, Rule[]) addRulesForAll(clazz, false, rules)}
   */
  @SafeVarargs
  public final <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addRulesForAll(Class<? extends PropertyType> propertyClass,
                                                                                    Rule<PropertyType>... rules) {
    return addRulesForAll(propertyClass, false, rules);
  }

  /**
   * Short version of {@link ReflexiveMultiRuleBuilder#addRulesForAll(Class, boolean, MultiRule[]) addRulesForAll(clazz, false, multiRules)}
   */
  @SafeVarargs
  public final <PropertyType> ReflexiveMultiRuleBuilder<PatientType> addRulesForAll(Class<? extends PropertyType> propertyClass,
                                                                                    MultiRule<PropertyType>... rules) {
    return addRulesForAll(propertyClass, false, rules);
  }

  public MultiRule<PatientType> build() {
    return multiRuleBuilder.build();
  }


  private <PropertyType> Function<PatientType, PropertyType> getterFunction(String propertyName) {
    try {
      Class<?> type = subjectClass.getDeclaredField(propertyName).getType();
      String verb = type.equals(boolean.class) ? "is" : "get";
      String methodName = verb + NameUtil.capitalize(propertyName);
      Method method = subjectClass.getMethod(methodName);
      return getterFunction(method);
    } catch (NoSuchMethodException | NoSuchFieldException e) {
      throw PropertyAccessException.noGetter(propertyName, e);
    }
  }

  private <PropertyType> Function<PatientType, PropertyType> getterFunction(Method method) {
    return object -> {
      try {
        return (PropertyType) method.invoke(object);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw PropertyAccessException.noGetter(method.getName(), object, e);
      }
    };
  }

  private <T> boolean classMatches(Class<?> actualClass, Class<T> classToMatch, boolean strict) {
    return strict ? actualClass.equals(classToMatch) : classToMatch.isAssignableFrom(actualClass);
  }

}
