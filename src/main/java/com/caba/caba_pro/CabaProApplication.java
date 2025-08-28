package com.caba.caba_pro;

import com.caba.caba_pro.domain.Referee;
import com.caba.caba_pro.repository.RefereeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CabaProApplication {

    public static void main(String[] args) {
        SpringApplication.run(CabaProApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(RefereeRepository refereeRepository) {
        return args -> {
            refereeRepository.save(Referee.builder()
                    .firstName("Isabella")
                    .lastName("Idarraga")
                    .idNumber("12345")
                    .phone("3001234567")
                    .email("isa@caba.com")
                    .specialty("CAMPO")
                    .level("FIBA")
                    .build());

            refereeRepository.save(Referee.builder()
                    .firstName("Juan")
                    .lastName("Rodríguez")
                    .idNumber("66666")
                    .phone("3019876543")
                    .email("juan@caba.com")
                    .specialty("MESA")
                    .level("PRIMERA")
                    .build());

            refereeRepository.save(Referee.builder()
                    .firstName("Diego")
                    .lastName("Gonzalez")
                    .idNumber("00001")
                    .phone("3019876543")
                    .email("diego@caba.com")
                    .specialty("LINEA")
                    .level("PRIMERA")
                    .build());
        };
    }
}
