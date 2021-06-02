package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.execution.RuleExecution;
import com.miquido.validoctor2.result.Ailment2;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExaminationDefinition<T> implements Rule2<T> {

  private final List<RuleExecution<T, ?>> ruleExecutions;
  private final Predicate<T> condition;
  private final Rule2<T> dependency;

  public ExaminationDefinition(List<RuleExecution<T, ?>> ruleExecutions) {
    this(ruleExecutions, obj -> true, null);
  }

  public ExaminationDefinition(List<RuleExecution<T, ?>> ruleExecutions, Predicate<T> condition, Rule2<T> dependency) {
    this.ruleExecutions = ruleExecutions;
    this.condition = condition;
    this.dependency = dependency;
  }

  @Override
  public Set<Ailment2> apply(T patient) {
    Set<Ailment2> dependencyAilments = dependency == null ? Collections.emptySet() : dependency.apply(patient);
    if (condition.test(patient) && dependencyAilments.isEmpty()) {
      return ruleExecutions.stream()
          .flatMap(re -> re.perform(patient).stream())
          .collect(Collectors.toSet());
    } else {
      return dependencyAilments;
    }
  }

  @Override
  public Rule2<T> withCondition(Predicate<T> condition) {
    return new ExaminationDefinition<>(ruleExecutions, condition, dependency);
  }

  @Override
  public Rule2<T> withDependency(Rule2<T> previousRule) {
    return new ExaminationDefinition<>(ruleExecutions, condition, previousRule);
  }

  @Override
  public Rule2<T> withViolationMessage(String violationMessage) {
    throw new IllegalArgumentException("violation message changes not supported for complex rules");
  }
}
