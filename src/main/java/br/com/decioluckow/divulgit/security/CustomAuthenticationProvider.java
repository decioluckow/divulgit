package br.com.decioluckow.divulgit.security;

import java.util.Optional;

import br.com.decioluckow.divulgit.model.Origin;
import br.com.decioluckow.divulgit.model.User;
import br.com.decioluckow.divulgit.repository.OriginRepository;
import br.com.decioluckow.divulgit.repository.UserRepository;
import br.com.decioluckow.divulgit.restcaller.exception.CallerException;
import br.com.decioluckow.divulgit.restcaller.user.GitLabUser;
import br.com.decioluckow.divulgit.restcaller.user.CurrentUserCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private CurrentUserCaller currentUserCaller;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OriginRepository originRepos;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final String originURL = authentication.getPrincipal().toString();
		final Origin origin = verifyOrigin(originURL);
		final String originToken = authentication.getCredentials().toString();
		Authentication authenticatedUser;
		try {
			final Optional<GitLabUser> gitUser = currentUserCaller.retrieveCurrentUser(originToken);
			if (gitUser.isPresent()) {
				authenticatedUser = findOrCreateUser(origin, originToken, gitUser);
			} else {
				throw new InsufficientAuthenticationException("Não foi possível realizar a autenticação");
			}
		} catch (CallerException e) {
			throw new BadCredentialsException(e.getMessage(), e);
		}
		return authenticatedUser;
	}

	private UserAuthentication findOrCreateUser(Origin origin, String originToken, Optional<GitLabUser> gitUser) {
		Optional<User> user = userRepository.findByExternalUserIdAndOriginId(gitUser.get().getInternalId(), origin.getId());
		boolean firstLogin = false;
		if (!user.isPresent()) {
			final User newUser = User.of(gitUser.get(), origin);
			firstLogin = true;
			user = Optional.of(userRepository.save(newUser));
		}
		return UserAuthentication.of(user.get(), originToken, firstLogin);
	}

	private Origin verifyOrigin(String originURL) {
		//TODO cadastrar automaticamente, verificando que tipo de repositorio é por meio de chamada de api
		final Optional<Origin> origin = originRepos.findByUrl(originURL);
		if (!origin.isPresent()) {
			throw new OriginNotFoundException("Origin " + originURL + " not found");
		}
		return origin.get();
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}