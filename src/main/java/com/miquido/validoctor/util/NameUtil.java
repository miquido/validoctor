package com.miquido.validoctor.util;

public class NameUtil {

  private NameUtil() {}

  public static String capitalize(String str) {
    char capChar = Character.toUpperCase(str.charAt(0));
    return String.valueOf(capChar) + str.substring(1);
  }

  public static String uncapitalize(String str) {
    char uncapChar = Character.toLowerCase(str.charAt(0));
    return String.valueOf(uncapChar) + str.substring(1);
  }

}
