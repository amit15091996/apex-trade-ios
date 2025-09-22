package com.apex.trade.ios.kyc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/aadhaar")
public class AadharKYCController {

    private final WebClient webClient;

    public AadharKYCController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    //    {
//        "aadhaar": "123412341234"
//    }
    @PostMapping("/request-otp")
    public Mono<ResponseEntity<String>> requestOtp(@RequestBody Map<String, String> req) {
        String aadhaar = req.get("aadhaar");
        Map<String, Object> payload = Map.of(
                "aadhaar", aadhaar,
                "channel", "SMS"
        );
        return webClient.post()
                .uri("https://sandbox-aadhaar-provider.example.com/otp/request")
                .bodyValue(payload)
                .retrieve()
                .toEntity(String.class);
//        {
//            "status": "SUCCESS",
//                "message": "OTP sent successfully",
//                "txnId": "TXN123456"
//        }
    }

    @PostMapping("/verify-otp")
    public Mono<ResponseEntity<String>> verifyOtp(@RequestBody Map<String, String> req) {
//        {
//                "otp":"564556",
//                "txnId":"TXN123456"
//        }
        String txnId = req.get("txnId");
        String otp = req.get("otp");
        Map<String, Object> payload = Map.of("txnId", txnId, "otp", otp);
        return webClient.post()
                .uri("https://sandbox-aadhaar-provider.example.com/otp/verify")
                .bodyValue(payload)
                .retrieve()
                .toEntity(String.class);
    }
}
