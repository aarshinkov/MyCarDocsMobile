package bg.forcar.mobile.responses;

import bg.forcar.mobile.responses.users.User;

import java.io.Serializable;
import java.util.List;

public class AuthenticationResponse implements Serializable {

    private String tokenType;
    private String accessToken;
    private List<String> access;
    private User user;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<String> getAccess() {
        return access;
    }

    public void setAccess(List<String> access) {
        this.access = access;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
