package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Registers a user plus roles attached to that user.
 * */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "system_users", schema = "public", catalog = "project_db")
public class SystemUserModel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username",  nullable = false, unique = true)
    private String username;

    private String password;
    private Boolean isActive;
    private Date lastLoggedInAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(
            name = "role_id"
    )
    private SystemRoleModel role;
    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (role == null) {
            return authorities;
        }
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleCode()));
            for (SystemPermissionModel permission : role.getPermissions()) {
                if (permission != null) {
                    authorities.add(
                            new SimpleGrantedAuthority(permission.getPermissionCode())
                    );
                }
            }
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    @NullMarked
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
