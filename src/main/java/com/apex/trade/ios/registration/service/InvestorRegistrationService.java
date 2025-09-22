package com.apex.trade.ios.registration.service;

import com.apex.trade.ios.registration.beans.InvestorRegistrationRequest;
import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.utils.KycStatus;
import org.springframework.web.multipart.MultipartFile;

public interface InvestorRegistrationService {

    Investor registerInvestor(InvestorRegistrationRequest request);
}
