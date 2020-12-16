package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.rule.Rule2;
import com.miquido.validoctor2.execution.RuleExecution;
import com.miquido.validoctor2.execution.TypeRuleExecution;
import com.miquido.validoctor2.target.TypeRuleTarget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeFieldsRulesBuilder<T, P> extends AbstractFieldsRulesBuilder<T> {

  private final TypeRuleTarget<T, P> target;

  TypeFieldsRulesBuilder(Class<? extends P> fieldsClass, boolean strictMatch,
                         RuleBuilder<T> ruleBuilder) {
    super(ruleBuilder);
    target = new TypeRuleTarget<>(ruleBuilder.objectClass, fieldsClass, strictMatch);
  }

  @SafeVarargs
  public final TypeFieldsRulesBuilder<T, P> rules(Rule2<? super P>... rules) {
    List<RuleExecution<T, ?>> ruleExecutions = Arrays.stream(rules)
        .map(rule -> new TypeRuleExecution<>(target, (Rule2<P>) rule))
        .collect(Collectors.toList());
    updateBranch(ruleExecutions);
    return this;
  }
}
