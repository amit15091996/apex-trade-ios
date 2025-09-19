package com.apex.trade.ios.registration.controller;

import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.repo.InvestorRegistrationRepository;
import com.apex.trade.ios.registration.utils.KycStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/investors")
public class EmailVerificationController {

    @Autowired
    private InvestorRegistrationRepository investorRepository;

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        Optional<Investor> optionalInvestor = investorRepository.findByEmailVerificationToken(token);

        if (optionalInvestor.isPresent()) {
            Investor investor = optionalInvestor.get();
            investor.setEmailVerified(true);
            investor.setEmailVerificationToken(null); // clear token
            investorRepository.save(investor);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Email verified successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Invalid or expired token."));
        }
    }


    @PostMapping("/kyc/upload")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<?> uploadKyc(@RequestParam("file") MultipartFile file,
                                       @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Investor investor = investorRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        // Store file
        String path = "uploads/" + investor.getId() + "_" + file.getOriginalFilename();
        File dest = new File(path);
        file.transferTo(dest);

        investor.setKycStatus(KycStatus.PENDING);
        investorRepository.save(investor);

        return ResponseEntity.ok(Map.of("status", "success", "message", "KYC uploaded"));
    }

}

