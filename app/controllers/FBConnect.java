package controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import play.Play;
import play.exceptions.UnexpectedException;
import play.libs.WS;
import play.libs.ws.WSUrlFetch;
import play.modules.fbconnect.FBConnectPlugin;
import play.modules.fbconnect.FBConnectSession;
import play.mvc.Controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class FBConnect extends Controller {

    public static void callback() {
        FBConnectPlugin fbplugin = Play.plugin(FBConnectPlugin.class);
        FBConnectSession fbsession = fbplugin.session();
        String code = params.get("code");
        String error = params.get("error");
        if (error != null) {
            String landUrl = fbsession.getLandUrl();
            landUrl += landUrl.contains("?") ? "&" : "?";
            landUrl += "error_reason=" + WS.encode(params.get("error_reason")) +
                    "&error_description=" + WS.encode(params.get("error_description")) +
                    "&error=" + WS.encode(params.get("error"));
            redirect(landUrl);
        }
        if (code != null) {
            String authUrl = fbsession.getAuthUrl(code);
            WSUrlFetch ws = new WSUrlFetch();
            String response = ws.newRequest(authUrl, "UTF-8").get().getString();
            String accessToken = null;
            Integer expires = null;
            String[] pairs = response.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length != 2) {
                    throw new UnexpectedException("Module fbconnect got an unexpected auth response from facebook");
                } else {
                    if (kv[0].equals("access_token")) {
                        accessToken = kv[1];
                    }
                    if (kv[0].equals("expires")) {
                        expires = Integer.valueOf(kv[1]);
                    }
                }
            }
            if (accessToken != null) {
                try {
                    Class model = Class.forName(fbsession.getModel());
                    Method method = model.getMethod("facebookOAuthCallback", new Class[]{JsonObject.class});
                    if (Modifier.isStatic(method.getModifiers())) {
                        String uri = "https://graph.facebook.com/me?access_token=" + WS.encode(accessToken);
                        JsonObject jsonData = ws.newRequest(uri, "UTF-8").get().getJson().getAsJsonObject();
                        jsonData.add("accessToken", new JsonPrimitive(accessToken));
                        if (expires != null) {
                            jsonData.add("expires", new JsonPrimitive(expires));
                        }
                        method.invoke(null, jsonData);
                    } else {
                        throw new UnexpectedException("Module fbconnect expects your facebookOAuthCallback method to be static");
                    }
                } catch (ClassNotFoundException e) {
                    throw new UnexpectedException("Module fbconnect cannot find your model class " + fbsession.getModel());
                } catch (NoSuchMethodException e) {
                    throw new UnexpectedException("Module fbconnect requires that your model class " + fbsession.getModel() + " must provide a method with this signature: [public static void facebookOAuthCallback(JsonObject data)]");
                } catch (IllegalAccessException e) {
                    throw new UnexpectedException("Module fbconnect does not have access to call your model's findForFacebookOAuth");
                } catch (InvocationTargetException e) {
                    throw new UnexpectedException("Module fbconnect encountered an error while calling your model's findForFacebookOAuth: " + e.getMessage());
                }
            } else {
                throw new UnexpectedException("Module fbconnect could not find access token in facebook callback");
            }
        }
        redirect(fbsession.getLandUrl());
    }
}