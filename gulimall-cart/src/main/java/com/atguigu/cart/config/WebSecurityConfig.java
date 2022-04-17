package com.atguigu.cart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@EnableWebSecurity
public class WebSecurityConfig {

//    @Autowired
//    private UserDetailsService userDetailService;
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        super.configure(http);
//        System.err.println(http.toString());
//        http.authorizeRequests()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin()
//                .loginPage("http://auth.gulimall.com/login.html")//设置登录页
//                .defaultSuccessUrl("http://cart.gulimall.com/cartList.html")
//                .and()
//                .httpBasic();

//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        System.err.println(auth.toString());
//        super.configure(auth);
//        auth.inMemoryAuthentication().withUser("root").password("123456").roles("admin");
//        auth.userDetailsService(userDetailService);
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
}

// http
//         .authorizeRequests()//开启登录配置
//         .antMatchers("/hello").hasRole("admin")//表示访问 /hello 这个接口，需要具备 admin 这个角色
//         .anyRequest().authenticated()//表示剩余的其他接口，登录之后就能访问
//         .and()
//         .formLogin()
//         //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
//         .loginPage("/login_p")
//         //登录处理接口
//         .loginProcessingUrl("/doLogin")
//         //定义登录时，用户名的 key，默认为 username
//         .usernameParameter("uname")
//         //定义登录时，用户密码的 key，默认为 password
//         .passwordParameter("passwd")
//         //登录成功的处理器
//         .successHandler(new AuthenticationSuccessHandler() {
//@Override
//public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
//        resp.setContentType("application/json;charset=utf-8");
//        PrintWriter out = resp.getWriter();
//        out.write("success");
//        out.flush();
//        }
//        })
//        .failureHandler(new AuthenticationFailureHandler() {
//@Override
//public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception) throws IOException, ServletException {
//        resp.setContentType("application/json;charset=utf-8");
//        PrintWriter out = resp.getWriter();
//        out.write("fail");
//        out.flush();
//        }
//        })
//        .permitAll()//和表单登录相关的接口统统都直接通过
//        .and()
//        .logout()
//        .logoutUrl("/logout")
//        .logoutSuccessHandler(new LogoutSuccessHandler() {
//@Override
//public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
//        resp.setContentType("application/json;charset=utf-8");
//        PrintWriter out = resp.getWriter();
//        out.write("logout success");
//        out.flush();
//        }
//        })
//        .permitAll()
//        .and()
//        .httpBasic()
//        .and()
//        .csrf().disable();