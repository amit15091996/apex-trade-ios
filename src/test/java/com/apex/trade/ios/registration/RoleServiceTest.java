package com.apex.trade.ios.registration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.apex.trade.ios.registration.entities.Investor;
import com.apex.trade.ios.registration.entities.Role;
import com.apex.trade.ios.registration.repo.InvestorRegistrationRepository;
import com.apex.trade.ios.registration.repo.RoleRepository;
import com.apex.trade.ios.registration.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private InvestorRegistrationRepository investorRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Investor investor;
    private Role role;

    @BeforeEach
    public void setup() {
        investor = new Investor();
        investor.setEmail("test@example.com");
        investor.setRoles(new HashSet<>());

        role = new Role();
        role.setName("ROLE_USER");
    }

    @Test
    public void testGetRolesByEmail_Success() {
        investor.getRoles().add(role);
        when(investorRepository.findByEmail(investor.getEmail())).thenReturn(Optional.of(investor));

        Set<Role> roles = roleService.getRolesByEmail(investor.getEmail());

        assertNotNull(roles);
        assertTrue(roles.contains(role));
        verify(investorRepository).findByEmail(investor.getEmail());
    }

    @Test
    public void testGetRolesByEmail_InvestorNotFound() {
        when(investorRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            roleService.getRolesByEmail("nonexistent@example.com");
        });

        assertEquals("Investor not found", thrown.getMessage());
    }

    @Test
    public void testAddRoleToInvestor_Success() {
        when(investorRepository.findByEmail(investor.getEmail())).thenReturn(Optional.of(investor));
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        when(investorRepository.save(any(Investor.class))).thenReturn(investor);

        roleService.addRoleToInvestor(investor.getEmail(), role.getName());

        assertTrue(investor.getRoles().contains(role));
        verify(investorRepository).findByEmail(investor.getEmail());
        verify(roleRepository).findByName(role.getName());
        verify(investorRepository).save(investor);
    }

    @Test
    public void testAddRoleToInvestor_InvestorNotFound() {
        when(investorRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            roleService.addRoleToInvestor("nonexistent@example.com", role.getName());
        });

        assertEquals("Investor not found", thrown.getMessage());
        verify(investorRepository).findByEmail("nonexistent@example.com");
        verify(roleRepository, never()).findByName(any());
        verify(investorRepository, never()).save(any());
    }

    @Test
    public void testAddRoleToInvestor_RoleNotFound() {
        when(investorRepository.findByEmail(investor.getEmail())).thenReturn(Optional.of(investor));
        when(roleRepository.findByName("NON_EXISTENT_ROLE")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            roleService.addRoleToInvestor(investor.getEmail(), "NON_EXISTENT_ROLE");
        });

        assertEquals("Role not found", thrown.getMessage());
        verify(investorRepository).findByEmail(investor.getEmail());
        verify(roleRepository).findByName("NON_EXISTENT_ROLE");
        verify(investorRepository, never()).save(any());
    }

    @Test
    public void testRemoveRoleFromInvestor_Success() {
        investor.getRoles().add(role);

        when(investorRepository.findByEmail(investor.getEmail())).thenReturn(Optional.of(investor));
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        when(investorRepository.save(any(Investor.class))).thenReturn(investor);

        roleService.removeRoleFromInvestor(investor.getEmail(), role.getName());

        assertFalse(investor.getRoles().contains(role));
        verify(investorRepository).findByEmail(investor.getEmail());
        verify(roleRepository).findByName(role.getName());
        verify(investorRepository).save(investor);
    }

    @Test
    public void testRemoveRoleFromInvestor_InvestorNotFound() {
        when(investorRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            roleService.removeRoleFromInvestor("nonexistent@example.com", role.getName());
        });

        assertEquals("Investor not found", thrown.getMessage());
        verify(investorRepository).findByEmail("nonexistent@example.com");
        verify(roleRepository, never()).findByName(any());
        verify(investorRepository, never()).save(any());
    }

    @Test
    public void testRemoveRoleFromInvestor_RoleNotFound() {
        when(investorRepository.findByEmail(investor.getEmail())).thenReturn(Optional.of(investor));
        when(roleRepository.findByName("NON_EXISTENT_ROLE")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            roleService.removeRoleFromInvestor(investor.getEmail(), "NON_EXISTENT_ROLE");
        });

        assertEquals("Role not found", thrown.getMessage());
        verify(investorRepository).findByEmail(investor.getEmail());
        verify(roleRepository).findByName("NON_EXISTENT_ROLE");
        verify(investorRepository, never()).save(any());
    }

    @Test
    public void testCreateRole_Success_WithPrefix() {
        String roleName = "ROLE_ADMIN";

        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        Role savedRole = new Role();
        savedRole.setName(roleName);
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        Role result = roleService.createRole(roleName);

        assertEquals(roleName, result.getName());
        verify(roleRepository).findByName(roleName);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    public void testCreateRole_Success_WithoutPrefix() {
        String roleName = "admin";

        String expectedName = "ROLE_ADMIN";
        when(roleRepository.findByName(expectedName)).thenReturn(Optional.empty());

        Role savedRole = new Role();
        savedRole.setName(expectedName);
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        Role result = roleService.createRole(roleName);

        assertEquals(expectedName, result.getName());
        verify(roleRepository).findByName(expectedName);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    public void testCreateRole_RoleAlreadyExists() {
        String roleName = "ROLE_ADMIN";

        Role existingRole = new Role();
        existingRole.setName(roleName);

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(existingRole));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            roleService.createRole(roleName);
        });

        assertTrue(thrown.getMessage().contains("Role already exists"));
        verify(roleRepository).findByName(roleName);
        verify(roleRepository, never()).save(any());
    }
}

