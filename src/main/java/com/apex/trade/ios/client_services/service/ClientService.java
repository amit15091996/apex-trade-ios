package com.apex.trade.ios.client_services.service;

import com.apex.trade.ios.client_services.UnauthorizedException;
import com.apex.trade.ios.client_services.repo.ClientServiceRepository;
import com.apex.trade.ios.config.JwtUtil;
import com.apex.trade.ios.registration.entities.Investor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientService {

        @Autowired
        private ClientServiceRepository clientServiceRepository;

        @Autowired
        private JwtUtil jwtUtil;

        public Investor verifyInvestorFromToken(String token) {
            Claims claims = jwtUtil.getClaimsFromToken(token);
            log.info("All claims: {}", claims);

            String userId = claims.getSubject();
            //System.out.println(userId);
            String email = claims.get("email", String.class);
            String phoneNumber = claims.get("phoneNumber", String.class);
            String panNumber = claims.get("panNumber", String.class);
            // Fetch investor from DB by userId
            Investor investor = clientServiceRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("Investor not found"));
            log.info("DB email: {}, Token email: {}", investor.getEmail(), email);
            log.info("DB phone: {}, Token phone: {}", investor.getPhoneNumber(), phoneNumber);
            log.info("DB pan: {}, Token pan: {}", investor.getPanNumber(), panNumber);

            // Verify the details match
            if (!investor.getEmail().equals(email) ||
                    !investor.getPanNumber().equals(panNumber) ||
                    !investor.getPhoneNumber().equals(phoneNumber)) {
                throw new UnauthorizedException("Investor details do not match");
            }

            // Verified successfully
            return investor;
        }

}
