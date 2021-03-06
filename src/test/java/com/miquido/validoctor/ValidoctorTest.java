package com.miquido.validoctor;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.ailment.SpecsKey;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.diagnosis.DiagnosisException;
import com.miquido.validoctor.rule.Rules;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.miquido.validoctor.ailment.Ailment.*;
import static org.junit.Assert.*;

public class ValidoctorTest {

  private final ExecutorService executor = Executors.newFixedThreadPool(4);

  @Test
  public void nonExceptionalValidoctor_notThrows() {
    Validoctor validoctor = Validoctor.builder().exceptional(false).build();
    Diagnosis diagnosis = null;
    try {
      diagnosis = validoctor.examine("", Rules.stringNotEmpty());
    } catch (DiagnosisException e) {
      fail("Exception thrown from non-exceptional validoctor running a non-exceptional rule");
    }
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
    assertEquals(1, diagnosis.getAilments().size());
  }

  @Test
  public void exceptionalValidoctor_throws() {
    Validoctor validoctor = Validoctor.builder().exceptional(true).build();
    try {
      validoctor.examine("", Rules.stringNotEmpty());
    } catch (DiagnosisException e) {
      assertEquals(1, e.getDiagnosis().getAilments().size());
      return;
    }
    fail("Exception not thrown from exceptional validoctor");
  }

  @Test
  public void exceptionalValidoctor_exceptionFactory() {
    Validoctor validoctor = Validoctor.builder()
        .exceptional(true)
        .exceptionFactory(diagnosis -> new IllegalStateException())
        .build();
    try {
      validoctor.examine("", Rules.stringNotEmpty());
    } catch (IllegalStateException e) {
      return;
    } catch (RuntimeException e) {
      fail("Wrong exception type thrown");
    }
    fail("Exception not thrown from exceptional validoctor");
  }

  @Test
  public void pedanticValidoctor_testsAll() {
    Validoctor validoctor = Validoctor.builder().pedantic(true).build();
    Diagnosis diagnosis = validoctor.examine(-5, "a", Rules.numberNonNegative(), Rules.numberInRange(0, 5));
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
    assertEquals(2, diagnosis.getAilments().get("a").size());
  }

  @Test
  public void nonPedanticValidoctor_stopsOnFirstFail() {
    Validoctor validoctor = Validoctor.builder().pedantic(false).build();
    Diagnosis diagnosis = validoctor.examine(-5, "a", Rules.numberNonNegative(), Rules.numberInRange(0, 5));
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
    assertEquals(1, diagnosis.getAilments().get("a").size());
  }

  @Test
  public void validoctor_putsRuleParamsAndPatientValueInSpecs() {
    Validoctor validoctor = Validoctor.builder().build();
    Diagnosis diagnosis = validoctor.examine(-5, "a", Rules.numberNonNegative(), Rules.numberInRange(0, 5));
    Set<Ailment> ailments = diagnosis.getAilments().get("a");
    Ailment ailment = ailments.stream().filter(a ->
        a.getName().equals(Rules.numberNonNegative().peekAilment().getName())).findFirst().orElse(null);
    assertNotNull(ailment);
    assertTrue(ailment.getSpecs().toString(), ailment.getSpecs().containsKey(SpecsKey.PATIENT_VALUE));
    ailment = ailments.stream().filter(a ->
        a.getName().equals(Rules.numberInRange(0, 5).peekAilment().getName())).findFirst().orElse(null);
    assertNotNull(ailment);
    assertTrue(ailment.getSpecs().toString(), ailment.getSpecs().containsKey(SpecsKey.PATIENT_VALUE));
    assertTrue(ailment.getSpecs().toString(), ailment.getSpecs().containsKey(SpecsKey.MIN_RANGE));
    assertTrue(ailment.getSpecs().toString(), ailment.getSpecs().containsKey(SpecsKey.MAX_RANGE));
  }

  @Test
  public void validoctor_multithreaded() throws ExecutionException, InterruptedException {
    Validoctor validoctor = Validoctor.builder().build();
    List<Future<Diagnosis>> futures = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      int patient = i;
      Future<Diagnosis> future = executor.submit(() -> {
        System.out.println("validoctor_multithreaded: Thread: " + Thread.currentThread().getId());
        return validoctor.examine(-patient, "a", Rules.numberPositive());
      });
      futures.add(futures.size(), future);
    }
    for (int i = 0; i < 10; i++) {
      Diagnosis diagnosis = futures.get(i).get();
      Set<Ailment> ailments = diagnosis.getAilments().get("a");
      Ailment ailment = ailments.stream().filter(a ->
          a.getName().equals(Rules.numberPositive().peekAilment().getName())).findFirst().orElse(null);
      assertNotNull(ailment);
      assertEquals(-i, ailment.getSpecs().get(SpecsKey.PATIENT_VALUE));
      System.out.println("validoctor_multithreaded: diagnosis for " + i + ": " + diagnosis);
    }
  }


}
