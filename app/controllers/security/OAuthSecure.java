package controllers.security;


import play.Logger;
import play.modules.oauthclient.OAuthClient;
import play.mvc.Controller;
import play.mvc.Scope;

public abstract class OAuthSecure extends Controller implements ISecure {

    protected String consumerKey;
	protected String consumerSecret;
    protected String callback;

    private String requestURL;
	private String accessURL;
	private String authorizeURL;

    protected OAuthClient connector = null;


    public OAuthSecure(String requestURL, String accessURL, String authorizeURL){
        this.requestURL = requestURL;
		this.accessURL = accessURL;
		this.authorizeURL = authorizeURL;
        init();
    }

    public abstract void init();

    protected OAuthClient getConnector() {
		if (connector == null) {
			connector = new OAuthClient(
					requestURL,
					accessURL,
					authorizeURL,
					consumerKey,
					consumerSecret);
		}
		return connector;
	}

    @Override
    public void login() {
        try {
            authenticate(callback);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

    }

    @Override
    public boolean check(String profile) {
        return false;
    }

    @Override
    public void logout() {
        session().put(SESSION_EMAIL_KEY, null);
        session().put(SESSION_IMPL_KEY, null);
        Secure.authetification();
    }

    abstract void authenticate(String callback) throws Exception;
    abstract Scope.Session session();
}
