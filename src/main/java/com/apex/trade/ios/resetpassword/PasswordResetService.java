package com.apex.trade.ios.resetpassword;

import com.apex.trade.ios.email.EmailService;
import com.apex.trade.ios.registration.Investor;
import com.apex.trade.ios.registration.InvestorRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final InvestorRegistrationRepository investorRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int EXPIRATION_MINUTES = 30;

    public void createPasswordResetToken(String email) {
        Investor investor = investorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        // Delete old tokens for this email
        tokenRepository.deleteByEmail(email);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(email, token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Token expired");
        }

        Investor investor = investorRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        investor.setPassword(passwordEncoder.encode(newPassword));
        investorRepository.save(investor);

        // Token is single-use, delete after successful reset
        tokenRepository.delete(resetToken);
    }
}
