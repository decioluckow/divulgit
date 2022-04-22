package org.divulgit.security;

import java.util.Collection;

import org.divulgit.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import lombok.Builder;

@Builder
public class UserAuthentication implements Authentication {

	private static final long serialVersionUID = 2860593790255593860L;
	
	private String name;
    private String principal;
    private UserDetails details;
    private boolean authenticated;

    public static UserAuthentication of(final User user, final String remoteToken) {
        return UserAuthentication.builder()
                .name(user.getName())
                .principal(user.getUsername())
                .details(UserDetails.builder().user(user).remoteToken(remoteToken).build())
                .authenticated(true).build();
    }

    public UserDetails getUserDetails() {
        return details;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        this.authenticated = authenticated;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getRemoteToken() {
        return details.getRemoteToken();
    }

}
