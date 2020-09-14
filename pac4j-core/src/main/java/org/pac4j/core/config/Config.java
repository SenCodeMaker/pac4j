package org.pac4j.core.config;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.WebContextFactory;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.profile.factory.ProfileManagerFactory2;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The default configuration with clients, authorizers, matchers, etc.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class Config {

    public static final Config INSTANCE = new Config();

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private ProfileManagerFactory profileManagerFactory;

    @Deprecated
    private ProfileManagerFactory2 profileManagerFactory2;

    protected Clients clients;

    protected Map<String, Authorizer> authorizers = new HashMap<>();

    protected Map<String, Matcher> matchers = new HashMap<>();

    protected SessionStore sessionStore;

    protected HttpActionAdapter httpActionAdapter;

    protected SecurityLogic securityLogic;

    protected CallbackLogic callbackLogic;

    protected LogoutLogic logoutLogic;

    protected WebContextFactory webContextFactory;

    public Config() {}

    public Config(final Client client) {
        this.clients = new Clients(client);
    }

    public Config(final Clients clients) {
        this.clients = clients;
    }

    public Config(final List<Client> clients) {
        this.clients = new Clients(clients);
    }

    public Config(final Client... clients) {
        this.clients = new Clients(clients);
    }

    public Config(final String callbackUrl, final Client client) {
        this.clients = new Clients(callbackUrl, client);
    }

    public Config(final String callbackUrl, final Client... clients) {
        this.clients = new Clients(callbackUrl, clients);
    }

    public Config(final String callbackUrl, final List<Client> clients) {
        this.clients = new Clients(callbackUrl, clients);
    }

    public Config(final Map<String, Authorizer> authorizers) {
        setAuthorizers(authorizers);
    }

    public Config(final Clients clients, final Map<String, Authorizer> authorizers) {
        this.clients = clients;
        setAuthorizers(authorizers);
    }

    public Config(final Client client, final Map<String, Authorizer> authorizers) {
        this.clients = new Clients(client);
        setAuthorizers(authorizers);
    }

    public Config(final Map<String, Authorizer> authorizers, final Client... clients) {
        this.clients = new Clients(clients);
        setAuthorizers(authorizers);
    }

    public Config(final String callbackUrl, final Map<String, Authorizer> authorizers, final Client... clients) {
        this.clients = new Clients(callbackUrl, clients);
        setAuthorizers(authorizers);
    }

    public Config(final String callbackUrl, final Client client, final Map<String, Authorizer> authorizers) {
        this.clients = new Clients(callbackUrl, client);
        setAuthorizers(authorizers);
    }

    public Clients getClients() {
        return clients;
    }

    public void setClients(final Clients clients) {
        this.clients = clients;
    }

    public Map<String, Authorizer> getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizer(final Authorizer authorizer) {
        CommonHelper.assertNotNull("authorizer", authorizer);
        this.authorizers.put(authorizer.getClass().getSimpleName(), authorizer);
    }

    public void setAuthorizers(final Map<String, Authorizer> authorizers) {
        CommonHelper.assertNotNull("authorizers", authorizers);
        this.authorizers = authorizers;
    }

    public void addAuthorizer(final String name, final Authorizer authorizer) {
        authorizers.put(name, authorizer);
    }

    public Map<String, Matcher> getMatchers() {
        return matchers;
    }

    public void setMatcher(final Matcher matcher) {
        CommonHelper.assertNotNull("matcher", matcher);
        this.matchers.put(matcher.getClass().getSimpleName(), matcher);
    }

    public void setMatchers(final Map<String, Matcher> matchers) {
        CommonHelper.assertNotNull("matchers", matchers);
        this.matchers = matchers;
    }

    public void addMatcher(final String name, final Matcher matcher) {
        matchers.put(name, matcher);
    }

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    public void setSessionStore(final SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    public HttpActionAdapter getHttpActionAdapter() {
        return httpActionAdapter;
    }

    public void setHttpActionAdapter(final HttpActionAdapter httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
    }

    public SecurityLogic getSecurityLogic() {
        return securityLogic;
    }

    public void setSecurityLogic(final SecurityLogic securityLogic) {
        this.securityLogic = securityLogic;
    }

    public CallbackLogic getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(final CallbackLogic callbackLogic) {
        this.callbackLogic = callbackLogic;
    }

    public LogoutLogic getLogoutLogic() {
        return logoutLogic;
    }

    public void setLogoutLogic(final LogoutLogic logoutLogic) {
        this.logoutLogic = logoutLogic;
    }

    public WebContextFactory getWebContextFactory() {
        return webContextFactory;
    }

    public void setWebContextFactory(final WebContextFactory webContextFactory) {
        this.webContextFactory = webContextFactory;
    }

    public static ProfileManagerFactory getProfileManagerFactory() {
        return INSTANCE.profileManagerFactory;
    }

    public static void setProfileManagerFactory(final String name, final ProfileManagerFactory profileManagerFactory) {
        CommonHelper.assertNotNull("profileManagerFactory", profileManagerFactory);
        LOGGER.info("Setting Config.profileManagerFactory: {}", name);
        INSTANCE.profileManagerFactory = profileManagerFactory;
    }

    public static void defaultProfileManagerFactory(final String name, final ProfileManagerFactory profileManagerFactory) {
        if (INSTANCE.profileManagerFactory == null) {
            synchronized (INSTANCE) {
                if (INSTANCE.profileManagerFactory == null) {
                    setProfileManagerFactory(name, profileManagerFactory);
                }
            }
        }
    }

    @Deprecated
    public static ProfileManagerFactory2 getProfileManagerFactory2() {
        return INSTANCE.profileManagerFactory2;
    }

    @Deprecated
    public static void setProfileManagerFactory2(final String name, final ProfileManagerFactory2 profileManagerFactory2) {
        CommonHelper.assertNotNull("profileManagerFactory2", profileManagerFactory2);
        LOGGER.info("Setting Config.profileManagerFactory2: {}", name);
        INSTANCE.profileManagerFactory2 = profileManagerFactory2;
    }

    @Deprecated
    public static void defaultProfileManagerFactory2(final String name, final ProfileManagerFactory2 profileManagerFactory2) {
        if (INSTANCE.profileManagerFactory2 == null) {
            synchronized (INSTANCE) {
                if (INSTANCE.profileManagerFactory2 == null) {
                    setProfileManagerFactory2(name, profileManagerFactory2);
                }
            }
        }
    }

    public static void setConfig(final Config config) {
        INSTANCE.setClients(config.getClients());
        INSTANCE.setAuthorizers(config.getAuthorizers());
        INSTANCE.setMatchers(config.getMatchers());
        INSTANCE.setSessionStore(config.getSessionStore());
        INSTANCE.setHttpActionAdapter(config.getHttpActionAdapter());
        INSTANCE.setSecurityLogic(config.getSecurityLogic());
        INSTANCE.setCallbackLogic(config.getCallbackLogic());
        INSTANCE.setLogoutLogic(config.getLogoutLogic());
        INSTANCE.setWebContextFactory(config.getWebContextFactory());
    }
}
