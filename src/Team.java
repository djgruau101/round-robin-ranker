import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Team class represents a football team competing in a round-robin tournament
 * and maintains a record of matches it played against other teams.
 *
 * Attributes:
 * name: the name of the team
 * matches: a string->string map representing the matches it played
 *
 * The keys of the 'matches' map represent the team's rivals' names.
 * Its values represent the corresponding scores against a team.
 * Each score is a string represented in the format "goalsByThisTeam-goalsByRivalTeam".
 * For example, a key-value pair of ("Real Madrid", "3-1") indicates that
 * in a match against Real Madrid, the team scored 3 goals and Real Madrid scored 1 goal.
 *
 * Usage:
 * The Team class provides functionality for adding and removing match data, calculating the amount of
 * points the team has received (+3 per win, +1 per draw, +0 per loss) and calculating its goal difference
 * (goals scored - goals conceded), which will be used to rank the team with its rivals. Other data such as
 * number of matches played, number of wins, number of draws, number of losses, goals scored, goals conceded,
 * and goal difference will also be displayed in a table.
 *
 * This class also contains two public overloaded static methods called createInstance, which will be
 * called by the TeamFactory class to ensure that all Team instances are unique in name.
 * They are made public only for the TeamFactory class to be able to use them.
 * The first method creates an instance given a name and a map of matches,
 * while the second method creates an instance given a name and sets its matches to an empty map.
 *
 * @author Daniel Luo
 */
public class Team {

    private final String name;
    private Map<String,String> matches = new HashMap<>();

    private Team(String name, Map<String,String> matches) {
        this.name = name;
        this.matches.putAll(matches);
    }

    private Team(String name) {
        this.name = name;
        this.matches = new HashMap<>();
    }

    /**
     * Adds a match played by the team or updates the score of an existing match.
     *
     * @param opponent The name of a team that the team is playing or has played against.
     * @param score The score of the match against said team.
     *              It consists of two nonnegative integers separated by a '-'.
     *              Its format is "goalsByThisTeam-goalsByRivalTeam".
     * @throws IllegalArgumentException If 'score' is not in the format indicated above.
     */
    public void addMatch(String opponent, String score) throws IllegalArgumentException {
        if (isScoreInvalid(score)) {
            throw new IllegalArgumentException("The score must be two nonnegative integers separated by '-'.");
        }
        this.matches.put(opponent, score);
    }

    /**
     * Removes the match that the team played against the given opponent.
     * It does nothing if 'opponent' is not a key in 'matches'.
     *
     * @param opponent The name of a team that the team is playing or has played against.
     */
    public void removeMatch(String opponent) {
        this.matches.remove(opponent);
    }

    /**
     * Gets the current value of 'matches'.
     * It represents the data for matches that the team has played.
     * Each match is represented by a key-value pair,
     * the key being an opponent's team name and the value being the score between the two teams.
     *
     * @return The current value of 'matches'.
     */
    public Map<String,String> getMatches() {
        return new HashMap<>(matches);
    }

    /**
     * Takes a string representing the score of a match and returns whether its format is incorrect.
     * A correctly formatted score consists of two nonnegative integers separated by a '-'.
     * Examples of correctly formatted scores: "0-0", "0-2", "1-0", "2-3", "10-20"
     * Examples of incorrectly formatted scores: "02-1", "3-00", "2- 1", "5s-3"
     *
     * @param score The score of a match played by the team.
     * @return true if the score is incorrectly formatted, false otherwise.
     */
    private static boolean isScoreInvalid(String score) {
        String pattern = "^(0|[1-9]\\d*)-(0|[1-9]\\d*)$"; // nonnegative integer, hyphen, nonnegative integer
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(score);
        return !matcher.matches();
    }

    /**
     * Creates an instance of type Team and sets its matches according to the 'matches' input.
     * This method will be called by the TeamFactory class to ensure uniqueness of all Team instances.
     *
     * @param name The name of the instance.
     * @param matches A string->string map that represents the matches the team has played.
     * @throws IllegalArgumentException If at least one score is incorrectly formatted.
     */
    public static Team createInstance(String name, Map<String,String> matches) throws IllegalArgumentException{
        if (matches.values().parallelStream().anyMatch(Team::isScoreInvalid)) {
            throw new IllegalArgumentException("The score must be two nonnegative integers separated by '-'.");
        }
        return new Team(name, matches);
    }

    /**
     * Creates an instance of type Team and sets its matches to an empty map.
     * This method will be called by the TeamFactory class to ensure uniqueness of all Team instances.
     *
     * @param name The name of the instance.
     */
    public static Team createInstance(String name) {
        return new Team(name);
    }

    /**
     * A human-readable, string representation of the current instance, showing its name and matches played.
     */
    public String toString() {
        return this.matches.entrySet().stream()
                .map(entry -> String.format("'%s'='%s'", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", ", "Team{name='" + name + "', matches=", "}"));
    }
}
