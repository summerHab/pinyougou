package com.pinyougou.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;


/**
 *认证类
 * @author ASUS
 *
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	private SellerService sellerService;
	
	public SellerService getSellerService() {
		return sellerService;
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
		
		grantedAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		//获取一个seller对象
		TbSeller seller = sellerService.findOne(username);
		
		if(seller!=null){
			if(seller.getStatus().equals("1")){
				return new User(username, seller.getPassword(),grantedAuths);
		}else{
				return null;
			}   
		}else{
			return null;
		}
		
	}

}
