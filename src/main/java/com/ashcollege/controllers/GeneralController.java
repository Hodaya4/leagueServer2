package com.ashcollege.controllers;
import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BalanceResponse;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.utils.DbUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.ashcollege.entities.Match;
import java.util.List;
import static com.ashcollege.utils.Errors.*;




@RestController
public class GeneralController {
    private final List<SseEmitter> clients = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();
    private final List<Match> matches = new ArrayList<>();
    private final List<Match> currentRoundMatches = new ArrayList<>();
    private int currentRoundNumber = 1;
    private int currentMinute;
    private boolean betEnable = false;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (true) {
                // Start of a season
                teams.clear();
                matches.clear();
                currentRoundMatches.clear();
                currentRoundNumber = 1;
                createTeams();
                fillCurrentRound();
                notifySeasonStart();
                // Run rounds
                while (currentRoundNumber <= 9) { // Run for 9 rounds
                    betEnable = true;
                    currentMinute = 3;
                    sendCurrentRoundToClients();
                    notifyRoundStart();
                    try {
                        Thread.sleep(30000); // Betting phase lasts for 30 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    notifyBettingEnd();
                    betEnable = false;
                    int iterations = 0;
                    while (iterations < 30) { // 30 iterations of 1 second each
                        try {
                            Thread.sleep(1000); // Sleep for 1 second
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        decideGoal(); // Simulate goals
                        sendCurrentRoundToClients(); // Send the current round to clients
                        iterations++;
                        currentMinute += 3;
                    }
                    updateTeams(); // Update teams after each round
                    sendCurrentRoundToClients();
                    notifyRoundEnd();
                    currentMinute = 3;
                    try {
                        Thread.sleep(5000); // Pause for 5 seconds before starting the next round
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    currentRoundNumber++; // Move to the next round
                    fillCurrentRound();
                }
                // End of a season
                notifySeasonEnd();
                try {
                    Thread.sleep(60000); // Sleep for 1 minute between seasons
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @RequestMapping(value = "/init-page-details", method = {RequestMethod.GET, RequestMethod.POST})
    public InitialPageDetails getInitialPageDetails() {

        return new InitialPageDetails(currentRoundMatches, teams, betEnable);
    }

    @RequestMapping(value = "/start-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter startStream (){
        SseEmitter sseEmitter = new SseEmitter((long)(10*60*1000));
        clients.add(sseEmitter);
        return sseEmitter;
    }

    @RequestMapping(value = "/update-table", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Team> updateTable() {
        updateLeagueTable();
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
    public ResponseEntity<?> getUserBalance(@RequestParam String username) {
        float balance = persist.getUserBalance(username);
        if (balance >= 0) {
            return ResponseEntity.ok(new BalanceResponse(true, balance, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BalanceResponse(false, null, "User not found"));
        }
    }

    @RequestMapping(value = "/update-user-balance", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> updateUserBalance(@RequestBody Map<String, Object> requestData) {
        String username = (String) requestData.get("username");
        float amount = Float.parseFloat(requestData.get("amount").toString());
        boolean success = persist.updateUserBalance(username, amount);

        if (success) {
            return ResponseEntity.ok(new BalanceResponse(true, null, "User balance updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BalanceResponse(false, null, "User not found"));
        }
    }

    @RequestMapping(value = "/update-username", method = RequestMethod.POST)
    public BasicResponse updateUsername(String username, String password, String newUsername) {
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

    @RequestMapping(value = "/check-winning-bets", method = {RequestMethod.GET, RequestMethod.POST})
    public List<List<Map<String, Object>>> checkWinningBets(@RequestBody Map<String, Object> payload) {
        List<List<Map<String, Object>>> forms = (List<List<Map<String, Object>>>) payload.get("forms");
        List<List<Map<String, Object>>> validatedForms = new ArrayList<>();
        for (List<Map<String, Object>> form : forms) {
            boolean allCorrect = true; // flag to track if all bets in this form are correct

            for (Map<String, Object> bet : form) {
                int roundNumber = (int) bet.get("roundNumber");
                int matchNumber = (int) bet.get("matchNumber");
                int betValue = (int) bet.get("bet");

                Match correspondingMatch = matches.stream()
                        .filter(m -> m.getRound() == roundNumber && m.getMatch() == matchNumber)
                        .findFirst()
                        .orElse(null);

                boolean isCorrect = correspondingMatch != null && correspondingMatch.getResult() == betValue;

                // Add a boolean indicating whether the bet is correct or not to the bet map
                bet.put("isCorrect", isCorrect);

                if (!isCorrect) {
                    allCorrect = false;
                }
            }

            // Optionally, if you want to add a boolean indicating whether the entire form is a win or not
            boolean isFormWin = allCorrect;
            // Add a boolean indicating whether the form is a win or not to each form
            if (form.size() > 0) {
                form.get(0).put("isFormWin", isFormWin);
            }

            // Add the modified form to the list of validated forms
            validatedForms.add(form);
        }
        return validatedForms;
    }

    @RequestMapping(value = "/update-league-table", method = {RequestMethod.GET, RequestMethod.POST})
    public void updateLeagueTable() {
        // Sort teams based on points, goals difference, and name
        teams.sort((a, b) -> {
            // Sort teams based on points
            if (a.getPoints() != b.getPoints()) {
                return b.getPoints() - a.getPoints();
            }
            // If points are equal, sort based on goal difference
            int goalDifferenceA = a.getGoalsFor() - a.getGoalsAgainst();
            int goalDifferenceB = b.getGoalsFor() - b.getGoalsAgainst();
            if (goalDifferenceA != goalDifferenceB) {
                return goalDifferenceB - goalDifferenceA;
            }
            // If goal difference is equal, sort alphabetically by team name
            return a.getName().compareTo(b.getName());
        });
    }


    public void createTeams (){
        for (int i = 0; i < 10; i++) {
            Team team = new Team(i);
            teams.add(team);
        }
        generateMatches(teams);
    }


    private void generateMatches(List<Team> teams) {
        matches.clear(); // Clear the Matches list before generating new matches

        int n = teams.size();
        List<Integer> teamIndices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            teamIndices.add(i);
        }
        for (int round = 0; round < n - 1; round++) {
            List<Match> roundMatches = new ArrayList<>();
            for (int i = 0; i < n / 2; i++) {
                int team1Index = teamIndices.get(i);
                int team2Index = teamIndices.get(n - 1 - i);
                Team team1 = teams.get(team1Index);
                Team team2 = teams.get(team2Index);
                Match match = new Match(round + 1, i + 1, team1.getName(),team1.getId(),team2.getId(), team2.getName(), team1.getSkills(), team2.getSkills());
                roundMatches.add(match);
                matches.add(match); // Add match to the Matches list
            }
            teamIndices.add(1, teamIndices.remove(teamIndices.size() - 1));
        }
    }



    private void fillCurrentRound() {
        currentRoundMatches.clear(); // Clear the currentRound list before populating
        for (Match match : matches) {
            if (match.getRound() == currentRoundNumber) {
                currentRoundMatches.add(match);
            }
        }
    }

    private void decideGoal () {
        for (Match match : currentRoundMatches) {
            Team homeTeam = teams.stream().filter(team -> team.getId() == match.getHomeTeamId()).findFirst().orElse(null);
            Team awayTeam = teams.stream().filter(team -> team.getId() == match.getAwayTeamId()).findFirst().orElse(null);
            float probability = 85f + match.getWeatherFactor(); // Adjust probability based on weather
            // Randomly decide whether a goal will be scored based on adjusted probability
            if (Math.random() * 100 >= probability) {
                assert homeTeam != null;
                assert awayTeam != null;

                float updateHomeSkills = match.getHomeSkills() - (homeTeam.getNumInjuredPlayers() + homeTeam.getNumRedCards() + homeTeam.getIsWinLastMatch());
                float updateAwaySkills = match.getAwaySkills() - (awayTeam.getNumInjuredPlayers() + awayTeam.getNumRedCards() + awayTeam.getIsWinLastMatch());
                float homeTeamChance = updateHomeSkills / (updateHomeSkills + updateAwaySkills);
                float awayTeamChance = 1f - homeTeamChance;
                float rand = (float) Math.random();
                if (rand < homeTeamChance) {
                    // Home team scores
                    match.setHomeGoals(match.getHomeGoals() + 1);
                } else {
                    // Away team scores
                    match.setAwayGoals(match.getAwayGoals() + 1);
                }
            }
        }
    }

    private void sendCurrentRoundToClients() {
        List<SseEmitter> disconnectedEmitters = new ArrayList<>();
        for (SseEmitter sseEmitter : clients) {
            try {
                // Convert currentRound list to JSON format
                ObjectMapper mapper = new ObjectMapper();
                String currentRoundJson = mapper.writeValueAsString(currentRoundMatches);

                // Create a JSON object containing currentRound, thisRoundNumber, and currentMinute
                String json = "{\"currentRound\": " + currentRoundJson + ", \"thisRoundNumber\": " + currentRoundNumber + ", \"currentMinute\": " + currentMinute + "}";
                sseEmitter.send(SseEmitter.event().data(json));
            } catch (IOException e) {
                // If an IOException occurs, it likely means the client connection is closed
                // Add the emitter to the list of disconnected emitters
                disconnectedEmitters.add(sseEmitter);
            } catch (IllegalStateException e) {
                // If the emitter is already completed, remove it from the clients list
                disconnectedEmitters.add(sseEmitter);
            }
        }

        // Remove disconnected emitters from the clients list
        clients.removeAll(disconnectedEmitters);
    }

    private void updateTeams() {
        for (Match updatedMatch : currentRoundMatches) {
            // Find the corresponding match in the Matches list
            for (Match originalMatch : matches) {
                if (originalMatch.getRound() == updatedMatch.getRound() && originalMatch.getMatch() == updatedMatch.getMatch()) {
                    // Update team statistics based on match result
                    Team homeTeam = teams.stream().filter(team -> team.getId() == originalMatch.getHomeTeamId()).findFirst().orElse(null);
                    Team awayTeam = teams.stream().filter(team -> team.getId() == originalMatch.getAwayTeamId()).findFirst().orElse(null);

                    if (homeTeam != null && awayTeam != null) {
                        if (updatedMatch.getHomeGoals() > updatedMatch.getAwayGoals()) {
                            // Home team won
                            homeTeam.setPoints(homeTeam.getPoints() + 3);
                            homeTeam.setWinLastMatch(3);
                            homeTeam.setLastGames("W");
                            awayTeam.setWinLastMatch(-3);
                            awayTeam.setLastGames("L");
                            updatedMatch.setResult(1);
                        } else if (updatedMatch.getHomeGoals() < updatedMatch.getAwayGoals()) {
                            // Away team won
                            awayTeam.setPoints(awayTeam.getPoints() + 3);
                            awayTeam.setWinLastMatch(3);
                            awayTeam.setLastGames("W");
                            homeTeam.setWinLastMatch(-3);
                            homeTeam.setLastGames("L");
                            updatedMatch.setResult(2);
                        } else {
                            // Draw
                            homeTeam.setPoints(homeTeam.getPoints() + 1);
                            awayTeam.setPoints(awayTeam.getPoints() + 1);
                            updatedMatch.setResult(0);
                            homeTeam.setLastGames("D");
                            awayTeam.setLastGames("D");
                        }
                        Random random = new Random();
                        int homeNewInjuredPlayers = random.nextInt(6) - 3;
                        int homeNewRedCards = random.nextInt(5) - 2;
                        int awayNewInjuredPlayers = random.nextInt(6) - 3;
                        int awayNewRedCards = random.nextInt(5) - 2;
                        homeTeam.setNumInjuredPlayers(homeNewInjuredPlayers);
                        homeTeam.setNumRedCards(homeNewRedCards);
                        awayTeam.setNumInjuredPlayers(awayNewInjuredPlayers);
                        awayTeam.setNumRedCards(awayNewRedCards);
                        // Update goals statistics
                        homeTeam.setGoalsFor(homeTeam.getGoalsFor() + updatedMatch.getHomeGoals());
                        homeTeam.setGoalsAgainst(homeTeam.getGoalsAgainst() + updatedMatch.getAwayGoals());
                        awayTeam.setGoalsFor(awayTeam.getGoalsFor() + updatedMatch.getAwayGoals());
                        awayTeam.setGoalsAgainst(awayTeam.getGoalsAgainst() + updatedMatch.getHomeGoals());
                    }
                    // Update the match in the Matches list
                    originalMatch.setHomeGoals(updatedMatch.getHomeGoals());
                    originalMatch.setAwayGoals(updatedMatch.getAwayGoals());
                    originalMatch.setResult(updatedMatch.getResult());
                    // Exit the loop after finding the corresponding match
                    break;
                }
            }
        }
    }


    private void notifySeasonStart() {
        for (SseEmitter sseEmitter : clients) {
            try {
                // Send a message indicating the start of the season
                sseEmitter.send(SseEmitter.event().name("season-start").data("The season has started."));
            } catch (IOException e) {
                // Handle IO exception
            }
        }
    }

    private void notifyRoundEnd() {
        for (SseEmitter sseEmitter : clients) {
            try {
                // Send a message indicating the end of a round
                sseEmitter.send(SseEmitter.event().name("round-end").data("Round " + currentRoundNumber + " has ended."));
            } catch (IOException e) {
                // Handle IO exception
            }
        }
    }

    private void notifySeasonEnd() {
        for (SseEmitter sseEmitter : clients) {
            try {
                // Send a message indicating the end of the season
                sseEmitter.send(SseEmitter.event().name("season-end").data("The season has ended."));
            } catch (IOException e) {
                // Handle IO exception
            }
        }
    }

    private void notifyRoundStart() {
        for (SseEmitter sseEmitter : clients) {
            try {
                // Send a message indicating the start of a round
                sseEmitter.send(SseEmitter.event().name("round-start").data("Betting phase has started."));
            } catch (IOException e) {
                // Handle IO exception
            }
        }
    }

    private void notifyBettingEnd() {
        for (SseEmitter sseEmitter : clients) {
            try {
                // Send a message indicating the end of the betting phase
                sseEmitter.send(SseEmitter.event().name("betting-end").data("Betting phase has ended."));
            } catch (IOException e) {
                // Handle IO exception
            }
        }
    }

}
