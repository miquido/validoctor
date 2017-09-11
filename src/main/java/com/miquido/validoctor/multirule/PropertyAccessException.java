package com.miquido.validoctor.multirule;

import org.apache.commons.lang3.StringUtils;

class PropertyAccessException extends RuntimeException {

  static PropertyAccessException noGetter(String propertyName, Object object, Exception cause) {
    return new PropertyAccessException("Property '" + propertyName + "' was not found in object '" + object + "'", cause);
  }

  static PropertyAccessException noGetter(String propertyName, Exception cause) {
    return new PropertyAccessException("Property '" + propertyName + "' was not found", cause);
  }

  static PropertyAccessException noIsSetMethod(String propertyName, Object object, Exception cause) {
    return new PropertyAccessException("is" + StringUtils.capitalize(propertyName) + "Set method was not found in object '" + object + "'", cause);
  }

  static PropertyAccessException noIsSetMethod(String propertyName, Exception cause) {
    return new PropertyAccessException("Primitive boolean returning is" + StringUtils.capitalize(propertyName) + "Set method was not found", cause);
  }

  PropertyAccessException(String message) {
    super(message);
  }

  PropertyAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
