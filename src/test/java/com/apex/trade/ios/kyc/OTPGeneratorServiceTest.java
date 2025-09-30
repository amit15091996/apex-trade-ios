package com.apex.trade.ios.kyc;

import com.apex.trade.ios.kyc.entity.KYCOTP;
import com.apex.trade.ios.kyc.repository.KYCRepo;
import com.apex.trade.ios.kyc.service.OTPGeneratorServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class OTPGeneratorServiceTest {

    @Mock
    private KYCRepo kycRepo;

    @InjectMocks
    private OTPGeneratorServiceImplementation otpService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOTPGenerator() {
        String aadhaar = "123456789012";
        String channel = "SMS";

        Map<String, Object> response = otpService.OTPGenerator(aadhaar, channel);

        assertEquals("SUCCESS", response.get("status"));
        assertEquals("OTP sent successfully to " + channel, response.get("message"));
        assertEquals(aadhaar, response.get("aadhaar"));
        assertTrue(((String) response.get("txnId")).startsWith("TXN"));
    }

    @Test
    void testVerifyOTP_Success() {
        String txnId = "TXN123";
        String otp = "123456";

        KYCOTP kycOTP = new KYCOTP(txnId, otp);
        when(kycRepo.findById(txnId)).thenReturn(Optional.of(kycOTP));

        Map<String, Object> response = otpService.verifyOTP(otp, txnId);

        assertEquals("otp verified succesfully", response.get("status"));
    }

    @Test
    void testVerifyOTP_Failed() {
        String txnId = "TXN123";
        String otp = "654321";

        KYCOTP kycOTP = new KYCOTP(txnId, "123456"); // correct otp is different
        when(kycRepo.findById(txnId)).thenReturn(Optional.of(kycOTP));

        Map<String, Object> response = otpService.verifyOTP(otp, txnId);

        assertEquals("otp verification failed", response.get("status"));
    }

    @Test
    void testVerifyOTP_NotFound() {
        String txnId = "TXN123";
        String otp = "123456";

        when(kycRepo.findById(txnId)).thenReturn(Optional.empty());

        Map<String, Object> response = otpService.verifyOTP(otp, txnId);

        assertEquals("otp not found,regenerate otp", response.get("status"));
    }

    @Test
    void testGenerateOTP_Length() {
        String otp = otpService.generateOTP(6);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}")); // only digits
    }
}
