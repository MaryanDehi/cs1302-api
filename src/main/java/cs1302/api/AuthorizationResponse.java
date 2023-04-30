package cs1302.api;

import com.google.gson.annotations.*;

/** Represents response from authorization endpoint.*/
public class AuthorizationResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private float expiresIn;


    // Getter Methods

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public float getExpiresIn() {
        return expiresIn;
    }

    // Setter Methods

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresIn(float expiresIn) {
        this.expiresIn = expiresIn;
    }
} // AuthorizationResponse
