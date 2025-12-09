package com.apex.trade.ios.registration.controller;

import com.apex.trade.ios.registration.beans.CorporateRegistrationRequest;
import com.apex.trade.ios.registration.entities.CorporateInvestor;
import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.service.CorporateInvesterRegistrationService;
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
@RequestMapping("/api/corporate/investers")
@RequiredArgsConstructor
public class CorporateRegistrationController {
    private final CorporateInvesterRegistrationService corporateInvesterRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> register(@Valid @RequestBody CorporateRegistrationRequest corporateRegistrationRequest) {
       Map<String,Object> map=new HashMap<>();
        try {
            CorporateInvestor investor = corporateInvesterRegistrationService.registerCorporateInvestor(corporateRegistrationRequest);

            map.put("status", "success");
            map.put("created", "true");
            map.put("id", investor.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(map);

        } catch (IllegalArgumentException e) {
            map.put("status", "failed");
            map.put("created", "false");
            map.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
}
