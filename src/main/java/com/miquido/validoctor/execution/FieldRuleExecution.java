package com.miquido.validoctor.execution;

import com.miquido.validoctor.result.Ailment;
import com.miquido.validoctor.definition.Rule;
import com.miquido.validoctor.target.FieldRuleTarget;

import java.util.Set;
import java.util.stream.Collectors;

public class FieldRuleExecution<T, P> extends RuleExecution<T, P> {

  public FieldRuleExecution(FieldRuleTarget<T, P> target, Rule<P> rule) {
    super(target, rule);
  }

  @Override
  public Set<Ailment> perform(T patient) {
    return target.getPatients(patient).stream()
        .flatMap(p ->
            rule.apply(p).stream()
                .map(ailment -> {
                  String fieldName = target.getFieldNames().get(0);
                  if (ailment.field != null) {
                    fieldName += "." + ailment.field;
                  }
                  return new Ailment(fieldName, ailment.ailments);
                })
        )
        .collect(Collectors.toSet());
  }
}
