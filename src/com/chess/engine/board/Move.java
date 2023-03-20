package com.chess.engine.board;

import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move {

    Board board;
    protected final Piece movedPiece;
    final int destinationCoordinate;
    final boolean isFirstMove;

    private Move(final Board board,
                 final Piece movedPiece,
                 final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();

    }

    private Move(final Board board,
                 final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }
      
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.getPiecePosition();
        return result;
    }

    @Override                                                       // this method is used to compare objects for equality
    public boolean equals(final Object other) {                     // equals will compare the current object with the object other 
        if(this == other) {                                         // (other represents piece that is being moved)
            return true;                                            // return true if piece that is being moved is the same as the piece that is moving
        }
        if(!(other instanceof final Move otherMove)) {                              // return false if piece that is being moved is not the same as the piece that is moving
            return false;
        }
        return getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
               getMovedPiece().equals(otherMove.getMovedPiece());
    } 

    public Board getBoard() { return this.board; }

    public int getCurrentCoordinate() { return this.getMovedPiece().getPiecePosition();}

    public int getDestinationCoordinate() { return this.destinationCoordinate; }

    public Piece getMovedPiece() { return this.movedPiece; }

    public boolean isAttack() { return false; }

    public boolean isCastlingMove() { return false; }

    public Piece getAttackedPiece() { return null; }

   
    public Board execute() {                                        // this method is used to execute the move
        final Builder builder = new Builder();                      // Builder is a class that is used to build the board
        for(final Piece piece : this.board.currentPlayer().getActivePieces()) { // getActivePieces() is a method of the class Player
            if(!this.movedPiece.equals(piece)) {                    // if the piece that is moving is not the same as the piece that is being moved
                builder.setPiece(piece);                            // then set the piece on the board
            }                                        
        }
        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }
        
        builder.setPiece(this.movedPiece.movePiece(this));             // this is the piece that was moved
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance()); // it is used to set the next player
        return builder.build();
    }


    public static class MajorMove extends Move {

        public MajorMove(  final Board board,                           // board is object of Board class // MajorMove is a subclass of Move class
                           final Piece movedPiece,
                           final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece)) {                    // todo hashcode and equals for pieces
                    builder.setPiece(piece);                            // equals() is a method of the class Object
                }                                                       // equals is used to compare objects for equality
            }
    
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            
            builder.setPiece(this.movedPiece.movePiece(this ));             // this is the piece that was moved
            builder.setMoveMaker(this.board.currentPlayer().getAlliance()); // it is used to set the piece on the board which was moved
            return builder.build();                                         // builder.setMoveMaker takes the alliance of the player 
        }
    }


    public static class AttackMove extends Move { 

        final Piece attackedPiece;

        public AttackMove( final Board board,
                           final Piece movedPiece,
                           final int destinationCoordinate,
                           final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if(this == other) {
                return true;
            }
            if(!(other instanceof final AttackMove otherAttackMove)) {
                return false;
            }

            return super.equals(otherAttackMove) &&
                   getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public Board execute() { return null;}

        @Override
        public boolean isAttack() {return true;}

        @Override
        public Piece getAttackedPiece() { return this.attackedPiece;}
    }


    public static class PawnMove extends Move {

        public PawnMove(  final Board board, // board is object of Board class // MajorMove is a subclass of Move class
                          final Piece movedPiece,
                          final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            throw new UnsupportedOperationException("Unimplemented method 'execute");
        }
    }
    
    public static  class PawnAttackMove extends AttackMove {
        public PawnAttackMove(  final Board board, // board is object of Board class // MajorMove is a subclass of Move class
                                final Piece movedPiece,
                                final int destinationCoordinate,
                                final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
    }

    public static class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(  final Board board, // board is object of Board class // MajorMove is a subclass of Move class
                                final Piece movedPiece,
                                final int destinationCoordinate,
                                final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
    }

    public static final class PawnJump extends Move {

        public PawnJump(  final Board board, // board is object of Board class // MajorMove is a subclass of Move class
                          final Piece movedPiece,
                          final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece); // for each piece in the opponent's active pieces set the piece on the board
            }
            // recording the move of opponent's pawn that was jumped over to be able to capture it en passant
            // so it can be captured by next player´s pawn in the next move
            final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build(); 
        }
    }
    

    static abstract class CastleMove extends Move {

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(  final Board board, // board is object of Board class // MajorMove is a subclass of Move class
                            final Piece movedPiece,
                            final int destinationCoordinate,
                            final Rook casttleRook,
                            final int castleRookStart,
                            final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = casttleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece); // for each piece in the opponent's active pieces set the piece on the board
            }
            // todo look into the first move on the king side castle
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

    }

    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(  final Board board, // board is object of Board class // MajorMove is a subclass of Move class
                                    final Piece movedPiece,
                                    final int destinationCoordinate,
                                    final Rook rook,
                                    final int castleRookStart,
                                    final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, rook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "O-O";
        }
        
    }

    public static final class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove( final Board board, // board is object of Board class // MajorMove is a subclass of Move class
                                    final Piece movedPiece,
                                    final int destinationCoordinate,
                                    final Rook casttleRook,
                                    final int castleRookStart,
                                    final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, casttleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "O-O-O";
        } 
    }

    // creates a null move (an invalid move)
    public static class NullMove extends Move {

        public NullMove() { super(null, -1); }

        @Override
        public int getCurrentCoordinate() { return -1; }

        @Override
        public int getDestinationCoordinate() { return -1; }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute the null move!");
        }

        @Override
        public String toString() { return "Null Move"; }
    }

    // creates possible moves or returns a null move
    public static class MoveFactory {

        private static final Move NULL_MOVE = new NullMove();

        private MoveFactory() {
            throw new RuntimeException("Cannot create all the moves!");
        }

        public static Move getNullMove() { return NULL_MOVE; }

        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {

            for(final Move move : board.getAllLegalMoves()) {
                if(move.getCurrentCoordinate() == currentCoordinate &&
                   move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}

