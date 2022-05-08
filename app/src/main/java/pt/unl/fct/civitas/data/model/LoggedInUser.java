package pt.unl.fct.civitas.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String username;
    private String tokenId;
    private Long creationData;
    private Long expirationData;

    public LoggedInUser() { }

    public LoggedInUser(String username, String tokenId, Long creationData, Long expirationData) {
        this.username = username;
        this.tokenId = tokenId;
        this.creationData = creationData;
        this.expirationData = expirationData;
    }

    public String getUsername() {
        return username;
    }

    public String getTokenId() { return tokenId; }

    public Long getCreationDate() { return creationData; }

    public Long getExpirationDate() { return expirationData; }
}