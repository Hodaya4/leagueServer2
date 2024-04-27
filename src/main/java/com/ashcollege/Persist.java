package com.ashcollege;


import com.ashcollege.entities.User;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

//    public void updateUserBalance(String username, String password, float balance) {
//        try (Connection connection = dataSource.getConnection()) {
//            PreparedStatement preparedStatement = connection.prepareStatement(
//                    "UPDATE users SET balance = ? WHERE username = ? AND password = ?");
//            preparedStatement.setFloat(1, balance);
//            preparedStatement.setString(2, username);
//            preparedStatement.setString(3, password);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

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
//    public float getUserBalance(String username) {
//        float balance = 3;
//        System.out.println("0");
//        try (Connection connection = dataSource.getConnection()) {
//            System.out.println("1");
//            PreparedStatement preparedStatement = connection.prepareStatement(
//                    "SELECT balance FROM users WHERE username = ?");
//            System.out.println("2");
//            preparedStatement.setString(1, username);
//            System.out.println("3");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            System.out.println("4");
//            if (resultSet.next()) {
//                System.out.println("5");
//                balance = resultSet.getFloat("balance");
//            }else {
//                System.out.println("6");
//            }
//        } catch (SQLException e) {
//            System.out.println("7");
//            e.printStackTrace();
//        }
//        System.out.println("8");
//        return balance;
//    }


    public User login(String username, String password) {
        return (User) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM User WHERE username = :username AND password = :password")
                .setParameter("username", username)
                .setParameter("password", password)
                .setMaxResults(1)
                .uniqueResult();
    }

    public float getUserBalance(String username) {
        System.out.println(username);
        float balance = 0;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT balance FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getFloat("balance");
                System.out.println(1);
            } else {
                // If the user is not found, set balance to -1
                System.out.println(2);
                balance = -1;
            }
        } catch (SQLException e) {
            System.out.println(3);
            e.printStackTrace();
            // Set balance to -1 in case of an exception
            balance = -1;
        }
        return balance;
    }


    public boolean updateUserBalance(String username, float amount) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE users SET balance = ? WHERE username = ?");
            preparedStatement.setFloat(1, amount);
            preparedStatement.setString(2, username);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}