package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.execution.CollectionFieldRuleExecution;
import com.miquido.validoctor2.execution.EnclosingObjectRuleExecution;
import com.miquido.validoctor2.execution.FieldRuleExecution;
import com.miquido.validoctor2.execution.MultipleFieldsRuleExecution;
import com.miquido.validoctor2.execution.ReducedFieldsRuleExecution;
import com.miquido.validoctor2.execution.RuleExecution;
import com.miquido.validoctor2.execution.TypeRuleExecution;
import com.miquido.validoctor2.target.CollectionFieldRuleTarget;
import com.miquido.validoctor2.target.EnclosingObjectTarget;
import com.miquido.validoctor2.target.FieldRuleTarget;
import com.miquido.validoctor2.target.MultipleFieldsRuleTarget;
import com.miquido.validoctor2.target.ReducedFieldsRuleTarget;
import com.miquido.validoctor2.target.TypeRuleTarget;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RuleBuilder<T> {

  final List<RuleExecution<T, ?>> ruleExecutions;
  final Class<T> objectClass;

  public RuleBuilder(Class<T> objectClass) {
    this.objectClass = objectClass;
    ruleExecutions = new ArrayList<>();
  }

  /**
   * Add rules for single field.
   * @param field field name
   * @param rules rules
   * @param <P> field type
   * @return builder
   */
  @SafeVarargs
  public final <P> RuleBuilder<T> field(String field, Rule2<P>... rules) {
    FieldRuleTarget<T, P> target = new FieldRuleTarget<>(field, objectClass);
    List<RuleExecution<T, P>> executions = Arrays.stream(rules)
        .map(rule -> new FieldRuleExecution<>(target, rule))
        .collect(Collectors.toList());
    ruleExecutions.addAll(executions);
    return this;
  }

  /**
   * Add rules for elements of a collection-type field.
   * @param field field name
   * @param rules rules
   * @param <P> elements type
   * @return builder
   */
  @SafeVarargs
  public final <P> RuleBuilder<T> elements(String field, Rule2<P>... rules) {
    CollectionFieldRuleTarget<T, P> collectionTarget = new CollectionFieldRuleTarget<>(field, objectClass);
    List<RuleExecution<T, P>> executions = Arrays.stream(rules)
        .map(rule -> new CollectionFieldRuleExecution<>(collectionTarget, rule))
        .collect(Collectors.toList());
    ruleExecutions.addAll(executions);
    return this;
  }

  /**
   * Add rules for all fields with specified type.
   * @param clazz class of fields
   * @param <P> type of fields
   * @return builder
   */
  @SafeVarargs
  public final <P> RuleBuilder<T> allTyped(Class<? extends P> clazz, Rule2<P>... rules) {
    return addClassRules(clazz, true, rules);
  }

  /**
   * Add rules for all fields assignable to specified class. Meaning, the specified class is either
   * the same or a superclass of their classes (passing Number.class will cover all Floats, all Integers, all Doubles, etc.).
   * @param clazz class
   * @param <P> type
   * @return builder
   */
  @SafeVarargs
  public final <P> RuleBuilder<T> allAssignable(Class<? extends P> clazz, Rule2<P>... rules) {
    return addClassRules(clazz, false, rules);
  }

  /**
   * Add rules for all fields of the object.
   * @return builder
   */
  @SafeVarargs
  public final RuleBuilder<T> all(Rule2<Object>... rules) {
    return addClassRules(Object.class, false, rules);
  }

  /**
   * Adds rules for a reduced value of two fields. Fields are passed to the reducer in order they are listed.
   * @param field1 name of first field
   * @param field2 name of second field
   * @param reducer reduction operation to be performed on fields before executing rules
   * @param <P> type of fields
   * @return builder
   */
  @SafeVarargs
  public final <P> RuleBuilder<T> reducedFields(String field1, String field2, BinaryOperator<P> reducer, Rule2<P>... rules) {
    ReducedFieldsRuleTarget<T, P> target = new ReducedFieldsRuleTarget<>(Arrays.asList(field1, field2), objectClass, reducer);
    List<RuleExecution<T, P>> executions = Arrays.stream(rules)
        .map(rule -> new ReducedFieldsRuleExecution<>(target, rule))
        .collect(Collectors.toList());
    ruleExecutions.addAll(executions);
    return this;
  }

  /**
   * Adds rules for a multiple fields of same type. Each field has the rules executed independently.
   * @param fields names of field
   * @param <P> type of fields
   * @return builder
   */
  @SafeVarargs
  public final <P> RuleBuilder<T> fields(List<String> fields, Rule2<P>... rules) {
    MultipleFieldsRuleTarget<T, P> target = new MultipleFieldsRuleTarget<>(fields, objectClass);
    List<RuleExecution<T, P>> executions = Arrays.stream(rules)
        .map(rule -> new MultipleFieldsRuleExecution<>(target, rule))
        .collect(Collectors.toList());
    ruleExecutions.addAll(executions);
    return this;
  }

  /**
   * Defines and adds a rule to run on the object as a whole. This is useful for defining rules that validate
   * relations between fields of the object.
   * @param violationMessage error message used in case of violation
   * @param fieldName name of field violation of this rule should be attached to. This can also be a custom non-field name
   * @param predicate predicate to test
   * @return builder
   */
  public RuleBuilder<T> rule(String violationMessage, String fieldName, Predicate<T> predicate) {
    EnclosingObjectTarget<T> target = new EnclosingObjectTarget<>(fieldName);
    RuleExecution<T, T> execution = new EnclosingObjectRuleExecution<>(target, new SimpleRule2<>(violationMessage, predicate));
    ruleExecutions.add(execution);
    return this;
  }

  /**
   * @return a composite rule holding all defined batches, ready to be passed into
   * {@link com.miquido.validoctor2.Validoctor2#examine(Object, Rule2[]) Validoctor's examine method}
   */
  public Rule2<T> build() {
    return new ExaminationDefinition<>(ruleExecutions);
  }


  @NotNull
  private <P> RuleBuilder<T> addClassRules(Class<? extends P> clazz, boolean strictMatch, Rule2<P>[] rules) {
    TypeRuleTarget<T, P> target = new TypeRuleTarget<>(objectClass, clazz, strictMatch);
    List<RuleExecution<T, P>> executions = Arrays.stream(rules)
        .map(rule -> new TypeRuleExecution<>(target, rule))
        .collect(Collectors.toList());
    ruleExecutions.addAll(executions);
    return this;
  }
}
