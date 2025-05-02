package com.chiggy.resumeviewer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthService {

    @Value("${UPLOAD_AUTH_USERNAME}")
    private String authUsername;

    @Value("${UPLOAD_AUTH_PASSWORD}")
    private String authPassword;

    public boolean isNotAuthorized(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return true;
        }
        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] values = credentials.split(":", 2);
        return values.length != 2 || !values[0].equals(authUsername) || !values[1].equals(authPassword);
    }
}