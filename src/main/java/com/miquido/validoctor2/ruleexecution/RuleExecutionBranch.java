package com.miquido.validoctor2.ruleexecution;

import com.miquido.validoctor2.Ailment2;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleExecutionBranch<T> {

  private final List<RuleExecution<T, ?>> ruleExecutions;
  private RuleExecutionBranch<T> nextBranch;

  public RuleExecutionBranch(List<RuleExecution<T, ?>> ruleExecutions) {
    this.ruleExecutions = ruleExecutions;
  }

  public void setNextBranch(RuleExecutionBranch<T> nextBranch) {
    this.nextBranch = nextBranch;
  }

  public Set<Ailment2> perform(T patient) {
    Set<Ailment2> ailments = ruleExecutions.stream()
        .flatMap(re -> re.perform(patient).stream())
        .collect(Collectors.toSet());
    if (nextBranch != null && ailments.isEmpty()) {
      ailments.addAll(nextBranch.perform(patient));
    }
    return ailments;
  }
}
