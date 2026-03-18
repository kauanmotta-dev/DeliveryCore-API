package com.deliverycore.after.model.user.entity;

import com.deliverycore.after.exception.exceptions.BusinessException;
import com.deliverycore.after.model.user.enums.DeliverymanStatus;
import com.deliverycore.after.model.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails{

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliverymanStatus deliverymanStatus = DeliverymanStatus.OFFLINE;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private int clientPenaltyCount = 0;

    @Column(nullable = false)
    private int deliverymanPenaltyCount = 0;

public User(String name, String email, String password, String cpf, UserRole role, Boolean active) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.role = role;
        this.active = active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getAuthority()));
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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() { return active; }

    public void promoteToDeliveryman() {

        if (this.role == UserRole.DELIVERYMAN) {
            throw new BusinessException("User already a deliveryman");
        }

        this.role = UserRole.DELIVERYMAN;
    }

}