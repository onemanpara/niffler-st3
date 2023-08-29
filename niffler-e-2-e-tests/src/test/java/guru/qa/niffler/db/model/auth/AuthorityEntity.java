package guru.qa.niffler.db.model.auth;

import java.util.UUID;

public class AuthorityEntity {

    private UUID id;
    private Authority authority;
    private AuthUserEntity user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public AuthUserEntity getUser() {
        return user;
    }

    public void setUser(AuthUserEntity user) {
        this.user = user;
    }
}
