package com.microservice.TokenService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.microservice.TokenService.entity.Role;
import com.microservice.TokenService.repository.RoleRepository;

@Component
public class RoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (roleRepository.count() == 0) {
            initializeRoles();
        }
    }

    private void initializeRoles() {
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        Role operatorRole = new Role();
        operatorRole.setName("OPERATOR");
        roleRepository.save(operatorRole);

        Role teamLeaderRole = new Role();
        teamLeaderRole.setName("TEAM_LEADER");
        roleRepository.save(teamLeaderRole);
    }
}

