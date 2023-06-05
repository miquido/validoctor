package com.miquido.validoctor;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TestClasses {

  public static class SimpleTestClass {
    private Integer id;
    private String name;
    private String title;
    private Boolean done;

    public SimpleTestClass(Integer id, String name, String title, Boolean done) {
      this.id = id;
      this.name = name;
      this.title = title;
      this.done = done;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof SimpleTestClass)) return false;
      SimpleTestClass that = (SimpleTestClass) o;
      return Objects.equals(id, that.id) && Objects.equals(name, that.name)
          && Objects.equals(title, that.title) && Objects.equals(done, that.done);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, name, title, done);
    }
  }


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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TestClass testClass = (TestClass) o;
      return Float.compare(testClass.volumeL, volumeL) == 0 && Objects.equals(name, testClass.name)
          && Objects.equals(skuId, testClass.skuId) && Objects.equals(description, testClass.description)
          && Objects.equals(weightKg, testClass.weightKg) && Objects.equals(kcal, testClass.kcal)
          && Objects.equals(inside, testClass.inside) && Objects.equals(intSet, testClass.intSet)
          && Objects.equals(insideList, testClass.insideList);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, skuId, description, weightKg, volumeL, kcal, inside, intSet, insideList);
    }
  }


  public static class TestInsideClass {
    private String name;
    private double score;
    private double optScore;
    private List<String> list;

    public TestInsideClass(String name, double score) {
      this.name = name;
      this.score = score;
    }

    public TestInsideClass(String name, double score, double optScore, List<String> list) {
      this.name = name;
      this.score = score;
      this.optScore = optScore;
      this.list = list;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TestInsideClass that = (TestInsideClass) o;
      return Double.compare(that.score, score) == 0 && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, score);
    }
  }
}
