import model.Board;
import model.Move;
import model.Piece;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Perft, the move path enumeration performace test, recursively generates moves
 * for current position and all children up to a certain depth. This is a popular and efficient test
 * for Chess engines because the count of nodes can be compared to a list of pre-determined (<a href="https://www.chessprogramming.org/Perft_Results">values</a>)
 * to ensure accuracy.
 */
public class TestMoveGeneration {

    // Counts the number of positions that can be reached after a certain number of moves
    int MoveGenerationTest (int depth, boolean color, Board board) {
        if (depth == 0) {
            return 1;
        }

        HashMap<Piece, ArrayList<int[]>> moves = new HashMap<>(board.getAllPossibleMoves(color));
        int numPositions = 0;

        for (Piece p : moves.keySet()) {
            ArrayList<int[]> pMoves = new ArrayList<>(moves.get(p));
            for (int[] move : pMoves) {
                board.movePiece(new Move(p, move[0], move[1]));
                numPositions += MoveGenerationTest( depth - 1, !color, board);
                board.undoLastMove();
            }
        }

        return numPositions;
    }
}
