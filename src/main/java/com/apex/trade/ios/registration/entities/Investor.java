package com.apex.trade.ios.registration.entities;

import com.apex.trade.ios.registration.utils.KycStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "investors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Investor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private boolean emailVerified = false;

    private String emailVerificationToken;

    private String panNumber;


    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String nominee;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private InvestorType type;

    @Enumerated(EnumType.STRING)
    private InvestorRole role;



    @PrePersist
    public void prePersist() {
        if (kycStatus == null) {
            kycStatus = KycStatus.PENDING;
        }
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "investor_roles",
            joinColumns = @JoinColumn(name = "investor_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

}

