package com.miquido.validoctor;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;
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

import static com.miquido.validoctor.rule.MeticulousRuleDecorator.*;
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
  public void pedanticValidoctor_testsAll() {
    Validoctor validoctor = Validoctor.builder().pedantic(true).build();
    Diagnosis diagnosis = validoctor.examine(-5, Rules.numberNonNegative(), Rules.numberInRange(0, 5));
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
    assertEquals(2, diagnosis.getAilments().get(null).size());
  }

  @Test
  public void nonPedanticValidoctor_stopsOnFirstFail() {
    Validoctor validoctor = Validoctor.builder().pedantic(false).build();
    Diagnosis diagnosis = validoctor.examine(-5, Rules.numberNonNegative(), Rules.numberInRange(0, 5));
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
    assertEquals(1, diagnosis.getAilments().get(null).size());
  }

  @Test
  public void meticulousValidoctor_putsAdditionalEntriesInSpecs() {
    Validoctor validoctor = Validoctor.builder().meticulous(true).build();
    Diagnosis diagnosis = validoctor.examine(-5, Rules.numberNonNegative(), Rules.numberInRange(0, 5));
    Set<Ailment> ailments = diagnosis.getAilments().get(null);
    Ailment ailment = ailments.stream().filter(a ->
        a.getName().equals(Rules.numberNonNegative().peekAilment().getName())).findFirst().orElse(null);
    assertNotNull(ailment);
    assertTrue(ailment.getSpecs().toString(), ailment.getSpecs().containsKey(PATIENT_VALUE));
    assertTrue(ailment.getSpecs().toString(), ailment.getSpecs().containsKey(EXAMINATION_DURATION));
    ailment = ailments.stream().filter(a ->
        a.getName().equals(Rules.numberInRange(0, 5).peekAilment().getName())).findFirst().orElse(null);
    assertNotNull(ailment);
    assertTrue(ailment.getSpecs().toString(), ailment.getSpecs().containsKey(PATIENT_VALUE));
    assertTrue(ailment.getSpecs().toString(), ailment.getSpecs().containsKey(EXAMINATION_DURATION));
  }

  @Test
  public void nonMeticulousValidoctor_returnsDefaultSpecs() {
    Validoctor validoctor = Validoctor.builder().meticulous(false).build();
    Diagnosis diagnosis = validoctor.examine(-5, Rules.numberNonNegative(), Rules.numberInRange(0, 5));
    Set<Ailment> ailments = diagnosis.getAilments().get(null);
    Ailment ailment = ailments.stream().filter(a ->
        a.getName().equals(Rules.numberNonNegative().peekAilment().getName())).findFirst().orElse(null);
    assertNotNull(ailment);
    assertEquals(ailment.getSpecs().toString(), 0, ailment.getSpecs().size());
    ailment = ailments.stream().filter(a ->
        a.getName().equals(Rules.numberInRange(0, 5).peekAilment().getName())).findFirst().orElse(null);
    assertNotNull(ailment);
    assertEquals(ailment.getSpecs().toString(), 2, ailment.getSpecs().size());
  }

  @Test
  public void meticulousValidoctor_multithreaded() throws ExecutionException, InterruptedException {
    Validoctor validoctor = Validoctor.builder().meticulous(true).build();
    List<Future<Diagnosis>> futures = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      int patient = i;
      Future<Diagnosis> future = executor.submit(() -> {
        System.out.println("Thread: " + Thread.currentThread().getId());
        return validoctor.examine(-patient, Rules.numberPositive());
      });
      futures.add(future);
    }
    for (int i = 0; i < 10; i++) {
      Diagnosis diagnosis = futures.get(i).get();
      Set<Ailment> ailments = diagnosis.getAilments().get(null);
      Ailment ailment = ailments.stream().filter(a ->
          a.getName().equals(Rules.numberPositive().peekAilment().getName())).findFirst().orElse(null);
      assertNotNull(ailment);
      assertEquals(-i, ailment.getSpecs().get(PATIENT_VALUE));
    }
  }


}
