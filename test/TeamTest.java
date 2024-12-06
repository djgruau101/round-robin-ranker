import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TeamTest {

    @BeforeEach
    public void setUp() {
        Team.resetStaticState();
    }

    @AfterEach
    public void tearDown() {
        Team.resetStaticState();
    }

    // testing both constructors of Team

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
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreLeadingZero2() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-02"))));
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNonDigits() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-2g"))));
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNoHyphen1() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "0"))));
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreNoHyphen2() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "021"))));
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
    }

    @Test
    public void testInvalidScoreSpace() {
        TeamFactory factory = new TeamFactory();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1- 2"))));
        assertEquals("The score must be two non-negative integers separated by '-'.", exception.getMessage());
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
        assertEquals("The score must be two non-negative integers separated by '-', " +
                "and all opponents' names should be different from the team's name.", exception.getMessage());
    }

    // testing remove matches

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

    // testing calculations of number of wins, losses, draws
    // the following tests are based on the results from the 2022 FIFA World Cup

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
    public void testGetPointsDeductConstructor() {
        // initialize instance with 1 match, then add 2 matches
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina",
                Set.of(new Team.Match("Saudi Arabia", "1-2")), 3);
        argentina.addMatch("Mexico", "2-0", false);
        argentina.addMatch("Poland", "2-0", false);
        assertEquals(3, argentina.getPoints());
    }

    @Test
    public void testGetPointsDeductPoints() {
        // initialize instance with 1 match, then add 2 matches
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina",
                Set.of(new Team.Match("Saudi Arabia", "1-2")));
        argentina.addMatch("Mexico", "2-0", false);
        argentina.addMatch("Poland", "2-0", false);
        argentina.adjustPenaltyPoints(3);
        argentina.adjustPenaltyPoints(1);
        assertEquals(2, argentina.getPoints());
    }

    @Test
    public void testGetPointsSetDeductedPoints() {
        // initialize instance with 1 match, then add 2 matches
        TeamFactory factory = new TeamFactory();
        Team argentina = factory.createTeam("Argentina",
                Set.of(new Team.Match("Saudi Arabia", "1-2")));
        argentina.addMatch("Mexico", "2-0", false);
        argentina.addMatch("Poland", "2-0", false);
        argentina.setDeductedPoints(4);
        argentina.setDeductedPoints(1);
        assertEquals(5, argentina.getPoints());
    }

    // testing calculations of GF, GA, GD, Pld

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
    public void testNumberOfMatchesPlayed() {
        TeamFactory factory = new TeamFactory();
        Team saudiArabia = factory.createTeam("Saudi Arabia");
        saudiArabia.addMatches(
                new Team.Match("Argentina", "2-1"),
                new Team.Match("Mexico", "1-2"),
                new Team.Match("Poland", "0-2"));
        assertEquals(3, saudiArabia.getNumberOfMatchesPlayed());
    }

    @Test
    public void testGetCardsSimple() {
        TeamFactory factory = new TeamFactory();
        Team wales = factory.createTeam("Wales");
        wales.addMatch(new Team.Match("United States", "1-1", List.of(FIFAWorldCupGroup.Card.YELLOW, FIFAWorldCupGroup.Card.YELLOW), List.of()));
        assertEquals(Set.of(new Team.Match("United States", "1-1", List.of(FIFAWorldCupGroup.Card.YELLOW, FIFAWorldCupGroup.Card.YELLOW), List.of())), wales.getMatches());
        assertEquals(List.of(FIFAWorldCupGroup.Card.YELLOW, FIFAWorldCupGroup.Card.YELLOW), wales.getCards());
    }

    @Test
    public void testGetFairPlayPoints() {
        // Wales during the 2022 World Cup
        TeamFactory factory = new TeamFactory();
        Team wales = factory.createTeam("Wales");
        wales.addMatches(
                new Team.Match("United States", "1-1",
                        new ArrayList<Group.CardEnum>(Collections.nCopies(2, FIFAWorldCupGroup.Card.YELLOW)),
                        new ArrayList<Group.CardEnum>(Collections.nCopies(4, FIFAWorldCupGroup.Card.YELLOW))),
                new Team.Match("Poland", "0-2",
                        List.of(FIFAWorldCupGroup.Card.YELLOW, FIFAWorldCupGroup.Card.DIRECT_RED),
                        new ArrayList<Group.CardEnum>(Collections.nCopies(2, FIFAWorldCupGroup.Card.YELLOW))),
                new Team.Match("England", "0-3",
                        new ArrayList<Group.CardEnum>(Collections.nCopies(2, FIFAWorldCupGroup.Card.YELLOW)),
                        List.of()));
        assertEquals(-9, wales.getFairPlayPoints());
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
        // notice that factory is not being used
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

    // testing home and away matches

    @Test
    public void testGetHomeMatches() {
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Manchester City");
        team.addMatches(
                new Team.Match("Liverpool", "1-2", true),
                new Team.Match("Manchester United", "4-4", false),
                new Team.Match("Chelsea", "3-1", true),
                new Team.Match("Arsenal", "0-1", false));
        assertEquals(Set.of(new Team.Match("Manchester United", "4-4", false),
                new Team.Match("Arsenal", "0-1", false)), team.getHomeMatches());
    }

    @Test
    public void testGetAwayMatches() {
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Manchester City");
        team.addMatches(
                new Team.Match("Liverpool", "1-2", true),
                new Team.Match("Manchester United", "4-4", false),
                new Team.Match("Chelsea", "3-1", true),
                new Team.Match("Arsenal", "0-1", false));
        assertEquals(Set.of(new Team.Match("Liverpool", "1-2", true),
                new Team.Match("Chelsea", "3-1", true)), team.getAwayMatches());
    }

    @Test
    public void testGetAwayGoals() {
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Manchester City");
        team.addMatches(
                new Team.Match("Liverpool", "1-2", true),
                new Team.Match("Manchester United", "4-4", false),
                new Team.Match("Chelsea", "3-1", true),
                new Team.Match("Arsenal", "0-1", false));
        assertEquals(4, team.getAwayGoals());
    }

    @Test
    public void testGetNumberWinsHomeAway() {
        // Manchester City's 10th to 14th matches of the 2024-25 Premier League
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Manchester City");
        team.addMatches(
                new Team.Match("Bournemouth", "1-2", true),
                new Team.Match("Brighton & Hove Albion", "1-2", true),
                new Team.Match("Tottenham Hotspur", "0-4", false),
                new Team.Match("Liverpool", "0-2", true),
                new Team.Match("Nottingham Forest", "3-0", false));
        assertEquals(1, team.getNumberWins());
    }

    @Test
    public void testGetNumberLossesHomeAway() {
        // Manchester City's 10th to 14th matches of the 2024-25 Premier League
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Manchester City");
        team.addMatches(
                new Team.Match("Bournemouth", "1-2", true),
                new Team.Match("Brighton & Hove Albion", "1-2", true),
                new Team.Match("Tottenham Hotspur", "0-4", false),
                new Team.Match("Liverpool", "0-2", true),
                new Team.Match("Nottingham Forest", "3-0", false));
        assertEquals(4, team.getNumberLosses());
    }

    @Test
    public void testGetNumberDrawsHomeAway() {
        // Liverpool's 9th to 14th matches of the 2024-25 Premier League
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Liverpool");
        team.addMatches(
                new Team.Match("Arsenal", "2-2", true),
                new Team.Match("Brighton & Hove Albion", "2-1", false),
                new Team.Match("Aston Villa", "2-0", false),
                new Team.Match("Southampton", "3-2", true),
                new Team.Match("Manchester City", "2-0", false),
                new Team.Match("Newcastle United", "3-3", true));
        assertEquals(2, team.getNumberDraws());
    }

    // testing getName and toString

    @Test
    public void testGetName() {
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Manchester City");
        assertEquals("Manchester City", team.getName());
    }

    @Test
    public void testTeamToStringNoMatches() {
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Manchester City");
        assertEquals("Team{name=Manchester City, matches={}}", team.toString());
    }

    @Test
    public void testTeamToStringWithMatchesOneLeg() {
        TeamFactory factory = new TeamFactory();
        Team germany = factory.createTeam("Germany", Set.of(new Team.Match("Japan", "1-4")));
        germany.addMatches(
                new Team.Match("Japan", "1-2"),
                new Team.Match("Spain", "1-1"),
                new Team.Match("Costa Rica", "4-2")
        );
        assertEquals("Team{name=Germany, matches={Match{opponentName=Japan, score=1-2, isAway=false, selfCards=[], opponentCards=[]}, Match{opponentName=Spain, score=1-1, isAway=false, selfCards=[], opponentCards=[]}, Match{opponentName=Costa Rica, score=4-2, isAway=false, selfCards=[], opponentCards=[]}}}",
                germany.toString());
    }

    @Test
    public void testTeamToStringWithMatchesTwoLegs() {
        TeamFactory factory = new TeamFactory();
        Team team = factory.createTeam("Manchester City");
        team.addMatch("Chelsea", "4-4", false);
        team.addMatch("Arsenal", "0-1", true);
        assertEquals("Team{name=Manchester City, matches={Match{opponentName=Chelsea, score=4-4, isAway=false, selfCards=[], opponentCards=[]}, Match{opponentName=Arsenal, score=0-1, isAway=true, selfCards=[], opponentCards=[]}}}", team.toString());
    }
}
