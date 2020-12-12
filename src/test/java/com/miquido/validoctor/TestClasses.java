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
      return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(title, that.title) && Objects.equals(done, that.done);
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
