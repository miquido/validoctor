package com.miquido.validoctor2.ruledefinition;

import com.miquido.validoctor2.ruleexecution.RuleExecution;
import com.miquido.validoctor2.ruleexecution.RuleExecutionBranch;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

public class AbstractFieldsRulesBuilder<T> {
  protected final RuleBuilder<T> ruleBuilder;
  protected RuleExecutionBranch<T> currentBranch;

  protected AbstractFieldsRulesBuilder(RuleBuilder<T> ruleBuilder) {
    this.ruleBuilder = ruleBuilder;
  }

  public <P> SingleFieldRulesBuilder<T, P> field(String field) {
    return new SingleFieldRulesBuilder<>(field, ruleBuilder);
  }

  public <P> CollectionFieldRulesBuilder<T, P> collectionField(String field) {
    return new CollectionFieldRulesBuilder<>(field, ruleBuilder);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allTyped(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(clazz, true, ruleBuilder);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allAssignable(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(clazz, false, ruleBuilder);
  }

  public <P> ReducedFieldsRulesBuilder<T, P> fields(String field1, String field2, BinaryOperator<P> reducer) {
    return new ReducedFieldsRulesBuilder<>(ruleBuilder, Arrays.asList(field1, field2), reducer);
  }

  public ExaminationDefinition<T> build() {
    return ruleBuilder.build();
  }


  protected void updateBranch(List<RuleExecution<T, ?>> ruleExecutions) {
    RuleExecutionBranch<T> newBranch = new RuleExecutionBranch<>(ruleExecutions);
    if (currentBranch == null) {
      ruleBuilder.rootBranches.add(newBranch);
    } else {
      currentBranch.setNextBranch(newBranch);
    }
    currentBranch = newBranch;
  }
}
