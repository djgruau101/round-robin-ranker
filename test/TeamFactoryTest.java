import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TeamFactoryTest {

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
}
