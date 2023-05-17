package com.miquido.validoctor.definition;

import com.miquido.validoctor.execution.RuleExecution;
import com.miquido.validoctor.result.Ailment;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExaminationDefinition<T> implements Rule<T> {

  private final List<RuleExecution<T, ?>> ruleExecutions;
  private final Predicate<T> condition;
  private final Rule<T> dependency;
  private String messageOverride;

  public ExaminationDefinition(List<RuleExecution<T, ?>> ruleExecutions) {
    this(ruleExecutions, obj -> true, null);
  }

  public ExaminationDefinition(List<RuleExecution<T, ?>> ruleExecutions, Predicate<T> condition, Rule<T> dependency) {
    this.ruleExecutions = ruleExecutions;
    this.condition = condition;
    this.dependency = dependency;
  }

  @Override
  public Set<Ailment> apply(T patient) {
    Set<Ailment> dependencyAilments = dependency == null ? Collections.emptySet() : dependency.apply(patient);
    if (condition.test(patient) && dependencyAilments.isEmpty()) {
      return ruleExecutions.stream()
          .flatMap(re -> re.perform(patient).stream())
          .map(ailment -> {
            if (messageOverride != null) return new Ailment(ailment.field, messageOverride);
            else return ailment;
          })
          .collect(Collectors.toSet());
    } else {
      return dependencyAilments;
    }
  }

  @Override
  public Rule<T> withCondition(Predicate<T> condition) {
    return new ExaminationDefinition<>(ruleExecutions, condition, dependency);
  }

  @Override
  public Rule<T> withDependency(Rule<T> previousRule) {
    return new ExaminationDefinition<>(ruleExecutions, condition, previousRule);
  }

  @Override
  public Rule<T> withViolationMessage(String violationMessage) {
    messageOverride = violationMessage;
    return this;
  }
}
