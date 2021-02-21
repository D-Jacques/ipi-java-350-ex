package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class EmployeServiceIntegrationTest {

    //Test unitaire = test de code => le code fait ce qui est attendu
    //Test d'intégration = test de fonctionalité

    @Autowired
    private EmployeService employeService;

    @Autowired
    private EmployeRepository employeRepository;

    @Test
    public void testEmbauchePremierEmploye() throws EmployeException {
        //Given Pas d'employés en base
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        //Then
        Employe employe = employeRepository.findByMatricule("T00001");
        Assertions.assertThat(employe).isNotNull();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(1.0);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getMatricule()).isEqualTo("T00001");
    }

    @Test
    public void testCalculPerformanceCommercial() throws EmployeException{
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.LICENCE;
        Double tempsPartiel = 1.0;
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        Long caTraite = 45000L;
        Long objectifCa = 40000L;
        String matricule = "C"+employeRepository.findLastMatricule();

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        //then
        Employe employe = employeRepository.findByMatricule(matricule);
        System.out.println(employe.getPerformance());
        Assertions.assertThat(employe.getPerformance()).isEqualTo(3);
    }

    @ParameterizedTest(name = "caTraite {0}, objectifCa {1} => performanceAttendu {2}")
    @CsvSource({
            "45000, 40000, 3", // ca entre 5% et 20% (entre 42 000 et 48 000)
            "50000, 40000, 6", // ca supérieur à 20% (> a 48 000) + bonus de perf superieur à perf moyenne
            "30000, 40000, 1", // ca à -20% (< à 32 000)
            "35000, 40000, 1", // ca entre -20% et -5% (entre 32 000 et 38 000)
            "40000, 40000, 1", // ca entre -5% et 5% (38 000 et 42 000)

    })
    public void testParametreCalculPerformanceCommercial(Long caTraite, Long objectifCa,
                                                         Integer performanceAttendue) throws EmployeException{
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.LICENCE;
        Double tempsPartiel = 1.0;
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        String matricule = "C"+employeRepository.findLastMatricule();
        System.out.println(matricule);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        //then
        Employe employe = employeRepository.findByMatricule(matricule);
        System.out.println(employe.getPerformance());
        Assertions.assertThat(employe.getPerformance()).isEqualTo(performanceAttendue);
    }

    @AfterEach
    public void dbCleaner(){
        employeRepository.deleteAll();
    }

}