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

  /**
   * Add a batch of rules for field value.<br>
   * Multiple rules stated in one call of this method will all execute independently of each others' success.<br>
   * Multiple calls of this method can be chained. Each chained call adds a rules batch that will only be executed
   * if its preceding batch fully succeeds (no violations).
   * @param rules vararg list of rules for field value
   * @return this builder for chaining
   */
  @SafeVarargs
  public final SingleFieldRulesBuilder<T, P> rules(Rule2<P>... rules) {
    List<RuleExecution<T, ?>> ruleExecutions = Arrays.stream(rules)
        .map(rule -> new FieldRuleExecution<>(target, rule))
        .collect(Collectors.toList());
    updateBatch(ruleExecutions);
    return this;
  }
}
