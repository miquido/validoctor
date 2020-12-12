package com.miquido.validoctor.execution;

import com.miquido.validoctor.result.Ailment;
import com.miquido.validoctor.definition.Rule;
import com.miquido.validoctor.target.ReducedFieldsRuleTarget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReducedFieldsRuleExecution<T, P> extends RuleExecution<T, P> {

  public ReducedFieldsRuleExecution(ReducedFieldsRuleTarget<T, P> target, Rule<P> rule) {
    super(target, rule);
  }

  @Override
  public Set<Ailment> perform(T patient) {
    List<String> fieldNames = target.getFieldNames();
    P value = target.getPatients(patient).get(0); //always one reduced value
    Set<Ailment> ailments = rule.apply(value);
    return fieldNames.stream() //return ailments for each involved field
        .flatMap(name ->
            ailments.stream().map(ailment -> {
              String fieldName = name;
              if (ailment.field != null) {
                fieldName += "." + ailment.field;
                //TODO improve this, as currently its not clear the ailment comes from reduced value
              }
              return new Ailment(fieldName, ailment.ailments);
            })
        )
        .collect(Collectors.toSet());
  }
}
