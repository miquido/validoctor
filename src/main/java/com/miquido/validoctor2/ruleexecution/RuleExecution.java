package com.miquido.validoctor2.ruleexecution;

import com.miquido.validoctor2.Ailment2;
import com.miquido.validoctor2.Rule2;
import com.miquido.validoctor2.ruletarget.RuleTarget;

import java.util.Set;

public abstract class RuleExecution<T, P> {
  protected final RuleTarget<T, P> target;
  protected final Rule2<P> rule;

  public RuleExecution(RuleTarget<T, P> target, Rule2<P> rule) {
    this.target = target;
    this.rule = rule;
  }

  public abstract Set<Ailment2> perform(T patient);
}
