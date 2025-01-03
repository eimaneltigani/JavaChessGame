import model.Board;
import model.Move;
import model.Piece;
import model.pieces.Pawn;
import model.pieces.Queen;
import model.pieces.Rook;
import org.junit.Before;
import org.junit.Test;

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
        Move move = new Move(board.findPieceByLocation(1, 0), 3,  0);
        board.movePiece(move);

        board.undoLastMove();

        assertNotNull("Pawn should be back at its original position", board.findPieceByLocation(1,0));
        assertNull("Pawn's new position should be empty after undo", board.findPieceByLocation(3,0));
        assertTrue("Pawn's first move should be restore", board.findPieceByLocation(1,0).isFirstMove());
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
        assertEquals(1, pawnToPromote.getRow());
        assertEquals(2, pawnToPromote.getCol());
        assertFalse("Queen should no long exist in board", board.getBlackPieces().contains(newQueen));
        assertTrue("Pawn should exist in board", board.getBlackPieces().contains(pawnToPromote));
    }

    @Test
    public void testKingSideCastling() {
        Piece king = board.getBlackKing();
        board.placePiece(null,0, 5);
        board.placePiece(null,0, 6);
        Move move = new Move(king,0, 6);
        board.movePiece(move);

        assertNull("Previous king position should now be empty", board.findPieceByLocation(0, 4));
        assertNull("Previous rook position should now be empty", board.findPieceByLocation(0, 7));
        assertEquals(king, board.findPieceByLocation(0,6));
        assertTrue("Rook should occupy position left of king", board.findPieceByLocation(0, 5) instanceof Rook);
        assertFalse("King's First move should be false", king.isFirstMove());
        assertFalse("Rook's First move should be false", board.findPieceByLocation(0, 5).isFirstMove());
    }

    @Test
    public void undoQueenSideCastling() {
        Piece king = board.getBlackKing();
        Move move = new Move(king,0, 2);
        board.movePiece(move);
        board.undoLastMove();

        assertTrue("Rook should occupy position left of king", board.findPieceByLocation(0, 0) instanceof Rook);
        assertEquals(king, board.findPieceByLocation(0,4));
        assertTrue("King's First move should be false", king.isFirstMove());
        assertTrue("Rook's First move should be false", king.isFirstMove());
    }

}
