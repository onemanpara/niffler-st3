package guru.qa.niffler.db.dao.impl.springjdbc;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.springjdbc.AuthorityEntityRowMapper;
import guru.qa.niffler.db.springjdbc.UserEntityRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO {

    private final TransactionTemplate authTtpl;
    private final JdbcTemplate authJdbcTemplate;

    public AuthUserDAOSpringJdbc() {
        JdbcTransactionManager authTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH));

        this.authTtpl = new TransactionTemplate(authTm);
        this.authJdbcTemplate = new JdbcTemplate(authTm.getDataSource());
    }

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        return authTtpl.execute(status -> {
            KeyHolder kh = new GeneratedKeyHolder();

            authJdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
            }, kh);
            final UUID userId = (UUID) kh.getKeyList().get(0).get("id");
            user.setId(userId);
            authJdbcTemplate.batchUpdate("INSERT INTO authorities (user_id, authority) VALUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, userId);
                    ps.setObject(2, Authority.values()[i].name());
                }

                @Override
                public int getBatchSize() {
                    return Authority.values().length;
                }
            });
            return user;
        });
    }

    @Override
    public AuthUserEntity getUserFromAuthUserById(UUID userId) {
        AuthUserEntity user;
        try {
            user = authJdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE id = ? ",
                    UserEntityRowMapper.instance,
                    userId
            );
            List<AuthorityEntity> authorities = authJdbcTemplate.query(
                    "SELECT * FROM authorities WHERE user_id = ?", AuthorityEntityRowMapper.instance, userId
            );
            user.setAuthorities(authorities);
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(("User in UserAuth with id " + userId + " not found"));
        }
    }

    @Override
    public void updateUser(AuthUserEntity user) {
        authJdbcTemplate.update("UPDATE users " +
                        "SET password = ?, " +
                        "enabled = ?, " +
                        "account_non_expired = ?, " +
                        "account_non_locked = ?, " +
                        "credentials_non_expired = ? " +
                        "WHERE id = ?",
                pe.encode(user.getPassword()),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                user.getId());
    }

    @Override
    public void deleteUserByIdInAuth(UUID userId) {
        authTtpl.executeWithoutResult(status -> {
            authJdbcTemplate.update("DELETE FROM authorities WHERE user_id = ?", userId);
            authJdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
        });
    }
}
