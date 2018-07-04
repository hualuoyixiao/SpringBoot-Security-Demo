package cn.linchaokun.securitytest1.service;

import cn.linchaokun.securitytest1.dataobject.User;

/**
 * User 服务接口.
 *
 */
public interface UserService {
	/**
	 * 保存用户
	 * @param user
	 * @return
	 */
	User saveUser(User user);
	
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	void removeUser(Long id);
	

}
