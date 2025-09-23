package com.apex.trade.ios.registration.service;

import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.entities.Role;
import com.apex.trade.ios.registration.repo.InvestorRegistrationRepository;
import com.apex.trade.ios.registration.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final InvestorRegistrationRepository investorRepository;
    private final RoleRepository roleRepository;

    // Get roles by email
    public Set<Role> getRolesByEmail(String email) {
        Investor investor = investorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
        return investor.getRoles();
    }

    // Add a role to an investor
    public void addRoleToInvestor(String email, String roleName) {
        Investor investor = investorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        investor.getRoles().add(role);
        investorRepository.save(investor);
    }

    // Remove a role from an investor
    public void removeRoleFromInvestor(String email, String roleName) {
        Investor investor = investorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        investor.getRoles().remove(role);
        investorRepository.save(investor);
    }

    public Role createRole(String roleName) {
        // Ensure role starts with "ROLE_" (optional, but recommended)
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName.toUpperCase();
        }

        // Check if role already exists
        String finalRoleName = roleName;
        roleRepository.findByName(roleName).ifPresent(role -> {
            throw new RuntimeException("Role already exists: " + finalRoleName);
        });

        Role newRole = new Role();
        newRole.setName(roleName);
        return roleRepository.save(newRole);
    }
}
