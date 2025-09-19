package com.apex.trade.ios.registration.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvestorRegistrationResponse {
    private String email;
    private String fullName;
    private String panNumber;
    private String kycStatus;
    private boolean emailVerified;
}

