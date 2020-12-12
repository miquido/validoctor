package com.miquido.validoctor.execution;

import com.miquido.validoctor.definition.Rule;
import com.miquido.validoctor.result.Ailment;
import com.miquido.validoctor.target.RuleTarget;

import java.util.Set;
import java.util.stream.Collectors;

public class EnclosingObjectRuleExecution<T> extends RuleExecution<T, T> {

  public EnclosingObjectRuleExecution(RuleTarget<T, T> target, Rule<T> rule) {
    super(target, rule);
  }

  @Override
  public Set<Ailment> perform(T patient) {
    //this could be simplified but is kept consistent with other RuleExecutions in case of future developments
    return target.getPatients(patient).stream()
        .flatMap(p ->
            rule.apply(p).stream()
                .map(ailment -> {
                  String fieldName = target.getFieldNames().get(0);
                  return new Ailment(fieldName, ailment.ailments);
                })
        )
        .collect(Collectors.toSet());
  }
}
