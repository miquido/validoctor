package com.miquido.validoctor.execution;

import com.miquido.validoctor.result.Ailment;
import com.miquido.validoctor.definition.Rule;
import com.miquido.validoctor.target.CollectionFieldRuleTarget;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CollectionFieldRuleExecution<T, P> extends RuleExecution<T, P> {

  public CollectionFieldRuleExecution(CollectionFieldRuleTarget<T, P> target, Rule<P> rule) {
    super(target, rule);
  }

  public Set<Ailment> perform(T patient) {
    final String fieldName = target.getFieldNames().get(0);
    List<P> patients = target.getPatients(patient);
    return IntStream.range(0, patients.size())
        .mapToObj(index ->
            rule.apply(patients.get(index)).stream()
                .map(ailment -> {
                    String field = Stream.of(fieldName + "[" + index + "]", ailment.field)
                        .filter(s -> s != null && !s.isEmpty())
                        .collect(Collectors.joining("."));
                    return new Ailment(field, ailment.ailments);
                })
        )
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }
}
