package com.miquido.validoctor.execution;

import com.miquido.validoctor.result.Ailment;
import com.miquido.validoctor.definition.Rule;
import com.miquido.validoctor.target.RuleTarget;

import java.util.Set;

/**
 * Abstract base class for definition of how the rule should be executed.<br>
 * It holds {@link RuleTarget} that provides patient objects for the rule, extracting them from the enclosing patient passed
 * in <b>perform</b> method, and the rule itself.
 * @param <T> type of enclosing patient class
 * @param <P> type of the actual rule patient class
 */
public abstract class RuleExecution<T, P> {
  protected final RuleTarget<T, P> target;
  protected final Rule<P> rule;

  public RuleExecution(RuleTarget<T, P> target, Rule<P> rule) {
    this.target = target;
    this.rule = rule;
  }

  public abstract Set<Ailment> perform(T patient);
}
