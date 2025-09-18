package com.apex.trade.ios.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvestorRegistrationServiceImpl implements InvestorRegistrationService {

    private final InvestorRegistrationRepository investorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Investor registerInvestor(InvestorRegistrationRequest request) {
        if (investorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        Investor investor = new Investor();
        investor.setEmail(request.getEmail());
        investor.setFullName(request.getFullName());
        investor.setPhoneNumber(request.getPhoneNumber());
        investor.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        investor.setKycStatus(KycStatus.PENDING);

        return investorRepository.save(investor);
    }
}
