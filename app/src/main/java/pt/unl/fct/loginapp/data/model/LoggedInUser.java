package pt.unl.fct.loginapp.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String tokenId;
    private Long creationDate;
    private Long expirationDate;

    public LoggedInUser(String userId, String tokenId, Long creationDate, Long expirationDate) {
        this.userId = userId;
        this.tokenId = tokenId;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getTokenId() { return tokenId; }

    public Long getCreationDate() { return creationDate; }

    public Long getExpirationDate() { return expirationDate; }
}