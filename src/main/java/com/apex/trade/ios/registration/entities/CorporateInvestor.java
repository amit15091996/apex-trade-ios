package com.apex.trade.ios.registration.entities;

import com.apex.trade.ios.registration.utils.KycStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "corporate_investor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorporateInvestor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String panNumber;

    @Column(nullable = false)
    private boolean emailVerified = false;

    private String emailVerificationToken;

    @Column(length = 20)
    private String password;

    @NotBlank
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;
}
