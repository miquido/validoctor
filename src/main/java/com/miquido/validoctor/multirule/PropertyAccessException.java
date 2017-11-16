package com.miquido.validoctor.multirule;

class PropertyAccessException extends RuntimeException {

  static PropertyAccessException noGetter(String propertyName, Object object, Exception cause) {
    return new PropertyAccessException("Property '" + propertyName + "' was not found in object '" + object + "'", cause);
  }

  static PropertyAccessException noGetter(String propertyName, Exception cause) {
    return new PropertyAccessException("Property '" + propertyName + "' was not found", cause);
  }

  private PropertyAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
