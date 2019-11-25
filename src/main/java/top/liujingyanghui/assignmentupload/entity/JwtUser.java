package top.liujingyanghui.assignmentupload.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * security需要的UserDetails实现类
 */
@Data
public class JwtUser implements UserDetails {
    private static final long serialVersionUID = -4959252432107932674L;
    private final long id;
    private final String username;
    private final String password;
    private final String role;
    private final String name;
    private Integer schoolId;
    /**
     * 权限类.
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * 在createJwtFactory里注入
     */
    public JwtUser(User user, Collection<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.authorities = authorities;
        this.name = user.getName();
        this.schoolId = user.getSchoolId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
