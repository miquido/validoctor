package com.miquido.validoctor.execution;

import com.miquido.validoctor.definition.Rule;
import com.miquido.validoctor.result.Ailment;
import com.miquido.validoctor.target.MultipleFieldsRuleTarget;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MultipleFieldsRuleExecution<T, P> extends RuleExecution<T, P> {

  public MultipleFieldsRuleExecution(MultipleFieldsRuleTarget<T, P> target, Rule<P> rule) {
    super(target, rule);
  }

  @Override
  public Set<Ailment> perform(T patient) {
    List<String> fieldNames = target.getFieldNames();
    List<P> patients = target.getPatients(patient);
    return IntStream.range(0, patients.size())
        .mapToObj(index ->
            rule.apply(patients.get(index)).stream()
                .map(ailment -> {
                    String field = Stream.of(fieldNames.get(index), ailment.field)
                        .filter(s -> s != null && !s.isEmpty())
                        .collect(Collectors.joining("."));
                    return new Ailment(field, ailment.ailments);
                })
        )
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }
}
