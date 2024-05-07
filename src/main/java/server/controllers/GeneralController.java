package server.controllers;
import server.Persist;
import server.entities.*;
import server.responses.BasicResponse;
import server.responses.LoginResponse;
import server.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

import java.util.List;
import static server.utils.Errors.*;




@RestController
public class GeneralController {
    private final List<SseEmitter> clients = new ArrayList<>();

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;

//    @PostConstruct
//    public void init() {
//        new Thread(() -> {
//            while (true) {
//                // Start of a season
//                initializeSeason();
//                runSeason();
//                endSeason();
//                pauseBetweenSeasons();
//            }
//        }).start();
//    }

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

    @RequestMapping(value = "/update-username", method = RequestMethod.POST)
    public BasicResponse updateUsername(String username, String password, String newUsername) {
        persist.updateUsername(username, password, newUsername);
        return new BasicResponse(true, null); // Return success response
    }


}