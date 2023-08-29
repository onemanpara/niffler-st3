package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class AuthUserDAOJdbc implements AuthUserDAO {

    private static DataSource authDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH);

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        UUID generatedUserId = null;
        try (Connection conn = authDs.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

                 PreparedStatement authorityPs = conn.prepareStatement(
                         "INSERT INTO authorities (user_id, authority) " +
                                 "VALUES (?, ?)")) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, pe.encode(user.getPassword()));
                usersPs.setBoolean(3, user.getEnabled());
                usersPs.setBoolean(4, user.getAccountNonExpired());
                usersPs.setBoolean(5, user.getAccountNonLocked());
                usersPs.setBoolean(6, user.getCredentialsNonExpired());

                usersPs.executeUpdate();

                try (ResultSet generatedKeys = usersPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedUserId = UUID.fromString(generatedKeys.getString("id"));
                        user.setId(generatedUserId);
                    } else {
                        throw new IllegalStateException("Can`t obtain id from given ResultSet");
                    }
                }

                for (Authority authority : Authority.values()) {
                    authorityPs.setObject(1, generatedUserId);
                    authorityPs.setString(2, authority.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }

                authorityPs.executeBatch();
                user.setId(generatedUserId);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public AuthUserEntity getUserFromAuthUserById(UUID userId) {
        AuthUserEntity user = new AuthUserEntity();
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement authPs = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
                 PreparedStatement authorityPs = conn.prepareStatement("SELECT * FROM authorities WHERE user_id = ?")) {
                authPs.setObject(1, userId);
                authorityPs.setObject(1, userId);
                ResultSet authResultSet = authPs.executeQuery();
                ResultSet authotiryResultSet = authorityPs.executeQuery();

                if (authResultSet.next()) {
                    user.setId((UUID) authResultSet.getObject("id"));
                    user.setUsername(authResultSet.getString("username"));
                    user.setPassword(authResultSet.getString("password"));
                    user.setEnabled(authResultSet.getBoolean("enabled"));
                    user.setAccountNonExpired(authResultSet.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(authResultSet.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(authResultSet.getBoolean("credentials_non_expired"));

                    var authorities = new ArrayList<AuthorityEntity>();
                    while (authotiryResultSet.next()) {
                        var a = new AuthorityEntity();
                        a.setAuthority(Authority.valueOf(authotiryResultSet.getString("authority")));
                        authorities.add(a);
                    }
                    user.setAuthorities(authorities);
                    return user;
                } else {
                    throw new IllegalArgumentException("User in UserAuth with id " + userId + " not found");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUser(AuthUserEntity user) {
        try (Connection conn = authDs.getConnection()) {
            PreparedStatement usersPs = conn.prepareStatement("UPDATE users SET password = ?, enabled = ?, account_non_expired = ?, " +
                    "account_non_locked = ?, credentials_non_expired = ? WHERE id = ?");
            usersPs.setObject(1, user.getPassword());
            usersPs.setBoolean(2, user.getEnabled());
            usersPs.setBoolean(3, user.getAccountNonExpired());
            usersPs.setBoolean(4, user.getAccountNonLocked());
            usersPs.setBoolean(5, user.getCredentialsNonExpired());
            usersPs.setObject(6, user.getId());

            int updatedRows = usersPs.executeUpdate();
            if (updatedRows == 0) {
                throw new IllegalArgumentException("User with id " + user.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUserByIdInAuth(UUID userId) {
        try (Connection conn = authDs.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement authorityPs = conn.prepareStatement(
                    "DELETE FROM authorities WHERE user_id = ?");
                 PreparedStatement usersPs = conn.prepareStatement(
                         "DELETE FROM users WHERE id = ?")
            ) {

                usersPs.setObject(1, userId);
                authorityPs.setObject(1, userId);
                authorityPs.executeUpdate();
                usersPs.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
