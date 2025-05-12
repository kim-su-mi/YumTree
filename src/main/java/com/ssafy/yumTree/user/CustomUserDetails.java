package com.ssafy.yumTree.user;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails{
	private final UserDto userDto;
	
	public CustomUserDetails(UserDto userDto) {
		this.userDto = userDto;
	}

	/**
	 * Role값 반환 
	 */
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userDto.getUserRole();
            }
        });

        return collection;
    }

	/**
	 * PW값 반환 
	 */
    @Override
    public String getPassword() {

        return userDto.getUserPw();
    }

    /**
     *Id값 반환 
     */
    @Override
    public String getUsername() {

        return userDto.getUserId();
    }

    /**
     * 계정이 만료 되었는지 
     */
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
