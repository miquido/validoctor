package com.miquido.validoctor.util;

public class PropertyAccessException extends RuntimeException {

  public static PropertyAccessException noGetter(String methodName, Object object, Exception cause) {
    return new PropertyAccessException("Can not call '" + methodName + "' method of object '" + object + "'", cause);
  }

  public static PropertyAccessException noGetter(String propertyName, Exception cause) {
    return new PropertyAccessException("Property '" + propertyName + "' was not found", cause);
  }

  private PropertyAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
