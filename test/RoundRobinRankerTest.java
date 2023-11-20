import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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