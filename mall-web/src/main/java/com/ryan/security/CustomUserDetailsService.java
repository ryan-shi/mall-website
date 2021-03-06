package com.ryan.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ryan.dto.UserDTO;
import com.ryan.service.UserService;

@Component
public class CustomUserDetailsService implements UserDetailsService {
	
	private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
    	log.info("username：{} 验证开始",username);
        UserDTO userDTO = userService.findByUsername(username,1); //1前台
        log.info("userDTO: {}",userDTO);
        SecurityUser user=new SecurityUser();
        if(userDTO==null){
        	user=null;
        }else{
        	BeanUtils.copyProperties(userDTO, user);
        }
        log.info("user find by username: {}",user);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在！");
        } else if (!user.isEnabled()) {
            throw new DisabledException("用户被禁用！");
        } else if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("用户已过期！");
        } else if (!user.isAccountNonLocked()) {
            throw new LockedException("用户已被锁定！");
        } else if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("凭证过期！");
        }
        log.info("user权限：{}",user.getAuthorities());
        return user;
    }
}
