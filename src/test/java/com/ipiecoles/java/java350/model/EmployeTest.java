package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;


class EmployeTest {

    // TESTS NB ANCIENNETE

    @Test
    void testGetNbAnneeAncienneteDateEmbaucheNull(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);
        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();
        //Then
        Assertions.assertThat(nbAnneeAnciennete).isNull();
    }

    @Test
    void testGetNbAnneeAncienneteDateEmbaucheInfNow(){
        //Given
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.of(2015,8,21), 1500d, 1, 1.0);

        //When
        Integer anneeAnciennete = employeTest.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete).isNotNegative().isLessThan(100);
    }

    @Test
    void testGetNbAnneeAncienneteDateEmbaucheSupNow(){
        //Given
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.of(2222,8,21), 1500d, 1, 1.0);

        //When
        Integer anneeAnciennete = employeTest.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete).isNull();
    }

    @Test
    void testGetNbAnneeAncienneteDateEmbaucheNow(){
        //Given
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0);

        //When
        Integer anneeAnciennete = employeTest.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete).isZero();
    }

    // TESTS PRIME ANNUELLE //

    @Test
    void testGetPrimmeAnnuelleTempsPartielNegatif(){
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, -1.0);

        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        Assertions.assertThat(primeAnnuelle).isNull();

    }

    @Test
    void testGetPrimmeAnnuellePrimeAncienneteNegatif(){
        //Given
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, -1.0);

        //When
        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(primeAnnuelle).isNull();

    }

    @Test
    void testGetPrimmeAnnuellePerformanceNegative(){
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, (-1), 1.0);

        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        Assertions.assertThat(primeAnnuelle).isNegative();

    }

    @Test
    void testGetPrimmeAnnuelleManager(){
        Employe employeTest = new Employe("Doe", "John", "M12345", LocalDate.now(), 1600d, 1, 1.0);

        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        Assertions.assertThat(primeAnnuelle).isEqualTo(1700);

    }

    @Test
    void testGetPrimmeAnnuellePerfNull(){
        Employe employeTest = new Employe("Doe", "John", "T12345", LocalDate.now(), 1600d, null, 1.0);

        Double primeAnnuelle = employeTest.getPrimeAnnuelle();

        Assertions.assertThat(primeAnnuelle).isPositive().isEqualTo(1000);

    }

    @ParameterizedTest(name = "Perf {0}, matricule {1}, txActivite {2}, anciennete {3} => prime {4}")
    @CsvSource({
            "1, 'T12345', 1.0, 0, 1000.0",
            "1, 'T12345', 0.5, 0, 500.0",
            "2, 'T12345', 1.0, 0, 2300.0",
            "1, 'T12345', 1.0, 2, 1200.0",
            })
    void testGetPrimeAnnuelle(Integer performance, String matricule, Double tauxActivite, Long nbAnneesAnciennete,
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
    void testGetPrimeAnnuelleMatriculeNull(){
        //Given
        Employe employe = new Employe("Doe", "John", null, LocalDate.now(), 1500d, 1, 1.0);
        //When
        Double prime = employe.getPrimeAnnuelle();
        //Then
        Assertions.assertThat(prime).isEqualTo(1000.0);
    }

    // TESTS AUGMENTER SALAIRE //

    @Test
    void testAugmenterSalaireAugmentEmployeSalaire(){
        //Given
        //On initialise le test en créant un employe nommé john doe avec un salaire de 1600€ (en double)
        //On stocke le salaire de base dans une variable
        Double salaireBase = 1600.0;
        Employe employe = new Employe("Doe", "John", "T40404", LocalDate.now(), salaireBase, 1, 1.0);

        //When
        //On execute la méthode augmenterSalaire, on passe en argument le pourcentage d'augmentation du salaire
        Double nouveauSalaire = employe.augmenterSalaire(10);

        //Then
        //On s'assure que le salaire retourné est bien supérieur au salaire de base (augmenté de 10% dans notre cas)
        Assertions.assertThat(nouveauSalaire).isGreaterThan(salaireBase);
    }

    @Test
    void testAugmenterSalaireAugmentEmployeSalairenull(){
        //Given
        //Ici on passe un employé avec un salaire incorrect
        Employe employe = new Employe("Doe", "John", "T40404", LocalDate.now(), null, 1, 1.0);

        //When
        Double nouveauSalaire = employe.augmenterSalaire(10);

        //Then
        //On s'assure que le nouveau Salaire n'est pas null
        Assertions.assertThat(nouveauSalaire).isNotNull();
    }

    @Test
    void testAugmenterSalairePourcentagenegatif(){
        //Given
        Double salaireBase = 1600d;
        //On créer un employé
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now(), salaireBase, 1, 1.0);

        //When
        //On lui passe un pourcentage négatif
        Double nouveauSalaire = employe.augmenterSalaire(-(10d));

        //Then
        //On vérifie que le nouveau salaire est bien positif et non null
        Assertions.assertThat(nouveauSalaire).isNotNull().isPositive();
    }

    @Test
    void testAugmenterSalaireSalaireIncorrect(){
        //Given
        //On créer un employé avec un salaire inférieur au salaire de base de l'entreprise
        Double salaireBase = 100d;
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now(), salaireBase, 1, 1.0);

        //When
        Double nouveauSalaire = employe.augmenterSalaire(10);

        //Then
        //On s'assure que la condition dans notre fonction attribue bien le salaire de base à l'employé et qu'il à bien été augmenté de 10%
        Assertions.assertThat(nouveauSalaire).isNotNull().isNotEqualTo(0).isGreaterThan(Entreprise.SALAIRE_BASE);
    }

    //test parametrés sur plusieurs cas d'augmentation de salaire
    @ParameterizedTest(name = "Perf {0}, matricule {1}, txActivite {2}, anciennete {3}, salaire {4}, pourcentage {5} => salaireAttendu {6}")
    @CsvSource({
            "1, 'T12345', 1.0, 10, 1552.21, 10.0, 1707.43", // augmentation de base de 10%
            "1, 'T12345', 0.5, 4, 1400.01, 25.0, 1901.53", // augmentation d'un salaire de 25% (< au salaire de base entreprise)
            "2, 'T12345', 1.0, 0, 1950.1, 0.0, 1950.1", // augmentation d'un salaire de 0%
            "1, 'T12345', 1.0, 2, 2000.0, -10.0, 2000.0", // augmentation d'un salaire de -10%
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
        //On s'assure que pour tous les tests et selon les paramètres, le salaire est bien traité et retourne bien le salaire attendu
        Assertions.assertThat(salaireAugmente).isEqualTo(salaireAttendu);
    }

    // TESTS GET NOMBRE RTT //

    @Test
    void testGetNbRttNotNullOrEqualZero(){
        //Given
        //On créer un employé
        Employe employe = new Employe("Doe", "John", "T01234", LocalDate.of(2019, 10, 10), 1700d, 1, 1.0);

        //When
        //On ne passe pas de date de référence (la date d'embauche de l'employé est prise par défaut
        Integer nbRtt = employe.getNbRtt();

        //Then
        //On s'assure que le nombre de rtt n'est pas null ou égal à 0
        Assertions.assertThat(nbRtt).isNotNull().isNotEqualTo(0);
    }

    //Test paramétrés pour le nombre de rtt
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
        "1.0, 2016, 9"
    })
    void testGetNbRtt(Double tpsPartiel, Integer dateReference, Integer nbRttAttendu){
        //Given
        Employe employe = new Employe("Doe", "John", "T01234", LocalDate.of(2000, 10, 10), 1700d, 1, tpsPartiel);

        //When
        Integer nbRtt = employe.getNbRtt(LocalDate.of(dateReference, 1, 1));

        //Then
        //On s'assure que pour tous les cas présentés dans les paramètres, notre nombre de RTT correspond bien à celui attendu
        Assertions.assertThat(nbRtt).isEqualTo(nbRttAttendu);
    }

    //TESTS ADDITIONNELS//

    @Test
    void testGetNbConge(){
        //Given
        Employe employe = new Employe("Doe", "John", "T01234", LocalDate.of(2015, 10, 10), 1700d, 1, 1.0);

        //When
        Integer nbConges = employe.getNbConges();

        //Then
        //On s'assure que selon l'anciennetée de l'employé son nombre de congé est supérieur a celui de base
        Assertions.assertThat(nbConges).isGreaterThan(Entreprise.NB_CONGES_BASE);
    }

}
