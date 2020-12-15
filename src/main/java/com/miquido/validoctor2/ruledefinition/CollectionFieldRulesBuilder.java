package com.miquido.validoctor2.ruledefinition;

import com.miquido.validoctor2.Rule2;
import com.miquido.validoctor2.ruleexecution.CollectionFieldRuleExecution;
import com.miquido.validoctor2.ruleexecution.FieldRuleExecution;
import com.miquido.validoctor2.ruleexecution.RuleExecution;
import com.miquido.validoctor2.ruletarget.CollectionFieldRuleTarget;
import com.miquido.validoctor2.ruletarget.FieldRuleTarget;

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

  @SafeVarargs
  public final CollectionFieldRulesBuilder<T, P> rules(Rule2<Collection<P>>... rules) {
    List<RuleExecution<T, ?>> ruleExecutions = Arrays.stream(rules)
        .map(rule -> new FieldRuleExecution<>(fieldTarget, rule))
        .collect(Collectors.toList());
    updateBranch(ruleExecutions);
    return this;
  }

  @SafeVarargs
  public final CollectionFieldRulesBuilder<T, P> elementsRules(Rule2<P>... rules) {
    List<RuleExecution<T, ?>> ruleExecutions = Arrays.stream(rules)
        .map(rule -> new CollectionFieldRuleExecution<>(collectionTarget, rule))
        .collect(Collectors.toList());
    updateBranch(ruleExecutions);
    return this;
  }
}
