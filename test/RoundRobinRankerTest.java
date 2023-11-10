import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

public class RoundRobinRankerTest {

    @Test
    public void testGetMatchesEmpty() {
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Germany");
        assertEquals(Map.of(), team.getMatches());
    }

    @Test
    public void testGetMatchesNonEmpty() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Map.of("Japan", "1-2"));
        assertEquals(Map.of("Japan", "1-2"), germany.getMatches());
    }

    // testing the score regex

    @Test
    public void testScoreZeroDraw() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Map.of("Japan", "0-0"));
        assertEquals(Map.of("Japan", "0-0"), germany.getMatches());
    }

    @Test
    public void testZeroLoss() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Map.of("South Korea", "0-2"));
        assertEquals(Map.of("South Korea", "0-2"), germany.getMatches());
    }

    @Test
    public void testZeroWin() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Mexico", Map.of("Germany", "1-0"));
        assertEquals(Map.of("Germany", "1-0"), germany.getMatches());
    }

    @Test
    public void testBigIntegers() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Mexico", Map.of("Germany", "100-10"));
        assertEquals(Map.of("Germany", "100-10"), germany.getMatches());
    }

    @Test
    public void testInvalidScoreLeadingZero1() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Map.of("Japan", "01-2"));
        });
        assertEquals("The score must be two positive integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreLeadingZero2() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Map.of("Japan", "1-02"));
        });
        assertEquals("The score must be two positive integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNonDigits() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Map.of("Japan", "1-2g"));
        });
        assertEquals("The score must be two positive integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNoHyphen1() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Map.of("Japan", "0"));
        });
        assertEquals("The score must be two positive integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNoHyphen2() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Map.of("Japan", "021"));
        });
        assertEquals("The score must be two positive integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreSpace() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTeam("Germany", Map.of("Japan", "1- 2"));
        });
        assertEquals("The score must be two positive integers separated by '-'.", exception.getMessage());
    }

    // testing adding and removing matches

    @Test
    public void testAddMatch1() {
        // Add a match
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Map.of("Japan", "1-2"));
        germany.addMatch("Spain", "1-1");
        assertEquals(Map.of("Japan", "1-2", "Spain", "1-1"), germany.getMatches());
    }

    @Test
    public void testAddMatch2() {
        // Add a match
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Map.of("Japan", "1-2"));
        germany.addMatch("Costa Rica", "4-2");
        assertEquals(Map.of("Japan", "1-2", "Costa Rica", "4-2"), germany.getMatches());
    }

    @Test
    public void testAddMatchUpdateScore() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Map.of("Japan", "1-2"));
        germany.addMatch("Japan", "1-4");
        assertEquals(Map.of("Japan", "1-4"), germany.getMatches());
    }

    @Test
    public void testRemoveMatch() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Map.of("Japan", "1-2"));
        germany.removeMatch("Japan");
        assertEquals(Map.of(), germany.getMatches());
    }

    @Test
    public void testRemoveMatchKeyNotInMatches() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Map.of("Japan", "1-2"));
        germany.removeMatch("Spain");
        assertEquals(Map.of("Japan", "1-2"), germany.getMatches());
    }

    // testing creation of unique team instances

    @Test
    public void testFactoryCreateTeam1() {
        // The matches stay the same
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Map.of("Japan", "1-2"));
        Team germany2 = factory.createTeam("Germany", Map.of("Costa Rica", "4-2"));
        assertEquals(Map.of("Japan", "1-2"), germany.getMatches());
        assertEquals(Map.of("Japan", "1-2"), germany2.getMatches());
    }

    @Test
    public void testFactoryCreateTeam2() {
        // The matches stay the same
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        Team germany2 = factory.createTeam("Germany", Map.of("Costa Rica", "4-2"));
        assertEquals(Map.of(), germany.getMatches());
        assertEquals(Map.of(), germany2.getMatches());
    }

    @Test
    public void testMatchesMapMutability() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        Team germany2 = factory.createTeam("Germany", Map.of("Costa Rica", "4-2"));
        germany.addMatch("Japan", "1-2"); // should apply for both germany and germany2
        germany2.addMatch("Japan", "1-4");
        assertEquals(Map.of("Japan", "1-4"), germany.getMatches());
        assertEquals(Map.of("Japan", "1-4"), germany2.getMatches());
    }

    @Test
    public void testToStringNonEmpty() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Map.of("Costa Rica", "4-2"));
        assertEquals("Team{name='Germany', matches='Costa Rica'='4-2'}", germany.toString());
    }
}