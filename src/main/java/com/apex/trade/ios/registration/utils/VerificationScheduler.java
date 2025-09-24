package com.apex.trade.ios.registration.utils;

import com.apex.trade.ios.email.NotificationService;
import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.repo.InvestorRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationScheduler {

    private final InvestorRegistrationRepository investorRepository;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

//    @Scheduled(fixedDelay = 60000) // every 60s
    public void processPendingKycs() {
        List<Investor> pending = investorRepository.findAllByKycStatus(KycStatus.PENDING);
        log.info("Processing {} pending KYC(s)", pending.size());

        for (Investor investor : pending) {
            KycStatus newStatus = secureRandom.nextBoolean() ? KycStatus.VERIFIED : KycStatus.REJECTED;
            investor.setKycStatus(newStatus);
            investorRepository.save(investor);

            notificationService.sendKycStatusUpdateEmail(investor.getEmail(), newStatus);
            log.info("Sent KYC status update to {}", investor.getEmail());
        }
    }
}
