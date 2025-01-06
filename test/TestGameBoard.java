import controller.AIPlayer;
import controller.Player;
import model.Board;
import model.Move;
import model.Piece;
import model.pieces.King;
import model.pieces.Pawn;
import model.pieces.Queen;
import model.pieces.Rook;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestGameBoard {
    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testUndoMove() {
        // Move pawn forward
        Pawn pawn = (Pawn) board.findPieceByLocation(1, 0);
        Move move = new Move(pawn, 3,  0);
        board.movePiece(move);

        board.undoLastMove();

        assertNotNull("Pawn should be back at its original position", board.findPieceByLocation(1,0));
        assertNull("Pawn's new position should be empty after undo", board.findPieceByLocation(3,0));
        assertEquals("Pawn's should be back at its original position", board.findPieceByLocation(1,0), pawn);
        assertTrue("Pawn's first move should be restore", pawn.getFirstMove());
    }

    @Test
    public void testCaptureMove() {
        // Place a piece for capturing
        Piece pieceToCapture = new Pawn(true, 2, 0);
        board.placePiece(pieceToCapture, 2, 0);

        // Move a pawn to capture the piece
        Move move = new Move(board.findPieceByLocation(1, 1),  2, 0);
        board.movePiece(move);

        // Verify the captured piece is removed
        assertTrue("Captured piece should be in captured list", board.getCapturedPieces().contains(pieceToCapture));
        assertNull("Previous piece position should now be empty", board.findPieceByLocation(1, 1));
        assertNotNull("Pawn should occupy the captured piece's position", board.findPieceByLocation(2, 0));
    }

    @Test
    public void testPawnPromotion() {
        // Check automation promotion for black pawn
        Piece pawnToPromote = board.findPieceByLocation(1,2);
        board.placePiece(pawnToPromote,6,2);

        // Move pawn to finish line
        Piece capturedPiece = board.findPieceByLocation(7, 1);
        Move move = new Move(pawnToPromote, 7, 1);
        board.movePiece(move);

        assertTrue("Captured piece should be in captured list", board.getCapturedPieces().contains(capturedPiece));
        assertFalse("Pawn should no longer be part of board", board.getAllPieces().contains(pawnToPromote));
        assertTrue("Queen should occupy captured piece's position", board.findPieceByLocation(7, 1) instanceof Queen);
    }

    @Test
    public void undoPawnPromotion() {
        Piece pawnToPromote = board.findPieceByLocation(1,2);
        Move move = new Move(pawnToPromote, 7, 1);
        board.movePiece(move);
        Piece newQueen = board.findPieceByLocation(7,1);
        board.undoLastMove();

        assertFalse("Last move should no longer exist", board.getLastMoves().contains(move));
        assertFalse("Queen should no longer exist in board", board.getBlackPieces().contains(newQueen));
        assertTrue("Pawn should exist in board", board.getAllPieces().contains(pawnToPromote));
        assertTrue("Pawn should exist in board", board.getBlackPieces().contains(pawnToPromote));
    }

    @Test
    public void testKingSideCastling() {
        King king = (King) board.getKing(false);
        board.placePiece(null,0, 5);
        board.placePiece(null,0, 6);
        Move move = new Move(king,0, 6);
        board.movePiece(move);

        assertNull("Previous king position should now be empty", board.findPieceByLocation(0, 4));
        assertNull("Previous rook position should now be empty", board.findPieceByLocation(0, 7));
        assertEquals(king, board.findPieceByLocation(0,6));
        assertTrue("Rook should occupy position left of king", board.findPieceByLocation(0, 5) instanceof Rook);
        assertFalse("King's First move should be false", king.getLastMoves().isEmpty());
        assertFalse("Rook's First move should be false", ((Rook) board.findPieceByLocation(0, 5)).getLastMoves().isEmpty());
    }

    @Test
    public void undoQueenSideCastling() {
        King king = (King) board.getKing(false);
        Move move = new Move(king,0, 2);
        board.movePiece(move);
        board.undoLastMove();

        assertTrue("Rook should be back in starting position", board.findPieceByLocation(0, 0) instanceof Rook);
        assertEquals(king, board.findPieceByLocation(0,4));
        assertTrue("King's First move should be true",  king.getLastMoves().isEmpty());
        assertTrue("Rook's First move should be true", ((Rook) board.findPieceByLocation(0, 0)).getLastMoves().isEmpty());
    }

    @Test
    public void testCastlingAfterKingMove() {
        King whiteKing = (King) board.findPieceByLocation(7, 4);
        board.movePiece(new Move(whiteKing,4,4));
        board.movePiece(new Move(whiteKing,7,4));

        ArrayList<int[]> legalMoves = whiteKing.availableMoves(board);
        // Assert that castling moves are not present
        for (int[] move : legalMoves) {
            assertFalse("Castling move should not be available after the king has moved.",move[1] == 6 || move[1] == 2);
        }
    }

    @Test
    public void PerftTest() {
        TestMoveGeneration testMoveGen = new TestMoveGeneration();

        int depth1 = 1;
        int depth2 = 2;
        int depth3 = 3;
        int depth4 = 4;

        int expectedPositionsDepth1 = 20;
        int expectedPositionsDepth2 = 400;
        int expectedPositionsDepth3 = 8902;
        int expectedPositionsDepth4 = 197281;

        int actualPositionsDepth1 = testMoveGen.MoveGenerationTest(depth1, true, board);
        int actualPositionsDepth2 = testMoveGen.MoveGenerationTest(depth2, true, board);
        int actualPositionsDepth3 = testMoveGen.MoveGenerationTest(depth3, true, board);
        int actualPositionsDepth4 = testMoveGen.MoveGenerationTest(depth4, true, board);

        assertEquals("The number of positions generated at depth 1 is incorrect", expectedPositionsDepth1, actualPositionsDepth1);
        assertEquals("The number of positions generated at depth 2 is incorrect", expectedPositionsDepth2, actualPositionsDepth2);
        assertEquals("The number of positions generated at depth 3 is incorrect", expectedPositionsDepth3, actualPositionsDepth3);
        assertEquals("The number of positions generated at depth 4 is incorrect", expectedPositionsDepth4, actualPositionsDepth4);
    }

    @Test
    public void testMinMaxSearchPerformance() {
        int depth = 3;

        AIPlayer ai = new AIPlayer();
        ai.getBestMoveMinimax(board, depth);
        long minimaxNodes = ai.totalNodes;
        long minimaxTime = ai.totalTime;

        AIPlayer ai2 = new AIPlayer();
        ai2.getBestMoveAlphaBeta(board, depth);
        long negamaxNodes = ai2.totalNodes;
        long negamaxTime = ai2.totalTime;

        System.out.println("Results for minimax search:");
        System.out.println(minimaxNodes + " nodes searched in " + minimaxTime);
        System.out.println("Results for alpha-beta search:");
        System.out.println(negamaxNodes + " nodes searched in " + negamaxTime);

        assertTrue("Minimax search takes longest", minimaxTime >= negamaxTime);
        assertTrue("Minimax searches more nodes", minimaxNodes >= negamaxNodes);
    }

}
