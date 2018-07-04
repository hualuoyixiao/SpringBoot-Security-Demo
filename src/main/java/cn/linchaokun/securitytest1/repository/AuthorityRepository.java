package cn.linchaokun.securitytest1.repository;


import cn.linchaokun.securitytest1.dataobject.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Authority 仓库.

 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
