package com.apex.trade.ios.registration.repo;

import com.apex.trade.ios.registration.entities.CorporateInvestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface  CorporateInvestorRegistrationRepository extends JpaRepository<CorporateInvestor, Long> {
    @Query("select c from CorporateInvestor c where c.panNumber=:pan")
    Optional<CorporateInvestor> findbyPan(@Param("pan") String pan);
}
