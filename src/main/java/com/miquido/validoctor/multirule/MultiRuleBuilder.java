package com.miquido.validoctor.multirule;

import com.miquido.validoctor.rule.Rule;
import com.miquido.validoctor.rule.Rules;

import java.util.Collection;
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
   * - Subject class may contain getters for boolean properties that start with "is".<br/>
   */
  public ReflexiveMultiRuleBuilder<T> reflexiveProperties(Class<T> subjectClass) {
    return new ReflexiveMultiRuleBuilder<>(this, subjectClass);
  }

  /**
   * @param propertyName   name of the property the rules apply to
   * @param propertyGetter getter of the property the rules apply to
   * @param rules          rules to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  @SafeVarargs
  public final <PropertyType> MultiRuleBuilder<T> addRules(String propertyName,
                                                           Function<T, PropertyType> propertyGetter,
                                                           Rule<PropertyType>... rules) {
    return addRules(t -> true, propertyName, propertyGetter, rules);
  }

  /**
   * Adds a multiRule for validating an object that is a property of patient.
   *
   * @param propertyName   name of property to apply the multiRule to
   * @param propertyGetter getter of the property the rules apply to
   * @param multiRule      multiRule to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  public <PropertyType> MultiRuleBuilder<T> addMultiRule(String propertyName,
                                                         Function<T, PropertyType> propertyGetter,
                                                         MultiRule<PropertyType> multiRule) {
    return addMultiRule(t -> true, propertyName, propertyGetter, multiRule);
  }

  /**
   * Adds a multiRule for validating a collection of objects that is a property of patient.
   *
   * @param propertyName   name of property to apply the multiRule to
   * @param propertyGetter getter of the collection property that will have rules applied to its elements
   * @param multiRule      multiRule to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  public <PropertyType> MultiRuleBuilder<T> addMultiRuleForElements(String propertyName,
                                                                    Function<T, Collection<PropertyType>> propertyGetter,
                                                                    MultiRule<PropertyType> multiRule) {
    return addMultiRuleForElements(t -> true, propertyName, propertyGetter, multiRule);
  }

  /**
   * @param condition      conditional predicate determining whether to run validation on the property
   * @param propertyName   name of the property the rules apply to
   * @param propertyGetter getter of the property the rules apply to
   * @param rules          rules to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  @SafeVarargs
  public final <PropertyType> MultiRuleBuilder<T> addRules(Predicate<T> condition,
                                                           String propertyName,
                                                           Function<T, PropertyType> propertyGetter,
                                                           Rule<PropertyType>... rules) {
    for (Rule<PropertyType> rule : rules) {
      this.rules.add(new ConditionalPropertyRule<>(propertyName, propertyGetter, condition, rule));
    }
    return this;
  }

  /**
   * Adds a multiRule for validating an object that is a property of patient.
   *
   * @param condition      conditional predicate determining whether to run validation on the property
   * @param propertyName   name of property to apply the multiRule to
   * @param propertyGetter getter of the property the rules apply to
   * @param multiRule      multiRule to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  public <PropertyType> MultiRuleBuilder<T> addMultiRule(Predicate<T> condition,
                                                         String propertyName,
                                                         Function<T, PropertyType> propertyGetter,
                                                         MultiRule<PropertyType> multiRule) {
    for (PropertyRule<PropertyType> rule : multiRule) {
      addRules(condition, propertyName + "." + rule.getProperty(), propertyGetter, rule);
    }
    return this;
  }

  /**
   * Adds a multiRule for validating a collection of objects that is a property of patient.
   *
   * @param condition      conditional predicate determining whether to run validation on the property
   * @param propertyName   name of property to apply the multiRule to
   * @param propertyGetter getter of the collection property that will have rules applied to its elements
   * @param multiRule      multiRule to apply
   * @param <PropertyType> type of property
   * @return builder for chaining
   */
  public <PropertyType> MultiRuleBuilder<T> addMultiRuleForElements(Predicate<T> condition,
                                                                    String propertyName,
                                                                    Function<T, Collection<PropertyType>> propertyGetter,
                                                                    MultiRule<PropertyType> multiRule) {
    for (PropertyRule<PropertyType> rule : multiRule) {
      addRules(condition, propertyName + "_element." + rule.getProperty(), propertyGetter, Rules.each(rule));
    }
    return this;
  }

  public MultiRule<T> build() {
    return rules;
  }

}
