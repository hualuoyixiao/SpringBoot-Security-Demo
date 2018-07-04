#Spring Security
Spring Security 提供了基于javaEE的企业应用软件全面的安全服务。
这里已一个简单的登录栗子来讲解如何使用Spring Security
## 项目依赖pom
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

## 配置SecurityConfig
```
/**
 * Spring Security 配置类.
 *
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法安全设置
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final String KEY = "linchaokun.cn";

	@Qualifier("userServiceImpl")
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();   // 使用 BCrypt 加密
    }  
	
	@Bean
    public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder); // 设置密码加密方式
        return authenticationProvider;  
    }  
 
	/**
	 * 自定义配置
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/css/**", "/js/**", "/fonts/**", "/index").permitAll() // 都可以访问
				.antMatchers("/admins/**").hasRole("ADMIN") // 需要相应的角色才能访问
				.and()
				.formLogin()   //基于 Form 表单登录验证
				.loginPage("/login").failureUrl("/login-error") // 自定义登录界面
				.and().rememberMe().key(KEY) // 启用 remember me
				.and().exceptionHandling().accessDeniedPage("/403");  // 处理异常，拒绝访问就重定向到 403 页面
	}
	
	/**
	 * 认证信息管理
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authenticationProvider());
	}
}

```
## User实体
```

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

```
## Authority 实体
```
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
```
## UserRepository 仓库
```
/**
 * 用户仓库.

 */
public interface UserRepository extends JpaRepository<User, Long> {
	/**
	 * 根据名称查询
	 * @param username
	 * @return
	 */
	User findByUsername(String username);
}

```
## Authority 仓库
```
/**
 * Authority 仓库.

 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
```
## UserService
```

/**
 * User 服务.

 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username);
	}
}
```

## AuthorityService
```
/**
 * Authority 服务.

 */
@Service
public class AuthorityServiceImpl  implements AuthorityService {
	
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Override
	public Authority getAuthorityById(Long id) {
		return authorityRepository.findById(id).get();
	}

}
```

## MainController
```

/**
 * 主页控制器.

 */
@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/admins";
    }

	/**
	 * 获取登录界面
	 * @return
	 */
	@GetMapping("/login")
	public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof AnonymousAuthenticationToken){
            return "login";
        }else{
            return "redirect:/admins";
        }
	}

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        model.addAttribute("errorMsg", "登陆失败，账号或者密码错误！");
        return "login";
    }

}
```

## AdminController
```
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
```
### 源码获取
项目完整源码地址：
https://github.com/hualuoyixiao/SpringBoot-Security-Demo