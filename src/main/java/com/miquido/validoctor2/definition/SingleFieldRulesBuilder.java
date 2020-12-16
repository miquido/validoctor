package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.rule.Rule2;
import com.miquido.validoctor2.execution.FieldRuleExecution;
import com.miquido.validoctor2.execution.RuleExecution;
import com.miquido.validoctor2.target.FieldRuleTarget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SingleFieldRulesBuilder<T, P> extends AbstractFieldsRulesBuilder<T> {

  private final FieldRuleTarget<T, P> target;

  SingleFieldRulesBuilder(String field, RuleBuilder<T> ruleBuilder) {
    super(ruleBuilder);
    target = new FieldRuleTarget<>(field, ruleBuilder.objectClass);
  }

  @SafeVarargs
  public final SingleFieldRulesBuilder<T, P> rules(Rule2<P>... rules) {
    List<RuleExecution<T, ?>> ruleExecutions = Arrays.stream(rules)
        .map(rule -> new FieldRuleExecution<>(target, rule))
        .collect(Collectors.toList());
    updateBranch(ruleExecutions);
    return this;
  }
}
