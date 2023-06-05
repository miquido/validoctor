package com.miquido.validoctor.execution;

import com.miquido.validoctor.result.Ailment;
import com.miquido.validoctor.definition.Rule;
import com.miquido.validoctor.target.FieldRuleTarget;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                    String fieldName = Stream.of(target.getFieldNames().get(0), ailment.field)
                        .filter(s -> s != null && !s.isEmpty())
                        .collect(Collectors.joining("."));
                    return new Ailment(fieldName, ailment.ailments);
                })
        )
        .collect(Collectors.toSet());
  }
}
