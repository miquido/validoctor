package com.miquido.validoctor2;

import java.util.List;
import java.util.Set;

public class TestClasses {

  public static class TestClass {
    private String name;
    String skuId;
    public String description;
    private Float weightKg;
    private float volumeL;
    private Integer kcal;
    private TestInsideClass inside;
    private Set<Integer> intSet;
    private List<TestInsideClass> insideList;

    public TestClass(String name, String skuId, String description, Float weightKg,
                     float volumeL, Integer kcal, TestInsideClass inside, Set<Integer> intSet,
                     List<TestInsideClass> insideList) {
      this.name = name;
      this.skuId = skuId;
      this.description = description;
      this.weightKg = weightKg;
      this.volumeL = volumeL;
      this.kcal = kcal;
      this.inside = inside;
      this.intSet = intSet;
      this.insideList = insideList;
    }
  }


  public static class TestInsideClass {
    private String name;
    private double score;

    public TestInsideClass(String name, double score) {
      this.name = name;
      this.score = score;
    }
  }
}
