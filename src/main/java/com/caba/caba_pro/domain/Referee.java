package com.caba.caba_pro.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "referees")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Referee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String firstName;

    @Column(nullable = false, length = 60)
    private String lastName;

    @Column(length = 30)
    private String idNumber;

    @Column(length = 20)
    private String phone;

    @Column(length = 120)
    private String email;

    private String profilePhotoUrl;

    @Column(length = 20)
    private String specialty;  // luego lo cambiamos a ENUM

    @Column(length = 20)
    private String level;      // luego lo cambiamos a ENUM

    private LocalDate birthDate;

    private boolean active = true;
}
