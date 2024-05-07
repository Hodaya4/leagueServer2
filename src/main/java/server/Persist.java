package server;
import server.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {

    private static final Logger LOGGER = LoggerFactory.getLogger(Persist.class);
    @PersistenceContext
    private EntityManager entityManager;


    private final SessionFactory sessionFactory;

    private Connection connection;
    @Autowired
    private DataSource dataSource;



    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public Session getQuerySession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    public <T> T loadObject(Class<T> clazz, int oid) {
        return this.getQuerySession().get(clazz, oid);
    }

    public <T> List<T> loadList(Class<T> clazz) {
        return this.sessionFactory.getCurrentSession().createQuery("FROM User").list();
    }

    public void updateUsername(String username, String password, String newUsername) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE users SET username = ? WHERE username = ? AND password = ?");
            preparedStatement.setString(1, newUsername);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User login(String username, String password) {
        return (User) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM User WHERE username = :username AND password = :password")
                .setParameter("username", username)
                .setParameter("password", password)
                .setMaxResults(1)
                .uniqueResult();
    }


}