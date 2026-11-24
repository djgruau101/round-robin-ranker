package org.example;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class Match {

    private final String opponentName;
    private String score;
    private final boolean isAway;
    private List selfCards;
    private List opponentCards;

    public Match(String opponentName, String score, boolean isAway) throws IllegalArgumentException {
        scoreInvalid = isScoreInvalid(score);
        throwExceptionMessage();
        this.opponentName = opponentName;
        this.score = score;
        this.isAway = isAway;
        this.selfCards = List.of();
        this.opponentCards = List.of();
    }

    public Match(String opponentName, String score) throws IllegalArgumentException {
        scoreInvalid = isScoreInvalid(score);
        throwExceptionMessage();
        this.opponentName = opponentName;
        this.score = score;
        this.isAway = false;
        this.selfCards = List.of();
        this.opponentCards = List.of();
    }

    public Match(String opponentName, String score, boolean isAway, List selfCards,
                 List opponentCards) throws IllegalArgumentException {
        scoreInvalid = isScoreInvalid(score);
        throwExceptionMessage();
        this.opponentName = opponentName;
        this.score = score;
        this.isAway = isAway;
        this.selfCards = selfCards;
        this.opponentCards = opponentCards;
    }

    public Match(String opponentName, String score, List selfCards,
                 List opponentCards) throws IllegalArgumentException {
        scoreInvalid = isScoreInvalid(score);
        throwExceptionMessage();
        this.opponentName = opponentName;
        this.score = score;
        this.isAway = false;
        this.selfCards = selfCards;
        this.opponentCards = opponentCards;
    }

    public List getSelfCards() {
        return new ArrayList<>(selfCards);
    }

    public void setSelfCards(List cards) {
        if (cards == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }
        this.selfCards = List.copyOf(cards);
    }

    public List getOpponentCards() {
        return new ArrayList<>(opponentCards);
    }

    public void setOpponentCards(List cards) {
        if (cards == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }
        this.opponentCards = List.copyOf(cards);
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
    public enum Outcome {

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
        return opponentName;
    }

    /**
     * Returns whether the team plays the match away from home during a double-legged tournament.
     *
     * @return true if the match is played away from home during a double-legged tournament,
     *         false if the match is played at home or if the tournament is single-legged.
     */
    public boolean isAway() {
        return this.isAway;
    }

    public String getScore() {
        return score;
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
        return Integer.parseInt(score.split("-")[0]);
    }

    /**
     * Returns the number of goals conceded by the team in this match.
     *
     * @return Number of goals conceded.
     */
    public int getGoalsConceded() {
        return Integer.parseInt(score.split("-")[1]);
    }

    public String getReversedScore() {
        return getGoalsConceded() +"-"+ getGoalsScored();
    }

    /**
     * Returns the team's outcome for the match depending
     * on the number of goals it scored and conceded.
     *
     * @return An enum that represents the result of the match.
     */
    public Outcome getOutcome() {
        if (getGoalsScored() < getGoalsConceded()) {
            return Outcome.LOSS;
        } else if (getGoalsScored() == getGoalsConceded()) {
            return Outcome.DRAW;
        }
        return Outcome.WIN;
    }

    /**
     * Takes a string representing the score of a match and returns whether its format is incorrect.
     * A correctly formatted score consists of two non-negative integers separated by a '-'.
     * Examples of correctly formatted scores: "0-0", "0-2", "1-0", "2-3", "10-20"
     * Examples of incorrectly formatted scores: "02-1", "3-00", "2- 1", "5s-3"
     *
     * @param score The score of a match played by the team.
     * @return true if the score is incorrectly formatted, false otherwise.
     */
    public static boolean isScoreInvalid(String score) {
        String pattern = "^(0|[1-9]\\d*)-(0|[1-9]\\d*)$"; // non-negative integer, hyphen, non-negative integer
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(score);
        return !matcher.matches();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Match otherMatch = (Match) obj;
        return opponentName.equals(otherMatch.opponentName) &&
                score.equals(otherMatch.score) && (isAway == otherMatch.isAway) &&
                selfCards.equals(otherMatch.selfCards) && opponentCards.equals(otherMatch.opponentCards);
    }

    public String toString() {
        return String.format("Match{opponentName=%s, score=%s, isAway=%b, selfCards=%s, opponentCards=%s}",
                opponentName, score, isAway, selfCards, opponentCards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opponentName, score);
    }

    private static boolean scoreInvalid = false;
    private static boolean isPlayingAgainstItself = false;
    private static final String scoreInvalidMessage = "The score must be two non-negative integers separated by '-'.";
    private static final String isPlayingAgainstItselfMessage =
            "All opponents' names should be different from the team's name.";
    private static final String compoundErrorMessage =
            scoreInvalidMessage.substring(0, scoreInvalidMessage.length() - 1) +
                    ", and " + isPlayingAgainstItselfMessage.toLowerCase();

    static void throwExceptionMessage() {
        if (scoreInvalid && isPlayingAgainstItself) {
            throw new IllegalArgumentException(compoundErrorMessage);
        } if (scoreInvalid) {
            throw new IllegalArgumentException(scoreInvalidMessage);
        } if (isPlayingAgainstItself) {
            throw new IllegalArgumentException(isPlayingAgainstItselfMessage);
        }
    }
}
