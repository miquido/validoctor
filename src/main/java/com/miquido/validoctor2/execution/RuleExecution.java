package com.miquido.validoctor2.execution;

import com.miquido.validoctor2.result.Ailment2;
import com.miquido.validoctor2.rule.Rule2;
import com.miquido.validoctor2.target.RuleTarget;

import java.util.Set;

/**
 * Abstract base class for definition of how the rule should be executed.<br>
 * It holds {@link RuleTarget} that provides patient objects for the rule extracted from the enclosing patient passed
 * in <b>perform</b> method, and the rule itself.
 * @param <T> type of enclosing patient class
 * @param <P> type of the actual rule patient class
 */
public abstract class RuleExecution<T, P> {
  protected final RuleTarget<T, P> target;
  protected final Rule2<P> rule;

  public RuleExecution(RuleTarget<T, P> target, Rule2<P> rule) {
    this.target = target;
    this.rule = rule;
  }

  public abstract Set<Ailment2> perform(T patient);
}
