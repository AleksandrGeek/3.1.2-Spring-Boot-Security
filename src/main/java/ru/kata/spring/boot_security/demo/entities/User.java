package ru.kata.spring.boot_security.demo.entities;


import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import java.util.*;


@NoArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
//На сколько критично ???
@ToString(exclude = "roles")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;


    @ManyToMany(fetch = FetchType.EAGER)


    @JoinTable(name = "users_roles",
            //user_id → ссылка на users.id
            joinColumns = @JoinColumn(name = "user_id"),
            //role_id → ссылка на roles.id
            inverseJoinColumns = @JoinColumn(name = "roles_id"))
    // Set предотвращает дублирование ролей
    // у одного пользователя (невозможно добавить две одинаковые роли)
    private Set<Role> roles = new HashSet<>();


    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof User user)) return false;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
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
    public boolean isEnabled() {
        return true;
    }
}


