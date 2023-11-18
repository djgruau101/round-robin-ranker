import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

public class RoundRobinRankerTest {

    @BeforeEach
    public void setUp() {
        Team.resetStaticState();
    }

    @AfterEach
    public void tearDown() {
        Team.resetStaticState();
    }

    /**
     * Constructs a new FIFAWorldCupGroup that represents Group C from the 2022 FIFA World Cup.
     * Its teams are Argentina, Saudi Arabia, Poland and Mexico.
     *
     * @return Group C of the 2022 FIFA World Cup
     */
    FIFAWorldCupGroup groupC2022() {
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina");
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        Team poland = factory.createTeam("Poland");
        Team mexico = factory.createTeam("Mexico");
        Team[] teams = new Team[]{argentina, saudiArabia, poland, mexico};
        return new FIFAWorldCupGroup(teams);
    }

    /**
     * Constructs a new FIFAWorldCupGroup that represents the
     * actual results of Group C from the 2022 FIFA World Cup.
     * Its teams are Argentina, Saudi Arabia, Poland and Mexico.
     *
     * @return Group C of the 2022 FIFA World Cup
     */
    FIFAWorldCupGroup groupC2022Complete() {
        FIFAWorldCupGroup groupC2022 = groupC2022();
        groupC2022.addMatch("Argentina", "Saudi Arabia", "1-2");
        groupC2022.addMatch("Poland", "Mexico", "0-0");
        groupC2022.addMatch("Poland", "Saudi Arabia", "2-0");
        groupC2022.addMatch("Argentina", "Mexico", "2-0");
        groupC2022.addMatch("Poland", "Argentina", "0-2");
        groupC2022.addMatch("Saudi Arabia", "Mexico", "1-2");
        return groupC2022;
    }

    @Test
    public void testGetMatchesEmpty() {
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Germany");
        assertEquals(Set.of(), team.getHomeMatches());
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
        Team mexico = factory.createTeam("Mexico", Set.of(new Team.Match("Germany", "1-0")));
        assertEquals(Set.of(new Team.Match("Germany", "1-0")), mexico.getMatches());
    }

    @Test
    public void testBigIntegers() {
        TeamFactory factory = new TeamFactory();
        Team mexico = factory.createTeam("Mexico", Set.of(new Team.Match("Germany", "100-10")));
        assertEquals(Set.of(new Team.Match("Germany", "100-10")), mexico.getMatches());
    }

    @Test
    public void testInvalidScoreLeadingZero1() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "01-2"))));
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreLeadingZero2() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-02"))));
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNonDigits() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2g"))));
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNoHyphen1() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "0"))));
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNoHyphen2() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "021"))));
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreSpace() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1- 2"))));
        assertEquals("The score must be two nonnegative integers separated by '-'.", exception.getMessage());
    }

    // testing adding and removing matches for single-legged tournaments

    @Test
    public void testAddMatch1() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.addMatch("Spain", "1-1", false);
        assertEquals(Set.of(
                new Team.Match("Japan", "1-2"),
                new Team.Match("Spain", "1-1")), germany.getMatches());
    }

    @Test
    public void testAddMatch2() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.addMatch(new Team.Match("Costa Rica", "4-2"));
        assertEquals(Set.of(
                new Team.Match("Japan", "1-2"),
                new Team.Match("Costa Rica", "4-2")), germany.getMatches());
    }

    @Test
    public void testAddMatchUpdateScore1() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.addMatch("Japan", "1-4", false);
        assertEquals(Set.of(new Team.Match("Japan", "1-4")), germany.getMatches());
    }

    @Test
    public void testAddMatchUpdateScore2() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.addMatch(new Team.Match("Japan", "1-4"));
        assertEquals(Set.of(new Team.Match("Japan", "1-4")), germany.getMatches());
    }

    @Test
    public void testAddMultipleMatches() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.addMatches(
                new Team.Match("Japan", "1-4"),
                new Team.Match("Spain", "1-1"),
                new Team.Match("Costa Rica", "4-2")
                );
        assertEquals(Set.of(new Team.Match("Japan", "1-4"),
                new Team.Match("Spain", "1-1"),
                new Team.Match("Costa Rica", "4-2")), germany.getMatches());
    }

    @Test
    public void testRemoveMatch() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.removeMatchByOpponentName("Japan", false);
        assertEquals(Set.of(), germany.getMatches());
    }

    @Test
    public void testRemoveMatchKeyNotInMatches() {
        TeamFactory factory = new TeamFactory();
        // Update the results of an existing match
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2")));
        germany.removeMatchByOpponentName("Spain", false);
        assertEquals(Set.of(new Team.Match("Japan", "1-2")), germany.getMatches());
    }

    // test adding and removing matches from groups for single-legged tournaments

    @Test
    public void testGroupRemoveMatchOneLeg() {
        Group groupC2022 = groupC2022Complete();
        groupC2022.removeMatch("Argentina", "Poland");
        groupC2022.removeMatch("Mexico", "Saudi Arabia");
        assertEquals(Set.of(new Team.Match("Saudi Arabia", "1-2"),
                new Team.Match("Mexico", "2-0")),
                groupC2022.getTeamByName("Argentina").getMatches());
        assertEquals(Set.of(new Team.Match("Poland", "0-2"),
                new Team.Match("Argentina", "2-1")),
                groupC2022.getTeamByName("Saudi Arabia").getMatches());
        assertEquals(Set.of(new Team.Match("Mexico", "0-0"),
                new Team.Match("Saudi Arabia", "2-0")),
                groupC2022.getTeamByName("Poland").getMatches());
        assertEquals(Set.of(new Team.Match("Poland", "0-0"),
                new Team.Match("Argentina", "0-2")),
                groupC2022.getTeamByName("Mexico").getMatches());
    }

    @Test
    public void testGroupRemoveMatchTwiceOneLeg() {
        Group groupC2022 = groupC2022Complete();
        groupC2022.removeMatch("Argentina", "Poland");
        assertEquals(Set.of(new Team.Match("Saudi Arabia", "1-2"),
                new Team.Match("Mexico", "2-0")),
                groupC2022.getTeamByName("Argentina").getMatches());
        assertEquals(Set.of(new Team.Match("Mexico", "0-0"),
                new Team.Match("Saudi Arabia", "2-0")),
                groupC2022.getTeamByName("Poland").getMatches());
        groupC2022.removeMatch("Argentina", "Poland");
        assertEquals(Set.of(new Team.Match("Saudi Arabia", "1-2"),
                new Team.Match("Mexico", "2-0")),
                groupC2022.getTeamByName("Argentina").getMatches());
        assertEquals(Set.of(new Team.Match("Mexico", "0-0"),
                new Team.Match("Saudi Arabia", "2-0")),
                groupC2022.getTeamByName("Poland").getMatches());
    }

    @Test
    public void testGroupAddMatchOneLeg() {
        Group groupC2022 = groupC2022();
        groupC2022.addMatch("Argentina", "Saudi Arabia", "1-2");
        assertEquals(Set.of(new Team.Match("Saudi Arabia", "1-2")),
                groupC2022.getTeamByName("Argentina").getMatches());
        assertEquals(Set.of(new Team.Match("Argentina", "2-1")),
                groupC2022.getTeamByName("Saudi Arabia").getMatches());
    }

    // testing adding and removing matches for double-legged tournaments

    @Test
    public void testGroupAddMatchTwoLegs() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team[] teams = new Team[]{manCity, chelsea, arsenal};
        PremierLeague pl = new PremierLeague(teams);
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals(Set.of(
                new Team.Match("Chelsea", "2-1", false),
                new Team.Match("Chelsea", "4-2", true)), manCity.getMatches());
        assertEquals(Set.of(
                new Team.Match("Manchester City", "1-2", true),
                new Team.Match("Manchester City", "2-4", false),
                new Team.Match("Arsenal", "4-1", false)), chelsea.getMatches());
        assertFalse(pl.isComplete());
    }

    @Test
    public void testGroupUpdateMatchTwoLegs() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team[] teams = new Team[]{manCity, chelsea, arsenal};
        PremierLeague pl = new PremierLeague(teams);
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        pl.addMatch("Chelsea", "Manchester City", "4-2");
        assertEquals(Set.of(
                new Team.Match("Chelsea", "2-1", false),
                new Team.Match("Chelsea", "2-4", true)), manCity.getMatches());
        assertEquals(Set.of(
                new Team.Match("Manchester City", "1-2", true),
                new Team.Match("Manchester City", "4-2", false),
                new Team.Match("Arsenal", "4-1", false)), chelsea.getMatches());
        assertFalse(pl.isComplete());
    }

    @Test
    public void testGroupRemoveMatchTwoLegs() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team[] teams = new Team[]{manCity, chelsea, arsenal};
        PremierLeague pl = new PremierLeague(teams);
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "3-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        pl.removeMatch("Chelsea", "Manchester City");
        assertEquals(Set.of(new Team.Match("Chelsea", "2-1", false)), manCity.getMatches());
        assertEquals(Set.of(new Team.Match("Arsenal", "4-1", false),
                new Team.Match("Manchester City", "1-2", true)), chelsea.getMatches());
        assertFalse(pl.isComplete());
    }

    @Test
    public void testGroupRemoveMatchTwiceTwoLegs() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team liverpool = factory.createTeam("Liverpool");
        Team[] teams = new Team[]{manCity, chelsea, arsenal, liverpool};
        PremierLeague pl = new PremierLeague(teams);
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "3-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        pl.removeMatch("Chelsea", "Manchester City");
        assertEquals(Set.of(new Team.Match("Manchester City", "1-2", true),
                new Team.Match("Arsenal", "4-1", false)), chelsea.getMatches());
        pl.removeMatch("Chelsea", "Liverpool");
        assertEquals(Set.of(new Team.Match("Manchester City", "1-2", true),
                new Team.Match("Arsenal", "4-1", false)), chelsea.getMatches());
    }

    // testing exception handling for adding matches

    @Test
    public void testGroupAddMatchTeamNameNotInGroup() {
        Group groupC2022 = groupC2022();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.addMatch("Argentina", "Australia", "2-1"));
        assertEquals("Team names must be among the ones in the group.", exception.getMessage());
    }

    @Test
    public void testGroupRemoveMatchHomeEqualsAwayError() {
        Group groupC2022 = groupC2022Complete();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.removeMatch("Argentina", "Argentina"));
        assertEquals("The names of the two teams facing each other cannot be the same.", exception.getMessage());
    }

    @Test
    public void testGroupRemoveMatchTeamNotInGroupError() {
        Group groupC2022 = groupC2022Complete();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.removeMatch("Argentina", "Australia"));
        assertEquals("Team names must be among the ones in the group.", exception.getMessage());
    }

    @Test
    public void testGroupAddMatchPlayingAgainstItself() {
        Group groupC2022 = groupC2022();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.addMatch("Argentina", "Argentina", "1-2"));
        assertEquals("The names of the two teams facing each other cannot be the same.", exception.getMessage());
    }

    // testing creation of unique team instances

    @Test
    public void testFactoryCreateTeam1() {
        // The matches stay the same
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany",
                Set.of(new Team.Match("Japan", "1-2")));
        Team germany2 = factory.createTeam("Germany",
                Set.of(new Team.Match("Costa Rica", "4-2")));
        assertEquals(Set.of(new Team.Match("Japan", "1-2")), germany.getMatches());
        assertEquals(Set.of(new Team.Match("Japan", "1-2")), germany2.getMatches());
    }

    @Test
    public void testFactoryCreateTeam2() {
        // The matches stay the same
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        Team germany2 = factory.createTeam("Germany",
                Set.of(new Team.Match("Costa Rica", "4-2")));
        assertEquals(Set.of(), germany.getMatches());
        assertEquals(Set.of(), germany2.getMatches());
    }

    @Test
    public void testMatchesSetMutability() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        Team germany2 = factory.createTeam("Germany",
                Set.of(new Team.Match("Costa Rica", "4-2")));
        germany.addMatch("Japan", "1-2", false); // should apply for both germany and germany2
        germany2.addMatch("Japan", "1-4", false);
        assertEquals(Set.of(new Team.Match("Japan", "1-4")), germany.getMatches());
        assertEquals(Set.of(new Team.Match("Japan", "1-4")), germany2.getMatches());
    }

    // testing calculation of points

    @Test
    public void testGetPointsAndAddMatch() {
        // initialize instance with no matches, then add 3
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        germany.addMatch("Japan", "1-2", false);
        germany.addMatch("Spain", "1-1", false);
        germany.addMatch("Costa Rica", "4-2", false);
        assertEquals(4, germany.getPoints());
    }

    @Test
    public void testGetPointsAndMatchesConstructor() {
        // initialize instance with 3 matches
        TeamFactory factory = new TeamFactory();
        Team croatia = factory.createTeam("Croatia", Set.of(
                new Team.Match("Morocco", "0-0"),
                new Team.Match("Canada", "4-1"),
                new Team.Match("Belgium", "0-0")
        ));
        assertEquals(5, croatia.getPoints());
    }

    @Test
    public void testGetPointsMixedConstructor() {
        // initialize instance with 1 match, then add 2 matches
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina",
                Set.of(new Team.Match("Saudi Arabia", "1-2")));
        argentina.addMatch("Mexico", "2-0", false);
        argentina.addMatch("Poland", "2-0", false);
        assertEquals(6, argentina.getPoints());
    }

    @Test
    public void testGetPointsTwoLegged() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team[] teams = new Team[]{manCity, chelsea, arsenal};
        PremierLeague pl = new PremierLeague(teams);
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals(4, chelsea.getPoints());
    }

    // testing calculations of number of wins, losses, draws, GF, GA, GD

    @Test
    public void testNumberMatchesWon() {
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina");
        argentina.addMatches(
                new Team.Match("Saudi Arabia", "1-2"),
                new Team.Match("Mexico", "2-0"),
                new Team.Match("Poland", "2-0"));
        assertEquals(2, argentina.getNumberWins());
    }

    @Test
    public void testNumberMatchesDrawn() {
        TeamFactory factory = new TeamFactory();
        Team poland = factory.createTeam("Poland");
        poland.addMatches(
                new Team.Match("Saudi Arabia", "2-0"),
                new Team.Match("Mexico", "0-0"),
                new Team.Match("Argentina", "0-2"));
        assertEquals(1, poland.getNumberDraws());
    }

    @Test
    public void testNumberMatchesLost() {
        TeamFactory factory = new TeamFactory();
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        saudiArabia.addMatches(
                new Team.Match("Argentina", "2-1"),
                new Team.Match("Mexico", "1-2"),
                new Team.Match("Poland", "0-2"));
        assertEquals(2, saudiArabia.getNumberLosses());
    }

    @Test
    public void testGDIntegerPositive() {
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina");
        argentina.addMatches(
                new Team.Match("Saudi Arabia", "1-2"),
                new Team.Match("Mexico", "2-0"),
                new Team.Match("Poland", "2-0"));
        assertEquals(3, argentina.getGoalDifference());
    }

    @Test
    public void testGDIntegerZero() {
        TeamFactory factory = new TeamFactory();
        Team poland = factory.createTeam("Poland");
        poland.addMatches(
                new Team.Match("Saudi Arabia", "2-0"),
                new Team.Match("Mexico", "0-0"),
                new Team.Match("Argentina", "0-2"));
        assertEquals(0, poland.getGoalDifference());
    }

    @Test
    public void testGDIntegerNegative() {
        TeamFactory factory = new TeamFactory();
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        saudiArabia.addMatches(
                new Team.Match("Argentina", "2-1"),
                new Team.Match("Mexico", "1-2"),
                new Team.Match("Poland", "0-2"));
        assertEquals(-2, saudiArabia.getGoalDifference());
    }

    @Test
    public void testGDStringPositive() {
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina");
        argentina.addMatches(
                new Team.Match("Saudi Arabia", "1-2"),
                new Team.Match("Mexico", "2-0"),
                new Team.Match("Poland", "2-0"));
        assertEquals("+3", argentina.getGoalDifferenceToString());
    }

    @Test
    public void testGDStringZero() {
        TeamFactory factory = new TeamFactory();
        Team poland = factory.createTeam("Poland");
        poland.addMatches(
                new Team.Match("Saudi Arabia", "2-0"),
                new Team.Match("Mexico", "0-0"),
                new Team.Match("Argentina", "0-2"));
        assertEquals("0", poland.getGoalDifferenceToString());
    }

    @Test
    public void testGDStringNegative() {
        TeamFactory factory = new TeamFactory();
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        saudiArabia.addMatches(
                new Team.Match("Argentina", "2-1"),
                new Team.Match("Mexico", "1-2"),
                new Team.Match("Poland", "0-2"));
        assertEquals("-2", saudiArabia.getGoalDifferenceToString());
    }

    @Test
    public void testGDTwoLegged() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team[] teams = new Team[]{manCity, chelsea, arsenal};
        PremierLeague pl = new PremierLeague(teams);
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals(0, chelsea.getGoalDifference());
    }

    @Test
    public void testGF() {
        TeamFactory factory = new TeamFactory();
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        saudiArabia.addMatches(
                new Team.Match("Argentina", "2-1"),
                new Team.Match("Mexico", "1-2"),
                new Team.Match("Poland", "0-2"));
        assertEquals(3, saudiArabia.getGoalsFor());
    }

    @Test
    public void testGFTwoLegged() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team[] teams = new Team[]{manCity, chelsea, arsenal};
        PremierLeague pl = new PremierLeague(teams);
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals(7, chelsea.getGoalsFor());
    }

    @Test
    public void testGA() {
        TeamFactory factory = new TeamFactory();
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        saudiArabia.addMatches(
                new Team.Match("Argentina", "2-1"),
                new Team.Match("Mexico", "1-2"),
                new Team.Match("Poland", "0-2"));
        assertEquals(5, saudiArabia.getGoalsAgainst());
    }

    @Test
    public void testNumberOfMatchesPlayed() {
        TeamFactory factory = new TeamFactory();
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        saudiArabia.addMatches(
                new Team.Match("Argentina", "2-1"),
                new Team.Match("Mexico", "1-2"),
                new Team.Match("Poland", "0-2"));
        assertEquals(3, saudiArabia.getNumberOfMatchesPlayed());
    }

    // testing instantiation of team with match containing the team's name

    @Test
    public void testTeamNameInMatchesInstantiation() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Argentina", Set.of(new Team.Match("Argentina", "1-2"))));
        assertEquals("All opponents' names should be different from the team's name.", exception.getMessage());
    }

    @Test
    public void testTeamNameInMatchesAddMatch1() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> germany.addMatch(new Team.Match("Germany", "2-3")));
        assertEquals("All opponents' names should be different from the team's name.", exception.getMessage());
    }

    @Test
    public void testTeamNameInMatchesAddMatch2() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> germany.addMatch("Germany", "2-3", false));
        assertEquals("All opponents' names should be different from the team's name.", exception.getMessage());
    }

    @Test
    public void testMultipleErrors() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> germany.addMatch("Germany", "2- 3", false));
        assertEquals("The score must be two nonnegative integers separated by '-', " +
                "and all opponents' names should be different from the team's name.", exception.getMessage());
    }

    // testing equality between teams

    @Test
    public void testTeamEquals() {
        Team manCity1 = Team.createInstance("Manchester City");
        manCity1.addMatches(
                new Team.Match("Liverpool", "1-2"),
                new Team.Match("Manchester United", "4-4"),
                new Team.Match("Chelsea", "3-1"));
        Team manCity2 = Team.createInstance("Manchester City",
                Set.of(new Team.Match("Manchester United", "4-4")));
        manCity2.addMatch("Chelsea", "3-1", false);
        manCity2.addMatch(new Team.Match("Liverpool", "1-2"));
        assertEquals(manCity2, manCity1);
    }

    @Test
    public void testTeamNotEquals() {
        Team manCity1 = Team.createInstance("Manchester City");
        manCity1.addMatches(
                new Team.Match("Liverpool", "1-2"),
                new Team.Match("Manchester United", "4-4"),
                new Team.Match("Chelsea", "3-1"));
        Team manCity2 = Team.createInstance("Manchester City",
                Set.of(new Team.Match("Manchester United", "4-2")));
        manCity2.addMatch("Chelsea", "3-1", false);
        manCity2.addMatch(new Team.Match("Liverpool", "1-2"));
        assertNotEquals(manCity2, manCity1);
    }

    // testing specific operations for groups exceptions

    @Test
    public void testGroupAddMatchScoreInvalid() {
        Group groupC2022 = groupC2022();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.addMatch("Argentina", "Mexico", "2 -0"));
        assertEquals("The score must be two nonnegative integers separated by '-'.\n", exception.getMessage());
    }

    @Test
    public void testGroupMultipleErrors() {
        Group groupC2022 = groupC2022();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.addMatch("Argentina", "Argentina", "2 -0"));
        assertEquals("The score must be two nonnegative integers separated by '-'.\n" +
                "The names of the two teams facing each other cannot be the same.", exception.getMessage());
    }

    @Test
    public void testTooManyTeamsInGroup() {
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina");
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        Team poland = factory.createTeam("Poland");
        Team mexico = factory.createTeam("Mexico");
        Team australia = factory.createTeam("Australia");
        Team[] teams = new Team[]{argentina, saudiArabia, poland, mexico, australia};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new FIFAWorldCupGroup(teams));
        assertEquals("The number of teams must not exceed 4.", exception.getMessage());
    }

    @Test
    public void testGetTeamByNameAndEncapsulation() {
        Group groupC2022 = groupC2022();
        Team argentina = groupC2022.getTeamByName("Argentina");
        TeamFactory factory = new TeamFactory();
        assertEquals(factory.createTeam("Argentina", Set.of()), argentina);
        // test defensive copying and encapsulation
        argentina.addMatch("Australia", "2-1", false);
        // the Argentina team instance in the group should not change
        assertEquals(factory.createTeam("Argentina", Set.of()), groupC2022.getTeamByName("Argentina"));
    }

    @Test
    public void testGetTeamByNameError() {
        Group groupC2022 = groupC2022();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.getTeamByName("Australia"));
        assertEquals("No team of name Australia is in this group.", exception.getMessage());
    }

    // testing ranking of teams in a group

    @Test
    public void testFIFARankingGDTie() {
        Group groupC2022 = groupC2022Complete();
        Team argentina = groupC2022.getTeamByName("Argentina");
        Team poland = groupC2022.getTeamByName("Poland");
        Team mexico = groupC2022.getTeamByName("Mexico");
        Team saudiArabia = groupC2022.getTeamByName("Saudi Arabia");
        // trying out both compareTeams methods
        assertTrue(groupC2022.compareTeams(argentina, poland) > 0);
        assertTrue(groupC2022.compareTeams("Argentina", "Mexico") > 0);
        assertTrue(groupC2022.compareTeams(argentina, saudiArabia) > 0);
        assertTrue(groupC2022.compareTeams("Poland", "Mexico") > 0);
        assertTrue(groupC2022.compareTeams("Poland", "Saudi Arabia") > 0);
        assertTrue(groupC2022.compareTeams(saudiArabia, mexico) < 0);
    }

    @Test
    public void testFIFASortGroupPositions() {
        Group groupC2022 = groupC2022Complete();
        groupC2022.sortTeams();
        assertEquals(1, groupC2022.getTeamPositionByName("Argentina"));
        assertEquals(2, groupC2022.getTeamPositionByName("Poland"));
        assertEquals(3, groupC2022.getTeamPositionByName("Mexico"));
        assertEquals(4, groupC2022.getTeamPositionByName("Saudi Arabia"));
    }

    @Test
    public void testFIFASortedGroupPositions() {
        Group groupC2022 = groupC2022Complete();
        Team argentina = groupC2022.getTeamByName("Argentina");
        Team poland = groupC2022.getTeamByName("Poland");
        Team mexico = groupC2022.getTeamByName("Mexico");
        Team saudiArabia = groupC2022.getTeamByName("Saudi Arabia");
        Team[] sortedTeams = groupC2022.sortedTeams();
        assertEquals(argentina, sortedTeams[0]);
        assertEquals(poland, sortedTeams[1]);
        assertEquals(mexico, sortedTeams[2]);
        assertEquals(saudiArabia, sortedTeams[3]);
    }

    @Test
    public void testCompletenessOneLeg() {
        Group groupC2022 = groupC2022Complete();
        assertTrue(groupC2022.isComplete());
    }

    // testing specific methods for two-legged tournaments: home and away matches and goals

    @Test
    public void testGetHomeAndAwayMatches() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team liverpool = factory.createTeam("Liverpool");
        Team[] teams = new Team[]{manCity, chelsea, arsenal, liverpool};
        PremierLeague pl = new PremierLeague(teams);
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "3-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        pl.removeMatch("Chelsea", "Manchester City");
        assertEquals(Set.of(
                new Team.Match("Arsenal", "4-1", false)), chelsea.getHomeMatches());
        assertEquals(Set.of(
                new Team.Match("Manchester City", "1-2", true)), chelsea.getAwayMatches());
    }

    @Test
    public void testGetAwayGoals() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team liverpool = factory.createTeam("Liverpool");
        Team[] teams = new Team[]{manCity, chelsea, arsenal, liverpool};
        PremierLeague pl = new PremierLeague(teams);
        pl.addMatch("Chelsea", "Manchester City", "2-1");
        pl.addMatch("Arsenal", "Chelsea", "4-1");
        pl.addMatch("Liverpool", "Chelsea", "4-5");
        assertEquals(6, chelsea.getAwayGoals());
    }

    // test printing table

    @Test
    public void testGetTableRowByTeamName() {
        Group groupC2022 = groupC2022Complete();
        groupC2022.sortTeams();
        assertEquals("1: Argentina, Pld: 3, W: 2, D: 0, L: 1, GF: 5, GA: 2, GD: +3, Pts: 6",
                groupC2022.getTableRowByTeamName("Argentina"));
        assertEquals("2: Poland, Pld: 3, W: 1, D: 1, L: 1, GF: 2, GA: 2, GD: 0, Pts: 4",
                groupC2022.getTableRowByTeamName("Poland"));
        assertEquals("3: Mexico, Pld: 3, W: 1, D: 1, L: 1, GF: 2, GA: 3, GD: -1, Pts: 4",
                groupC2022.getTableRowByTeamName("Mexico"));
        assertEquals("4: Saudi Arabia, Pld: 3, W: 1, D: 0, L: 2, GF: 3, GA: 5, GD: -2, Pts: 3",
                groupC2022.getTableRowByTeamName("Saudi Arabia"));
    }
}