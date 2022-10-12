package org.divulgit.security;

import org.divulgit.model.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetails {
    private User user;
    private String organization;
}