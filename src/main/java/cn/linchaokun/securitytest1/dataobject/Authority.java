package cn.linchaokun.securitytest1.dataobject;

import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Table(appliesTo = "authority",comment = "角色权限表")
@Data
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false,columnDefinition = "varchar(32) comment '角色名'")
    private String name;


    @Override
    public String getAuthority() {
        return name;
    }
}
