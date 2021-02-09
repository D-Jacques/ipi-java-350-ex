package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


public class EmployeTest {

    @Test
    public void testGetNbAnneeAncienneteDateEmbaucheNull(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);
        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();
        //Then
        Assertions.assertThat(nbAnneeAnciennete).isNull();
    }

    @Test
    public void testGetNbAnneeAncienneteDateEmbaucheInfNow(){
        //Given
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.of(2015,8,21), 1500d, 1, 1.0);

        //When
        Integer anneeAnciennete = employeTest.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete).isGreaterThanOrEqualTo(0);
        Assertions.assertThat(anneeAnciennete).isLessThan(100);
    }

    @Test
    public void testGetNbAnneeAncienneteDateEmbaucheSupNow(){
        //Given
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.of(2222,8,21), 1500d, 1, 1.0);

        //When
        Integer anneeAnciennete = employeTest.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete).isNull();
    }

    @Test
    public void testGetNbAnneeAncienneteDateEmbaucheNow(){
        //Given
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0);

        //When
        Integer anneeAnciennete = employeTest.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete).isEqualTo(0);
    }

    @Test
    public void testGetPrimmeAnnuelleTempsPartielNegatif(){
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, -1.0);

        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        Assertions.assertThat(primeAnnuelle).isNull();

    }

    @Test
    public void testGetPrimmeAnnuellePrimeAncienneteNegatif(){
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, -1.0);

        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        Assertions.assertThat(primeAnnuelle).isNull();

    }

    @Test
    public void testGetPrimmeAnnuellePerformanceNegative(){
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, (-1), 1.0);

        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        System.out.println(primeAnnuelle);

        Assertions.assertThat(primeAnnuelle).isLessThanOrEqualTo(0);

    }



}
