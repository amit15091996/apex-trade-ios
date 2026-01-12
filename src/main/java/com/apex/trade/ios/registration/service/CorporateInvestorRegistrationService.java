package com.apex.trade.ios.registration.service;

import com.apex.trade.ios.registration.beans.CorporateRegistrationRequest;
import com.apex.trade.ios.registration.entities.CorporateInvestor;


public interface CorporateInvestorRegistrationService {

    CorporateInvestor registerCorporateInvestor(CorporateRegistrationRequest request);
}
