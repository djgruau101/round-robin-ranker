import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchTest {

    @Test
    public void testConstructorFull() {
        Team.Match match = new Team.Match("Liverpool", "1-2", true);
        assertEquals("Liverpool", match.getOpponentName());
        assertEquals("1-2", match.getScore());
        assertTrue(match.isAway());
    }

    @Test
    public void testConstructorNotAway() {
        Team.Match match = new Team.Match("Japan", "3-2");
        assertEquals("Japan", match.getOpponentName());
        assertEquals("3-2", match.getScore());
        assertFalse(match.isAway());
    }

    @Test
    public void testConstructorInvalidScoreExtraChars() throws IllegalArgumentException {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Team.Match("Japan", "1 -2"));
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testConstructorInvalidScoreNoHyphen() throws IllegalArgumentException {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Team.Match("Japan", "12"));
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testConstructorInvalidScoreOneInteger() throws IllegalArgumentException {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Team.Match("Japan", "1-"));
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testConstructorCards() {
        Team.Match match = new Team.Match("Japan", "3-2",
                List.of(FIFAWorldCupGroup.Card.YELLOW), List.of(FIFAWorldCupGroup.Card.DIRECT_RED));
        assertEquals(List.of(FIFAWorldCupGroup.Card.YELLOW), match.getSelfCards());
        assertEquals(List.of(FIFAWorldCupGroup.Card.DIRECT_RED), match.getOpponentCards());
    }

    @Test
    public void testSetScore() {
        Team.Match match = new Team.Match("Japan", "3-2");
        match.setScore("1-2");
        assertEquals("1-2", match.getScore());
    }

    @Test
    public void testSetScoreInvalid() throws IllegalArgumentException {
        Team.Match match = new Team.Match("Japan", "3-2");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> match.setScore("-"));
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testGetGoalsScored() {
        Team.Match match = new Team.Match("Japan", "3-2");
        match.setScore("1-2");
        assertEquals(1, match.getGoalsScored());
    }

    @Test
    public void testGetGoalsConceded() {
        Team.Match match = new Team.Match("Japan", "1-2");
        assertEquals(2, match.getGoalsConceded());
    }

    @Test
    public void testGetReversedScore() {
        Team.Match match = new Team.Match("Japan", "1-2");
        assertEquals("2-1", match.getReversedScore());
    }

    @Test
    public void testGetReversedScoreDraw() {
        Team.Match match = new Team.Match("Spain", "1-1");
        assertEquals("1-1", match.getReversedScore());
    }

    @Test
    public void testGetOutcomeWin() {
        Team.Match match = new Team.Match("Japan", "3-2");
        assertEquals(3, match.getOutcome().getPoints());
    }

    @Test
    public void testGetOutcomeDraw() {
        Team.Match match = new Team.Match("Spain", "1-1");
        assertEquals(1, match.getOutcome().getPoints());
    }

    @Test
    public void testGetOutcomeLoss() {
        Team.Match match = new Team.Match("Japan", "1-2");
        assertEquals(0, match.getOutcome().getPoints());
    }

    @Test
    public void testMatchEquality() {
        Team.Match match1 = new Team.Match("Japan", "1-2");
        Team.Match match2 = new Team.Match("Japan", "3-2", false);
        match2.setScore("1-2");
        assertEquals(match2, match1);
    }

    @Test
    public void testMatchInequalityHomeAway() {
        Team.Match match1 = new Team.Match("Japan", "1-2");
        Team.Match match2 = new Team.Match("Japan", "1-2", true);
        assertNotEquals(match2, match1);
    }

    @Test
    public void testMatchInequalityDifferentScore() {
        Team.Match match1 = new Team.Match("Japan", "1-2");
        Team.Match match2 = new Team.Match("Japan", "2-2");
        assertNotEquals(match2, match1);
    }

    @Test
    public void testMatchInequalityDifferentOpponent() {
        Team.Match match1 = new Team.Match("Japan", "1-2");
        Team.Match match2 = new Team.Match("Mexico", "1-2");
        assertNotEquals(match2, match1);
    }

    @Test
    public void testMatchToString() {
        Team.Match match = new Team.Match("Japan", "1-2");
        assertNotEquals("Match{name=Japan, score=1-2}", match.toString());
    }
}
