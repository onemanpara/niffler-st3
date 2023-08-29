package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDataUserDAOJdbc implements UserDataUserDAO {

    private static DataSource userdataDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);

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
            PreparedStatement userdataPs = conn.prepareStatement("UPDATE users SET currency = ?, firstname = ?, " +
                    "surname = ?, photo = ? WHERE id = ?");
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
                            "DELETE FROM friends WHERE user_id = ? OR friend_id = ?");
                    PreparedStatement usersPs = conn.prepareStatement(
                            "DELETE FROM users WHERE id = ?")
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
