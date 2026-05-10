package com.taskmanager.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Standalone UserDetailsService bean to break the circular dependency between
 * SecurityConfig (which defines UserDetailsService) and JwtAuthenticationFilter
 * (which requires both UserDetailsService and is consumed by SecurityConfig).
 * <p>
 * By extracting user lookup here, SecurityConfig only needs to reference this
 * bean, and JwtAuthenticationFilter can be injected without cycles.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String ADMIN_USERNAME = "admin";
    // BCrypt hash of "admin123"
    private static final String ADMIN_PASSWORD =
            new BCryptPasswordEncoder().encode("admin123");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!ADMIN_USERNAME.equals(username)) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return User.builder()
                .username(ADMIN_USERNAME)
                .password(ADMIN_PASSWORD)
                .roles("ADMIN")
                .build();
    }
}
