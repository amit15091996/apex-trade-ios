package com.apex.trade.ios.registration.repo;

import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.utils.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorRegistrationRepository extends JpaRepository<Investor, Long> {

    Optional<Investor> findByEmail(String email);

    Optional<Investor> findByEmailVerificationToken(String token);

    List<Investor> findAllByKycStatus(KycStatus status);
}

