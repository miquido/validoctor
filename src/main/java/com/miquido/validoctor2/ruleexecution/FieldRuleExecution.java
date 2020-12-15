package com.miquido.validoctor2.ruleexecution;

import com.miquido.validoctor2.Ailment2;
import com.miquido.validoctor2.Rule2;
import com.miquido.validoctor2.ruletarget.FieldRuleTarget;

import java.util.Set;
import java.util.stream.Collectors;

public class FieldRuleExecution<T, P> extends RuleExecution<T, P> {

  public FieldRuleExecution(FieldRuleTarget<T, P> target, Rule2<P> rule) {
    super(target, rule);
  }

  @Override
  public Set<Ailment2> perform(T patient) {
    return target.getPatients(patient).stream()
        .flatMap(p ->
            rule.apply(p).stream()
                .map(ailment -> {
                  String fieldName = target.getFieldNames().get(0);
                  if (ailment.field != null) {
                    fieldName += "." + ailment.field;
                  }
                  return new Ailment2(fieldName, ailment.value, ailment.ailments);
                })
        )
        .collect(Collectors.toSet());
  }
}
