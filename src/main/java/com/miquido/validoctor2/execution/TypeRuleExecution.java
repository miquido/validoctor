package com.miquido.validoctor2.execution;

import com.miquido.validoctor2.result.Ailment2;
import com.miquido.validoctor2.rule.Rule2;
import com.miquido.validoctor2.target.TypeRuleTarget;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TypeRuleExecution<T, P> extends RuleExecution<T, P> {

  public TypeRuleExecution(TypeRuleTarget<T, P> target, Rule2<P> rule) {
    super(target, rule);
  }

  @Override
  public Set<Ailment2> perform(T patient) {
    List<String> fieldNames = target.getFieldNames();
    List<P> patients = target.getPatients(patient);
    return IntStream.range(0, patients.size())
        .mapToObj(index ->
            rule.apply(patients.get(index)).stream()
                .map(ailment -> new Ailment2(fieldNames.get(index), ailment.value, ailment.ailments))
        )
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }
}
