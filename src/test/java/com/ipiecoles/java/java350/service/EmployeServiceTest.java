package com.ipiecoles.java.java350.service;
import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.util.List;
@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {
    @InjectMocks
    private EmployeService employeService;
    @Mock
    private EmployeRepository employeRepository;

    @Test
    public void testEmbauchePremierEmploye() throws EmployeException {
        //Given Pas d'employés en base
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        //Simuler qu'aucun employé n'est présent (ou du moins aucun matricule)
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        //Simuler que la recherche par matricule ne renvoie pas de résultats
//        Mockito.when(employeRepository.findByMatricule(Mockito.anyString())).thenReturn(null);
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(null);
        Mockito.when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        //Then
//        Employe employe = employeRepository.findByMatricule("T00001");
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe).isNotNull();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(1.0);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getMatricule()).isEqualTo("T00001");
    }

    @Test
    public void testEmbaucheEmployeTpsPartielNull() throws EmployeException {
        //Given Pas d'employés en base
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = null;
        //Simuler qu'aucun employé n'est présent (ou du moins aucun matricule)
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        //Simuler que la recherche par matricule ne renvoie pas de résultats
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(null);
        Mockito.when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
//        Employe employe = employeRepository.findByMatricule("T00001");
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();

        Assertions.assertThat(employe.getSalaire()).isNotNull().isEqualTo(1825.464);

    }


    @Test
    public void testEmbaucheLimiteMatricule() {
        //Given Pas d'employés en base
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        //Simuler qu'il y a 99999 employés en base (ou du moins que le matricule le plus haut
        //est X99999
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");
        //When
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail("embaucheEmploye aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            Mockito.verify(employeRepository, Mockito.never()).save(Mockito.any(Employe.class));
            Assertions.assertThat(e.getMessage()).isEqualTo("Limite des 100000 matricules atteinte !");
        }

    }

    @Test
    public void testEmbaucheEmployeExisteDeja() throws EmployeException {
        //Given Pas d'employés en base
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        Employe employeExistant = new Employe("Doe", "Jane", "T00001", LocalDate.now(), 1500d, 1, 1.0);
        //Simuler qu'aucun employé n'est présent (ou du moins aucun matricule)
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        //Simuler que la recherche par matricule renvoie un employé (un employé a été embauché entre temps)
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(employeExistant);
        //When
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail("embaucheEmploye aurait dû lancer une exception");
        } catch (Exception e){
            //Then
            Mockito.verify(employeRepository, Mockito.never()).save(Mockito.any(Employe.class));
            Assertions.assertThat(e).isInstanceOf(EntityExistsException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("L'employé de matricule T00001 existe déjà en BDD");
        }
    }

    @Test
    void testCalculPerformanceCommercialCaSuperieur() throws EmployeException{
        //Given
        String nom = "Doe";
        String prenom = "John";
        String matricule = "C12345";
        Long caTraite = 45000L;
        Long objectifCa = 40000L;
        Integer basePerformance = 1;
        Employe employeTest = new Employe(nom, prenom, matricule, LocalDate.now(), 2400d, basePerformance, 1.0);
        Mockito.when(employeRepository.findByMatricule(matricule)).thenReturn(employeTest);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(2d);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        //then
        ArgumentCaptor<Employe> caCommercialCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(caCommercialCaptor.capture());
        Employe employeRetour = caCommercialCaptor.getValue();
        Assertions.assertThat(employeRetour.getPerformance()).isGreaterThan(basePerformance);
    }

    @Test
    void testCalculPerformanceCommercialCanull(){
        //Given
        String matricule = "C12345";
        Long caTraite = null;
        Long objectifCa = 40000L;
        //On a pas besoin de mocker les methodes findByMatricule et avgPerformanceWhereMatriculeStartsWith car dans notre test on ne
        //rencontrera JAMAIS ces méthodes

        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
            Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (Exception e){
            //Then
            Mockito.verify(employeRepository, Mockito.never()).save(Mockito.any(Employe.class));
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("Le chiffre d'affaire traité ne peut être négatif ou null !");
        }
    }

    @Test
    void testCalculPerformanceCommercialCanegatif(){
        //Given
        String matricule = "C12345";
        Long caTraite = -(45000L);
        Long objectifCa = 40000L;
        //On a pas besoin de mocker les methodes findByMatricule et avgPerformanceWhereMatriculeStartsWith car dans notre test on ne
        //rencontrera JAMAIS ces méthodes

        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
            Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (Exception e){
            //Then
            Mockito.verify(employeRepository, Mockito.never()).save(Mockito.any(Employe.class));
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("Le chiffre d'affaire traité ne peut être négatif ou null !");
        }
    }

    @Test
    void testCalculPerformanceCommercialObjectifCaNull() throws EmployeException{
        //Given
        String matricule = "C12345";
        Long caTraite = 50000L;
        Long objectifCa = null;

        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
            Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (Exception e){
            //Then
            Mockito.verify(employeRepository, Mockito.never()).save(Mockito.any(Employe.class));
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
        }
    }

    @Test
    void testCalculPerformanceCommercialObjectifCaNegatif() throws EmployeException{
        //Given
        String matricule = "C12345";
        Long caTraite = 50000L;
        Long objectifCa = -(45000L);

        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
            Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (Exception e){
            //Then
            Mockito.verify(employeRepository, Mockito.never()).save(Mockito.any(Employe.class));
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
        }
    }

    @Test
    void testCalculPerformanceCommercialMatriculeNull() throws EmployeException{
        //Given
        String matricule = null;
        Long caTraite = 50000L;
        Long objectifCa = 45000L;

        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
            Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (Exception e){
            //Then
            Mockito.verify(employeRepository, Mockito.never()).save(Mockito.any(Employe.class));
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("Le matricule ne peut être null et doit commencer par un C !");
        }
    }

    @ParameterizedTest(name = "Perf {0}, caTraite {1}, objectifCa {2} => performanceAttendu {3}")
    @CsvSource({
            "1, 45000, 40000, 2", // ca entre 5% et 20% (entre 42 000 et 48 000)
            "4, 50000, 40000, 9", // ca supérieur à 20% (> a 48 000) + bonus de perf superieur à perf moyenne
            "2, 30000, 40000, 1", // ca à -20% (< à 32 000)
            "4, 35000, 40000, 2", // ca entre -20% et -5% (entre 32 000 et 38 000)
            "2, 40000, 40000, 2", // ca entre -5% et 5% (38 000 et 42 000)

    })
    void testParametreCalculPerformanceCommercial(Integer basePerformance, Long caTraite, Long objectifCa,
                                                                 Integer performanceAttendue) throws EmployeException{
        //Given
        String nom = "Doe";
        String prenom = "John";
        String matricule = "C12345";
        Employe employeTest = new Employe(nom, prenom, matricule, LocalDate.now(), 2400d, basePerformance, 1.0);
        Mockito.when(employeRepository.findByMatricule(matricule)).thenReturn(employeTest);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(3d);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        //then
        ArgumentCaptor<Employe> caCommercialCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(caCommercialCaptor.capture());
        Employe employeRetour = caCommercialCaptor.getValue();
        System.out.println(employeRetour.getPerformance());
        Assertions.assertThat(employeRetour.getPerformance()).isEqualTo(performanceAttendue);
    }

}