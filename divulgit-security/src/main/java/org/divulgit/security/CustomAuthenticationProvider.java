package org.divulgit.security;

import java.util.Optional;

import org.divulgit.service.UserService;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.remote.remote.model.RemoteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.divulgit.remote.RemoteCallerFacade;
import org.divulgit.remote.RemoteCallerFacadeFactory;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
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
		final Remote remote = verifyRemote(remoteURL);
		final String remoteToken = authentication.getCredentials().toString();
		Authentication authenticatedUser;
		final RemoteCallerFacade caller = callerFactory.build(remote);
		final Optional<RemoteUser> remoteUser = caller.retrieveRemoteUser(remoteToken);
		if (remoteUser.isPresent()) {
			authenticatedUser = findOrCreateUser(remote, remoteToken, remoteUser.get());
		} else {
			throw new InsufficientAuthenticationException("Não foi possível realizar a autenticação");
		}
		return authenticatedUser;
	}

	private UserAuthentication findOrCreateUser(Remote remote, String remoteToken, RemoteUser remoteUser) {
		Optional<User> user = userRepository.findByExternalUserIdAndRemoteId(remoteUser.getInternalId(), remote.getId());
		if (!user.isPresent()) {
			user = Optional.of(userService.save(remoteUser, remote));
		}
		return UserAuthentication.of(user.get(), remoteToken);
	}

	private Remote verifyRemote(String remoteURL) {
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