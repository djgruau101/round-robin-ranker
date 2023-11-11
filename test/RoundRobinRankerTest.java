import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

public class RoundRobinRankerTest {

    @Test
    public void testGetMatchesEmpty() {
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Germany");
        assertEquals(Set.of(), team.getMatches());
    }

    @Test
    public void testGetMatchesNonEmpty() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        assertEquals(Set.of(new Team.Match("Japan", "1-2")), germany.getMatches());
    }

    // testing the score regex

    @Test
    public void testScoreZeroDraw() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "0-0")));
        assertEquals(Set.of(new Team.Match("Japan", "0-0")), germany.getMatches());
    }

    @Test
    public void testZeroLoss() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("South Korea", "0-2")));
        assertEquals(Set.of(new Team.Match("South Korea", "0-2")), germany.getMatches());
    }

    @Test
    public void testZeroWin() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Mexico", Set.of(new Team.Match("Germany", "1-0")));
        assertEquals(Set.of(new Team.Match("Germany", "1-0")), germany.getMatches());
    }

    @Test
    public void testBigIntegers() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Mexico", Set.of(new Team.Match("Germany", "100-10")));
        assertEquals(Set.of(new Team.Match("Germany", "100-10")), germany.getMatches());
    }

    @Test
    public void testInvalidScoreLeadingZero1() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Set.of(new Team.Match("Japan", "01-2")));
        });
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreLeadingZero2() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-02")));
        });
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNonDigits() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2g")));
        });
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNoHyphen1() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Set.of(new Team.Match("Japan", "0")));
        });
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNoHyphen2() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Set.of(new Team.Match("Japan", "021")));
        });
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreSpace() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1- 2")));
        });
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    // testing adding and removing matches

    @Test
    public void testAddMatch1() {
        // Add a match
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.addMatch("Spain", "1-1");
        assertEquals(Set.of(
                new Team.Match("Japan", "1-2"),
                new Team.Match("Spain", "1-1")), germany.getMatches());
    }

    @Test
    public void testAddMatch2() {
        // Add a match
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.addMatch("Costa Rica", "4-2");
        assertEquals(Set.of(
                new Team.Match("Japan", "1-2"),
                new Team.Match("Costa Rica", "4-2")), germany.getMatches());
    }

    @Test
    public void testAddMatchUpdateScore() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.addMatch("Japan", "1-4");
        assertEquals(Set.of(new Team.Match("Japan", "1-4")), germany.getMatches());
    }

    @Test
    public void testRemoveMatch() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.removeMatchByOpponentName("Japan");
        assertEquals(Set.of(), germany.getMatches());
    }

    @Test
    public void testRemoveMatchKeyNotInMatches() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.removeMatchByOpponentName("Spain");
        assertEquals(Set.of(new Team.Match("Japan", "1-2")), germany.getMatches());
    }

    // testing creation of unique team instances

    @Test
    public void testFactoryCreateTeam1() {
        // The matches stay the same
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        Team germany2 = factory.createTeam("Germany", Set.of(new Team.Match("Costa Rica", "4-2")));
        assertEquals(Set.of(new Team.Match("Japan", "1-2")), germany.getMatches());
        assertEquals(Set.of(new Team.Match("Japan", "1-2")), germany2.getMatches());
    }

    @Test
    public void testFactoryCreateTeam2() {
        // The matches stay the same
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        Team germany2 = factory.createTeam("Germany", Set.of(new Team.Match("Costa Rica", "4-2")));
        assertEquals(Set.of(), germany.getMatches());
        assertEquals(Set.of(), germany2.getMatches());
    }

    @Test
    public void testMatchesSetMutability() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        Team germany2 = factory.createTeam("Germany", Set.of(new Team.Match("Costa Rica", "4-2")));
        germany.addMatch("Japan", "1-2"); // should apply for both germany and germany2
        germany2.addMatch("Japan", "1-4");
        assertEquals(Set.of(new Team.Match("Japan", "1-4")), germany.getMatches());
        assertEquals(Set.of(new Team.Match("Japan", "1-4")), germany2.getMatches());
    }
}