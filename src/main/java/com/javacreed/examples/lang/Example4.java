package com.javacreed.examples.lang;

import java.lang.reflect.Field;

public class Example4 {

  public static void main(final String[] args) throws Exception {
    final Class<String> type = String.class;
    final Field valueField = type.getDeclaredField("value");
    valueField.setAccessible(true);

    final String s = "Immutable String";
    valueField.set(s, "Mutable String".toCharArray());

    System.out.println("Immutable String");

    final String o = "Immutable String";
    System.out.println(o);
  }
}
