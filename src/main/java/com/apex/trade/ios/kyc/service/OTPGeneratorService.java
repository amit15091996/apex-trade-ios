package com.apex.trade.ios.kyc.service;

import java.util.Map;

public interface OTPGeneratorService {
    public Map<String, Object> OTPGenerator(String aadhaar, String channel);

    public Map<String, Object> verifyOTP(String otp, String txnId);
}
