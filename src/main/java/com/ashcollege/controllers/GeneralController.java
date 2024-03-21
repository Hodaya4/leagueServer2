package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;


    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object hello() {
        return "Hello From Server";
    }

    @RequestMapping(value = "/generateTeams", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Team> generateTeams() {
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Team team = new Team();
            teams.add(team);
        }
        return teams;
    }


    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login(String username, String password) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        if (username != null && username.length() > 0) {
            if (password != null && password.length() > 0) {
                User user = persist.login(username,password);
                if (user != null) {
                    basicResponse = new LoginResponse(true, errorCode, user.getId());
                } else {
                    errorCode = ERROR_LOGIN_WRONG_CREDS;
                }
            } else {
                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
            }
        } else {
            errorCode = ERROR_SIGN_UP_NO_USERNAME;
        }
        if (errorCode != null) {
            basicResponse = new BasicResponse(success, errorCode);
        }
        return basicResponse;
    }

    @RequestMapping(value = "/get-user-balance", method = {RequestMethod.GET, RequestMethod.POST})
    public float getUserBalance(String username) {
        System.out.println(username);
        return persist.getUserBalance(username);
    }

    @RequestMapping(value = "/update-user-balance", method = RequestMethod.POST)
    public BasicResponse updateUserBalance(String username, String password, float balance) {
        persist.updateUserBalance(username, password, balance);
        System.out.println("update balance " + balance);
        return new BasicResponse(true, null); // Return success response
    }

    @RequestMapping(value = "/update-username", method = RequestMethod.POST)
    public BasicResponse updateUsername(String username, String password, String newUsername) {
        System.out.println("username " + username);
        System.out.println("password " + password);
        System.out.println("updated username " + newUsername);
        persist.updateUsername(username, password, newUsername);
        return new BasicResponse(true, null); // Return success response
    }

    @RequestMapping(value = "add-user")
    public boolean addUser(String username, String password, String email) {
        User userToAdd = new User(username, password, email, 1000);
        return dbUtils.addUser(userToAdd);
    }


    @RequestMapping(value = "get-users")
    public List<User> getUsers() {
        return dbUtils.getAllUsers();
    }


}
