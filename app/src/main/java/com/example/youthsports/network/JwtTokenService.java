package com.example.youthsports.network;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.example.youthsports.authentication.signin;
import org.json.JSONObject;
import com.example.youthsports.util.UserDetailsInSharedPreferences;

public class JwtTokenService {
    private static final String JWT_TOKEN_KEY = "jwtToken";
    private final Context context;

    public JwtTokenService(Context context) {
        this.context = context;
        UserDetailsInSharedPreferences.initialize(context);
    }

    public void storeToken(String token) {
        Log.i("JwtTokenService", "Storing token: " + token);
        UserDetailsInSharedPreferences.storeValue(JWT_TOKEN_KEY, token);
    }

    public void removeToken() {
        UserDetailsInSharedPreferences.clearValue(JWT_TOKEN_KEY);
        redirectToSignIn();
    }

    public String getToken() {
        String token = UserDetailsInSharedPreferences.getValue(JWT_TOKEN_KEY);
        if (isValidToken(token)) {
            return token;
        } else {
            removeToken();
            return null;
        }
    }

    public boolean hasValidToken() {
        String token = UserDetailsInSharedPreferences.getValue(JWT_TOKEN_KEY);
        return isValidToken(token);
    }

    public boolean isValidToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            String[] splitToken = token.split("\\.");
            String base64EncodedBody = splitToken[1];
            String body = new String(Base64.decode(base64EncodedBody, Base64.URL_SAFE));
            JSONObject jwtBody = new JSONObject(body);

            long expiration = jwtBody.getLong("exp");
            long currentTimeInSeconds = System.currentTimeMillis() / 1000;

            boolean isValid = expiration > currentTimeInSeconds;
            if (!isValid) {
                redirectToSignIn();  // Redirect to sign-in if token is not valid
            }
            return isValid;
        } catch (Exception e) {
            Log.e("JwtTokenService", "Token validation error", e);
            redirectToSignIn();
            return false;
        }
    }

    public Object getClaimFromToken(String claim) {
        String token = getToken(); // Use getToken to ensure validity check
        if (token == null) {
            return null;
        }

        try {
            String[] splitToken = token.split("\\.");
            String base64EncodedBody = splitToken[1];
            String body = new String(Base64.decode(base64EncodedBody, Base64.URL_SAFE));
            JSONObject jwtBody = new JSONObject(body);

            return jwtBody.opt(claim);
        } catch (Exception e) {
            Log.e("JwtTokenService", "Error getting claim from token", e);
            return null;
        }
    }

    private void redirectToSignIn() {
        Intent intent = new Intent(context, signin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
