import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Team class represents a football team competing in a round-robin tournament
 * and maintains a record of matches it played against other teams.
 *
 * Attributes:
 * name: the name of the team.
 * matches: a set of matches, each of them specifying a rival team and the score.
 *
 * Usage:
 * The Team class provides functionality for adding and removing match data
 * by creating instances of the inner Match class.
 * It also calculates the amount of points the team has received (+3 per win, +1 per draw, +0 per loss)
 * and its goal difference (goals scored - goals conceded),
 * which will be used to rank the team with its rivals.
 * Other data such as the number of matches played, number of wins, number of draws,
 * number of losses, goals scored, goals conceded, and goal difference will also be displayed in a table.
 *
 * This class also contains two public overloaded static methods called createInstance, which will be
 * called by the TeamFactory class to ensure that all Team instances are unique.
 * They are made public only for the TeamFactory class to be able to use them.
 * The first method creates an instance given a name and a set of matches,
 * while the second method creates an instance given a name and sets its matches to an empty set.
 *
 * @author Daniel Luo
 */
public class Team {

    private final String name;
    private Set<Match> matches = new HashSet<>();

    private Team(String name, Set<Match> matches) {
        this.name = name;
        this.matches.addAll(matches);
    }

    private Team(String name) {
        this.name = name;
        this.matches = new HashSet<>();
    }

    /**
     * Adds a match played by the team or updates the score of an existing match.
     *
     * @param opponentName The name of a team that the team is playing or has played against.
     * @param score The score of the match against said team.
     *              It consists of two nonnegative integers separated by a '-'.
     *              Its format is "goalsByThisTeam-goalsByRivalTeam".
     * @throws IllegalArgumentException If 'score' is not in the format indicated above.
     */
    public void addMatch(String opponentName, String score) throws IllegalArgumentException {
        if (Match.isScoreInvalid(score)) {
            throw new IllegalArgumentException("The score must be two nonnegative integers separated by '-'.");
        }
        if (this.matches.parallelStream().anyMatch(match -> match.getOpponentName().equals(opponentName))) {
            matches.stream()
                    .filter(match -> match.getOpponentName().equals(opponentName))
                    .forEach(match -> match.setScore(score)); // update score of existing match
        } else {
            this.matches.add(new Match(opponentName, score));
        }
    }

    /**
     * Removes the match in which the team played against the given opponent team.
     * It does nothing if the team has played no match against the given team.
     *
     * @param opponentName The name of a team that the team played against.
     */
    public void removeMatchByOpponentName(String opponentName) {
        this.matches.removeIf(match -> match.getOpponentName().equals(opponentName));
    }

    /**
     * Calculates the team's number of points based on its matches.
     *
     * @return The number of points the team has accumulated.
     */
    public int getPoints() {
        return 0; // to be implemented
    }

    /**
     * Gets the set of matches that the team has played.
     *
     * @return The set of matches that the team has played.
     */
    public Set<Match> getMatches() {
        return Collections.unmodifiableSet(matches);
    }

    /**
     * Gets the name of the team.
     *
     * @return The name of the team.
     */
    public String getName() {
        return this.name;
    }

    /**
     * The Match class represents a football match that the team plays.
     * It can therefore not be instantiated outside the Team class.
     *
     * Attributes:
     * opponentName: the name of an opponent team.
     * score: the result of the match.
     *
     * Each score is a string represented in the format "goalsByThisTeam-goalsByRivalTeam".
     * For example, a match with arguments "Real Madrid" and "3-1" represents a match
     * played against Real Madrid, where the team scored 3 goals and Real Madrid scored 1 goal.
     */
    static class Match {
        private final String opponentName;
        private String score;

        public Match(String opponentName, String score) throws IllegalArgumentException {
            if (isScoreInvalid(score)) {
                throw new IllegalArgumentException("The score must be two nonnegative integers separated by '-'.");
            }
            this.opponentName = opponentName;
            this.score = score;
        }

        private enum Outcome {
            LOSS, DRAW, WIN
        }

        public String getOpponentName() {
            return this.opponentName;
        }

        public String getScore() {
            return this.score;
        }

        public void setScore(String score) {
            if (isScoreInvalid(score)) {
                throw new IllegalArgumentException("The score must be two nonnegative integers separated by '-'.");
            }
            this.score = score;
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

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Match otherMatch = (Match) obj;
            return opponentName.equals(otherMatch.opponentName) && score.equals(otherMatch.score);
        }

        @Override
        public int hashCode() {
            return opponentName.hashCode();
        }
    }

    /**
     * Creates an instance of type Team and sets its matches according to the 'matches' input.
     * This method will be called by the TeamFactory class to ensure uniqueness of all Team instances.
     *
     * @param name The name of the instance.
     * @param matches The set of matches that the team has played.
     * @return A new Team instance with its name and matches correctly set.
     * @throws IllegalArgumentException If at least one score from the matches is incorrectly formatted.
     */
    public static Team createInstance(String name, Set<Match> matches) throws IllegalArgumentException{
        if (matches.parallelStream().map(Match::getScore).anyMatch(Match::isScoreInvalid)) {
            throw new IllegalArgumentException("The score must be two nonnegative integers separated by '-'.");
        }
        return new Team(name, matches);
    }

    /**
     * Creates an instance of type Team and sets its matches to an empty set.
     * This method will be called by the TeamFactory class to ensure uniqueness of all Team instances.
     *
     * @param name The name of the instance.
     * @return The new Team instance.
     */
    public static Team createInstance(String name) {
        return new Team(name);
    }
}
