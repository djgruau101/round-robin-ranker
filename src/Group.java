import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
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
 *
 * Example usage:
 * ```
 * Group group = new FIFAWorldCupGroup(); // OneLegGroup denotes a group where each pair of teams face each other once only.
 * Team argentina = factory.createTeam("Argentina);
 * Team saudiArabia = factory.createTeam("Saudi Arabia");
 *
 * ```
 */
public abstract class Group {

    private final Team[] teams;
    private final int numberOfLegs;
    private final Map<Team,Integer> teamByPosition = new HashMap<>();

    public Group(Team[] teams, int groupSize, int numberOfLegs) throws IllegalArgumentException {
        if (teams.length > groupSize) {
            throw new IllegalArgumentException(String.format("The number of teams must not exceed %d.", groupSize));
        } if (numberOfLegs != 1 && numberOfLegs != 2) {
            throw new IllegalArgumentException("The number of legs must be either 1 or 2.");
        }
        this.teams = teams;
        this.numberOfLegs = numberOfLegs;
    }

    /**
     * Adds a match played between a home team and an away team. Each team name must belong in the group.
     * In one-legged group stages, the away team automatically gets a match added to its matches where its
     * opponent is the home team and the input score is reversed.
     *
     * @param homeTeamName The name of the home team.
     * @param awayTeamName The name of the away team.
     * @throws IllegalArgumentException If at least one of the teams' name does not correspond to a team in the group,
     *                                  'opponentName' is equal to the team's name,
     *                                  or the match's score is not expressed in the following format:
     *                                  two nonnegative integers separated by a '-'.
     */
    public void addMatch(String homeTeamName, String awayTeamName, String score) throws IllegalArgumentException {
        String errorMessage = ""; // error message for scoreInvalid and/or isPlayingAgainstItself
        if (Team.Match.isScoreInvalid(score)) {
            errorMessage += "The score must be two nonnegative integers separated by '-'.\n";
        } if (homeTeamName.equals(awayTeamName)) {
            errorMessage += "The home team and away team cannot be the same.";
        } if (!errorMessage.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        // check if both homeTeamName and awayTeamName correspond to teams in the group
        Optional<Team> matchingHomeTeam = Arrays.stream(teams).filter(t ->
                t.getName().equals(homeTeamName)).findFirst();
        Optional<Team> matchingAwayTeam = Arrays.stream(teams).filter(t ->
                t.getName().equals(awayTeamName)).findFirst();
        if (matchingHomeTeam.isPresent() && matchingAwayTeam.isPresent()) {
            Team homeTeam = matchingHomeTeam.get();
            Team awayTeam = matchingAwayTeam.get();
            Team.Match newMatch = new Team.Match(awayTeamName, score);
            homeTeam.addMatch(newMatch);
            sortTeams(); // update the team positions
            if (numberOfLegs == 1) {
                awayTeam.addMatch(homeTeam.getName(), newMatch.getReversedScore()); // 1-2 becomes 2-1
            }
        } else {
            throw new IllegalArgumentException("Team names must be among the ones in the group.");
        }
    }

    public void removeMatch(String homeTeamName, String awayTeamName) throws IllegalArgumentException {
        if (homeTeamName.equals(awayTeamName)) {
            throw new IllegalArgumentException("The home team and away team cannot be the same.");
        }
        // check if both homeTeamName and awayTeamName correspond to teams in the group
        Optional<Team> matchingHomeTeam = Arrays.stream(teams).filter(t ->
                t.getName().equals(homeTeamName)).findFirst();
        Optional<Team> matchingAwayTeam = Arrays.stream(teams).filter(t ->
                t.getName().equals(awayTeamName)).findFirst();
        if (matchingHomeTeam.isPresent() && matchingAwayTeam.isPresent()) {
            Team homeTeam = matchingHomeTeam.get();
            Team awayTeam = matchingAwayTeam.get();
            homeTeam.removeMatchByOpponentName(awayTeamName);
            if (numberOfLegs == 1) {
                awayTeam.removeMatchByOpponentName(homeTeamName);
            }
            sortTeams();
        } else {
            throw new IllegalArgumentException("Team names must be among the ones in the group.");
        }
    }

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
     * @param team2 A football team that team1 is compared to.
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
    public void sortTeams() {
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
                .allMatch(n -> n == teams.length-1);
    }
}