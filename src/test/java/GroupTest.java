import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GroupTest {

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
        groupC2022.addMatch("Argentina", "Saudi Arabia", "1-2",
                List.of(), new ArrayList<>(Collections.nCopies(6, FIFAWorldCupGroup.Card.YELLOW)));
        groupC2022.addMatch("Poland", "Mexico", "0-0",
                List.of(FIFAWorldCupGroup.Card.YELLOW),
                new ArrayList<>(Collections.nCopies(2, FIFAWorldCupGroup.Card.YELLOW)));
        groupC2022.addMatch("Poland", "Saudi Arabia", "2-0",
                new ArrayList<>(Collections.nCopies(3, FIFAWorldCupGroup.Card.YELLOW)),
                new ArrayList<>(Collections.nCopies(2, FIFAWorldCupGroup.Card.YELLOW)));
        groupC2022.addMatch("Argentina", "Mexico", "2-0",
                List.of(FIFAWorldCupGroup.Card.YELLOW),
                new ArrayList<>(Collections.nCopies(4, FIFAWorldCupGroup.Card.YELLOW)));
        groupC2022.addMatch("Poland", "Argentina", "0-2",
                List.of(FIFAWorldCupGroup.Card.YELLOW), List.of(FIFAWorldCupGroup.Card.YELLOW));
        groupC2022.addMatch("Saudi Arabia", "Mexico", "1-2",
                new ArrayList<>(Collections.nCopies(6, FIFAWorldCupGroup.Card.YELLOW)),
                List.of(FIFAWorldCupGroup.Card.YELLOW));
        return groupC2022;
    }

    /**
     * Constructs a new PremierLeague small group with no matches played.
     *
     * @return a group of type PremierLeague.
     */
    PremierLeague premierLeague() {
        TeamFactory factory = new TeamFactory();
        Team manCity = factory.createTeam("Manchester City");
        Team chelsea = factory.createTeam("Chelsea");
        Team arsenal = factory.createTeam("Arsenal");
        Team liverpool = factory.createTeam("Liverpool");
        Team[] teams = new Team[]{manCity, chelsea, arsenal, liverpool};
        return new PremierLeague(teams);
    }

    // testing constructor

    @Test
    @DisplayName("Should throw an exception when more than 4 teams are added to a FIFAWorldCupGroup")
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
    @DisplayName("Should throw an exception when duplicate teams are added to the group")
    public void testDuplicateTeamsInGroup() {
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina");
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        Team poland1 = factory.createTeam("Poland");
        Team poland2 = factory.createTeam("Poland", Set.of(new Team.Match("Argentina", "2-0")));
        Team[] teams = new Team[]{argentina, poland1, saudiArabia, poland2};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new FIFAWorldCupGroup(teams));
        assertEquals("Teams with duplicate names detected.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw an exception if the number of legs is not 1 or 2")
    public void testInvalidNumberOfLegs() {
        class TestGroup extends Group {

            TestGroup(Team[] teams) {
                super(teams, 4, 3); // incorrect usage of constructor
            }

            @Override
            public int compareTeams(Team team1, Team team2) {
                return 0;
            }

            protected int compareTeamsBeforeHeadToHead(Team team1, Team team2) {
                return 0;
            }

            protected TestGroup createSubGroup(Team team) {
                return new TestGroup(new Team[]{team});
            }
        }
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina");
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        Team poland = factory.createTeam("Poland");
        Team mexico = factory.createTeam("Mexico");
        Team[] teams = new Team[]{argentina, poland, saudiArabia, mexico};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TestGroup(teams));
        assertEquals("The number of legs must be either 1 or 2.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw an exception if a match is set as away in a single-legged tournament")
    public void testAwayMatchesOneLegError() {
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina",
                Set.of(new Team.Match("Saudi Arabia", "1-2", true)));
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        Team poland = factory.createTeam("Poland");
        Team mexico = factory.createTeam("Mexico");
        Team[] teams = new Team[]{argentina, poland, saudiArabia, mexico};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new FIFAWorldCupGroup(teams));
        assertEquals("Single-legged tournaments can not contain away matches.", exception.getMessage());
    }

    @Test
    @DisplayName("Should remove matches where opponent is not in the group")
    public void testRemoveMatchesWhereOpponentNotInGroup() {
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina",
                Set.of(new Team.Match("Australia", "2-1")));
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        Team poland = factory.createTeam("Poland");
        Team mexico = factory.createTeam("Mexico");
        Team[] teams = new Team[]{argentina, poland, saudiArabia, mexico};
        FIFAWorldCupGroup groupC2022 = new FIFAWorldCupGroup(teams);
        // Argentina VS Australia match will be removed
        assertEquals(Set.of(), groupC2022.getTeamByName("Argentina").getMatches());
    }

    @Test
    @DisplayName("Should return matches were opponents are in the group")
    public void testMatchesNotRemovedIfOpponentInGroup() {
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina",
                Set.of(new Team.Match("Mexico", "2-0"),
                        new Team.Match("Poland", "2-0"),
                        new Team.Match("Croatia", "3-0")));
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        Team poland = factory.createTeam("Poland");
        Team mexico = factory.createTeam("Mexico");
        Team[] teams = new Team[]{argentina, poland, saudiArabia, mexico};
        FIFAWorldCupGroup groupC2022 = new FIFAWorldCupGroup(teams);
        assertEquals(Set.of(new Team.Match("Mexico", "2-0"),
                new Team.Match("Poland", "2-0")),
                groupC2022.getTeamByName("Argentina").getMatches());
    }

    // testing adding matches

    @Test
    public void testAddMatchScoreInvalid() {
        Group groupC2022 = groupC2022();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.addMatch("Argentina", "Mexico", "2 -0"));
        assertEquals("The score must be two nonnegative integers separated by '-'.\n", exception.getMessage());
    }

    @Test
    public void testAddMatchTeamNamesIdentical() {
        Group groupC2022 = groupC2022();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.addMatch("Argentina", "Argentina", "0-0"));
        assertEquals("The names of the two teams facing each other cannot be the same.", exception.getMessage());
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
    public void testAddMatchTeamNameNotPresent() {
        Group groupC2022 = groupC2022();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.addMatch("Argentina", "Australia", "2-1"));
        assertEquals("Team names must be among the ones in the group.", exception.getMessage());
    }

    @Test
    public void testAddMatchOneLeg() {
        Group groupC2022 = groupC2022();
        groupC2022.addMatch("Argentina", "Saudi Arabia", "1-2");
        assertEquals(Set.of(new Team.Match("Saudi Arabia", "1-2")),
                groupC2022.getTeamByName("Argentina").getMatches());
        assertEquals(Set.of(new Team.Match("Argentina", "2-1")),
                groupC2022.getTeamByName("Saudi Arabia").getMatches());
        assertEquals(Set.of(), groupC2022.getTeamByName("Poland").getMatches());
        assertEquals(Set.of(), groupC2022.getTeamByName("Mexico").getMatches());
    }

    @Test
    public void testUpdateMatchOneLeg() {
        Group groupC2022 = groupC2022();
        groupC2022.addMatch("Argentina", "Saudi Arabia", "1-0");
        groupC2022.addMatch("Argentina", "Saudi Arabia", "1-2");
        assertEquals(Set.of(new Team.Match("Saudi Arabia", "1-2")),
                groupC2022.getTeamByName("Argentina").getMatches());
        assertEquals(Set.of(new Team.Match("Argentina", "2-1")),
                groupC2022.getTeamByName("Saudi Arabia").getMatches());
        assertEquals(Set.of(), groupC2022.getTeamByName("Poland").getMatches());
        assertEquals(Set.of(), groupC2022.getTeamByName("Mexico").getMatches());
    }

    @Test
    public void testAddMatchTwoLegs() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Arsenal", "Manchester City", "1-0");
        assertEquals(Set.of(new Team.Match("Manchester City", "1-0")),
                pl.getTeamByName("Arsenal").getMatches());
        assertEquals(Set.of(new Team.Match("Arsenal", "0-1", true)),
                pl.getTeamByName("Manchester City").getMatches());
    }

    @Test
    public void testUpdateMatchTwoLegs() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Arsenal", "Manchester City", "2-6");
        pl.addMatch("Arsenal", "Manchester City", "1-0");
        assertEquals(Set.of(new Team.Match("Manchester City", "1-0")),
                pl.getTeamByName("Arsenal").getMatches());
        assertEquals(Set.of(new Team.Match("Arsenal", "0-1", true)),
                pl.getTeamByName("Manchester City").getMatches());
    }

    @Test
    public void testGroupAddMatchesTwoLegs() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals(Set.of(
                new Team.Match("Chelsea", "2-1", false),
                new Team.Match("Chelsea", "4-2", true)),
                pl.getTeamByName("Manchester City").getMatches());
        assertEquals(Set.of(
                new Team.Match("Manchester City", "1-2", true),
                new Team.Match("Manchester City", "2-4", false),
                new Team.Match("Arsenal", "4-1", false)),
                pl.getTeamByName("Chelsea").getMatches());
    }

    @Test
    public void testUpdateMatchesTwoLegs() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "0-3");
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-2");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "2-3");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals(Set.of(
                new Team.Match("Chelsea", "2-1", false),
                new Team.Match("Chelsea", "4-2", true)),
                pl.getTeamByName("Manchester City").getMatches());
        assertEquals(Set.of(
                new Team.Match("Manchester City", "1-2", true),
                new Team.Match("Manchester City", "2-4", false),
                new Team.Match("Arsenal", "4-1", false)),
                pl.getTeamByName("Chelsea").getMatches());
    }

    @Test
    public void testGetFairPlayPoints() {
        FIFAWorldCupGroup groupC2022 = groupC2022Complete();
        assertEquals(-2, groupC2022.getTeamByName("Argentina").getFairPlayPoints());
        assertEquals(-5, groupC2022.getTeamByName("Poland").getFairPlayPoints());
        assertEquals(-7, groupC2022.getTeamByName("Mexico").getFairPlayPoints());
        assertEquals(-14, groupC2022.getTeamByName("Saudi Arabia").getFairPlayPoints());
    }

    // test removing matches

    @Test
    public void testRemoveMatchTeamNamesIdentical() {
        Group groupC2022 = groupC2022Complete();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.removeMatch("Argentina", "Argentina"));
        assertEquals("The names of the two teams facing each other cannot be the same.",
                exception.getMessage());
    }

    @Test
    public void testRemoveMatchTeamNameNotInGroup() {
        Group groupC2022 = groupC2022Complete();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupC2022.removeMatch("Argentina", "Australia"));
        assertEquals("Team names must be among the ones in the group.", exception.getMessage());
    }

    @Test
    public void testGroupRemoveMatchOneLeg() {
        Group groupC2022 = groupC2022Complete();
        groupC2022.removeMatch("Argentina", "Mexico");
        assertEquals(Set.of(new Team.Match("Saudi Arabia", "1-2",
                        List.of(), new ArrayList<>(Collections.nCopies(6, FIFAWorldCupGroup.Card.YELLOW))),
                new Team.Match("Poland", "2-0", List.of(FIFAWorldCupGroup.Card.YELLOW),
                        List.of(FIFAWorldCupGroup.Card.YELLOW))),
                groupC2022.getTeamByName("Argentina").getMatches());
    }

    @Test
    public void testGroupRemoveMatchTwiceOneLeg() {
        Group groupC2022 = groupC2022Complete();
        groupC2022.removeMatch("Argentina", "Poland");
        assertEquals(Set.of(new Team.Match("Saudi Arabia", "1-2",
                        List.of(), new ArrayList<>(Collections.nCopies(6, FIFAWorldCupGroup.Card.YELLOW))),
                new Team.Match("Mexico", "2-0", List.of(FIFAWorldCupGroup.Card.YELLOW),
                        new ArrayList<>(Collections.nCopies(4, FIFAWorldCupGroup.Card.YELLOW)))),
                groupC2022.getTeamByName("Argentina").getMatches());
        groupC2022.removeMatch("Argentina", "Poland");
        assertEquals(Set.of(new Team.Match("Saudi Arabia", "1-2",
                        List.of(), new ArrayList<>(Collections.nCopies(6, FIFAWorldCupGroup.Card.YELLOW))),
                new Team.Match("Mexico", "2-0", List.of(FIFAWorldCupGroup.Card.YELLOW),
                        new ArrayList<>(Collections.nCopies(4, FIFAWorldCupGroup.Card.YELLOW)))),
                groupC2022.getTeamByName("Argentina").getMatches());
    }

    @Test
    public void testRemoveMatchTwoLegs() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "3-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        pl.removeMatch("Chelsea", "Manchester City");
        assertEquals(Set.of(new Team.Match("Chelsea", "2-1", false)),
                pl.getTeamByName("Manchester City").getMatches());
        assertEquals(Set.of(new Team.Match("Arsenal", "4-1", false),
                new Team.Match("Manchester City", "1-2", true)),
                pl.getTeamByName("Chelsea").getMatches());
    }

    @Test
    public void testRemoveMatchTwiceTwoLegs() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "3-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        pl.removeMatch("Chelsea", "Manchester City");
        assertEquals(Set.of(new Team.Match("Manchester City", "1-2", true),
                new Team.Match("Arsenal", "4-1", false)),
                pl.getTeamByName("Chelsea").getMatches());
        assertEquals(Set.of(new Team.Match("Chelsea", "2-1", false)),
                pl.getTeamByName("Manchester City").getMatches());
        pl.removeMatch("Chelsea", "Manchester City");
        assertEquals(Set.of(new Team.Match("Manchester City", "1-2", true),
                new Team.Match("Arsenal", "4-1", false)),
                pl.getTeamByName("Chelsea").getMatches());
        assertEquals(Set.of(new Team.Match("Chelsea", "2-1", false)),
                pl.getTeamByName("Manchester City").getMatches());
    }

    // testing getTeamByName

    @Test
    public void testGetTeamByName() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "3-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals(Team.createInstance("Chelsea",
                Set.of(new Team.Match("Manchester City", "1-2", true),
                        new Team.Match("Manchester City", "3-2", false),
                        new Team.Match("Arsenal", "4-1", false))),
                pl.getTeamByName("Chelsea"));
    }

    @Test
    public void testGetTeamNameNotPresent() {
        PremierLeague pl = premierLeague();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pl.getTeamByName("Real Madrid"));
        assertEquals("No team of name Real Madrid is in this group.", exception.getMessage());
    }

    @Test
    public void testGetTeamNames() {
        Group groupC2022 = groupC2022Complete();
        assertEquals(Set.of("Argentina", "Saudi Arabia", "Poland", "Mexico"), groupC2022.getTeamNames());
    }

    // testing compareTeams

    @Test
    public void testFIFACompareTeams() {
        // easy case: Poland and Mexico have same number of points but different GD
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
    public void testPremierLeagueCompareTeams() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "1-0");
        pl.addMatch("Chelsea", "Arsenal", "2-2");
        pl.addMatch("Arsenal", "Liverpool", "1-0");
        pl.addMatch("Liverpool", "Manchester City", "1-0");
        Team manCity = pl.getTeamByName("Manchester City");
        Team chelsea = pl.getTeamByName("Chelsea");
        Team arsenal = pl.getTeamByName("Arsenal");
        Team liverpool = pl.getTeamByName("Liverpool");
        // Man City: 3 pts, Chelsea: 1 pt, Arsenal: 4 pts, Liverpool: 3 pts
        assertEquals(pl.compareTeams(manCity, liverpool), 0);
        assertTrue(pl.compareTeams("Arsenal", "Liverpool") > 0);
        assertTrue(pl.compareTeams("Arsenal", "Manchester City") > 0);
        assertTrue(pl.compareTeams(liverpool, chelsea) > 0);
        assertTrue(pl.compareTeams("Manchester City", "Chelsea") > 0);
        assertTrue(pl.compareTeams(arsenal, manCity) > 0);
    }

    // testing sortedTeams

    @Test
    public void testSortedTeamsNonEmptyMatches() {
        Group groupC2022 = groupC2022Complete();
        Team[] sorted = groupC2022Complete().sortedTeams();
        assertEquals(groupC2022.getTeamByName("Argentina"), sorted[0]);
        assertEquals(groupC2022.getTeamByName("Poland"), sorted[1]);
        assertEquals(groupC2022.getTeamByName("Mexico"), sorted[2]);
        assertEquals(groupC2022.getTeamByName("Saudi Arabia"), sorted[3]);
    }

    // testing get position number for teams

    @Test
    public void testFIFASortGroupPositions() {
        Group groupC2022 = groupC2022Complete();
        assertEquals(1, groupC2022.getTeamPositionByName("Argentina"));
        assertEquals(2, groupC2022.getTeamPositionByName("Poland"));
        assertEquals(3, groupC2022.getTeamPositionByName("Mexico"));
        assertEquals(4, groupC2022.getTeamPositionByName("Saudi Arabia"));
    }

    @Test
    public void testPremierLeagueSortGroupPositions() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "1-0");
        pl.addMatch("Chelsea", "Arsenal", "2-2");
        pl.addMatch("Arsenal", "Liverpool", "1-0");
        pl.addMatch("Liverpool", "Manchester City", "1-0");
        // Man City: 3 pts, Chelsea: 1 pt, Arsenal: 4 pts, Liverpool: 3 pts
        // Liverpool and Man City tied on GD and GF
        assertEquals(1, pl.getTeamPositionByName("Arsenal"));
        assertEquals(2, pl.getTeamPositionByName("Manchester City"));
        assertEquals(2, pl.getTeamPositionByName("Liverpool"));
        assertEquals(4, pl.getTeamPositionByName("Chelsea"));
    }

    // testing Pts, GD, GF, GA methods from the Team object but after calls of Group.addMatch

    @Test
    public void testGetPointsTwoLegged() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals(4, pl.getTeamByName("Chelsea").getPoints());
    }

    @Test
    public void testGDZeroTwoLegged() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals("0", pl.getTeamByName("Chelsea").getGoalDifferenceToString());
    }

    @Test
    public void testGDPositiveTwoLegged() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals("+3", pl.getTeamByName("Manchester City").getGoalDifferenceToString());
    }

    @Test
    public void testGDNegativeTwoLegged() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals("-3", pl.getTeamByName("Arsenal").getGoalDifferenceToString());
    }

    @Test
    public void testGFTwoLegged() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertEquals(7, pl.getTeamByName("Chelsea").getGoalsFor());
    }

    @Test
    public void testGATwoLegged() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-2");
        assertEquals(8, pl.getTeamByName("Chelsea").getGoalsAgainst());
    }

    @Test
    public void testPldTwoLegged() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Chelsea", "Manchester City", "2-1");
        pl.addMatch("Arsenal", "Chelsea", "4-1");
        pl.addMatch("Liverpool", "Chelsea", "4-5");
        assertEquals(3, pl.getTeamByName("Chelsea").getNumberOfMatchesPlayed());
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
    public void testHavePlayedAgainstOneLeg() {
        Group groupC2022 = groupC2022();
        groupC2022.addMatch("Argentina", "Saudi Arabia", "1-2");
        assertTrue(groupC2022.havePlayedAgainst("Argentina", "Saudi Arabia"));
        assertFalse(groupC2022.havePlayedAgainst("Poland", "Mexico"));
    }

    @Test
    public void testHavePlayedAgainstTwoLegs() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertTrue(pl.havePlayedAgainst("Manchester City", "Chelsea"));
        assertFalse(pl.havePlayedAgainst("Arsenal", "Manchester City"));
    }

    @Test
    public void testIncompletenessOneLeg() {
        Group groupC2022 = groupC2022();
        assertFalse(groupC2022.isComplete());
    }

    @Test
    public void testCompletenessOneLeg() {
        Group groupC2022 = groupC2022Complete();
        assertTrue(groupC2022.isComplete());
    }

    @Test
    public void testIncompletenessTwoLegs() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "2-4");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        assertFalse(pl.isComplete());
    }

    @Test
    public void testCompletenessTwoLegs() {
        PremierLeague pl = premierLeague();
        String[] teamNames = {"Manchester City", "Chelsea", "Arsenal", "Liverpool"};
        for (String teamName1 : teamNames) {
            for (String teamName2 : teamNames) {
                if (!teamName1.equals(teamName2)) {
                    pl.addMatch(teamName1, teamName2, "2-1");
                }
            }
        }
        assertTrue(pl.isComplete());
    }

    // testing specific methods for two-legged tournaments: home and away matches and goals

    @Test
    public void testGetHomeAndAwayMatches() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Manchester City", "Chelsea", "2-1");
        pl.addMatch("Chelsea", "Manchester City", "3-2");
        pl.addMatch("Chelsea", "Arsenal", "4-1");
        pl.removeMatch("Chelsea", "Manchester City");
        assertEquals(Set.of(
                new Team.Match("Arsenal", "4-1", false)),
                pl.getTeamByName("Chelsea").getHomeMatches());
        assertEquals(Set.of(
                new Team.Match("Manchester City", "1-2", true)),
                pl.getTeamByName("Chelsea").getAwayMatches());
    }

    @Test
    public void testGetAwayGoals() {
        PremierLeague pl = premierLeague();
        pl.addMatch("Chelsea", "Manchester City", "2-1");
        pl.addMatch("Arsenal", "Chelsea", "4-1");
        pl.addMatch("Liverpool", "Chelsea", "4-5");
        assertEquals(6, pl.getTeamByName("Chelsea").getAwayGoals());
    }

    // test printing table

    @Test
    public void testGetTableRowByTeamName() {
        Group groupC2022 = groupC2022Complete();
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