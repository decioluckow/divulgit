package org.divulgit.security;

import java.util.Optional;

import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.RemoteCallerFacadeFactory;
import org.divulgit.remote.RemoteFacade;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.repository.UserRepository;
import org.divulgit.util.vo.RemoteIdentify;
import org.divulgit.security.identify.RemoteIdentifyParser;
import org.divulgit.service.remote.RemoteDiscoveryService;
import org.divulgit.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private RemoteCallerFacadeFactory callerFactory;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RemoteDiscoveryService remoteDiscoveryService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final String principal = authentication.getPrincipal().toString();
		RemoteIdentify remoteIdentify = RemoteIdentifyParser.parsePrincipal(principal);
		log.info("Autenticating {}", remoteIdentify.toString());
		RemoteAuthentication remoteAuthentication;
		try {
			final String credential = authentication.getCredentials().toString();
			remoteAuthentication = RemoteAuthentication.of(remoteIdentify.getUsername(), credential);
			final Remote remote = remoteDiscoveryService.findRemote(remoteIdentify, remoteAuthentication);
			final Optional<RemoteUser> remoteUser = retrieveRemoteUser(remote, remoteAuthentication);
			if (remoteUser.isPresent()) {
				remoteAuthentication = findOrCreateUser(remoteIdentify, remote, remoteAuthentication, remoteUser.get());
			} else {
				throw new InsufficientAuthenticationException("Unable to authenticate, please check your login information");
			}
		} catch (RemoteException e) {
			log.error(e.getMessage(), e);
			throw new InsufficientAuthenticationException("Unable to authenticate (" + e.getMessage() + ")", e);
		}
		return remoteAuthentication;
	}

	private Optional<RemoteUser> retrieveRemoteUser(Remote remote, RemoteAuthentication authentication) throws RemoteException {
		final RemoteFacade caller = callerFactory.build(remote);
		return caller.retrieveRemoteUser(remote, authentication);
	}

	private RemoteAuthentication findOrCreateUser(RemoteIdentify remoteIdentify, Remote remote, Authentication authenticated, RemoteUser remoteUser) {
		Optional<User> user = userRepository.findByExternalUserIdAndRemoteId(remoteUser.getInternalId(), remote.getId());
		if (!user.isPresent()) {
			user = Optional.of(userService.save(remoteUser, remote));
		}
		return RemoteAuthentication.of(remoteIdentify, user.get(), (String) authenticated.getCredentials());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
