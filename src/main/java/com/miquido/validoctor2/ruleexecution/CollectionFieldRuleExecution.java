package com.miquido.validoctor2.ruleexecution;

import com.miquido.validoctor2.Ailment2;
import com.miquido.validoctor2.Rule2;
import com.miquido.validoctor2.ruletarget.CollectionFieldRuleTarget;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CollectionFieldRuleExecution<T, P> extends RuleExecution<T, P> {

  public CollectionFieldRuleExecution(CollectionFieldRuleTarget<T, P> target, Rule2<P> rule) {
    super(target, rule);
  }

  public Set<Ailment2> perform(T patient) {
    final String fieldName = target.getFieldNames().get(0);
    List<P> patients = target.getPatients(patient);
    return IntStream.range(0, patients.size())
        .mapToObj(index ->
            rule.apply(patients.get(index)).stream()
                .map(ailment -> {
                  String field = fieldName + "[" + index + "]";
                  if (ailment.field != null) {
                    field += "." + ailment.field;
                  }
                  return new Ailment2(field, ailment.value, ailment.ailments);
                })
        )
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }
}
