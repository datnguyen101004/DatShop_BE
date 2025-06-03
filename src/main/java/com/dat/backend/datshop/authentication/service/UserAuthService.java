package com.dat.backend.datshop.authentication.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAuthService extends UserDetailsService {
    public UserDetails loadUserByUsername(String username);
}
