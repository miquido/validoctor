package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.execution.RuleExecution;
import com.miquido.validoctor2.execution.RuleExecutionBatch;
import com.miquido.validoctor2.rule.Rule2;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

public abstract class AbstractFieldsRulesBuilder<T> {
  protected final RuleBuilder<T> ruleBuilder;
  protected RuleExecutionBatch<T> currentBatch;

  protected AbstractFieldsRulesBuilder(RuleBuilder<T> ruleBuilder) {
    this.ruleBuilder = ruleBuilder;
  }

  /**
   * Start rule batches definition for a field.
   * @param field field name
   * @param <P> field type
   * @return builder that allows specifying rules in batches
   */
  public <P> SingleFieldRulesBuilder<T, P> field(String field) {
    return new SingleFieldRulesBuilder<>(field, ruleBuilder);
  }

  /**
   * Start rule batches definition for a collection type field. This differs from a normal field in that it also allows
   * defining rules for elements of collection.
   * @param field field name
   * @param <P> type of elements of collection
   * @return builder that allows specifying rules for both collection and its elements in batches
   */
  public <P> CollectionFieldRulesBuilder<T, P> collectionField(String field) {
    return new CollectionFieldRulesBuilder<>(field, ruleBuilder);
  }

  /**
   * Start rule batches definition for all fields with specified type.
   * @param clazz class of fields
   * @param <P> type of fields
   * @return builder that allows specifying rules in batches
   */
  public <P> TypeFieldsRulesBuilder<T, P> allTyped(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(clazz, true, ruleBuilder);
  }

  /**
   * Start rule batches definition for all fields assignable to specified class. Meaning, the specified class is either
   * the same or a superclass of their classes (passing Number.class will cover all Floats, all Integers, all Doubles, etc.).
   * @param clazz class
   * @param <P> type
   * @return builder that allows specifying rules in batches
   */
  public <P> TypeFieldsRulesBuilder<T, P> allAssignable(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(clazz, false, ruleBuilder);
  }

  /**
   * Start rule batches definition for all fields of the object.
   * @return builder that allows specifying rules in batches
   */
  public TypeFieldsRulesBuilder<T, Object> all() {
    return new TypeFieldsRulesBuilder<>(Object.class, false, ruleBuilder);
  }

  /**
   * Start rule batches definition for a reduced value of two fields.
   * @param field1 name of first field
   * @param field2 name of second field
   * @param reducer reduction operation to be performed on fields before executing rules
   * @param <P> type of fields
   * @return builder that allows specifying rules in batches
   */
  public <P> ReducedFieldsRulesBuilder<T, P> fields(String field1, String field2, BinaryOperator<P> reducer) {
    return new ReducedFieldsRulesBuilder<>(ruleBuilder, Arrays.asList(field1, field2), reducer);
  }

  /**
   * @return a composite rule holding all defined batches, ready to be passed into
   * {@link com.miquido.validoctor2.Validoctor2#examine(Object, Rule2[]) Validoctor's examine method}
   */
  public Rule2<T> build() {
    return ruleBuilder.build();
  }


  protected void updateBatch(List<RuleExecution<T, ?>> ruleExecutions) {
    RuleExecutionBatch<T> newBatch = new RuleExecutionBatch<>(ruleExecutions);
    if (currentBatch == null) {
      ruleBuilder.rootBatches.add(newBatch);
    } else {
      currentBatch.setNextBatch(newBatch);
    }
    currentBatch = newBatch;
  }
}
