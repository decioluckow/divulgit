package org.divulgit.security;

import java.util.Collection;

import org.divulgit.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import lombok.Builder;

@Builder
public class RemoteAuthentication implements Authentication {

	private static final long serialVersionUID = 2860593790255593860L;
	
	private String name;
    private String principal;
    private String credential;
    private UserDetails details;
    private boolean authenticated;

    public static RemoteAuthentication of(final String username, final String credential) {
        return RemoteAuthentication.builder()
                .principal(username)
                .credential(credential).build();
    }

    public static RemoteAuthentication of(final User user, final String credential) {
        return RemoteAuthentication.builder()
                .name(user.getName())
                .principal(user.getUsername())
                .credential(credential)
                .details(UserDetails.builder().user(user).build())
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
        return credential;
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
}
