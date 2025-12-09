package com.apex.trade.ios.registration.service;

import com.apex.trade.ios.email.EmailService;
import com.apex.trade.ios.registration.beans.CorporateRegistrationRequest;
import com.apex.trade.ios.registration.beans.InvestorRegistrationRequest;
import com.apex.trade.ios.registration.entities.CorporateInvestor;
import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.entities.Role;
import com.apex.trade.ios.registration.repo.CorporateInvestorRegistrationRepository;
import com.apex.trade.ios.registration.repo.InvestorRegistrationRepository;
import com.apex.trade.ios.registration.repo.RoleRepository;
import com.apex.trade.ios.registration.utils.KycStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CorporateInvesterRegistrationServiceImpl implements CorporateInvesterRegistrationService{

    private CorporateInvestorRegistrationRepository corporateInvestorRegistrationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public CorporateInvestor registerCorporateInvestor(CorporateRegistrationRequest request) {
        if (corporateInvestorRegistrationRepository.findbyPan(request.getPanNumber()).isPresent()) {
            throw new IllegalArgumentException("pan already registered");
        }

        CorporateInvestor corporateInvestor = new CorporateInvestor();
        corporateInvestor.setEmail(request.getEmail());
        corporateInvestor.setPassword(passwordEncoder.encode(request.getPassword()));
        corporateInvestor.setKycStatus(KycStatus.PENDING);
        String token = UUID.randomUUID().toString();
        corporateInvestor.setEmailVerificationToken(token);
        corporateInvestor.setEmailVerified(false);

        CorporateInvestor savedInvestor = corporateInvestorRegistrationRepository.save(corporateInvestor);

        emailService.sendVerificationEmail(savedInvestor.getEmail(), token);

        return savedInvestor;
    }
}
