package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.rule.Rule2;
import com.miquido.validoctor2.execution.ReducedFieldsRuleExecution;
import com.miquido.validoctor2.execution.RuleExecution;
import com.miquido.validoctor2.target.ReducedFieldsRuleTarget;

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

  /**
   * Add a batch of rules for reduced value.<br>
   * Multiple rules stated in one call of this method will all execute independently of each others' success.<br>
   * Multiple calls of this method can be chained. Each chained call adds a rules batch that will only be executed
   * if its preceding batch fully succeeds (no violations).
   * @param rules vararg list of rules for reduced value
   * @return this builder for chaining
   */
  @SafeVarargs
  public final ReducedFieldsRulesBuilder<T, P> rules(Rule2<P>... rules) {
    List<RuleExecution<T, ?>> ruleExecutions = Arrays.stream(rules)
        .map(rule -> new ReducedFieldsRuleExecution<>(target, rule))
        .collect(Collectors.toList());
    updateBatch(ruleExecutions);
    return this;
  }
}
