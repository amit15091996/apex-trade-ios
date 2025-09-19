package com.apex.trade.ios.login.controller;

import com.apex.trade.ios.config.JwtUtil;
import com.apex.trade.ios.email.EmailService;
import com.apex.trade.ios.login.beans.LoginRequest;
import com.apex.trade.ios.login.beans.OtpRequest;
import com.apex.trade.ios.login.entity.UserOtp;
import com.apex.trade.ios.login.repo.UserOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserOtpRepository userOtpRepository;

    @Autowired
    private EmailService emailService;

    private static final int OTP_EXPIRATION_MINUTES = 5;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Generate 6-digit OTP
            String otp = String.format("%06d", new SecureRandom().nextInt(999999));

            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);


            userOtpRepository.findByEmail(loginRequest.getEmail()).ifPresent(existingOtp -> userOtpRepository.delete(existingOtp));

            UserOtp userOtp = new UserOtp();
            userOtp.setEmail(loginRequest.getEmail());
            userOtp.setOtp(otp);
            userOtp.setExpiryTime(expiryTime);
            userOtpRepository.save(userOtp);

            emailService.sendOtpEmail(loginRequest.getEmail(), otp);

            return ResponseEntity.ok(Map.of("status", "otp_sent", "message", "OTP has been sent to your email"));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "failed", "message", "Invalid credentials"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest) {
        Optional<UserOtp> optionalUserOtp = userOtpRepository.findByEmail(otpRequest.getEmail());

        if (optionalUserOtp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "failed", "message", "OTP not found, please login again"));
        }

        UserOtp userOtp = optionalUserOtp.get();

        if (userOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            userOtpRepository.delete(userOtp);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "failed", "message", "OTP expired, please login again"));
        }

        if (!userOtp.getOtp().equals(otpRequest.getOtp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "failed", "message", "Invalid OTP"));
        }

        userOtpRepository.delete(userOtp);

        UserDetails userDetails = userDetailsService.loadUserByUsername(otpRequest.getEmail());
        String jwtToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("status", "success", "token", jwtToken, "email", otpRequest.getEmail()));
    }
}
