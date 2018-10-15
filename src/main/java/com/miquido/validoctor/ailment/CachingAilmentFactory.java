package com.miquido.validoctor.ailment;

import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of AilmentFactory that will cache last stated Ailment and return it in subsequent calls
 * to {@link AilmentFactory#state(Map)} made with the same argument.
 */
public class CachingAilmentFactory implements AilmentFactory {

  private Ailment stated;
  private final Function<Map<String, Object>, Ailment> factory;

  public CachingAilmentFactory(Function<Map<String, Object>, Ailment> factory) {
    this.factory = factory;
  }

  @Override
  public Ailment state(Map<String, Object> specs) {
    if (stated == null || !stated.getSpecs().equals(specs)) {
      stated = factory.apply(specs);
    }
    return stated;
  }
}
