import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FIFAWorldCupGroupRankingTest {

    /**
     * Constructs a new FIFAWorldCupGroup with the given team names.
     *
     * Usage:
     * createGroup(teamName1, teamName2, ...);
     *
     * @return A group playing in the FIFA World Cup.
     */
    FIFAWorldCupGroup createGroup(String... teamNames) {
        Team[] teams = Arrays.stream(teamNames).map(Team::createInstance).toArray(Team[]::new);
        return new FIFAWorldCupGroup(teams);
    }

    @Test
    public void testDifferentPoints() {
        // 2022 World Cup Group A: all teams had a different number of points
        FIFAWorldCupGroup groupA2022 = createGroup("Qatar", "Netherlands", "Senegal", "Ecuador");
        groupA2022.addMatch("Qatar", "Ecuador", "0-2");
        groupA2022.addMatch("Senegal", "Netherlands", "0-2");
        groupA2022.addMatch("Qatar", "Senegal", "1-3");
        groupA2022.addMatch("Netherlands", "Ecuador", "1-1");
        groupA2022.addMatch("Ecuador", "Senegal", "1-2");
        groupA2022.addMatch("Netherlands", "Qatar", "2-0");
        assertEquals("1: Netherlands, Pld: 3, W: 2, D: 1, L: 0, GF: 5, GA: 1, GD: +4, Pts: 7",
                groupA2022.getTableRowByTeamName("Netherlands"));
        assertEquals("2: Senegal, Pld: 3, W: 2, D: 0, L: 1, GF: 5, GA: 4, GD: +1, Pts: 6",
                groupA2022.getTableRowByTeamName("Senegal"));
        assertEquals("3: Ecuador, Pld: 3, W: 1, D: 1, L: 1, GF: 4, GA: 3, GD: +1, Pts: 4",
                groupA2022.getTableRowByTeamName("Ecuador"));
        assertEquals("4: Qatar, Pld: 3, W: 0, D: 0, L: 3, GF: 1, GA: 7, GD: -6, Pts: 0",
                groupA2022.getTableRowByTeamName("Qatar"));
    }

    @Test
    public void testIdenticalTeamError() {
        Group group = createGroup("Argentina", "Saudi Arabia", "Poland", "Mexico");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> group.compareTeams(group.getTeamByName("Argentina"),
                        group.getTeamByName("Argentina")));
        assertEquals("Can not compare two identical teams.", exception.getMessage());
    }

    @Test
    public void testDifferentGD() {
        // 2022 World Cup Group C: Poland and Mexico were tied on points but not on GD
        FIFAWorldCupGroup groupC2022 = createGroup("Argentina", "Saudi Arabia", "Poland", "Mexico");
        groupC2022.addMatch("Argentina", "Saudi Arabia", "1-2");
        groupC2022.addMatch("Poland", "Mexico", "0-0");
        groupC2022.addMatch("Poland", "Saudi Arabia", "2-0");
        groupC2022.addMatch("Argentina", "Mexico", "2-0");
        groupC2022.addMatch("Poland", "Argentina", "0-2");
        groupC2022.addMatch("Saudi Arabia", "Mexico", "1-2");
        assertEquals("1: Argentina, Pld: 3, W: 2, D: 0, L: 1, GF: 5, GA: 2, GD: +3, Pts: 6",
                groupC2022.getTableRowByTeamName("Argentina"));
        assertEquals("2: Poland, Pld: 3, W: 1, D: 1, L: 1, GF: 2, GA: 2, GD: 0, Pts: 4",
                groupC2022.getTableRowByTeamName("Poland"));
        assertEquals("3: Mexico, Pld: 3, W: 1, D: 1, L: 1, GF: 2, GA: 3, GD: -1, Pts: 4",
                groupC2022.getTableRowByTeamName("Mexico"));
        assertEquals("4: Saudi Arabia, Pld: 3, W: 1, D: 0, L: 2, GF: 3, GA: 5, GD: -2, Pts: 3",
                groupC2022.getTableRowByTeamName("Saudi Arabia"));
    }

    @Test
    public void testDifferentGF() {
        // World Cup 2022 Group H: South Korea and Uruguay were tied on points and GD, but not on GF
        FIFAWorldCupGroup groupH2022 = createGroup("Portugal", "Ghana", "South Korea", "Uruguay");
        groupH2022.addMatch("Uruguay", "South Korea", "0-0");
        groupH2022.addMatch("Portugal", "Ghana", "3-2");
        groupH2022.addMatch("South Korea", "Ghana", "2-3");
        groupH2022.addMatch("Portugal", "Uruguay", "2-0");
        groupH2022.addMatch("Ghana", "Uruguay", "0-2");
        groupH2022.addMatch("South Korea", "Portugal", "2-1");
        assertEquals("1: Portugal, Pld: 3, W: 2, D: 0, L: 1, GF: 6, GA: 4, GD: +2, Pts: 6",
                groupH2022.getTableRowByTeamName("Portugal"));
        assertEquals("2: South Korea, Pld: 3, W: 1, D: 1, L: 1, GF: 4, GA: 4, GD: 0, Pts: 4",
                groupH2022.getTableRowByTeamName("South Korea"));
        assertEquals("3: Uruguay, Pld: 3, W: 1, D: 1, L: 1, GF: 2, GA: 2, GD: 0, Pts: 4",
                groupH2022.getTableRowByTeamName("Uruguay"));
        assertEquals("4: Ghana, Pld: 3, W: 1, D: 0, L: 2, GF: 5, GA: 7, GD: -2, Pts: 3",
                groupH2022.getTableRowByTeamName("Ghana"));
    }

    @Test
    public void testHeadToHeadTwoTeams() {
        // Japan x Colombia and Senegal x Poland are tied on points and GD, and GF
        // but head-to-head result is not a draw
        FIFAWorldCupGroup groupH2018Adapted = createGroup("Senegal", "Japan", "Poland", "Colombia");
        groupH2018Adapted.addMatch("Colombia", "Japan", "1-2");
        groupH2018Adapted.addMatch("Poland", "Senegal", "1-2");
        groupH2018Adapted.addMatch("Japan", "Senegal", "5-2");
        groupH2018Adapted.addMatch("Poland", "Colombia", "2-5");
        groupH2018Adapted.addMatch("Japan", "Poland", "0-1");
        groupH2018Adapted.addMatch("Senegal", "Colombia", "0-1");

        // Pts: Japan 6, Senegal 3, Poland 3, Colombia 6
        // GD: Japan +3, Senegal -3, Poland -3, Colombia +3
        // GF: Japan 7, Senegal 4, Poland 4, Colombia 7

        assertTrue(groupH2018Adapted.compareTeams("Japan", "Colombia") > 0);
        assertTrue(groupH2018Adapted.compareTeams("Colombia", "Senegal") > 0);
        assertTrue(groupH2018Adapted.compareTeams("Senegal", "Poland") > 0);

        assertEquals("1: Japan, Pld: 3, W: 2, D: 0, L: 1, GF: 7, GA: 4, GD: +3, Pts: 6",
                groupH2018Adapted.getTableRowByTeamName("Japan"));
        assertEquals("2: Colombia, Pld: 3, W: 2, D: 0, L: 1, GF: 7, GA: 4, GD: +3, Pts: 6",
                groupH2018Adapted.getTableRowByTeamName("Colombia"));
        assertEquals("3: Senegal, Pld: 3, W: 1, D: 0, L: 2, GF: 4, GA: 7, GD: -3, Pts: 3",
                groupH2018Adapted.getTableRowByTeamName("Senegal"));
        assertEquals("4: Poland, Pld: 3, W: 1, D: 0, L: 2, GF: 4, GA: 7, GD: -3, Pts: 3",
                groupH2018Adapted.getTableRowByTeamName("Poland"));
    }

    @Test
    public void testDifferentFairPlayPoints() {
        // 2018 World Cup Group H: Japan and Senegal were tied on points,
        // GD, GF, head-to-head record, but not on fair play points
        FIFAWorldCupGroup groupH2018 = createGroup("Senegal", "Japan", "Poland", "Colombia");
        groupH2018.addMatch("Colombia", "Japan", "1-2",
                List.of(FIFAWorldCupGroup.Card.DIRECT_RED, FIFAWorldCupGroup.Card.YELLOW, FIFAWorldCupGroup.Card.YELLOW),
                List.of(FIFAWorldCupGroup.Card.YELLOW));
        groupH2018.addMatch("Poland", "Senegal", "1-2",
                List.of(FIFAWorldCupGroup.Card.YELLOW),
                new ArrayList<>(Collections.nCopies(2, FIFAWorldCupGroup.Card.YELLOW)));
        groupH2018.addMatch("Japan", "Senegal", "2-2",
                new ArrayList<>(Collections.nCopies(2, FIFAWorldCupGroup.Card.YELLOW)),
                new ArrayList<>(Collections.nCopies(3, FIFAWorldCupGroup.Card.YELLOW)));
        groupH2018.addMatch("Poland", "Colombia", "0-3",
                new ArrayList<>(Collections.nCopies(2, FIFAWorldCupGroup.Card.YELLOW)), List.of());
        groupH2018.addMatch("Japan", "Poland", "0-1",
                List.of(FIFAWorldCupGroup.Card.YELLOW), List.of());
        groupH2018.addMatch("Senegal", "Colombia", "0-1",
                List.of(FIFAWorldCupGroup.Card.YELLOW), List.of(FIFAWorldCupGroup.Card.YELLOW));
        assertEquals(-3, groupH2018.getTeamByName("Poland").getFairPlayPoints());
        assertEquals(-4, groupH2018.getTeamByName("Japan").getFairPlayPoints());
        assertEquals(-6, groupH2018.getTeamByName("Senegal").getFairPlayPoints());
        assertEquals(-7, groupH2018.getTeamByName("Colombia").getFairPlayPoints());
        assertEquals("1: Colombia, Pld: 3, W: 2, D: 0, L: 1, GF: 5, GA: 2, GD: +3, Pts: 6",
                groupH2018.getTableRowByTeamName("Colombia"));
        assertEquals("2: Japan, Pld: 3, W: 1, D: 1, L: 1, GF: 4, GA: 4, GD: 0, Pts: 4",
                groupH2018.getTableRowByTeamName("Japan"));
        assertEquals("3: Senegal, Pld: 3, W: 1, D: 1, L: 1, GF: 4, GA: 4, GD: 0, Pts: 4",
                groupH2018.getTableRowByTeamName("Senegal"));
        assertEquals("4: Poland, Pld: 3, W: 1, D: 0, L: 2, GF: 2, GA: 5, GD: -3, Pts: 3",
                groupH2018.getTableRowByTeamName("Poland"));
    }
}
