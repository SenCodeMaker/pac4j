package org.pac4j.core.authorization.checker;

import static org.pac4j.core.util.CommonHelper.areEqualsIgnoreCaseAndTrim;
import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;
import static org.pac4j.core.util.CommonHelper.isBlank;
import static org.pac4j.core.util.CommonHelper.isNotEmpty;
import static org.pac4j.core.util.CommonHelper.substringAfter;
import static org.pac4j.core.util.CommonHelper.substringBetween;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.CsrfAuthorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.IsFullyAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.IsRememberedAuthorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default way to check the authorizations (with default authorizers).
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultAuthorizationChecker implements AuthorizationChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthorizationChecker.class);

    protected static final CsrfAuthorizer CSRF_AUTHORIZER = new CsrfAuthorizer();
    protected static final IsAnonymousAuthorizer IS_ANONYMOUS_AUTHORIZER = new IsAnonymousAuthorizer();
    protected static final IsAuthenticatedAuthorizer IS_AUTHENTICATED_AUTHORIZER =new IsAuthenticatedAuthorizer();
    protected static final IsFullyAuthenticatedAuthorizer IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer();
    protected static final IsRememberedAuthorizer IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer();

    @Override
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles,
                                final String authorizersValue, final Map<String, Authorizer> authorizersMap, final List<Client> clients) {

        final var authorizers = computeAuthorizers(context, profiles, authorizersValue, authorizersMap, clients);
        return isAuthorized(context, sessionStore, profiles, authorizers);
    }

    protected List<Authorizer> computeAuthorizers(final WebContext context, final List<UserProfile> profiles, final String authorizersValue,
                                                  final Map<String, Authorizer> authorizersMap, final List<Client> clients) {
        final List<Authorizer> authorizers;
        if (isBlank(authorizersValue)) {
            authorizers = computeDefaultAuthorizers(context, profiles, clients, authorizersMap);
        } else {
                if (authorizersValue.trim().startsWith(Pac4jConstants.ADD_ELEMENT) || authorizersValue.trim().startsWith(Pac4jConstants.REMOVE_ELEMENT)) {
                	authorizers = computeDefaultAuthorizers(context, profiles, clients, authorizersMap);
                    
                    final var removedMatcherNames = substringAfter(authorizersValue, Pac4jConstants.REMOVE_ELEMENT);
					removeAuthorizersFromNames(removedMatcherNames, authorizers);
                    
                    
                    final var addedMatcherNames = substringBetween(authorizersValue, Pac4jConstants.ADD_ELEMENT, Pac4jConstants.REMOVE_ELEMENT);
					authorizers.addAll(addAuthorizersFromNames(addedMatcherNames, authorizersMap));
            } else {
					authorizers = addAuthorizersFromNames(authorizersValue, authorizersMap);
            }
        }
        return authorizers;
    }

    protected List<Authorizer> computeDefaultAuthorizers(final WebContext context, final List<UserProfile> profiles,
                                                         final List<Client> clients, final Map<String, Authorizer> authorizersMap) {
        final List<Authorizer> authorizers = new ArrayList<>();
        if (containsClientType(clients, IndirectClient.class)) {
            authorizers.add(retrieveAuthorizer(DefaultAuthorizers.CSRF_CHECK, authorizersMap));
        }
        if (!containsClientType(clients, AnonymousClient.class)) {
            authorizers.add(retrieveAuthorizer(DefaultAuthorizers.IS_AUTHENTICATED, authorizersMap));
        }
        return authorizers;
    }

	protected List<Authorizer> addAuthorizersFromNames(final String authorizerNames,
			final Map<String, Authorizer> authorizersMap) {
        assertNotNull("authorizersMap", authorizersMap);
        final List<Authorizer> authorizers = new ArrayList<>();
        final var names = authorizerNames.split(Pac4jConstants.ELEMENT_SEPARATOR);
        final var nb = names.length;
        for (var i = 0; i < nb; i++) {
            final var name = names[i].trim();
            if (!DefaultAuthorizers.NONE.equalsIgnoreCase(name)){
                final var result = retrieveAuthorizer(name, authorizersMap);
                // we must have an authorizer defined for this name
                assertTrue(result != null, "The authorizer '" + name + "' must be defined in the security configuration");
                authorizers.add(result);
            }
        }
        return authorizers;
    }

	protected List<Authorizer> removeAuthorizersFromNames(final String authorizerNames,
			final List<Authorizer> authorizers) {
		final var names = authorizerNames.split(Pac4jConstants.ELEMENT_SEPARATOR);
		final var nb = names.length;
		for (var i = 0; i < nb; i++) {
			final var name = names[i].trim();
			if (!DefaultAuthorizers.NONE.equalsIgnoreCase(name)) {
				final var results = retrieveAuthorizer(name, Collections.emptyMap());
				// we must have an authorizer defined for this name
				assertTrue(results != null,
						"The authorizer '" + name + "' must be defined in the security configuration");

				authorizers.remove(results);
			}
		}
		return authorizers;
	}

    protected Authorizer retrieveAuthorizer(final String authorizerName, final Map<String, Authorizer> authorizersMap) {
        Authorizer authorizer = null;
        for (final var entry : authorizersMap.entrySet()) {
            if (areEqualsIgnoreCaseAndTrim(entry.getKey(), authorizerName)) {
                authorizer = entry.getValue();
                break;
            }
        }
        if (authorizer == null) {
            if (DefaultAuthorizers.CSRF_CHECK.equalsIgnoreCase(authorizerName)) {
                return CSRF_AUTHORIZER;
            } else if (DefaultAuthorizers.IS_ANONYMOUS.equalsIgnoreCase(authorizerName)) {
                return IS_ANONYMOUS_AUTHORIZER;
            } else if (DefaultAuthorizers.IS_AUTHENTICATED.equalsIgnoreCase(authorizerName)) {
                return IS_AUTHENTICATED_AUTHORIZER;
            } else if (DefaultAuthorizers.IS_FULLY_AUTHENTICATED.equalsIgnoreCase(authorizerName)) {
                return IS_FULLY_AUTHENTICATED_AUTHORIZER;
            } else if (DefaultAuthorizers.IS_REMEMBERED.equalsIgnoreCase(authorizerName)) {
                return IS_REMEMBERED_AUTHORIZER;
            }
        }
        return authorizer;
    }

    protected boolean containsClientType(final List<Client> clients, final Class<? extends Client> clazz) {
        for (final var client : clients) {
            if (clazz.isAssignableFrom(client.getClass())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAuthorized(final WebContext context, final SessionStore sessionStore,
                                   final List<UserProfile> profiles, final List<Authorizer> authorizers) {
        // authorizations check comes after authentication and profile must not be null nor empty
        assertTrue(isNotEmpty(profiles), "profiles must not be null or empty");
        if (isNotEmpty(authorizers)) {
            // check authorizations using authorizers: all must be satisfied
            for (var authorizer : authorizers) {
                final var isAuthorized = authorizer.isAuthorized(context, sessionStore, profiles);
                LOGGER.debug("Checking authorizer: {} -> {}", authorizer, isAuthorized);
                if (!isAuthorized) {
                    return false;
                }
            }
        }
        return true;
    }
}
