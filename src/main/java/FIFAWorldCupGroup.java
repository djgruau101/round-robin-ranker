import java.util.Random;

public class FIFAWorldCupGroup extends Group {

    public FIFAWorldCupGroup(Team[] teams) {
        super(teams, 4, 1);
    }

    /**
     * How the FIFA World Cup ranking system breaks ties:
     *
     * 1. Points earned in all group matches (Pts)
     * 2. Goal difference in all group matches (GD)
     * 3. Number of goals scored in all group matches (GF)
     *
     * If there are teams that are still tied via the first three conditions,
     * the following are used to rank them:
     *
     * 4. Points earned in matches played between the teams in question
     * 5. Goal difference in matches played between the teams in question
     * 6. Number of goals scored in matches played between the teams in question
     * 7. Fair play points in all group matches:
     *     Yellow card: -1
     *     Indirect red card: -3
     *     Direct red card: -4
     *     Yellow card and direct red card: -5
     * 8. Drawing of lots
     *
     * @param team1 A football team.
     * @param team2 A football team that team1 is compared to.
     *
     * @return A positive integer if team1 is ranked above team2,
     *         a negative integer if team2 is ranked above team1.
     * @throws IllegalArgumentException If team1 and team2 are identical.
     */
    @Override
    public int compareTeams(Team team1, Team team2) throws IllegalArgumentException {
        if (team1.equals(team2)) {
            throw new IllegalArgumentException("Can not compare two identical teams.");
        }
        int resultBeforeHeadToHead = compareTeamsBeforeHeadToHead(team1, team2);
        if (resultBeforeHeadToHead != 0) {
            return resultBeforeHeadToHead;
        }
        // comparing head-to-head record can only be done if both teams have faced each other
        if (havePlayedAgainst(team1, team2)) {
            FIFAWorldCupGroup headToHeadGroup = createSubGroup(team1);
            int resultDuringHeadToHead = headToHeadGroup.compareTeamsBeforeHeadToHead(
                    headToHeadGroup.getTeamByName(team1.getName()), headToHeadGroup.getTeamByName(team2.getName()));
            if (resultDuringHeadToHead != 0) {
                return resultDuringHeadToHead;
            }
        }
        // compare disciplinary record
        if (team1.getFairPlayPoints() != team2.getFairPlayPoints()) {
            return Integer.compare(team1.getFairPlayPoints(), team2.getFairPlayPoints());
        }
        // drawing of lots
        Random random = new Random();
        int randomValue = random.nextInt(2); // will be either 0 or 1
        return (randomValue == 0) ? -1 : 1;
    }

    /**
     * How the FIFA World Cup group stage ranking system breaks ties before looking at head-to-head record:
     *
     * 1. Points earned in all group matches (Pts)
     * 2. Goal difference in all group matches (GD)
     * 3. Number of goals scored in all group matches (GF)
     *
     * @param team1 A football team.
     * @param team2 A football team that team1 is compared to.
     *
     * @return A positive integer if team1 is ranked above team2,
     *         a negative integer if team2 is ranked above team1,
     *         or 0 if team1 is tied with team2.
     */
    @Override
    protected int compareTeamsBeforeHeadToHead(Team team1, Team team2) {
        if (team1.getPoints() != team2.getPoints()) {
            return team1.getPoints() - team2.getPoints();
        } if (team1.getGoalDifference() != team2.getGoalDifference()) {
            return Integer.compare(team1.getGoalDifference(), team2.getGoalDifference());
        } if (team1.getGoalsFor() != team2.getGoalsFor()) {
            return Integer.compare(team1.getGoalsFor(), team2.getGoalsFor());
        }
        return 0;
    }

    @Override
    protected FIFAWorldCupGroup createSubGroup(Team team) {
        Team[] headToHeadTeams = this.getTeamNames().parallelStream()
                .map(this::getTeamByName).filter(t -> compareTeamsBeforeHeadToHead(team, t) == 0).toArray(Team[]::new);
        return new FIFAWorldCupGroup(headToHeadTeams);
    }

    enum Card implements CardEnum {

        YELLOW(-1),
        INDIRECT_RED(-3),
        DIRECT_RED(-4),
        YELLOW_AND_DIRECT_RED(-5);

        private final int penalty;

        Card(int penalty) {
            this.penalty = penalty;
        }

        @Override
        public int getPenalty() {
            return penalty;
        }
    }
}
