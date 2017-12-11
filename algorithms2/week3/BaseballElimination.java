// Created at 18.05.2016 Ср 21:36
// Author: Artyom K. <w1ld at inbox dot ru> 
//
// Assignment:
//  - http://coursera.cs.princeton.edu/algs4/assignments/baseball.html
//  - http://coursera.cs.princeton.edu/algs4/checklists/baseball.html

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;

// NEXT:
// - calculate certificate of elimination correctly
/*

Test 9: calls to certificateOfElimination()
  *  teams4.txt
  *  teams4a.txt
  *  teams4b.txt
  *  teams5.txt
     - invalid certificate of elimination for 'Detroit'
     - student   certificate of elimination = { Boston New_York }
     - reference certificate of elimination = { New_York Baltimore Boston Toronto }
  *  teams5a.txt
     - invalid certificate of elimination for 'Detroit'
     - student   certificate of elimination = { Toronto New_York Boston }
     - reference certificate of elimination = { New_York Baltimore Boston Toronto }
  *  teams5b.txt
  *  teams5c.txt
  *  teams7.txt
     - invalid certificate of elimination for 'Ireland'
     - student   certificate of elimination = { France U.S.A. }
     - reference certificate of elimination = { U.S.A. France Germany }
  *  teams8.txt
     - invalid certificate of elimination for 'Harvard'
     - student   certificate of elimination = { Princeton Brown }
     - reference certificate of elimination = { Brown Columbia Cornell Dartmouth Penn Princeton }
  *  teams10.txt
  *  teams12.txt
     - invalid certificate of elimination for 'Japan'
     - student   certificate of elimination = { Russia Poland }
     - reference certificate of elimination = { Poland Russia Brazil Iran }
  *  teams24.txt
  *  teams29.txt
     - invalid certificate of elimination for 'LA_Lakers'
     - student   certificate of elimination = { Cleveland Atlanta Chicago }
     - reference certificate of elimination = { Atlanta Boston Chicago Cleveland Dallas Denver }
  *  teams30.txt
     - invalid certificate of elimination for 'Team18'
     - student   certificate of elimination = { Team21 Team10 }
     - reference certificate of elimination = { Team10 Team16 Team19 Team21 }
  *  teams32.txt
     - invalid certificate of elimination for 'Team25'
     - student   certificate of elimination = { Team8 Team0 }
     - reference certificate of elimination = { Team0 Team6 Team8 Team11 Team26 }
  *  teams36.txt
     - invalid certificate of elimination for 'Team21'
     - student   certificate of elimination = { Team20 Team18 }
     - reference certificate of elimination = { Team18 Team20 Team22 Team23 }
  *  teams42.txt
  *  teams48.txt
     - invalid certificate of elimination for 'Team6'
     - student   certificate of elimination = { Team39 Team38 }
     - reference certificate of elimination = { Team38 Team39 Team40 }
  *  teams12-allgames.txt
==> FAILED

Test 12: check that certificateOfElimination() returns null
  *  teams4.txt
     - fails when checking the elimination status of 'Atlanta'
     - student   certificate of elimination = { }
     - reference certificate of elimination = null
  *  teams5.txt
     - fails when checking the elimination status of 'New_York'
     - student   certificate of elimination = { }
     - reference certificate of elimination = null
==> FAILED




******************************************************************************
*          MEMORY
******************************************************************************

Computing memory of BaseballElimination
*-----------------------------------------------------------
Running 4 total tests.

Student   vertices     = 0.50 N^2 + -0.50 N + 2.00   (R^2 = 1.000)
Reference vertices     = 0.50 N^2 + -0.50 N + 3.00   (R^2 = 1.000)
=> passed
Student   edges        = 1.50 N^2 + -3.64 N + 3.12   (R^2 = 1.000)
Reference edges        = 1.50 N^2 + -3.50 N + 2.00   (R^2 = 1.000)
=> passed
Student   memory of G  = 176.26 N^2 + -398.55 N + 461.00   (R^2 = 1.000)
Reference memory of G  = 176.00 N^2 + -384.00 N + 384.00   (R^2 = 1.000)
=> passed
Student   memory       = 238.85 N^2 + -267.90 N + 95.43   (R^2 = 1.000)
Reference memory       = 3.99 N^2 + 222.25 N + 396.00   (R^2 = 1.000)
=> FAILED, memory must grow asymptotically (N^2 term) no faster than 50x the reference solution.
Total: 3/4 tests passed!
*/ 

public class BaseballElimination {

    private class Team {
        public String Name;
        public int Index;
        public int Wins;
        public int Losses;
        public int Remaining;
    }

    private HashMap<String, Team> teamNames = new HashMap<String, Team>();
    private ArrayList<Team> teamIndexes = new ArrayList<Team>();
    private int[][] remaining;
    private FlowNetwork graph;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        parse_file(filename);
    }

    private void parse_file(String filename) {
        In in = new In(filename);

        int numberOfTeams = in.readInt();
        teamNames = new HashMap<String, Team>(numberOfTeams);
        teamIndexes = new ArrayList<Team>(numberOfTeams);

        remaining = new int[numberOfTeams][numberOfTeams];

        for(int i = 0; i < numberOfTeams; i++) {
            String name = in.readString();
            assert name != null;

            int wins = in.readInt();
            int losses = in.readInt();
            int remainingGames = in.readInt();
            Team t = new Team();
            t.Name = name;
            t.Index = i;
            t.Wins = wins;
            t.Losses = losses;
            t.Remaining = remainingGames;
            teamNames.put(name, t);
            teamIndexes.add(i, t);

            for(int j = 0; j < numberOfTeams; j++) {
                remaining[i][j] = in.readInt();
            }
        }
    } 

    // number of teams
    public int numberOfTeams() {
        return teamNames.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teamNames.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if(teamNames.get(team) == null)
            throw new java.lang.IllegalArgumentException();
        return teamNames.get(team).Wins;
    }

    // number of losses for given team
    public int losses(String team) {
        if(teamNames.get(team) == null)
            throw new java.lang.IllegalArgumentException();
        return teamNames.get(team).Losses;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if(teamNames.get(team) == null)
            throw new java.lang.IllegalArgumentException();
        return teamNames.get(team).Remaining;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if(teamNames.get(team1) == null)
            throw new java.lang.IllegalArgumentException();
        if(teamNames.get(team2) == null)
            throw new java.lang.IllegalArgumentException();
        int i = teamNames.get(team1).Index;
        int j = teamNames.get(team2).Index;
        return remaining[i][j];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if(teamNames.get(team) == null)
            throw new java.lang.IllegalArgumentException();
        Iterable<String> certificate = certificateOfElimination(team);
        
        if(certificate == null || !certificate.iterator().hasNext())
            return false;
        return true;
    }

    private HashMap<Integer, int[]> gameVertexToCommandIndexesMapping;

    private void construct_graph(Team checkedTeam) {
        assert teamNames.size() > 0;
        assert remaining.length > 0;
        assert teamIndexes.size() > 0;

        int V = 1 + 1; // source + target
        int numberOfTeamsLeft = teamIndexes.size() - 1; 
        V += numberOfTeamsLeft;
        // Add number of gameos:
        int games = (int) (numberOfTeamsLeft * (numberOfTeamsLeft - 1) / 2);
        V += games;

        graph = new FlowNetwork(V);
        gameVertexToCommandIndexesMapping = new HashMap<Integer, int[]>(games);

        // remaining table (with the excluded command) to the graph (without it)
        int sVertex = 0,
            tVertex = 1,
            row2 = -1,
            gamesCounter = 0;

        int checkedTeamMaxGames = checkedTeam.Wins + checkedTeam.Remaining;
        final int numberOfVertixesBeforeGames = 2;
        for(int row = 0; row < remaining.length; row++) {
            if(row == checkedTeam.Index) continue;
            row2++;
            
            int col2 = row2 - 1;
            for(int col = row; col < remaining[row].length; col++) {
                if(col == checkedTeam.Index) continue;
                col2++;
                if(col == row) continue;
                gamesCounter++;

                int remainingGames = remaining[row][col];
                if(remainingGames > 0) {
                    // add game edge:
                    int gameVertex = numberOfVertixesBeforeGames + (gamesCounter - 1); 
                    FlowEdge fromS = new FlowEdge(sVertex, gameVertex, remainingGames);
                    graph.addEdge(fromS);
                    gameVertexToCommandIndexesMapping.put(gameVertex, new int[] { col, row });

                    // add row command edge:
                    final int numberOfVertixesBeforeCommands = numberOfVertixesBeforeGames + games;
                    int rowCommandVertex = numberOfVertixesBeforeCommands + row2;
                    FlowEdge toRowCommand = new FlowEdge(gameVertex, rowCommandVertex, Double.POSITIVE_INFINITY);
                    graph.addEdge(toRowCommand);

                    // add col command edge:
                    int colCommandVertex = numberOfVertixesBeforeCommands + col2;
                    FlowEdge toColCommand = new FlowEdge(gameVertex, colCommandVertex, Double.POSITIVE_INFINITY);
                    graph.addEdge(toColCommand);
                }
            }
        }

        // add edges to target:
        final int numberOfVertixesBeforeCommands = numberOfVertixesBeforeGames + games;
        int i2 = -1;
        for(int i = 0; i < teamIndexes.size(); i++) {
            if(i == checkedTeam.Index) continue;
            i2++;
            int commandVertex = numberOfVertixesBeforeCommands + i2;
            double commandCapacity = checkedTeamMaxGames - teamIndexes.get(i).Wins;
            FlowEdge fromCommand = new FlowEdge(commandVertex, tVertex, commandCapacity);
            graph.addEdge(fromCommand);
        }
        
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        // check trivial elimination condition:
        Team t = teamNames.get(team);
        if(t == null)
            throw new java.lang.IllegalArgumentException();

        int maxWins = t.Wins + t.Remaining;
        for(int i = 0; i < teamIndexes.size(); i++) {
            Team ti = teamIndexes.get(i);
            if(ti.Wins > maxWins)
                return Arrays.asList(new String[] { ti.Name } );
        }

        // if we get here then it is not trivially eliminated
        construct_graph(t);
        //StdOut.print(graph.toString());

        FordFulkerson ff = new FordFulkerson(graph, 0, 1);

        // if all edges pointing from s are full -> team can be not eliminated:
        ArrayList<String> certificate = new ArrayList<String>();
        int gameVertex = -1;
        for(FlowEdge e : graph.adj(0)){
            if(e.residualCapacityTo(e.other(0)) != 0) {
                gameVertex = e.other(0);
                int[] commands = gameVertexToCommandIndexesMapping.get(gameVertex);
                String first = teamIndexes.get((int)commands[0]).Name;
                String second = teamIndexes.get((int)commands[1]).Name;

                if(!certificate.contains(first))
                    certificate.add(first);
                if(!certificate.contains(second))
                    certificate.add(second);
            }
        }
        return certificate;
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
