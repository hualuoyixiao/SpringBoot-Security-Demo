package cn.linchaokun.securitytest1.service;

import cn.linchaokun.securitytest1.dataobject.Authority;

/**
 * Authority 服务接口.

 */
public interface AuthorityService {
	 
	
	/**
	 * 根据id获取 Authority
	 * @return
	 */
	Authority getAuthorityById(Long id);
}
