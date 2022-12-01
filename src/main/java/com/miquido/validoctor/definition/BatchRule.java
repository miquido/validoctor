package com.miquido.validoctor.definition;

import com.miquido.validoctor.result.Ailment;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BatchRule<T> implements Rule<T> {

    private final List<Rule<T>> rules;
    private final Predicate<T> condition;
    private final Rule<T> dependency;

    public BatchRule(List<Rule<T>> rules) {
        this(rules, obj -> true, null);
    }

    public BatchRule(List<Rule<T>> rules, Predicate<T> condition, Rule<T> dependency) {
        this.rules = rules;
        this.condition = condition;
        this.dependency = dependency;
    }

    @Override
    public Set<Ailment> apply(T patient) {
        boolean conditionMet = condition.test(patient);
        Set<Ailment> dependencyAilments = dependency == null || !conditionMet
            ? Collections.emptySet() : dependency.apply(patient);
        if (conditionMet && dependencyAilments.isEmpty()) {
            return rules.stream()
                .flatMap(re -> re.apply(patient).stream())
                .collect(Collectors.toSet());
        } else {
            return dependencyAilments;
        }
    }

    @Override
    public Rule<T> withCondition(Predicate<T> condition) {
        return new BatchRule<>(rules, condition, dependency);
    }

    @Override
    public Rule<T> withDependency(Rule<T> previousRule) {
        return new BatchRule<>(rules, condition, previousRule);
    }

    /**
     * Not supported in this class - will just return this.
     * @inheritDoc
     * @param violationMessage new message to use on violation
     * @return this BatchRule
     */
    @Override
    public Rule<T> withViolationMessage(String violationMessage) {
        return this;
    }
}
