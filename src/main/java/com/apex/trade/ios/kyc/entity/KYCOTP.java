package com.apex.trade.ios.kyc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "kyc_otp")
@AllArgsConstructor
@NoArgsConstructor
public class KYCOTP {
    @Id
    private String TXNID;
    private String OTP;
}
