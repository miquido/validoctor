package com.miquido.validoctor.ailment;

import java.util.Map;

/**
 * Factory of Ailments to be used in {@link com.miquido.validoctor.rule.Rule} implementations for stating Ailments on
 * Rule violations.
 */
public interface AilmentFactory {

  /**
   * @param specs map of params to put in {@link Ailment#getSpecs() Ailment specs}.
   * @return Ailment instance.
   */
  Ailment state(Map<String, Object> specs);

}
