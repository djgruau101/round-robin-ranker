import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * The TeamFactory class manages the creation of unique Team instances, that is,
 * no two teams have the same name.
 *
 * Attributes:
 * teamRegistry: a map between football team names and their respective Team instances.
 *
 * teamRegistry stores Team instances created by the TeamFactory class and is used
 * to check whether an instance of a given name already exists before creating a new instance.
 *
 * Usage:
 * The TeamFactory class provides functionality for creating new Team instances via the
 * overloaded createTeam methods. The name of the team must be provided, and
 * the matches map is set to the input map, or empty map if there is no map in the input.
 *
 * Example usage:
 * ```
 * TeamFactory factory = new TeamFactory();
 * Team team1 = factory.createTeam("Real Madrid");
 * Team team2 = factory.createTeam("Manchester City", Map.of("Arsenal", "0-1"));
 * Team team3 = factory.createTeam("Manchester City");
 * ```
 *
 * In this example, team3 will refer to the same team instance as team2,
 * therefore maintaining the fact that Manchester City lost 0-1 to Arsenal.
 *
 * @author Daniel Luo
 */
public class TeamFactory {

    /**
     * The keys are the names of the teams for which a Team instance is created.
     * Each team name maps to a Team instance with the given name.
     */
    private Map<String,Team> teamRegistry;

    public TeamFactory() {
        this.teamRegistry = new HashMap<>();
    }

    /**
     * Creates an instance of the Team class if teamRegistry does not contain the given team name,
     * and sets its matches to the given 'matches' set.
     * Otherwise, return the Team instance from teamRegistry that has the given team name.
     *
     * This method ensures uniqueness of all Team instances, that is, all instances have different names.
     *
     * @param name A team name.
     * @param matches A set containing the matches the team has played.
     * @return A Team instance.
     */
    public Team createTeam(String name, Set<Team.Match> matches) {
        if (teamRegistry.containsKey(name)) {
            // If a team with the same name already exists, return it
            return teamRegistry.get(name);
        } else {
            // Otherwise, create a new team, register it, and return
            Team newTeam = Team.createInstance(name, matches);
            teamRegistry.put(name, newTeam);
            return newTeam;
        }
    }

    /**
     * Creates an instance of the Team class if teamRegistry does not contain the given team name,
     * and sets its matches to an empty set.
     * Otherwise, return the Team instance from teamRegistry that has the given team name.
     *
     * This method ensures uniqueness of all Team instances, that is, all instances have different names.
     *
     * @param name A team name.
     * @return A Team instance.
     */
    public Team createTeam(String name) {
        if (teamRegistry.containsKey(name)) {
            // If a team with the same name already exists, return it
            return teamRegistry.get(name);
        } else {
            // Otherwise, create a new team, register it, and return
            Team newTeam = Team.createInstance(name); // We don't need to care about matches
            teamRegistry.put(name, newTeam);
            return newTeam;
        }
    }
}
