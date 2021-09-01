package br.com.decioluckow.divulgit.security;

import br.com.decioluckow.divulgit.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetails {
    private User user;
    private String originToken;
    private boolean firstLogin;
}