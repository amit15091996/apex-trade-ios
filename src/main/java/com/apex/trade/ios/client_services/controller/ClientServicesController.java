package com.apex.trade.ios.client_services.controller;

import com.apex.trade.ios.client_services.UnauthorizedException;
import com.apex.trade.ios.client_services.service.ClientService;
import com.apex.trade.ios.registration.entities.Investor;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/investors")
public class ClientServicesController {

        private ClientService clientService;

        @GetMapping("/verify")
        public ResponseEntity<?> verifyInvestor(@RequestHeader("Authorization") String authorizationHeader) {
            try {
                // Extract token from "Bearer <token>"
                String token = authorizationHeader.replace("Bearer ", "");

                Investor verifiedInvestor = clientService.verifyInvestorFromToken(token);

                return ResponseEntity.ok(verifiedInvestor);
            } catch (UnauthorizedException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        }
    }

