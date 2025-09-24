package com.apex.trade.ios.kyc.controller;

import com.apex.trade.ios.kyc.service.OTPGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/sandbox-aadhaar-provider")
public class SandBoxController {
    @Autowired
    private OTPGeneratorService otpGeneratorService;

    @PostMapping("/otp/request")
    public ResponseEntity<Map<String, Object>> requestOtp(@RequestBody Map<String, Object> request) {
        String aadhaar = (String) request.get("aadhaar");
        String channel = (String) request.get("channel");
        Map<String, Object> response = otpGeneratorService.OTPGenerator(aadhaar, channel);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, Object> request) {
        String otp = (String) request.get("otp");
        String txnId = (String) request.get("txnId");
        Map<String, Object> response = otpGeneratorService.OTPGenerator(otp, txnId);
        return ResponseEntity.ok(response);
    }
}
