import java.util.*;
import java.util.stream.IntStream;

/**
 * The Group class represents a group of teams competing in a round-robin tournament
 * and provides their ranking based on their results. The rankings will differ
 * depending on the competition that the teams take part of.
 *
 * Attributes:
 * teams: the array of teams competing in the group.
 * groupSize: the maximum amount of teams in the group.
 * numberOfLegs: the number of matches played between two teams.
 *
 * Usage:
 * The Group class provides functionality for adding and removing match data between the teams in the group.
 * It also determines the rankings of each team in the group
 * based on its ranking system, which differs between tournaments.
 *
 * For groups where numberOfLegs = 2, each team's 'matches' field
 * will solely contain the team's games that are played at home.
 * The away goals rule may apply for tiebreaking purposes depending on the competition.
 */
public abstract class Group {

    private final Team[] teams;
    private final int numberOfLegs;
    private final Map<Team,Integer> teamByPosition = new HashMap<>();

    public Group(Team[] teams, int groupSize, int numberOfLegs) throws IllegalArgumentException {
        if (teams.length > groupSize) {
            throw new IllegalArgumentException(String.format("The number of teams must not exceed %d.", groupSize));
        } if (Arrays.stream(teams).map(Team::getName).distinct().count()
                != teams.length) {
            throw new IllegalArgumentException("Teams with duplicate names detected.");
        } if (numberOfLegs != 1 && numberOfLegs != 2) {
            throw new IllegalArgumentException("The number of legs must be either 1 or 2.");
        }
        this.teams = teams;
        this.numberOfLegs = numberOfLegs;
    }

    /**
     * Adds a match played between two teams whose names are denoted team1Name and team2Name.
     * Each team name must belong in the group.
     * In single-legged tournaments, the match is added to both teams' homeMatches.
     * In double-legged tournaments, the match is added to team1Name's homeMatches and team2Name's awayMatches.
     *
     * @param team1Name The name of the first team. If the tournament is double-legged, this is the home team.
     * @param team2Name The name of the second team. If the tournament is double-legged, this is the away team.
     * @throws IllegalArgumentException If at least one of the teams' name does not correspond to a team in the group,
     *                                  'opponentName' is equal to the team's name,
     *                                  or the match's score is not expressed in the following format:
     *                                  two nonnegative integers separated by a '-'.
     */
    public void addMatch(String team1Name, String team2Name, String score) throws IllegalArgumentException {
        String errorMessage = ""; // error message for scoreInvalid and/or isPlayingAgainstItself
        if (Team.Match.isScoreInvalid(score)) {
            errorMessage += "The score must be two nonnegative integers separated by '-'.\n";
        } if (team1Name.equals(team2Name)) {
            errorMessage += "The names of the two teams facing each other cannot be the same.";
        } if (!errorMessage.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        // check if both homeTeamName and awayTeamName correspond to teams in the group
        Optional<Team> matchingTeam1 = Arrays.stream(teams).filter(t ->
                t.getName().equals(team1Name)).findFirst();
        Optional<Team> matchingTeam2 = Arrays.stream(teams).filter(t ->
                t.getName().equals(team2Name)).findFirst();
        if (matchingTeam1.isPresent() && matchingTeam2.isPresent()) {
            Team team1 = matchingTeam1.get();
            Team team2 = matchingTeam2.get();
            Team.Match newHomeMatch = new Team.Match(team2Name, score, false);
            team1.addMatch(newHomeMatch);
            // if there are two legs, the awayTeam gets the match added to its awayMatches
            team2.addMatch(team1.getName(), newHomeMatch.getReversedScore(), numberOfLegs == 2);
            sortTeams(); // update the team positions
        } else {
            throw new IllegalArgumentException("Team names must be among the ones in the group.");
        }
    }

    /**
     * Removes a match played between two teams whose names are denoted team1Name and team2Name.
     * Each team name must belong in the group.
     *
     * In single-legged tournaments, the match is removed from both teams' homeMatches.
     * In double-legged tournaments, the match is removed from team1Name's homeMatches and team2Name's awayMatches.
     *
     * @param team1Name The name of the first team. If the tournament is double-legged, this is the home team.
     * @param team2Name The name of the second team. If the tournament is double-legged, this is the away team.
     * @throws IllegalArgumentException If at least one of the teams' name does not correspond to a team in the group,
     *                                  or 'opponentName' is equal to the team's name.
     */
    public void removeMatch(String team1Name, String team2Name) throws IllegalArgumentException {
        if (team1Name.equals(team2Name)) {
            throw new IllegalArgumentException("The names of the two teams facing each other cannot be the same.");
        }
        // check if both homeTeamName and awayTeamName correspond to teams in the group
        Optional<Team> matchingTeam1 = Arrays.stream(teams).filter(t ->
                t.getName().equals(team1Name)).findFirst();
        Optional<Team> matchingTeam2 = Arrays.stream(teams).filter(t ->
                t.getName().equals(team2Name)).findFirst();
        if (matchingTeam1.isPresent() && matchingTeam2.isPresent()) {
            Team team1 = matchingTeam1.get();
            Team team2 = matchingTeam2.get();
            team1.removeMatchByOpponentName(team2Name, false);
            team2.removeMatchByOpponentName(team1Name, numberOfLegs == 2);
            sortTeams();
        } else {
            throw new IllegalArgumentException("Team names must be among the ones in the group.");
        }
    }

    /**
     * Takes the name of a team and return a clone of the Team object associated with it,
     * if a team of the given name exists in the group.
     *
     * @param teamName The name of a team.
     * @throws IllegalArgumentException If no team of the given name is in the group.
     */
    public Team getTeamByName(String teamName) throws IllegalArgumentException {
        Optional<Team> matchingTeam = Arrays.stream(teams).filter(t -> t.getName().equals(teamName)).findFirst();
        if (matchingTeam.isPresent()) {
            // return a defensive copy
            TeamFactory factory = new TeamFactory();
            return factory.createTeam(matchingTeam.get().getName(), matchingTeam.get().getMatches());
        }
        throw new IllegalArgumentException(String.format("No team of name %s is in this group.", teamName));
    }

    /**
     * Compares the ranking of two teams. Each competition will have its own ranking system.
     *
     * @param team1 A football team.
     *              If the tournament is double-legged, this is the home team.
     * @param team2 A football team that team1 is compared to.
     *              If the tournament is double-legged, this is the away team.
     *
     * @return A positive integer if team1 is ranked above team2,
     *         a negative integer if team2 is ranked above team1.
     */
    public abstract int compareTeams(Team team1, Team team2);

    /**
     * An overloaded method for compareTeams where the input is two team names instead of two Team objects.
     * It retrieves the Team objects via the team names.
     *
     * @param team1Name The name of a football team.
     * @param team2Name The name of a football team that team1 is compared to.
     *
     * @return A positive integer if team1 is ranked above team2,
     *         a negative integer if team2 is ranked above team1.
     */
    public int compareTeams(String team1Name, String team2Name) {
        return compareTeams(getTeamByName(team1Name), getTeamByName(team2Name));
    }

    /**
     * Sorts the group's teams from highest ranked to lowest ranked
     * depending on the competition's ranking system.
     */
    private void sortTeams() {
        Arrays.sort(teams, (team1, team2) -> compareTeams(team2, team1));
        // indices start at 0 so add 1 to get the position
        IntStream.range(0, teams.length).forEach(i -> {
            if (i == 0) {
                teamByPosition.put(teams[i], 1);
            } else if (compareTeams(teams[i], teams[i-1]) < 0) {
                teamByPosition.put(teams[i], i+1); // + 1 because array indices start at 0
            } else {
                // position is the same as the team last added
                teamByPosition.put(teams[i], teamByPosition.get(teams[i-1]));
            }
        });
    }

    /**
     * Returns a clone of the array of the group's teams sorted from
     * highest ranked to lowest ranked depending on the competition's ranking system.
     *
     * @return A sorted array of teams.
     */
    public Team[] sortedTeams() {
        Team[] cloneTeams = teams.clone();
        Arrays.sort(cloneTeams, (team1, team2) -> compareTeams(team2, team1));
        return cloneTeams;
    }

    /**
     * Takes a string representing the name of a team and returns its position in the group.
     *
     * @param teamName The name of a football team.
     *
     * @return A positive integer if team1 is ranked above team2,
     *         a negative integer if team2 is ranked above team1.
     */
    public int getTeamPositionByName(String teamName) throws IllegalArgumentException {
        sortTeams();
        return teamByPosition.get(getTeamByName(teamName));
    }

    /**
     * Returns whether the group is complete, that is,
     * every team has faced each other (twice for double-legged format).
     *
     * @return true if the group is complete, false otherwise.
     */
    public boolean isComplete() {
        return Arrays.stream(teams).map(Team::getNumberOfMatchesPlayed)
                .allMatch(n -> n == (teams.length-1)*numberOfLegs);
    }

    /**
     * Takes the name of a team and returns a string displaying its position, name, number of matches played,
     * number of matches won, number of matches drawn, number of matches lost, number of goals scored,
     * number of goals conceded, goal difference and number of points.
     *
     * @return information about the team.
     */
    public String getTableRowByTeamName(String teamName) {
        Team team = getTeamByName(teamName);
        return String.format("%d: %s, Pld: %d, W: %d, D: %d, L: %d, GF: %d, GA: %d, GD: %s, Pts: %d",
                getTeamPositionByName(teamName), teamName, team.getNumberOfMatchesPlayed(),
                team.getNumberWins(), team.getNumberDraws(), team.getNumberLosses(), team.getGoalsFor(),
                team.getGoalsAgainst(), team.getGoalDifferenceToString(), team.getPoints());
    }
}