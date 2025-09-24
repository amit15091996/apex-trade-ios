package com.apex.trade.ios.client_services.repo;

import com.apex.trade.ios.registration.entities.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientServiceRepository  extends JpaRepository<Investor,Long> {

    Optional<Investor> findByEmail(String email);
}


