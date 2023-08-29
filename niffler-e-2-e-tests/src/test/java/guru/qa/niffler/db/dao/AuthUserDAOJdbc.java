package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class AuthUserDAOJdbc implements AuthUserDAO, UserDataUserDAO {

    private static DataSource authDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH);
    private static DataSource userdataDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);

    @Override
    public UUID createUser(UserEntity user) {
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
        return generatedUserId;
    }

    @Override
    public UserEntity getUserFromAuthUserById(UUID userId) {
        UserEntity user = new UserEntity();
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
    public void updateUser(UserEntity user) {
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

    @Override
    public UserDataEntity createUserInUserData(UserEntity user) {
        try (Connection conn = userdataDs.getConnection()) {
            PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, currency) " +
                            "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            usersPs.setString(1, user.getUsername());
            usersPs.setString(2, CurrencyValues.RUB.name());
            usersPs.executeUpdate();

            try (ResultSet generatedKeys = usersPs.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    UserDataEntity userData = new UserDataEntity();
                    userData.setId(UUID.fromString(generatedKeys.getString("id")));
                    userData.setUsername(user.getUsername());
                    userData.setCurrency(CurrencyValues.RUB);
                    return userData;
                } else {
                    throw new IllegalStateException("Can`t obtain id from given ResultSet");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDataEntity getUserFromUserDataByUsername(String username) {
        try (Connection conn = userdataDs.getConnection();
             PreparedStatement userdataPs = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            userdataPs.setObject(1, username);
            ResultSet resultSet = userdataPs.executeQuery();

            if (resultSet.next()) {
                UserDataEntity user = new UserDataEntity();
                user.setId(UUID.fromString(resultSet.getString("id")));
                user.setUsername(resultSet.getString("username"));
                user.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
                user.setFirstname(resultSet.getString("firstname"));
                user.setSurname(resultSet.getString("surname"));
                user.setPhoto(resultSet.getBytes("photo"));
                return user;
            } else {
                throw new IllegalArgumentException("User in UserData with id " + username + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUserInUserData(UserDataEntity user) {
        try (Connection conn = userdataDs.getConnection()) {
            PreparedStatement userdataPs = conn.prepareStatement("UPDATE users SET currency=?, firstname=?, surname=?, photo=? WHERE id=?");
            userdataPs.setString(1, user.getCurrency().name());
            userdataPs.setString(2, user.getFirstname());
            userdataPs.setString(3, user.getSurname());
            userdataPs.setObject(4, user.getPhoto());
            userdataPs.setObject(5, user.getId());
            int updatedRows = userdataPs.executeUpdate();
            if (updatedRows == 0) {
                throw new IllegalArgumentException("User in UserData with id " + user.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        UUID userDataId = getUserFromUserDataByUsername(username).getId();
        try (Connection conn = userdataDs.getConnection()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement friendsPs = conn.prepareStatement(
                            "DELETE FROM friends WHERE user_id=? OR friend_id=?");
                    PreparedStatement usersPs = conn.prepareStatement(
                            "DELETE FROM users WHERE id=?")
            ) {
                friendsPs.setObject(1, userDataId);
                friendsPs.setObject(2, userDataId);
                friendsPs.executeUpdate();
                usersPs.setObject(1, userDataId);
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
