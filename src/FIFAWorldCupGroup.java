

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
     */
    @Override
    public int compareTeams(Team team1, Team team2) {
        int resultBeforeHeadToHead = compareTeamsBeforeHeadToHead(team1, team2);
        if (resultBeforeHeadToHead != 0) {
            return resultBeforeHeadToHead;
        }
        // compare head-to-head record
        FIFAWorldCupGroup headToHeadGroup = createSubGroup(team1);

        // disciplinary record

        // drawing of lots

        return 0; // partial implementation for now
    }

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
        return new FIFAWorldCupGroup(new Team[]{team}); // to be implemented
    }
}
