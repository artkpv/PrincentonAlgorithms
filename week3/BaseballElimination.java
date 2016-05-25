// Created at 18.05.2016 Ср 21:36
// Author: Artyom K. <w1ld at inbox dot ru> 
//
// Assignment:
//  - http://coursera.cs.princeton.edu/algs4/assignments/baseball.html
//  - http://coursera.cs.princeton.edu/algs4/checklists/baseball.html

import java.lang.String;
import java.utils.ArrayList;
import java.utils.HashMap;
import edu.princeton.cs.algs4.In;

// NEXT:
// - exceptions
// - Construct FlowNetwork with FlowEdges. See:
//   http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/FlowNetwork.java.html
//   http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/FlowEdge.java.html
// - Run FF: 
//   http://algs4.cs.princeton.edu/64maxflow/FordFulkerson.java.html

public class BaseballElimination {

    private class Team {
        public String Name;
        public int Index;
        public int Wins;
        public int Losses;
        public int Remaining;
    }

    HashMap<String, Team> teamNames = new HashMap<String, Team>();
    ArrayList<Team> teamIndexes = new ArrayList<Team>();
    int[][] remaining;
    FlowNetwork graph;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        parse_file(filename);
    }

    private void parse_file(String filename) {
        In in = new In(filename);

        int numberOfTeams = in.readInt();
        myTeams = new ArrayList<String>(numberOfTeams);

        remaining = new int[numberOfTeams][numberOfTeams];

        for(int i = 0; i < numberOfTeams; i++) {
            String name = in.readString();
            assert name != null;

            int wins = in.readInt();
            int losses = in.readInt();
            int remaining = in.readInt();
            Team t = new Team();
            t.Name = name;
            t.Index = i;
            t.Wins = wins;
            t.Losses = losses;
            t.Remaining = remaining;
            teamNames.put(name, t);
            teamIndexes[i] = t;

            for(int j = 0; j < numberOfTeams; j++) {
                remaining[i][j] = in.readInt();
            }
        }
    } 

    // number of teams
    public int numberOfTeams() {
        return myTeams.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teamNames.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        return teamNames[team].Wins;
    }

    // number of losses for given team
    public int losses(String team) {
        return teamNames[team].Losses;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return teamNames[team].Remaining;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        int i = teamNames[team1].Index;
        int j = teamNames[team2].Index;
        return remaining[i][j];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        // check trivial elimination condition:
        Team t = teamName[team];
        int maxWins = t.Wins + t.Remaining;
        for(int i = 0; i < teamIndexes.size(); i++) {
            Team ti = teamIndexes[i];
            if(ti.Wins > maxWins)
                return true;
        }

        // if we get here then it is not trivially eliminated
        construct_graph(t);
    }

    private void construct_graph(Team checkedTeam) {
        assert teamNames.size() > 0;
        assert remaining.size() > 0;
        assert teamIndexes.size() > 0;

        int V = 1 + 1; // source + target
        int numberOfTeamsLeft = teamIndexes.size() - 1; 
        V += numberOfTeamsLeft;
        // Add number of gameos:
        int games = (int) (numberOfTeamsLeft * (numberOfTeamsLeft - 1) / 2);
        V += games;

        graph = new FlowNetwork(V);

        int checkedTeamMaxGames = checkedTeam.Wins + checkedTeam.Remaining;

        // remaining table (with the excluded command) to the graph (without it)
        int sVertex = 0,
            tVertex = 1,
            row2 = -1,
            gamesCounter = 0;

        for(int row = 0; row < remaining.size(); row++) {
            if(row == checkedTeam.Index) continue;
            row2++;
            
            int col2 = -1;
            for(int col = row; col < remaining[row].size(); col++) {
                if(col == checkedTeam.Index) continue;
                col2++;
                gamesCounter++;

                int remainingGames = remaining[i][j];
                if(remainingGames > 0) {
                    // add game edge:
                    int final numberOfVertixesBeforeGames = 2;
                    int gameVertex = (gamesCounter - 1) + numberOfVertixesBeforeGames; 
                    FlowEdge fromS = new FlowEdge(s, gameVertex, remainingGames);
                    graph.AddEdge(fromS);

                    // add row command edge:
                    int final numberOfVertixesBeforeCommands = numberOfVertixesBeforeGames + games;
                    int rowCommandVertex = numberOfVertixesBeforeCommands + row2;
                    FlowEdge toRowCommand = new FlowEdge(gameVertex, rowCommandVertex, Double.POSITIVE_INFINITY);
                    graph.AddEdge(toRowCommand);
                    double rowCommandCapacity = checkedTeamMaxGames - teamIndexes[row].Wins;
                    FlowEdge fromRowCommand = new FlowEdge(rowCommandVertex, tVertex, rowCommandCapacity);
                    graph.AddEdge(fromRowCommand);

                    // add col command edge:
                    int colCommandVertex = numberOfVertixesBeforeCommands + col2;
                    FlowEdge toColCommand = new FlowEdge(gameVertex, colCommandVertex, Double.POSITIVE_INFINITY);
                    graph.AddEdge(toColCommand);
                    double colCommandCapacity = checkedTeamMaxGames - teamIndexes[col].Wins;
                    FlowEdge fromColCommand = new FlowEdge(colCommandVertex, tVertex, colCommandCapacity);
                    graph.AddEdge(fromColCommand);
                }
            }
        }
        
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        throw null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
