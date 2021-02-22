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

    @Test
    void testAugmenterSalaireAugmentEmployeSalaire(){
        //Given
        //Je créer un employé avec un salaire de 1650
        Double salaireBase = 1650d;
        Employe employe = new Employe("Doe", "John", null, LocalDate.now(), salaireBase, 1, 1.0);

        //When
        //Je calcule le nouveau salaire de l'employé en passant en paramètres 10%
        Double nouveauSalaire = employe.augmenterSalaire(10);

        //Then
        //Je vérifie que le nouveauSalaire est bien supérieur à celui définit dans employe
        Assertions.assertThat(nouveauSalaire).isGreaterThan(salaireBase);
    }

    @Test
    void testAugmenterSalairePourcentagenegatif(){
        //Given
        Double salaireBase = 1600d;
        Employe employe = new Employe("Doe", "John", null, LocalDate.now(), salaireBase, 1, 1.0);

        //When
        Double nouveauSalaire = employe.augmenterSalaire(-(10d));

        //Then
        Assertions.assertThat(nouveauSalaire).isNotNull().isPositive();
    }

    @Test
    void testAugmenterSalaireSalaireIncorrect(){
        //Given
        Double salaireBase = 100d;
        Employe employe = new Employe("Doe", "John", null, LocalDate.now(), salaireBase, 1, 1.0);

        //When
        Double nouveauSalaire = employe.augmenterSalaire(10);

        //Then
        Assertions.assertThat(nouveauSalaire).isNotNull().isNotEqualTo(0);
    }

    @ParameterizedTest(name = "Perf {0}, matricule {1}, txActivite {2}, anciennete {3}, salaire {4}, pourcentage {5} => salaireAttendu {6}")
    @CsvSource({
            "1, 'T12345', 1.0, 10, 1552.21, 10.0, 1707.43",
            "1, 'T12345', 0.5, 4, 1400.01, 25.0, 1901.53",
            "2, 'T12345', 1.0, 0, 1950.1, 0.0, 1950.1",
            "1, 'T12345', 1.0, 2, 2000.0, -10.0, 2000.0",
    })
    void testAugmenterSalaire(Integer performance, String matricule, Double tauxActivite, Long nbAnneesAnciennete, Double salaire, Double pourcentage,
                                     Double salaireAttendu){
        //Given
        Employe employe = new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(nbAnneesAnciennete), salaire,
                performance, tauxActivite);
        //When
        Double salaireAugmente = employe.augmenterSalaire(pourcentage);
        //Then
        Assertions.assertThat(salaireAugmente).isEqualTo(salaireAttendu);
    }

    @Test
    void testGetNbRttNotNullOrEqualZero(){
        //Given
        Employe employe = new Employe("Doe", "John", "T01234", LocalDate.of(2019, 10, 10), 1700d, 1, 1.0);

        //When
        Integer nbRtt = employe.getNbRtt();
        System.out.println(nbRtt);

        //Then
        Assertions.assertThat(nbRtt).isNotNull().isNotEqualTo(0);;
    }

    @ParameterizedTest(name = "tpsPartiel {0}, dateReference {1} => nbRttAttendu {3}")
    @CsvSource({
        "1.0, 2019, 8",
        "1.0, 2021, 10",
        "1.0, 2022, 10",
        "1.0, 2032, 11",
        "1.0, 2039, 10",
        "1.5, 2022, 15",
        "0.5, 2021, 5",
        "0.5, 2019, 4",
        "1.0, 2026, 9",
        "1.0, 2020, 10"
    })
    void testGetNbRtt(Double tpsPartiel, Integer dateReference, Integer nbRttAttendu){
        //Given
        Employe employe = new Employe("Doe", "John", "T01234", LocalDate.of(2000, 10, 10), 1700d, 1, tpsPartiel);

        //When
        Integer nbRtt = employe.getNbRtt(LocalDate.of(dateReference, 1, 1));

        //Then
        Assertions.assertThat(nbRtt).isEqualTo(nbRttAttendu);
    }

    //test additionnels pour la couverture

    @Test
    void testGetNbConge(){
        //Given
        Employe employe = new Employe("Doe", "John", "T01234", LocalDate.of(2015, 10, 10), 1700d, 1, 1.0);

        //When
        Integer nbConges = employe.getNbConges();

        //Then
        Assertions.assertThat(nbConges).isGreaterThan(Entreprise.NB_CONGES_BASE);
    }

/*    @Test
    public void testGetNbRttThurhdayIsLeapYear(){
        //Given
        Employe employe = new Employe("Doe", "John", "T01234", LocalDate.of(2019, 10, 10), 1700d, 1, 1.0);

        //When
        Integer nbRtt = employe.getNbRtt();
        System.out.println(nbRtt);

        //Then
        Assertions.assertThat(nbRtt).isNotNull();
        Assertions.assertThat(nbRtt).isNotEqualTo(0);
    }*/

}
