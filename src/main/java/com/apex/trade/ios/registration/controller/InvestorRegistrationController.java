package com.apex.trade.ios.registration.controller;

import com.apex.trade.ios.registration.beans.InvestorRegistrationRequest;
import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.service.InvestorRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/investors")
@RequiredArgsConstructor
public class InvestorRegistrationController {


    private final InvestorRegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody InvestorRegistrationRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Investor investor = registrationService.registerInvestor(request);

            response.put("status", "success");
            response.put("created", true);
            response.put("id", investor.getId());
            response.put("name", investor.getFullName());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            response.put("status", "failed");
            response.put("created", false);
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

}
