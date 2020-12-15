package com.miquido.validoctor2.ruledefinition;

import com.miquido.validoctor2.Rule2;
import com.miquido.validoctor2.ruleexecution.ReducedFieldsRuleExecution;
import com.miquido.validoctor2.ruleexecution.RuleExecution;
import com.miquido.validoctor2.ruletarget.ReducedFieldsRuleTarget;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class ReducedFieldsRulesBuilder<T, P> extends AbstractFieldsRulesBuilder<T> {

  private final ReducedFieldsRuleTarget<T, P> target;

  protected ReducedFieldsRulesBuilder(RuleBuilder<T> ruleBuilder, List<String> fieldNames,
                                      BinaryOperator<P> reducer) {
    super(ruleBuilder);
    this.target = new ReducedFieldsRuleTarget<>(fieldNames, ruleBuilder.objectClass, reducer);
  }

  @SafeVarargs
  public final ReducedFieldsRulesBuilder<T, P> rules(Rule2<P>... rules) {
    List<RuleExecution<T, ?>> ruleExecutions = Arrays.stream(rules)
        .map(rule -> new ReducedFieldsRuleExecution<>(target, rule))
        .collect(Collectors.toList());
    updateBranch(ruleExecutions);
    return this;
  }
}
