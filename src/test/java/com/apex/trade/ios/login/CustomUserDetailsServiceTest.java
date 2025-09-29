package com.apex.trade.ios.login;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.apex.trade.ios.login.service.CustomUserDetailsService;
import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.entities.Role;
import com.apex.trade.ios.registration.repo.InvestorRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsServiceTest {

    @Mock
    private InvestorRegistrationRepository investorRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserFoundAndEmailVerified() {
        String email = "user@example.com";
        Investor investor = new Investor();
        investor.setEmail(email);
        investor.setPassword("encryptedPassword");
        investor.setEmailVerified(true);

        Role role = new Role();
        role.setName("ROLE_USER");
        investor.setRoles(Set.of(role));

        when(investorRepository.findByEmail(email)).thenReturn(Optional.of(investor));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
        assertEquals(investor.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet())
                .contains("ROLE_USER"));

        verify(investorRepository).findByEmail(email);
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        String email = "notfound@example.com";

        when(investorRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        assertTrue(thrown.getMessage().contains("User not found with email"));
        verify(investorRepository).findByEmail(email);
    }

    @Test
    public void testLoadUserByUsername_EmailNotVerified() {
        String email = "unverified@example.com";
        Investor investor = new Investor();
        investor.setEmail(email);
        investor.setPassword("password");
        investor.setEmailVerified(false);

        when(investorRepository.findByEmail(email)).thenReturn(Optional.of(investor));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        assertEquals("Please verify your email before logging in.", thrown.getMessage());
        verify(investorRepository).findByEmail(email);
    }
}

