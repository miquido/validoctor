package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.result.Ailment2;
import com.miquido.validoctor2.rule.Rule2;
import com.miquido.validoctor2.execution.RuleExecutionBranch;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExaminationDefinition<T> implements Rule2<T> {

  private final List<RuleExecutionBranch<T>> rootBranches;

  public ExaminationDefinition(List<RuleExecutionBranch<T>> branches) {
    this.rootBranches = branches;
  }

  @Override
  public Set<Ailment2> apply(T patient) {
    return rootBranches.stream()
        .flatMap(branch -> branch.perform(patient).stream())
        .collect(Collectors.toSet());
  }
}
