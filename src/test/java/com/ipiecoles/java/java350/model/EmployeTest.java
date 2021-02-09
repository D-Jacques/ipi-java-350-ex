package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        //Given
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, -1.0);

        //When
        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(primeAnnuelle).isNull();

    }

    @Test
    public void testGetPrimmeAnnuellePerformanceNegative(){
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, (-1), 1.0);

        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        Assertions.assertThat(primeAnnuelle).isLessThanOrEqualTo(0);

    }

    @ParameterizedTest(name = "Perf {0}, matricule {1}, txActivite {2}, anciennete {3} => prime {4}")
    @CsvSource({
            "1, 'T12345', 1.0, 0, 1000.0",
            "1, 'T12345', 0.5, 0, 500.0",
            "2, 'T12345', 1.0, 0, 2300.0",
            "1, 'T12345', 1.0, 2, 1200.0",
            })
    public void testGetPrimeAnnuelle(Integer performance, String matricule, Double tauxActivite, Long nbAnneesAnciennete,
                                     Double primeAttendue){
        //Given
        Employe employe = new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(nbAnneesAnciennete), 1500d,
                performance, tauxActivite);
        //When
        Double prime = employe.getPrimeAnnuelle();
        //Then
        Assertions.assertThat(prime).isEqualTo(primeAttendue);
    }

    @Test
    public void testGetPrimeAnnuelleMatriculeNull(){
        //Given
        Employe employe = new Employe("Doe", "John", null, LocalDate.now(), 1500d, 1, 1.0);
        //When
        Double prime = employe.getPrimeAnnuelle();
        //Then
        Assertions.assertThat(prime).isEqualTo(1000.0);
    }



}
