package com.puzzly.security.details;

import com.puzzly.enums.Authority;
import com.puzzly.enums.JoinType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Data
@Slf4j
@RequiredArgsConstructor
public class SecurityUser implements UserDetails {
    private Long userId;

    private String email;
    private String password;
    private String userName;
    private LocalDate birth;
    private boolean gender;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private JoinType joinType;
    @Enumerated(EnumType.STRING)
    private Authority authority;

    private LocalDateTime createDateTime;
    private LocalDateTime deleteDateTime;

    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(authority.toString()));
    }
    public String getEmail(){
        return this.email;
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
