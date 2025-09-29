package com.apex.trade.ios.resetpassword;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.apex.trade.ios.email.EmailService;
import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.repo.InvestorRegistrationRepository;
import com.apex.trade.ios.resetpassword.entity.PasswordResetToken;
import com.apex.trade.ios.resetpassword.repo.PasswordResetTokenRepository;
import com.apex.trade.ios.resetpassword.service.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private InvestorRegistrationRepository investorRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private final String email = "user@example.com";
    private final String token = UUID.randomUUID().toString();

    private Investor investor;

    @BeforeEach
    public void setup() {
        investor = new Investor();
        investor.setEmail(email);
        investor.setPassword("oldPassword");
    }

    @Test
    public void testCreatePasswordResetToken_Success() {
        when(investorRepository.findByEmail(email)).thenReturn(Optional.of(investor));

        // To capture saved token
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        when(tokenRepository.save(tokenCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        passwordResetService.createPasswordResetToken(email);

        verify(tokenRepository).deleteByEmail(email);
        verify(investorRepository).findByEmail(email);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(email, savedToken.getEmail());
        assertNotNull(savedToken.getToken());
        assertTrue(savedToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testCreatePasswordResetToken_EmailNotFound() {
        when(investorRepository.findByEmail(email)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.createPasswordResetToken(email);
        });

        assertEquals("Email not found", ex.getMessage());
        verify(investorRepository).findByEmail(email);
        verifyNoMoreInteractions(tokenRepository, emailService);
    }

    @Test
    public void testResetPassword_Success() {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(investorRepository.findByEmail(email)).thenReturn(Optional.of(investor));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        passwordResetService.resetPassword(token, "newPassword");

        verify(tokenRepository).findByToken(token);
        verify(investorRepository).findByEmail(email);
        verify(passwordEncoder).encode("newPassword");
        verify(investorRepository).save(investor);
        verify(tokenRepository).delete(resetToken);

        assertEquals("encodedNewPassword", investor.getPassword());
    }

    @Test
    public void testResetPassword_InvalidToken() {
        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword(token, "newPassword");
        });

        assertEquals("Invalid password reset token", ex.getMessage());
        verify(tokenRepository).findByToken(token);
        verifyNoMoreInteractions(investorRepository, passwordEncoder);
    }

    @Test
    public void testResetPassword_ExpiredToken() {
        PasswordResetToken expiredToken = new PasswordResetToken();
        expiredToken.setToken(token);
        expiredToken.setEmail(email);
        expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(expiredToken));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword(token, "newPassword");
        });

        assertEquals("Token expired", ex.getMessage());
        verify(tokenRepository).findByToken(token);
        verify(tokenRepository).delete(expiredToken);
        verifyNoMoreInteractions(investorRepository, passwordEncoder);
    }

    @Test
    public void testResetPassword_UserNotFound() {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(investorRepository.findByEmail(email)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword(token, "newPassword");
        });

        assertEquals("User not found", ex.getMessage());
        verify(tokenRepository).findByToken(token);
        verify(investorRepository).findByEmail(email);
        verifyNoMoreInteractions(passwordEncoder);
    }
}

