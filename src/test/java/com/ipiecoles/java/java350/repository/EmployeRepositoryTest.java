package com.ipiecoles.java.java350.repository;
import com.ipiecoles.java.java350.Java350Application;
import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {Java350Application.class})
//@DataJpaTest
@SpringBootTest
class EmployeRepositoryTest {

    @Autowired
    EmployeRepository employeRepository;

    @AfterEach
    public void dbCleaner(){
        employeRepository.deleteAll();
    }

    //A éviter !
    @ParameterizedTest(name = "Perf {0}, matricule {1}, txActivite {2}, anciennete {3}")
    @CsvSource({
            "1, 'T12345', 1.0, 0",
            "1, 'T23451', 0.5, 0",
            "2, 'T34512', 1.0, 0",
            "1, 'T45123', 1.0, 2",
    })
    public void test(Integer performance, String matricule, Double tauxActivite, Long nbAnneesAnciennete){
        //Given
        //Insérer des données en base
        employeRepository.save(new Employe("Doe", "John", matricule, LocalDate.now().minusYears(nbAnneesAnciennete), 1500d, performance, tauxActivite));
        //employeRepository.save(new Employe());

        //When
        //Exécuter des requêtes en base
        String lastMatricule = employeRepository.findLastMatricule();

        System.out.println(lastMatricule);

        //Then
        Assertions.assertThat(lastMatricule).isNotNull();
    }

    @Test
    public void testEmployeRepositoryMaxMatricule(){
        //Given
        //Insérer des données en base
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0));
        employeRepository.save(new Employe("Doe", "John", "T23456", LocalDate.now(), 1500d, 1, 1.0));
        employeRepository.save(new Employe("Doe", "John", "T34567", LocalDate.now(), 1500d, 1, 1.0));
        employeRepository.save(new Employe("Doe", "John", "M12345", LocalDate.now(), 1500d, 1, 1.0));
        //employeRepository.save(new Employe());

        //When
        //Exécuter des requêtes en base
        String lastMatricule = employeRepository.findLastMatricule();
        System.out.println(lastMatricule);

        //Then
        Assertions.assertThat(lastMatricule).isEqualTo("34567");
    }

}
