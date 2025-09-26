package com.apex.trade.ios.registration;

import com.apex.trade.ios.email.EmailService;
import com.apex.trade.ios.registration.beans.InvestorRegistrationRequest;
import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.entities.Role;
import com.apex.trade.ios.registration.repo.InvestorRegistrationRepository;
import com.apex.trade.ios.registration.repo.RoleRepository;
import com.apex.trade.ios.registration.service.InvestorRegistrationServiceImpl;
import com.apex.trade.ios.registration.utils.KycStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InvestorRegistrationServiceImplTest {

    @Mock
    private InvestorRegistrationRepository investorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private InvestorRegistrationServiceImpl registrationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterInvestor_Success() {
        InvestorRegistrationRequest request = new InvestorRegistrationRequest();
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPhoneNumber("1234567890");
        request.setPassword("password");

        when(investorRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        Role newInvestorRole = new Role();
        newInvestorRole.setName("NEW_INVESTOR");
        when(roleRepository.findByName("NEW_INVESTOR")).thenReturn(Optional.of(newInvestorRole));

        // Capture the investor saved
        ArgumentCaptor<Investor> investorCaptor = ArgumentCaptor.forClass(Investor.class);
        when(investorRepository.save(investorCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Investor savedInvestor = registrationService.registerInvestor(request);

        // Verify email existence check
        verify(investorRepository).findByEmail(request.getEmail());

        // Verify password encoding
        verify(passwordEncoder).encode(request.getPassword());

        // Verify role fetching
        verify(roleRepository).findByName("NEW_INVESTOR");

        // Verify save called
        verify(investorRepository).save(any(Investor.class));

        // Verify email sent
        verify(emailService).sendVerificationEmail(savedInvestor.getEmail(), savedInvestor.getEmailVerificationToken());

        Investor capturedInvestor = investorCaptor.getValue();

        assertEquals(request.getEmail(), capturedInvestor.getEmail());
        assertEquals("encodedPassword", capturedInvestor.getPassword());
        assertEquals(request.getFullName(), capturedInvestor.getFullName());
        assertEquals(request.getPhoneNumber(), capturedInvestor.getPhoneNumber());
        assertFalse(capturedInvestor.isEmailVerified());
        assertEquals(KycStatus.PENDING, capturedInvestor.getKycStatus());
        assertTrue(capturedInvestor.getRoles().contains(newInvestorRole));
        assertNotNull(capturedInvestor.getEmailVerificationToken());
    }

    @Test
    public void testRegisterInvestor_EmailAlreadyExists() {
        InvestorRegistrationRequest request = new InvestorRegistrationRequest();
        request.setEmail("existing@example.com");

        when(investorRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new Investor()));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerInvestor(request);
        });

        assertEquals("Email already registered", thrown.getMessage());
        verify(investorRepository).findByEmail(request.getEmail());
        verifyNoMoreInteractions(investorRepository, passwordEncoder, roleRepository, emailService);
    }

    @Test
    public void testRegisterInvestor_DefaultRoleNotFound() {
        InvestorRegistrationRequest request = new InvestorRegistrationRequest();
        request.setEmail("newuser@example.com");
        request.setFullName("New User");
        request.setPhoneNumber("12345");
        request.setPassword("pwd");

        when(investorRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPwd");
        when(roleRepository.findByName("NEW_INVESTOR")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            registrationService.registerInvestor(request);
        });

        assertEquals("Default role not found", thrown.getMessage());

        verify(investorRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(roleRepository).findByName("NEW_INVESTOR");
        verifyNoMoreInteractions(investorRepository, emailService);
    }
}
