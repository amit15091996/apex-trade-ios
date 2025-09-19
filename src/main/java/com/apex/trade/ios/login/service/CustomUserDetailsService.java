package com.apex.trade.ios.login.service;

import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.repo.InvestorRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private InvestorRegistrationRepository investorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Investor investor = investorRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!investor.isEmailVerified()) {
            throw new RuntimeException("Please verify your email before logging in.");
        }

        Set<GrantedAuthority> authorities = investor.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(investor.getEmail(), investor.getPassword(), authorities);
    }

}
