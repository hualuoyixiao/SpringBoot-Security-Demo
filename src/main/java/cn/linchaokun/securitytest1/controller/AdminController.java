package cn.linchaokun.securitytest1.controller;

import cn.linchaokun.securitytest1.dataobject.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户控制器.
 *
 */
@Controller
@RequestMapping("/admins")
public class AdminController {

	/**
	 * 获取后台管理主页面
	 * @return
	 */
	@GetMapping()
	public String listUsers(Model model) {
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();

        model.addAttribute("user",user);

        return "/admins/index";
	}
 
	 
}
