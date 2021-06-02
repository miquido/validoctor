package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.rule.Rule2;
import com.miquido.validoctor2.execution.CollectionFieldRuleExecution;
import com.miquido.validoctor2.execution.FieldRuleExecution;
import com.miquido.validoctor2.execution.RuleExecution;
import com.miquido.validoctor2.target.CollectionFieldRuleTarget;
import com.miquido.validoctor2.target.FieldRuleTarget;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionFieldRulesBuilder<T, P> extends AbstractFieldsRulesBuilder<T> {

  private final CollectionFieldRuleTarget<T, P> collectionTarget;
  private final FieldRuleTarget<T, Collection<P>> fieldTarget;

  CollectionFieldRulesBuilder(String field, RuleBuilder<T> ruleBuilder) {
    super(ruleBuilder);
    collectionTarget = new CollectionFieldRuleTarget<>(field, ruleBuilder.objectClass);
    fieldTarget = new FieldRuleTarget<>(field, ruleBuilder.objectClass);
  }

  /**
   * Add a batch of rules for collection as a whole.<br>
   * Multiple rules stated in one call of this method will all execute independently of each others' success.<br>
   * Multiple calls of this method can be chained. Each chained call adds a rules batch that will only be executed
   * if its preceding batch fully succeeds (no violations).
   * @param rules vararg list of rules for collection
   * @return this builder for chaining
   */
  @SafeVarargs
  public final CollectionFieldRulesBuilder<T, P> rules(Rule2<Collection<P>>... rules) {
    List<RuleExecution<T, ?>> ruleExecutions = Arrays.stream(rules)
        .map(rule -> new FieldRuleExecution<>(fieldTarget, rule))
        .collect(Collectors.toList());
    updateBatch(ruleExecutions);
    return this;
  }

  /**
   * Add a batch of rules for elements of collection. These rules will be executed for each element of collection.<br>
   * Multiple rules stated in one call of this method will all execute independently of each others' success.<br>
   * Multiple calls of this method can be chained. Each chained call adds a rules batch that will only be executed
   * if its preceding batch fully succeeds (no violations).<br>
   * This method can be mixed with <b>rules</b> method, for example to create batch of rules for elements that will
   * only execute after a batch of rules for collection itself succeeds:
   * <pre>{@code
   * <Number>collectionField("intSet")
   *  .rules(notNull(), collectionNotEmpty())
   *  .elementsRules(numberNonNegative())
   * }</pre>
   * @param rules vararg list of rules for collection
   * @return this builder for chaining
   */
  @SafeVarargs
  public final CollectionFieldRulesBuilder<T, P> elementsRules(Rule2<P>... rules) {
    List<RuleExecution<T, ?>> ruleExecutions = Arrays.stream(rules)
        .map(rule -> new CollectionFieldRuleExecution<>(collectionTarget, rule))
        .collect(Collectors.toList());
    updateBatch(ruleExecutions);
    return this;
  }
}
