package org.divulgit.service;

import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User save(RemoteUser remoteUser, Remote remote) {
        User user = User.builder()
                .externalUserId(remoteUser.getInternalId())
                .name(remoteUser.getName())
                .username(remoteUser.getUsername())
                .avatarURL(remoteUser.getAvatarURL())
                .remoteId(remote.getId()).build();
        return save(user);
    }
}
