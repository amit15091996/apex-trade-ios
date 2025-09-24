package com.apex.trade.ios.kyc.repository;

import com.apex.trade.ios.kyc.entity.KYCOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KYCRepo extends JpaRepository<KYCOTP, String> {

}
