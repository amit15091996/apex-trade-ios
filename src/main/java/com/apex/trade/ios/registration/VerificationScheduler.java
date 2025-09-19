package com.apex.trade.ios.registration;

import com.apex.trade.ios.email.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationScheduler {

    private final InvestorRegistrationRepository investorRepository;
    private final NotificationService notificationService;
    private final Random random = new Random();

    @Scheduled(fixedDelay = 60000) // every 60s
    public void processPendingKycs() {
        List<Investor> pending = investorRepository.findAllByKycStatus(KycStatus.PENDING);
        log.info("Processing {} pending KYC(s)", pending.size());

        for (Investor investor : pending) {
            KycStatus newStatus = random.nextBoolean() ? KycStatus.VERIFIED : KycStatus.REJECTED;
            investor.setKycStatus(newStatus);
            investorRepository.save(investor);

            notificationService.sendKycStatusUpdateEmail(investor.getEmail(), newStatus);
            log.info("Sent KYC status update to {}", investor.getEmail());
        }
    }
}
