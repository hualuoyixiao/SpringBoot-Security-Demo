package cn.linchaokun.securitytest1.dataobject;

import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(appliesTo = "user",comment = "角色权限表")
@Data
public class User implements UserDetails,Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id;

    @NotEmpty(message = "姓名不能为空")
    @Column(nullable = false,columnDefinition = "varchar(32) comment '姓名'")
    private String name;

    @Email(message = "邮箱格式错误")
    @Column(columnDefinition = "varchar(32) comment '邮箱'")
    private String email;

    @NotEmpty(message = "账号不能为空")
    @Column(nullable = false,columnDefinition = "varchar(32) comment '账号'")
    private String username;

    @NotEmpty(message = "密码不能为空")
    @Column(nullable = false,columnDefinition = "varchar(100) comment '密码'")
    private String password;

    @Column(columnDefinition = "varchar(32) comment '头像'")
    private String avatar;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id",referencedColumnName = "id"))
    private List<Authority> authorities;


    protected User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> simpleAuthorities = new ArrayList<>();
        for(GrantedAuthority authority : this.authorities){
            simpleAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
        }
        return simpleAuthorities;

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
