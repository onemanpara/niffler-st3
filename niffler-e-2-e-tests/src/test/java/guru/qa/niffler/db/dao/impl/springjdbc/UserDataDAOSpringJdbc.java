package guru.qa.niffler.db.dao.impl.springjdbc;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.UserDataDAO;
import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.userdata.UserDataEntity;
import guru.qa.niffler.db.springjdbc.UserDataEntityRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.util.UUID;

public class UserDataDAOSpringJdbc implements UserDataDAO {

    private final TransactionTemplate userdataTtpl;
    private final JdbcTemplate userdataJdbcTemplate;

    public UserDataDAOSpringJdbc() {
        JdbcTransactionManager userdataTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA));

        this.userdataTtpl = new TransactionTemplate(userdataTm);
        this.userdataJdbcTemplate = new JdbcTemplate(userdataTm.getDataSource());
    }

    @Override
    public UserDataEntity createUserInUserData(UserDataEntity user) {
        KeyHolder kh = new GeneratedKeyHolder();
        userdataJdbcTemplate.update(connection -> {
            PreparedStatement usersPs = connection.prepareStatement(
                    "INSERT INTO users (username, currency) " +
                            "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            usersPs.setString(1, user.getUsername());
            usersPs.setString(2, user.getCurrency().name());
            return usersPs;
        }, kh);
        final UUID userId = (UUID) kh.getKeyList().get(0).get("id");
        UserDataEntity userData = new UserDataEntity();
        userData.setId(userId);
        userData.setUsername(user.getUsername());
        userData.setCurrency(CurrencyValues.RUB);
        return userData;
    }

    @Override
    public UserDataEntity getUserFromUserDataByUsername(String username) {
        UserDataEntity user;
        try {
            user = userdataJdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE username = ?",
                    UserDataEntityRowMapper.instance,
                    username
            );
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(("User in UserAuth with username " + username + " not found"));
        }
    }

    @Override
    public void updateUserInUserData(UserDataEntity user) {
        userdataJdbcTemplate.update("UPDATE users SET " +
                        "currency = ?, " +
                        "firstname = ?, " +
                        "surname = ?, " +
                        "photo = ? " +
                        "WHERE id = ?",
                user.getCurrency().name(),
                user.getFirstname(),
                user.getSurname(),
                user.getPhoto(),
                user.getId());
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        UUID userId = getUserFromUserDataByUsername(username).getId();
        userdataTtpl.executeWithoutResult(status -> {
            userdataJdbcTemplate.update("DELETE FROM friends WHERE user_id = ? OR friend_id = ?", userId, userId);
            userdataJdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
        });
    }
}
