package pathfinder.informed;

import java.util.*;



/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, A* search.
 */
public class Pathfinder  {
    
    Pathfinder(MazeProblem problem) {  
        
    }
    
    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    public static ArrayList<String> solve(MazeProblem problem) {
        if(problem.KEY_STATE == null) {
            return null;
        }

        ArrayList<String> solutionKey = new ArrayList<String>();
        ArrayList<String> solutionGoal = new ArrayList<String>();
        Set<MazeState> keySet = Collections.singleton(problem.KEY_STATE);
        solutionKey = solveSearch(problem.INITIAL_STATE, keySet, problem);
        solutionGoal = solveSearch(problem.KEY_STATE, problem.GOAL_STATES, problem);

        if (solutionKey == null || solutionGoal == null) {
            return null;
        }
        solutionKey.addAll(solutionGoal);

        return solutionKey;

    }
    
    /**
     * Helper method that Given an initial state, set of goals, and a Maze problem,
     * returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * @param initial A maze state (row,col) where we're starting the search
     * @param goal A set of all the possible goals within the maze
     * @param problem problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    private static ArrayList<String> solveSearch(MazeState initial, Set<MazeState> goal, MazeProblem problem) {
        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<>();
        HashSet<MazeState> visited = new HashSet<MazeState>();

        frontier.add(new SearchTreeNode(initial, null, null, 0));

        while (!frontier.isEmpty()) {
            SearchTreeNode current = frontier.poll();

            if (goal.contains(current.state)) {
                return getPath(current);
            }

            if (!visited.contains(current.state)) {
                visited.add(current.state);

                for (Map.Entry<String, MazeState> action : problem.getTransitions(current.state).entrySet()) {
                    SearchTreeNode newNode = new SearchTreeNode(action.getValue(), action.getKey(), current, 0);

                    newNode.cost += (current.cost + problem.getCost(newNode.state) + getFutureCost(newNode, goal)
                            - getFutureCost(current, goal));

                    if (!visited.contains(newNode.state)) {
                        frontier.add(newNode);
                    }
                }

            }

        }

        return null;
    }

    /**
     * Given a goal node, returns a path of taken steps to a goal by traversing
     * upward the search tree 
     * @param current search tree node which is a desired goal
     * @return ArrayList of the action order from the initial to the goal state 
     */
    private static ArrayList<String> getPath(SearchTreeNode current) {
        ArrayList<String> solution = new ArrayList<String>();
        while (current.parent != null) {
            solution.add(0, current.action);
            current = current.parent;
        }

        return solution;

    }

    
    
    
    /**
     * Heuristic method that estimates the future cost of a node to the nearest goal
     * @param node  A search tree node starting point for cost estimation
     * @return An integer of the distance between a given node and the closest desired goal  
     */
    private static int getFutureCost(SearchTreeNode node, Set<MazeState> goals) {

        int minDis = Integer.MAX_VALUE;
        for (MazeState goal : goals) {
            minDis = Math.min(minDis, (Math.abs(node.state.col - goal.col) + Math.abs(node.state.row - goal.row)));
        }

        return minDis;

    }

}


/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 * [!] NOTE: Feel free to change this however you see fit to adapt your solution 
 *     for A* (including any fields, changes to constructor, additional methods)
 */
class SearchTreeNode implements Comparable<SearchTreeNode> {

    MazeState state;
    String action;
    SearchTreeNode parent;
    int cost;

    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state  The MazeState (row, col) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    SearchTreeNode(MazeState state, String action, SearchTreeNode parent, int cost) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.cost = cost;
    }

    @Override
    public int compareTo(SearchTreeNode compareNode) {
        return this.cost - compareNode.cost;
    }

}


