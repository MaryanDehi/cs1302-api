package cs1302.api;

import com.google.gson.annotations.*;

/** Represents response from Spotify API authorization endpoint.*/
public class AuthorizationResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private float expiresIn;


    // Getter Methods

    /**
     * Returns the accessToken.
     * @return String accessToken.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Returns Token Type.
     * @return String tokenType
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Returns expiration time.
     * @return float time limit.
     */
    public float getExpiresIn() {
        return expiresIn;
    }

    // Setter Methods

    /**
     * Sets access token.
     * @param accessToken sets access token to this.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Sets the token type.
     * @param tokenType sets token type to this.
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Sets the exiration time limit.
     * @param expiresIn sets expiration window.
     */
    public void setExpiresIn(float expiresIn) {
        this.expiresIn = expiresIn;
    }
} // AuthorizationResponse
