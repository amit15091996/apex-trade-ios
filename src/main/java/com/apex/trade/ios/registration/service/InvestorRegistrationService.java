package com.apex.trade.ios.registration.service;

import com.apex.trade.ios.registration.beans.InvestorRegistrationRequest;
import com.apex.trade.ios.registration.entities.Investor;

public interface InvestorRegistrationService {
    Investor registerInvestor(InvestorRegistrationRequest request);
}
