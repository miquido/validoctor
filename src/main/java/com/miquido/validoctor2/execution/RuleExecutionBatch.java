package com.miquido.validoctor2.execution;

import com.miquido.validoctor2.result.Ailment2;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleExecutionBatch<T> {

  private final List<RuleExecution<T, ?>> ruleExecutions;
  private RuleExecutionBatch<T> nextBatch;

  public RuleExecutionBatch(List<RuleExecution<T, ?>> ruleExecutions) {
    this.ruleExecutions = ruleExecutions;
  }

  public void setNextBatch(RuleExecutionBatch<T> nextBatch) {
    this.nextBatch = nextBatch;
  }

  public Set<Ailment2> perform(T patient) {
    Set<Ailment2> ailments = ruleExecutions.stream()
        .flatMap(re -> re.perform(patient).stream())
        .collect(Collectors.toSet());
    if (nextBatch != null && ailments.isEmpty()) {
      ailments.addAll(nextBatch.perform(patient));
    }
    return ailments;
  }
}
