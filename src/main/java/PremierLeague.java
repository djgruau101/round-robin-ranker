public class PremierLeague extends Group {

    public PremierLeague(Team[] teams) {
        super(teams, 20, 2);
    }

    /**
     * How the Premier League ranking system breaks ties:
     *
     * 1. Points earned in all group matches (Pts)
     * 2. Goal difference in all group matches (GD)
     * 3. Number of goals scored in all group matches (GF)
     *
     * If there are teams that are still tied via the first three conditions,
     * they occupy the same position on the league table.
     * However, the following will be applied to determine the League Champion,
     * clubs to be relegated, or clubs qualifying to other competitions amongst tied clubs:
     *
     * 4. Points earned in matches played between the clubs in question
     * 5. Number of away goals scored in matches played between the tied clubs
     * 6. Play-offs
     *
     * @param team1 A football team.
     * @param team2 A football team that team1 is compared to.
     *
     * @return A positive integer if team1 is ranked above team2,
     *         a negative integer if team2 is ranked above team1,
     *         or 0 if both teams are equal and their equality does not determine
     *         the League Champion, clubs to be relegated, or clubs qualifying
     *         to other competitions amongst tied clubs.
     */
    @Override
    public int compareTeams(Team team1, Team team2) {
        int resultBeforeHeadToHead = compareTeamsBeforeHeadToHead(team1, team2);
        if (resultBeforeHeadToHead != 0) {
            return resultBeforeHeadToHead;
        }

        // compare head-to-head record and away goals

        return 0; // partial implementation for now
    }

    @Override
    protected int compareTeamsBeforeHeadToHead(Team team1, Team team2) {
        if (team1.getPoints() != team2.getPoints()) {
            return team1.getPoints() - team2.getPoints(); // max number of points is 9, min is 0
        } if (team1.getGoalDifference() != team2.getGoalDifference()) { // compare GD
            return Integer.compare(team1.getGoalDifference(), team2.getGoalDifference());
        } if (team1.getGoalsFor() != team2.getGoalsFor()) { // compare GA
            return Integer.compare(team1.getGoalsFor(), team2.getGoalsFor());
        }
        return 0;
    }

    @Override
    protected Group createSubGroup(Team team) {
        Team[] headToHeadTeams = this.getTeamNames().parallelStream()
                .map(this::getTeamByName).filter(t -> compareTeamsBeforeHeadToHead(team, t) == 0).toArray(Team[]::new);
        return new PremierLeague(headToHeadTeams);
    }

    enum Card implements CardEnum {
        // fair play points are not used to break ties in the Premier League
        YELLOW(0),
        INDIRECT_RED(0),
        DIRECT_RED(0),
        YELLOW_AND_DIRECT_RED(0);

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
