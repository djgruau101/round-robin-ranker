import java.util.Random;

public class UEFAEuroGroup extends Group {

    public UEFAEuroGroup(Team[] teams) {
        super(teams, 4, 1);
    }

    @Override
    public int compareTeams(Team team1, Team team2) throws IllegalArgumentException {
        if (team1.equals(team2)) {
            throw new IllegalArgumentException("Can not compare two identical teams.");
        }
        if (team1.getNumberOfMatchesPlayed() == 0 && team2.getNumberOfMatchesPlayed() == 0) {
            return 0;
        }
        int resultBeforeHeadToHead = compareTeamsBeforeHeadToHead(team1, team2);
        if (resultBeforeHeadToHead != 0) {
            return resultBeforeHeadToHead;
        }
        // comparing head-to-head record can only be done if both teams have faced each other
        if (havePlayedAgainst(team1, team2)) {
            UEFAEuroGroup headToHeadGroup = createSubGroup(team1);
            Team team1HeadToHead = headToHeadGroup.getTeamByName(team1.getName());
            Team team2HeadToHead = headToHeadGroup.getTeamByName(team2.getName());
            int resultDuringHeadToHead = compareTeamsDuringHeadToHead(team1HeadToHead, team2HeadToHead);
            if (resultDuringHeadToHead != 0) {
                return resultDuringHeadToHead;
            }
            // only applies if the original subgroup has more than two teams
            UEFAEuroGroup smallerHeadToHeadGroup = headToHeadGroup.createSubGroup(team1);
            if (smallerHeadToHeadGroup.getTeams().length > 0) {
                team1HeadToHead = smallerHeadToHeadGroup.getTeamByName(team1.getName());
                team2HeadToHead = smallerHeadToHeadGroup.getTeamByName(team2.getName());
                resultDuringHeadToHead = compareTeamsDuringHeadToHead(team1HeadToHead, team2HeadToHead);
                if (resultDuringHeadToHead != 0) {
                    return resultDuringHeadToHead;
                }
            }
        }
        if (team1.getGoalDifference() != team2.getGoalDifference()) {
            return Integer.compare(team1.getGoalDifference(), team2.getGoalDifference());
        }
        if (team1.getGoalsFor() != team2.getGoalsFor()) {
            return Integer.compare(team1.getGoalsFor(), team2.getGoalsFor());
        }
        // will proceed there only if point deductions occur
        if (team1.getNumberWins() != team2.getNumberWins()) {
            return Integer.compare(team1.getNumberWins(), team2.getNumberWins());
        }
        if (tiedTeamsBeforeFairPlay(team1).length > 2) {
            // if there were exactly two teams equal at the very end, a penalty shoot-out will decide
            if (team1.getFairPlayPoints() != team2.getFairPlayPoints()) {
                return Integer.compare(team1.getFairPlayPoints(), team2.getFairPlayPoints());
            }
        }
        // technically, the last criterion is the position in the overall Euro Qualifiers rankings
        // penalty shoot-out criterion not implemented here
        Random random = new Random();
        int randomValue = random.nextInt(2); // will be either 0 or 1
        return (randomValue == 0) ? -1 : 1;
    }

    @Override
    protected int compareTeamsBeforeHeadToHead(Team team1, Team team2) {
        return Integer.compare(team1.getPoints(), team2.getPoints());
    }

    private int compareTeamsDuringHeadToHead(Team team1, Team team2) {
        if (team1.getPoints() != team2.getPoints()) {
            return Integer.compare(team1.getPoints(), team2.getPoints());
        }
        if (team1.getGoalDifference() != team2.getGoalDifference()) {
            return Integer.compare(team1.getGoalDifference(), team2.getGoalDifference());
        }
        if (team1.getGoalsFor() != team2.getGoalsFor()) {
            return Integer.compare(team1.getGoalsFor(), team2.getGoalsFor());
        }
        return 0;
    }

    @Override
    protected UEFAEuroGroup createSubGroup(Team team) {
        Team[] headToHeadTeams = this.getTeamNames().parallelStream()
                .map(this::getTeamByName).filter(t -> compareTeamsBeforeHeadToHead(team, t) == 0).toArray(Team[]::new);
        return new UEFAEuroGroup(headToHeadTeams);
    }

    /**
     * In the Euros, if more than two teams are still equal after their number of points, head-to-head record,
     * goal difference, goals scored and number of wins, compare the disciplinary record of the teams.
     *
     * If on the last round of group stage, there are only two tied teams that are still equal and they
     * draw their match, a penalty shoot-out will decide their ranking.
     *
     * This method takes a team and return the list of teams that are equal after their
     * number of points, head-to-head record, goal difference, goals scored and number of wins.
     *
     * @param team A football team competing in a Euro competition.
     * @return A group containing exclusively the tied teams.
     */
    private Team[] tiedTeamsBeforeFairPlay(Team team) {
        return this.getTeamNames().parallelStream()
                .map(this::getTeamByName).filter(t -> compareTeamsDuringHeadToHead(team, t) == 0).toArray(Team[]::new);
    }

    enum Card implements CardEnum {

        YELLOW(-1),
        INDIRECT_RED(-3),
        DIRECT_RED(-3),
        YELLOW_AND_DIRECT_RED(-4);

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
