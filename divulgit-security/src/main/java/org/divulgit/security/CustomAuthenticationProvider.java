package org.divulgit.security;

import java.util.Optional;

import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.RemoteCallerFacadeFactory;
import org.divulgit.remote.RemoteFacade;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.service.UserService;
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
	private RemoteRepository remoteRepository;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final String remoteURL = authentication.getPrincipal().toString();
		log.info("Autenticating user on {}", remoteURL);
		final Remote remote = loadRemote(remoteURL);
		final String remoteToken = authentication.getCredentials().toString();
		Authentication authenticatedUser;
		try {
			final Optional<RemoteUser> remoteUser = retrieveRemoteUser(remote, remoteToken);
			if (remoteUser.isPresent()) {
				authenticatedUser = findOrCreateUser(remote, remoteToken, remoteUser.get());
			} else {
				throw new InsufficientAuthenticationException("Não foi possível realizar a autenticação");
			}
		} catch (RemoteException e) {
			throw new InsufficientAuthenticationException("Não foi possível realizar a autenticação", e);
		}
		return authenticatedUser;
	}

	private Optional<RemoteUser> retrieveRemoteUser(Remote remote, String remoteToken) throws RemoteException {
		final RemoteFacade caller = callerFactory.build(remote);
		return caller.retrieveRemoteUser(remote, remoteToken);
	}

	private UserAuthentication findOrCreateUser(Remote remote, String remoteToken, RemoteUser remoteUser) {
		Optional<User> user = userRepository.findByExternalUserIdAndRemoteId(remoteUser.getInternalId(), remote.getId());
		if (!user.isPresent()) {
			user = Optional.of(userService.save(remoteUser, remote));
		}
		return UserAuthentication.of(user.get(), remoteToken);
	}

	private Remote loadRemote(String remoteURL) {
		//TODO cadastrar automaticamente, verificando que tipo de repositorio é por meio de chamada de api
		final Optional<Remote> remote = remoteRepository.findByUrl(remoteURL);
		if (!remote.isPresent()) {
			throw new RemoteNotFoundException("Remote " + remoteURL + " not found");
		}
		return remote.get();
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}