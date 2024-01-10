package com.project.soshuceapi.entities;

import com.project.soshuceapi.common.enums.security.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "phoneNumber", columnDefinition = "VARCHAR(15)", unique = true, nullable = false)
    private String phoneNumber;
    @Column(name = "name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;
    @Column(name = "email", columnDefinition = "VARCHAR(100)", unique = true, nullable = false)
    private String email;
    @Column(name = "password", columnDefinition = "VARCHAR(100)", nullable = false)
    private String password;
    @Column(name = "is_activated", columnDefinition = "BOOLEAN default true", nullable = false)
    private boolean isActivated;
    @Column(name = "is_deleted", columnDefinition = "BOOLEAN default false", nullable = false)
    private boolean isDeleted;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    @Column(name = "created_by", columnDefinition = "VARCHAR(36)", nullable = false)
    private String createdBy;
    @Column(name = "updated_by", columnDefinition = "VARCHAR(36)")
    private String updatedBy;
    @Enumerated(EnumType.STRING)
    private ERole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isDeleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActivated;
    }
}
