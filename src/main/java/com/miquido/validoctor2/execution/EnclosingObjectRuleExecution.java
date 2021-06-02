package com.miquido.validoctor2.execution;

import com.miquido.validoctor2.definition.Rule2;
import com.miquido.validoctor2.result.Ailment2;
import com.miquido.validoctor2.target.RuleTarget;

import java.util.Set;
import java.util.stream.Collectors;

public class EnclosingObjectRuleExecution<T> extends RuleExecution<T, T> {

  public EnclosingObjectRuleExecution(RuleTarget<T, T> target, Rule2<T> rule) {
    super(target, rule);
  }

  @Override
  public Set<Ailment2> perform(T patient) {
    //this could be simplified but is kept consistent with other RuleExecutions in case of future developments
    return target.getPatients(patient).stream()
        .flatMap(p ->
            rule.apply(p).stream()
                .map(ailment -> {
                  String fieldName = target.getFieldNames().get(0);
                  return new Ailment2(fieldName, ailment.ailments);
                })
        )
        .collect(Collectors.toSet());
  }
}
