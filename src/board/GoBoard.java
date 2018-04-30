package board;

import kgs_mcts.Board;
import kgs_mcts.CallLocation;
import kgs_mcts.Move;
import java.util.Arrays;
import java.util.ArrayList;

/* Functionality to simulate the board, for the MCTS */

/* TODO:
 *  - Implement getMoves (should be pretty easy)
 *  - Implement scoring (have to implement Komi)
 *  - Implement Suicide Rule and Ko Rule detection
 */

// Lots of this code is borrowed from or slightly modified from the Riddles.io Go
// engine. All credit to them for the simulation, we just cannibalized it to make the
// simulation work.

public class GoBoard implements kgs_mcts.Board {

    private int mFoundLiberties;
    private boolean[][] mAffectedFields;
    String[][] board;
    int width;
    int height;
    int player;

    public GoBoard(String[] field, int field_height, int field_width, int player)
    {
        board = new String[field_width][field_height];
        width = field_width;
        height = field_height;
        for (int i = 0; i < field.length; i++)
        {
            board[i % field_width][i / field_height] = field[i];
        }
        this.player = player;
    }

    private GoBoard(String[][] b, int p){
        this.board = new String[b.length][];
        for(int i = 0; i < b.length; i++){
            this.board[i] = Arrays.copyOf(b[i], b[i].length);
        }
        this.player = p;
    }

    @Override
    public Board duplicate() {
        return new GoBoard(this.board, this.player); // return a copy of the thing
    }

    @Override
    public ArrayList<Move> getMoves(CallLocation location) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if(board[i][j].equals(".")){
                    moves.add(new GoMove(i, j));
                }
            }
        }
        return moves;
    }

    // Heavily modified version of GoLogic.transformPlaceMove from the Go engine
    @Override
    public void makeMove(Move move) {

        GoMove m = (GoMove) move;

        String[][] originalBoard = getBoardArray(board);

        if (m.getX() > width || m.getY() > height || m.getX() < 0 || m.getY() < 0) {
            //move.setException(new InvalidMoveException("Move out of bounds"));
            // Not sure if we should bother throwing an exception here, maybe just return
        }

        if (!this.get(m.getX(), m.getY()).equals(".")) { /*Field is not available */
            //move.setException(new InvalidMoveException("Chosen position is already filled"));
        }

        this.set(m.getX(), m.getY(), this.playerString());
        board.setLastPosition(point); // Not sure what to do here

        int stonesTaken = checkCaptures(board, playerId); // Snatch this
        move.setStonesTaken(stonesTaken); // Snatch this

        // snatch this
        if (!checkSuicideRule(board, point, String.valueOf(playerId))) { /* Check Suicide Rule */
            //move.setException(new InvalidMoveException("Illegal Suicide Move"));
        }

        // Oh, this basically resets the board if something went wrong, rather than
        // checking if something went wrong before editing the board state. maybe
        // replace the exceptions with a bool
        if (move.getException() != null) {
            board.initializeFromArray(originalBoard);
        }
    }

    private Boolean checkSuicideRule(int x, int y, String move) {
        mFoundLiberties = 0;
        boolean[][] mark = new boolean[this.board.length][this.board[0].length];
        for (int tx = 0; tx < this.board.length; tx++) {
            for (int ty = 0; ty < this.board[tx].length; ty++) {
                mAffectedFields[tx][ty] = false;
                mark[tx][ty] = false;
            }
        }
        flood(mark, x, y, move, 0);
        return (mFoundLiberties > 0);
>>>>>>> 901f1543e5e3fa43678885f598fdec4413c70020
    }

    private void makeCaptures() {

    }

    private void eliminateSuicideMoves() {
        // any time a move would result in a capture should probably just place the move
        // and see if capture detection would detect one, then just mark that
    }

    private void eliminateKoMoves() {
        // if a move would result in a board state from 2 turns ago
    }

    private void detectCapture() {

    }

    private boolean isCapture(GoMove move)
    {
        return false;
    }

    private void set(int x, int y, String s)
    {
        board[x][y] = s;
    }

    private String get(int x, int y)
    {
        return board[x][y];
    }

    private String playerString()
    {
        if (player == 0) return "0";
        return "1";
    }

    // Not sure how to implement this...
    @Override
    public boolean gameOver() {
        return false;
    }

    @Override
    public int getCurrentPlayer() {
        return player;
    }

    @Override
    public int getQuantityOfPlayers() {
        return 2;
    }

    @Override
    public double[] getScore() {
        return new double[0];
    }

    @Override
    public double[] getMoveWeights() {
        return new double[0];
    }

    @Override
    public void bPrint() {
        String r = "";
        int counter = 0;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (counter > 0) {
                    r += ",";
                }
                r += board[x][y];
                counter++;
            }
        }
        System.out.println(r);
    }

    private void flood(boolean [][]mark, int x, int y, String srcColor, int stackCounter) {
        // Make sure row and col are inside the board
        if (x < 0) return;
        if (y < 0) return;
        if (x >= this.board.length) return;
        if (y >= this.board[0].length) return;

        // Make sure this field hasn't been visited yet
        if (mark[x][y]) return;

        // Make sure this field is the right color to fill
        if (!board[x][y].equals(srcColor)) {
            if (this.board[x][y].equals(".")) {
                mFoundLiberties++;
            }
            return;
        }
        // Fill field with target color and mark it as visited
        this.mAffectedFields[x][y] = true;
        mark[x][y] = true;

        // Recursively check surrounding fields
        if (stackCounter < 1024) {
            flood(mark, x - 1, y, srcColor, stackCounter+1);
            flood(mark, x + 1, y, srcColor, stackCounter+1);
            flood(mark, x, y - 1, srcColor, stackCounter+1);
            flood(mark, x, y + 1, srcColor, stackCounter+1);
        }
    }
}
