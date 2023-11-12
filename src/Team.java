import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;
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
    private static boolean scoreInvalid = false;
    private static boolean isPlayingAgainstItself = false;
    private static final String scoreInvalidMessage = "The score must be two nonnegative integers separated by '-'.";
    private static final String isPlayingAgainstItselfMessage =
            "All opponents' names should be different from the team's name.";
    private static final String compoundErrorMessage = "The score must be two nonnegative integers separated by '-', " +
            "and all opponents' names should be different from the team's name.";

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
     * @param match A football match.
     * @throws IllegalArgumentException If the match's score is not expressed in the following format:
     *                                  two nonnegative integers separated by a '-',
     *                                  or 'opponentName' is equal to the team's name.
     */
    public void addMatch(Match match) throws IllegalArgumentException {
        scoreInvalid = Match.isScoreInvalid(match.getScore());
        isPlayingAgainstItself = match.getOpponentName().equals(this.getName());
        throwExceptionMessage();
        if (this.getMatches().parallelStream().anyMatch(m -> m.getOpponentName().equals(match.getOpponentName()))) {
            this.matches.stream()
                    .filter(m -> m.getOpponentName().equals(match.getOpponentName()))
                    .forEach(m -> m.setScore(match.getScore())); // update score of existing match
        } else {
            this.matches.add(new Match(match.getOpponentName(), match.getScore()));
        }
    }

    /**
     * Constructs a Match instance, adds it in the team's matches
     * or updates the score of one of the team's existing matches.
     *
     * @param opponentName The name of a team that the team has played against.
     * @param score The score of the match against said team.
     *              It consists of two nonnegative integers separated by a '-'.
     *              Its format is "goalsByThisTeam-goalsByRivalTeam".
     * @throws IllegalArgumentException If 'score' is not in the format indicated above
     *                                  or 'opponentName' is equal to the team's name.
     */
    public void addMatch(String opponentName, String score) throws IllegalArgumentException {
        scoreInvalid = Match.isScoreInvalid(score);
        isPlayingAgainstItself = opponentName.equals(this.getName());
        throwExceptionMessage();
        addMatch(new Match(opponentName, score));
    }

    /**
     * Adds matches played by the team and updates the score of existing matches.
     *
     * @param matches The matches that the team has played.
     * @throws IllegalArgumentException If at least one score is not in the correct format:
     *                                  two nonnegative integers separated by a '-',
     *                                  or at least one match's opponent's name is equal to the team's name.
     */
    public void addMatches(Match... matches) throws IllegalArgumentException {
        scoreInvalid = Arrays.stream(matches).map(Match::getScore).anyMatch(Match::isScoreInvalid);
        isPlayingAgainstItself = Arrays.stream(matches).map(Match::getOpponentName)
                .anyMatch(n -> n.equals(this.getName()));
        throwExceptionMessage();
        Arrays.stream(matches).forEach(this::addMatch);
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

    // The following methods will be used to display data on a ranking table

    /**
     * Returns the number of matches the team has won.
     *
     * @return Number of won matches.
     */
    public int getNumberWins() {
        return (int) this.getMatches().parallelStream().map(Match::getOutcome)
                .filter(o -> o.equals(Match.Outcome.WIN)).count();
    }

    /**
     * Returns the number of matches the team has drawn.
     *
     * @return Number of drawn matches.
     */
    public int getNumberDraws() {
        return (int) this.getMatches().parallelStream().map(Match::getOutcome)
                .filter(o -> o.equals(Match.Outcome.DRAW)).count();
    }

    /**
     * Returns the number of matches the team has lost.
     *
     * @return Number of lost matches.
     */
    public int getNumberLosses() {
        return (int) this.getMatches().parallelStream().map(Match::getOutcome)
                .filter(o -> o.equals(Match.Outcome.LOSS)).count();
    }

    /**
     * Calculates the team's number of points in a round-robin tournament based on its matches' outcome.
     * A loss is 0 point, a draw is 1 point and a win is 3 points.
     *
     * @return The number of points the team has accumulated.
     */
    public int getPoints() {
        return this.getMatches().parallelStream().map(
                match -> match.getOutcome().getPoints())
                .mapToInt(Integer::intValue).sum();
    }

    /**
     * Calculates the total number of goals scored by the team during the tournament, also known as 'goals for' (GF).
     *
     * @return The number of goals scored.
     */
    public int getGoalsFor() {
        return this.matches.parallelStream().map(Match::getGoalsScored).mapToInt(Integer::intValue).sum();
    }

    /**
     * Calculates the total number of goals conceded by the team during the tournament, also known as 'goals against' (GA).
     *
     * @return The number of goals conceded.
     */
    public int getGoalsAgainst() {
        return this.matches.parallelStream().map(Match::getGoalsConceded).mapToInt(Integer::intValue).sum();
    }

    /**
     * Calculates the team's goal difference (GD), which is goalsFor - goalsAgainst.
     *
     * @return The goal difference.
     */
    public int getGoalDifference() {
        return this.getGoalsFor() - this.getGoalsAgainst();
    }

    /**
     * Returns a string representation of the team's goal difference.
     * It adds a '+' before the number if it is positive.
     *
     * @return The string representation of goal difference.
     */
    public String getGoalDifferenceToString() {
        if (this.getGoalDifference() > 0) {
            return "+" + this.getGoalDifference();
        }
        return Integer.toString(this.getGoalDifference());
    }

    /**
     * Returns the number of matches the team has played.
     *
     * @return The number of matches played.
     */
    public int getNumberOfMatchesPlayed() {
        return this.getMatches().size();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Team otherTeam = (Team) obj;
        return this.name.equals(otherTeam.name) && this.matches.equals(otherTeam.matches);
    }

    /**
     * Returns the set of matches that the team has played.
     *
     * @return The set of matches that the team has played.
     */
    public Set<Match> getMatches() {
        return Collections.unmodifiableSet(matches);
    }

    /**
     * Returns the name of the team.
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
     * Each score is a string represented in the format "goalsByThisTeam-goalsByOpponent".
     * For example, a match with arguments "Real Madrid" and "3-1" represents a match
     * played against Real Madrid, where the team scored 3 goals and Real Madrid scored 1 goal.
     */
    static class Match {

        private final String opponentName;
        private String score;

        public Match(String opponentName, String score) throws IllegalArgumentException {
            scoreInvalid = isScoreInvalid(score);

            throwExceptionMessage();
            this.opponentName = opponentName;
            this.score = score;
        }

        /**
         * Represents the outcome of a match played by the team.
         * The Outcome enum defines the possible states that a match can be in,
         * such as LOSS, DRAW, or WIN.
         *
         * Each enum constant has its number of points associated to it.
         * That is, the team wins 0 point if they lose a match,
         * 1 point if the match is drawn and 3 points if they win a match.
         */
        enum Outcome {

            /**
             * The team lost the match.
             */
            LOSS(0),

            /**
             * The match ended in a tie.
             */
            DRAW(1),

            /**
             * The team won the match.
             */
            WIN(3);

            private final int points;

            Outcome(int points) {
                this.points = points;
            }

            public int getPoints() {
                return points;
            }
        }

        public String getOpponentName() {
            return this.opponentName;
        }

        public String getScore() {
            return this.score;
        }

        public void setScore(String score) {
            scoreInvalid = isScoreInvalid(score);
            throwExceptionMessage();
            this.score = score;
        }

        /**
         * Returns the number of goals scored by the team in this match.
         *
         * @return Number of goals scored.
         */
        public int getGoalsScored() {
            return Integer.parseInt(this.score.split("-")[0]);
        }

        /**
         * Returns the number of goals conceded by the team in this match.
         *
         * @return Number of goals conceded.
         */
        public int getGoalsConceded() {
            return Integer.parseInt(this.score.split("-")[1]);
        }

        /**
         * Returns the team's outcome for the match depending
         * on the number of goals it scored and conceded.
         *
         * @return An enum that represents the result of the match.
         */
        public Outcome getOutcome() {
            if (this.getGoalsScored() < this.getGoalsConceded()) {
                return Outcome.LOSS;
            } else if (this.getGoalsScored() == this.getGoalsConceded()) {
                return Outcome.DRAW;
            }
            return Outcome.WIN;
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
     * @throws IllegalArgumentException If at least one score from the matches is incorrectly formatted
     *                                  or at least one match's opponent name is identical to the team's name.
     */
    public static Team createInstance(String name, Set<Match> matches) throws IllegalArgumentException{
        scoreInvalid = matches.parallelStream().map(Match::getScore).anyMatch(Match::isScoreInvalid);
        isPlayingAgainstItself = matches.parallelStream().map(Match::getOpponentName).anyMatch(n -> n.equals(name));
        throwExceptionMessage();
        return new Team(name, matches);
    }

    /**
     * Creates an instance of type Team and sets its matches to an empty set.
     * This method will be called by the TeamFactory class to ensure uniqueness of all Team instances.
     *
     * @param name The name of the instance.
     * @return The new Team instance.
     */
    static Team createInstance(String name) {
        return new Team(name);
    }

    /**
     * Throws an exception if either the user passed in an incorrectly formatted score
     * (the correct format is two nonnegative integers separated by a '-'),
     * or the opponentName of one Match instance equals the name of the Team instance that contains the Match instance.
     */
    private static void throwExceptionMessage() {
        if (scoreInvalid && isPlayingAgainstItself) {
            throw new IllegalArgumentException(compoundErrorMessage);
        } if (scoreInvalid) {
            throw new IllegalArgumentException(scoreInvalidMessage);
        } if (isPlayingAgainstItself) {
            throw new IllegalArgumentException(isPlayingAgainstItselfMessage);
        }
    }

    // will be set to private in production code
    static void resetStaticState() {
        scoreInvalid = false;
        isPlayingAgainstItself = false;
    }
}
