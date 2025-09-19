package com.apex.trade.ios.login;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {
    Optional<UserOtp> findByEmail(String email);
    void deleteByEmail(String email);
}

