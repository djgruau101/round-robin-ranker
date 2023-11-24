import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UEFAEuroGroupRankingTest {

    /**
     * Constructs a new UEFAEuroCupGroup with the given team names.
     *
     * Usage:
     * createGroup(teamName1, teamName2, ...);
     *
     * @return A group playing in the UEFA European Championship.
     */
    UEFAEuroGroup createGroup(String... teamNames) {
        Team[] teams = Arrays.stream(teamNames).map(Team::createInstance).toArray(Team[]::new);
        return new UEFAEuroGroup(teams);
    }

    @Test
    public void testDifferentPoints() {
        // Euro 2020 Group E: all teams had different number of points
        UEFAEuroGroup groupE2020 = createGroup("Spain", "Sweden", "Poland", "Slovakia");
        groupE2020.addMatch("Poland", "Slovakia", "1-2");
        groupE2020.addMatch("Spain", "Sweden", "0-0");
        groupE2020.addMatch("Sweden", "Slovakia", "1-0");
        groupE2020.addMatch("Spain", "Poland", "1-1");
        groupE2020.addMatch("Slovakia", "Spain", "0-5");
        groupE2020.addMatch("Sweden", "Poland", "3-2");
        assertEquals("1: Sweden, Pld: 3, W: 2, D: 1, L: 0, GF: 4, GA: 2, GD: +2, Pts: 7",
                groupE2020.getTableRowByTeamName("Sweden"));
        assertEquals("2: Spain, Pld: 3, W: 1, D: 2, L: 0, GF: 6, GA: 1, GD: +5, Pts: 5",
                groupE2020.getTableRowByTeamName("Spain"));
        assertEquals("3: Slovakia, Pld: 3, W: 1, D: 0, L: 2, GF: 2, GA: 7, GD: -5, Pts: 3",
                groupE2020.getTableRowByTeamName("Slovakia"));
        assertEquals("4: Poland, Pld: 3, W: 0, D: 1, L: 2, GF: 4, GA: 6, GD: -2, Pts: 1",
                groupE2020.getTableRowByTeamName("Poland"));
    }

    @Test
    public void testHeadToHeadPoints() {
        // Euro 2020 Group F: Germany and Portugal had the same number of points but they did not draw in their match
        UEFAEuroGroup groupF2020 = createGroup("France", "Germany", "Portugal", "Hungary");
        groupF2020.addMatch("Hungary", "Portugal", "0-3");
        groupF2020.addMatch("France", "Germany", "1-0");
        groupF2020.addMatch("Hungary", "France", "1-1");
        groupF2020.addMatch("Portugal", "Germany", "2-4");
        groupF2020.addMatch("Portugal", "France", "2-2");
        groupF2020.addMatch("Germany", "Hungary", "2-2");
        assertEquals("1: France, Pld: 3, W: 1, D: 2, L: 0, GF: 4, GA: 3, GD: +1, Pts: 5",
                groupF2020.getTableRowByTeamName("France"));
        assertEquals("2: Germany, Pld: 3, W: 1, D: 1, L: 1, GF: 6, GA: 5, GD: +1, Pts: 4",
                groupF2020.getTableRowByTeamName("Germany"));
        assertEquals("3: Portugal, Pld: 3, W: 1, D: 1, L: 1, GF: 7, GA: 6, GD: +1, Pts: 4",
                groupF2020.getTableRowByTeamName("Portugal"));
        assertEquals("4: Hungary, Pld: 3, W: 0, D: 2, L: 1, GF: 3, GA: 6, GD: -3, Pts: 2",
                groupF2020.getTableRowByTeamName("Hungary"));
    }

    @Test
    public void testDifferentGD() {
        // Euro 2020 Group A: Wales and Switzerland had the same number of points,
        // drew in their head-to-head match,but had a difference GD
        UEFAEuroGroup groupA2020 = createGroup("Italy", "Wales", "Switzerland", "Turkey");
        groupA2020.addMatch("Turkey", "Italy", "0-3");
        groupA2020.addMatch("Wales", "Switzerland", "1-1");
        groupA2020.addMatch("Turkey", "Wales", "0-2");
        groupA2020.addMatch("Italy", "Switzerland", "3-0");
        groupA2020.addMatch("Switzerland", "Turkey", "3-1");
        groupA2020.addMatch("Italy", "Wales", "1-0");
        assertEquals("1: Italy, Pld: 3, W: 3, D: 0, L: 0, GF: 7, GA: 0, GD: +7, Pts: 9",
                groupA2020.getTableRowByTeamName("Italy"));
        assertEquals("2: Wales, Pld: 3, W: 1, D: 1, L: 1, GF: 3, GA: 2, GD: +1, Pts: 4",
                groupA2020.getTableRowByTeamName("Wales"));
        assertEquals("3: Switzerland, Pld: 3, W: 1, D: 1, L: 1, GF: 4, GA: 5, GD: -1, Pts: 4",
                groupA2020.getTableRowByTeamName("Switzerland"));
        assertEquals("4: Turkey, Pld: 3, W: 0, D: 0, L: 3, GF: 1, GA: 8, GD: -7, Pts: 0",
                groupA2020.getTableRowByTeamName("Turkey"));
    }

    @Test
    public void testDifferentGF() {
        // Euro 2020 Group A: Wales and Switzerland had the same number of points,
        // drew in their head-to-head match,but had a difference GD
        UEFAEuroGroup groupA2020 = createGroup("Italy", "Wales", "Switzerland", "Turkey");
        groupA2020.addMatch("Turkey", "Italy", "0-3");
        groupA2020.addMatch("Wales", "Switzerland", "1-1");
        groupA2020.addMatch("Turkey", "Wales", "0-2");
        groupA2020.addMatch("Italy", "Switzerland", "3-0");
        groupA2020.addMatch("Switzerland", "Turkey", "3-1");
        groupA2020.addMatch("Italy", "Wales", "1-0");
        assertEquals("1: Italy, Pld: 3, W: 3, D: 0, L: 0, GF: 7, GA: 0, GD: +7, Pts: 9",
                groupA2020.getTableRowByTeamName("Italy"));
        assertEquals("2: Wales, Pld: 3, W: 1, D: 1, L: 1, GF: 3, GA: 2, GD: +1, Pts: 4",
                groupA2020.getTableRowByTeamName("Wales"));
        assertEquals("3: Switzerland, Pld: 3, W: 1, D: 1, L: 1, GF: 4, GA: 5, GD: -1, Pts: 4",
                groupA2020.getTableRowByTeamName("Switzerland"));
        assertEquals("4: Turkey, Pld: 3, W: 0, D: 0, L: 3, GF: 1, GA: 8, GD: -7, Pts: 0",
                groupA2020.getTableRowByTeamName("Turkey"));
    }

    @Test
    public void testThreeWayTie() {
        UEFAEuroGroup groupB2020 = createGroup("Belgium", "Denmark", "Finland", "Russia");
        groupB2020.addMatch("Denmark", "Finland", "0-1");
        groupB2020.addMatch("Belgium", "Russia", "3-0");
        groupB2020.addMatch("Finland", "Russia", "0-1");
        groupB2020.addMatch("Denmark", "Belgium", "1-2");
        groupB2020.addMatch("Russia", "Denmark", "1-4");
        groupB2020.addMatch("Finland", "Belgium", "0-2");
        assertEquals("1: Belgium, Pld: 3, W: 3, D: 0, L: 0, GF: 7, GA: 1, GD: +6, Pts: 9",
                groupB2020.getTableRowByTeamName("Belgium"));
        assertEquals("2: Denmark, Pld: 3, W: 1, D: 0, L: 2, GF: 5, GA: 4, GD: +1, Pts: 3",
                groupB2020.getTableRowByTeamName("Denmark"));
        assertEquals("3: Finland, Pld: 3, W: 1, D: 0, L: 2, GF: 1, GA: 3, GD: -2, Pts: 3",
                groupB2020.getTableRowByTeamName("Finland"));
        assertEquals("4: Russia, Pld: 3, W: 1, D: 0, L: 2, GF: 2, GA: 7, GD: -5, Pts: 3",
                groupB2020.getTableRowByTeamName("Russia"));
    }
}
