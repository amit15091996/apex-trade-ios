package com.apex.trade.ios.registration.controller;

import com.apex.trade.ios.registration.entities.Role;
import com.apex.trade.ios.registration.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // 1. Get user roles by email
    @GetMapping("/{email}")
    public ResponseEntity<Set<Role>> getUserRoles(@PathVariable String email) {
        return ResponseEntity.ok(roleService.getRolesByEmail(email));
    }

    // 2. Add role to user
    @PostMapping("/{email}")
    public ResponseEntity<String> addRoleToUser(
            @PathVariable String email,
            @RequestParam String roleName) {
        roleService.addRoleToInvestor(email, roleName);
        return ResponseEntity.ok("Role added successfully.");
    }

    // 3. Delete role from user
    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteRoleFromUser(
            @PathVariable String email,
            @RequestParam String roleName) {
        roleService.removeRoleFromInvestor(email, roleName);
        return ResponseEntity.ok("Role removed successfully.");
    }

    // ➕ Create a new role
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Role> createRole(@RequestParam String roleName) {
        Role createdRole = roleService.createRole(roleName);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }
}

