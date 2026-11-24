import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The Team class represents a football team competing in a round-robin tournament,
 * maintains a record of matches it played against other teams and stores the
 * amount of points deducted from the team due to policy violations.
 *
 * Attributes:
 * name: the name of the team.
 * matches: a set of matches, each of them specifying a rival team,
 *          the score and whether it is a home or away match.
 * deductedPoints: the amount of points deducted from the team
 *                 due to policy violations.
 *
 * Usage:
 * The Team class provides functionality for adding and removing match data
 * by creating instances of the inner Match class.
 * It also calculates the amount of points the team has received (+3 per win, +1 per draw, +0 per loss)
 * and its goal difference (goals scored - goals conceded),
 * which will be used to rank the team with its rivals.
 * Other data such as the number of matches played, number of wins, number of draws, number of losses,
 * goals scored, goals conceded, away goals scored and goal difference will also be displayed in a table.
 *
 * This class also contains two public overloaded static methods called createInstance, which will be
 * called by the TeamFactory class to ensure that all Team instances are unique.
 * They are made public only for the TeamFactory class to be able to use them.
 * The first method creates an instance given a name and a set of matches,
 * while the second method creates an instance given a name and sets its matches to an empty set.
 *
 * @author Daniel Luo
 */
public class Team implements Cloneable {

    private final String name;
    private Set<Match> matches = new HashSet<>();
    private int deductedPoints;

    // The following variables are used for displaying error messages
    private static boolean scoreInvalid = false;
    private static boolean isPlayingAgainstItself = false;
    private static final String scoreInvalidMessage = "The score must be two non-negative integers separated by '-'.";
    private static final String isPlayingAgainstItselfMessage =
            "All opponents' names should be different from the team's name.";
    private static final String compoundErrorMessage =
            scoreInvalidMessage.substring(0, scoreInvalidMessage.length() - 1) +
                    ", and " + isPlayingAgainstItselfMessage.toLowerCase();

    private Team(String name, Set<Match> matches, int deductedPoints) {
        this.name = name;
        this.matches.addAll(matches);
        setDeductedPoints(deductedPoints);
    }

    private Team(String name, Set<Match> matches) { this(name, matches, 0); }

    private Team(String name) { this(name, new HashSet<>(), 0); }

    private Team(String name, int deductedPoints) { this(name, new HashSet<>(), deductedPoints); }

    private Team(Team other) {
        this.name = other.name;
        this.matches = new HashSet<>();
        this.matches.addAll(other.matches);
        this.deductedPoints = other.deductedPoints;
    }

    /**
     * Adds a match played by the team or updates the score of an existing match.
     *
     * @param match A football match.
     * @throws IllegalArgumentException If the match's score is not expressed in the following format:
     *                                  two non-negative integers separated by a '-',
     *                                  or 'opponentName' is equal to the team's name.
     */
    public void addMatch(Match match) throws IllegalArgumentException {
        scoreInvalid = Match.isScoreInvalid(match.getScore());
        isPlayingAgainstItself = match.getOpponentName().equals(this.getName());
        throwExceptionMessage();
        if (matches.parallelStream().anyMatch(
                m -> m.getOpponentName().equals(match.getOpponentName()) && m.isAway() == match.isAway())) {
            matches.stream()
                    .filter(m -> m.getOpponentName().equals(match.getOpponentName()) && m.isAway() == match.isAway())
                    .forEach(m -> {
                        m.setScore(match.getScore());
                        m.setSelfCards(match.getSelfCards());
                        m.setOpponentCards(match.getOpponentCards());
                    }); // update score and cards of existing match
        } else {
            matches.add(new Match(match.getOpponentName(), match.getScore(),
                    match.isAway(), match.getSelfCards(), match.getOpponentCards()));
        }
    }

    /**
     * Constructs a Match instance, adds it in the team's matches
     * or updates the score of one of the team's existing matches.
     *
     * @param opponentName The name of a team that the team has played against.
     * @param score The score of the match against said team.
     *              It consists of two non-negative integers separated by a '-'.
     *              Its format is "goalsByThisTeam-goalsByRivalTeam".
     * @param isAway Whether the match is an away match.
     *               It will always be false if the tournament the competes in is single-legged.
     * @throws IllegalArgumentException If 'score' is not in the format indicated above
     *                                  or 'opponentName' is equal to the team's name.
     */
    public void addMatch(String opponentName, String score, boolean isAway) throws IllegalArgumentException {
        scoreInvalid = Match.isScoreInvalid(score);
        isPlayingAgainstItself = opponentName.equals(name);
        throwExceptionMessage();
        addMatch(new Match(opponentName, score, isAway));
    }

    /**
     * Constructs a Match instance, adds it in the team's matches
     * or updates the score of one of the team's existing matches.
     *
     * @param opponentName The name of a team that the team has played against.
     * @param score The score of the match against said team.
     *              It consists of two non-negative integers separated by a '-'.
     *              Its format is "goalsByThisTeam-goalsByRivalTeam".
     * @param isAway Whether the match is an away match.
     *               It will always be false if the tournament the competes in is single-legged.
     * @param selfCards The list of penalty cards received by the team during the match.
     * @param opponentCards The list of penalty cards received by the opponent during the match.
     * @throws IllegalArgumentException If 'score' is not in the format indicated above
     *                                  or 'opponentName' is equal to the team's name.
     */
    public void addMatch(String opponentName, String score,
                         boolean isAway, List<Group.CardEnum> selfCards,
                         List<Group.CardEnum> opponentCards) throws IllegalArgumentException {
        scoreInvalid = Match.isScoreInvalid(score);
        isPlayingAgainstItself = opponentName.equals(name);
        throwExceptionMessage();
        addMatch(new Match(opponentName, score, isAway, selfCards, opponentCards));
    }

    /**
     * Adds matches played by the team and updates the score of existing matches.
     *
     * @param matches The matches that the team has played.
     * @throws IllegalArgumentException If at least one score is not in the correct format:
     *                                  two non-negative integers separated by a '-',
     *                                  or at least one match's opponent's name is equal to the team's name.
     */
    public void addMatches(Match... matches) throws IllegalArgumentException {
        scoreInvalid = Arrays.stream(matches).map(Match::getScore).anyMatch(Match::isScoreInvalid);
        isPlayingAgainstItself = Arrays.stream(matches).map(Match::getOpponentName)
                .anyMatch(n -> n.equals(name));
        throwExceptionMessage();
        Arrays.stream(matches).forEach(this::addMatch);
    }

    /**
     * Removes the match in which the team played against the given opponent team.
     * It does nothing if the team has played no match against the given team.
     *
     * @param opponentName The name of a team that the team played against.
     * @param isAway Whether the match is an away match.
     *               It will always be false if the tournament the team competes in is single-legged.
     */
    public void removeMatchByOpponentName(String opponentName, boolean isAway) {
        matches.removeIf(match -> match.getOpponentName().equals(opponentName) && match.isAway() == isAway);
    }

    /**
     * Sets the number of points to deduct from the team due to policy violations.
     *
     * @param points The number of points to deduct from a team.
     * @throws IllegalArgumentException If the number of points is negative.
     */
    public void setDeductedPoints(int points) {
        if (points >= 0) {
            this.deductedPoints = points;
        } else {
            throw new IllegalArgumentException("Deducted points must be non-negative.");
        }
    }

    /**
     * Adjusts the number of deducted points for the team.
     *
     * @param points The number of points to adjust the total number of deducted points.
     *               Adds to the total if non-negative, subtracts if negative.
     * @throws IllegalArgumentException If the new total number of deducted points becomes negative.
     */
    public void adjustPenaltyPoints(int points) { setDeductedPoints(this.deductedPoints + points); }

    /**
     * Returns the set of matches that the team has played.
     *
     * @return The set of matches that the team has played.
     */
    public Set<Match> getMatches() {
        return Collections.unmodifiableSet(matches);
    }

    /**
     * Returns the number of matches that the team has won.
     *
     * @return Number of won matches.
     */
    public int getNumberWins() {
        return (int) this.getMatches().parallelStream().map(Match::getOutcome)
                .filter(o -> o.equals(Match.Outcome.WIN)).count();
    }

    /**
     * Returns the number of matches that the team has drawn.
     *
     * @return Number of drawn matches.
     */
    public int getNumberDraws() {
        return (int) this.getMatches().parallelStream().map(Match::getOutcome)
                .filter(o -> o.equals(Match.Outcome.DRAW)).count();
    }

    /**
     * Returns the number of matches that the team has lost.
     *
     * @return Number of lost matches.
     */
    public int getNumberLosses() {
        return (int) this.getMatches().parallelStream().map(Match::getOutcome)
                .filter(o -> o.equals(Match.Outcome.LOSS)).count();
    }

    /**
     * Returns the team's number of points in a round-robin tournament based on its matches' outcome,
     * subtracted by the number of points to deduct from the team for policy violations.
     * A loss is 0 point, a draw is 1 point and a win is 3 points.
     * For double-legged tournaments, this only applies to matches played at home.
     *
     * @return The number of points the team has accumulated.
     */
    public int getPoints() {
        return this.getMatches().parallelStream().map(match -> match.getOutcome().getPoints())
                .mapToInt(Integer::intValue).sum() - this.deductedPoints;
    }

    /**
     * Calculates the total number of goals scored by the team during the tournament, also known as 'goals for' (GF).
     *
     * @return The number of goals scored.
     */
    public int getGoalsFor() {
        return this.getMatches().parallelStream().map(Match::getGoalsScored).mapToInt(Integer::intValue).sum();
    }

    /**
     * Calculates the total number of goals conceded by the team during the tournament,
     * also known as 'goals against' (GA).
     *
     * @return The number of goals conceded.
     */
    public int getGoalsAgainst() {
        return this.getMatches().parallelStream().map(Match::getGoalsConceded).mapToInt(Integer::intValue).sum();
    }

    /**
     * Calculates the team's goal difference (GD), which is goalsFor - goalsAgainst.
     *
     * @return The goal difference.
     */
    public int getGoalDifference() {
        return getGoalsFor() - getGoalsAgainst();
    }

    /**
     * Returns a string representation of the team's goal difference.
     * It adds a '+' before the number if it is positive.
     *
     * @return The string representation of goal difference.
     */
    public String getGoalDifferenceToString() {
        if (getGoalDifference() > 0) {
            return "+" + getGoalDifference();
        }
        return Integer.toString(getGoalDifference());
    }

    /**
     * Returns the number of matches the team has played.
     *
     * @return The number of matches played.
     */
    public int getNumberOfMatchesPlayed() {
        return this.getMatches().size();
    }

    /**
     * Returns whether some other object is "equal to" this team object.
     *
     * @return The number of matches played.
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Team otherTeam = (Team) obj;
        return name.equals(otherTeam.name) && this.getMatches().equals(otherTeam.getMatches())
                && this.deductedPoints == otherTeam.deductedPoints;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, this.getMatches());
    }

    /**
     * Returns the set of home matches that the team has played.
     *
     * @return The set of home matches that the team has played.
     */
    public Set<Match> getHomeMatches() {
        return matches.parallelStream().filter(m -> !m.isAway()).collect(Collectors.toSet());
    }

    /**
     * Returns the set of away matches that the team has played, if the tournament is double-legged.
     *
     * @return The set of away matches that the team has played.
     */
    public Set<Match> getAwayMatches() {
        return matches.parallelStream().filter(m -> m.isAway()).collect(Collectors.toSet());
    }

    /**
     * Returns the number of goals the team has scored in away matches.
     * This will be considered in ranking teams from some tournaments like the Premier League.
     *
     * @return The number of away matches that the team has scored.
     */
    public int getAwayGoals() {
        return matches.parallelStream().filter(m -> m.isAway()).mapToInt(Match::getGoalsScored).sum();
    }

    public List<Group.CardEnum> getCards() {
        return matches.parallelStream()
                .map(match -> (List<Group.CardEnum>) match.getSelfCards())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public int getFairPlayPoints() {
        return matches.parallelStream().map(match -> (List<Group.CardEnum>) match.getSelfCards())
                .flatMap(List::stream).mapToInt(Group.CardEnum::getPenalty).sum();
    }

    /**
     * Returns the name of the team.
     *
     * @return The name of the team.
     */
    public String getName() {
        return name;
    }

    public String toString() {
        String initialString = "Team{name=%s, matches={";
        String result = matches.parallelStream().map(Match::toString)
                .collect(Collectors.joining(", ", initialString, "}}"));
        return String.format(result, name);
    }

    @Override
    public Team clone() {
        return new Team(this);
    }

    /**
     * The Match class represents a football match that the team plays.
     *
     * Attributes:
     * opponentName: the name of an opponent team.
     * score: the result of the match.
     * isAway: if the match is an away match in a double-legged tournament.
     * selfCards: the list of penalty cards received by the team.
     * opponentCards: the list of penalty cards received by the opponent.
     *
     * Each score is a string represented in the format "goalsByThisTeam-goalsByOpponent".
     * For example, a match with arguments "Real Madrid" and "3-1" represents a match
     * played against Real Madrid, where the team scored 3 goals and Real Madrid scored 1 goal.
     * Penalty cards will only be considered for tie-breaking purposes.
     */
    static class Match extends org.example.Match {

        public Match(String opponentName, String score, boolean isAway) throws IllegalArgumentException {
            super(opponentName, score, isAway);
        }

        public Match(String opponentName, String score) throws IllegalArgumentException {
            super(opponentName, score);
        }

        public Match(String opponentName, String score, boolean isAway, List<Group.CardEnum> selfCards,
                     List<Group.CardEnum> opponentCards) throws IllegalArgumentException {
            super(opponentName, score, isAway, selfCards, opponentCards);
        }

        public Match(String opponentName, String score, List<Group.CardEnum> selfCards,
                     List<Group.CardEnum> opponentCards) throws IllegalArgumentException {
            super(opponentName, score, selfCards, opponentCards);
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
    static Team createInstance(String name, Set<Match> matches) throws IllegalArgumentException{
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
     * Creates an instance of type Team and sets its matches according to the 'matches' input.
     * This method will be called by the TeamFactory class to ensure uniqueness of all Team instances.
     *
     * @param name The name of the instance.
     * @param matches The set of matches that the team has played.
     * @return A new Team instance with its name and matches correctly set.
     * @throws IllegalArgumentException If at least one score from the matches is incorrectly formatted
     *                                  or at least one match's opponent name is identical to the team's name.
     */
    static Team createInstance(String name, Set<Match> matches, int pointsDeducted) throws IllegalArgumentException{
        scoreInvalid = matches.parallelStream().map(Match::getScore).anyMatch(Match::isScoreInvalid);
        isPlayingAgainstItself = matches.parallelStream().map(Match::getOpponentName).anyMatch(n -> n.equals(name));
        throwExceptionMessage();
        return new Team(name, matches, pointsDeducted);
    }

    /**
     * Creates an instance of type Team and sets its matches to an empty set.
     * This method will be called by the TeamFactory class to ensure uniqueness of all Team instances.
     *
     * @param name The name of the instance.
     * @return The new Team instance.
     */
    static Team createInstance(String name, int pointsDeducted) {
        return new Team(name, pointsDeducted);
    }

    /**
     * Throws an exception if either the user passed in an incorrectly formatted score
     * (the correct format is two non-negative integers separated by a '-'),
     * or the opponentName of one Match instance equals the name of the Team instance that contains the Match instance.
     */
    static void throwExceptionMessage() {
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
