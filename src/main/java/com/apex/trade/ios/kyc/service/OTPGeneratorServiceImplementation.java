package com.apex.trade.ios.kyc.service;

import com.apex.trade.ios.kyc.entity.KYCOTP;
import com.apex.trade.ios.kyc.repository.KYCRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class OTPGeneratorServiceImplementation implements OTPGeneratorService {

    private static final SecureRandom random = new SecureRandom();
    @Autowired
    private KYCRepo kycRepo;

    public Map<String, Object> OTPGenerator(String aadhaar, String channel) {
        String otp = new OTPGeneratorServiceImplementation().generateOTP(6);
        Map<String, Object> response = Map.of(
                "status", "SUCCESS",
                "message", "OTP sent successfully to " + channel,
                "txnId", "TXN" + UUID.randomUUID(),
                "aadhaar", aadhaar
        );
        kycRepo.save(new KYCOTP((String) response.get("txnId"), otp));
        return response;
    }

    public Map<String, Object> verifyOTP(String otp, String txnId) {
        Optional<KYCOTP> kycOTP = kycRepo.findById(txnId);
        Map<String, Object> response = new HashMap<>();
        kycOTP.ifPresentOrElse(status -> {
            if (status.getOTP().equals(otp))
                response.put("status", "otp verified succesfully");
            else
                response.put("status", "otp verification failed");
        }, () -> response.put("status", "otp not found,regenerate otp"));
        return response;
    }

    public String generateOTP(int length) {
        StringBuffer otp = new StringBuffer();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // digits 0–9
        }
        return otp.toString();
    }
}
