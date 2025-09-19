package com.apex.trade.ios.registration;

import com.apex.trade.ios.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestorRegistrationServiceImpl implements InvestorRegistrationService {

    private final InvestorRegistrationRepository investorRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Override
    public Investor registerInvestor(InvestorRegistrationRequest request) {
        if (investorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        Investor investor = new Investor();
        investor.setEmail(request.getEmail());
        investor.setFullName(request.getFullName());
        investor.setPhoneNumber(request.getPhoneNumber());
        investor.setPassword(passwordEncoder.encode(request.getPassword()));
        investor.setKycStatus(KycStatus.PENDING);
        String token = UUID.randomUUID().toString();
        investor.setEmailVerificationToken(token);
        investor.setEmailVerified(false);
        Role role = roleRepository.findByName("ROLE_INVESTOR")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        investor.setRoles(Set.of(role));

        Investor savedInvestor = investorRepository.save(investor);

        emailService.sendVerificationEmail(savedInvestor.getEmail(), token);

        return savedInvestor;
    }
}
