package com.apex.trade.ios.registration.service;

import com.apex.trade.ios.registration.beans.CorporateRegistrationRequest;
import com.apex.trade.ios.registration.beans.InvestorRegistrationRequest;
import com.apex.trade.ios.registration.entities.CorporateInvestor;
import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.repo.CorporateInvestorRegistrationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


public interface CorporateInvesterRegistrationService {

    CorporateInvestor registerCorporateInvestor(CorporateRegistrationRequest request);
}
