    package com.chess.engine.player;

    import com.chess.engine.Alliance;
    import com.chess.engine.board.Board;
    import com.chess.engine.board.Move;
    import com.chess.engine.pieces.King;
    import com.chess.engine.pieces.Piece;
    import com.google.common.collect.ImmutableList;
    import com.google.common.collect.Iterables;

    import java.util.ArrayList;
    import java.util.Collection;

    public abstract class Player {
        
        
        protected final Board board;     // We want to access from subclasses
        protected final King playerKing;
        protected final Collection<Move> legalMoves;
        private final boolean isInCheck;

        Player(final Board board, 
               final Collection<Move> legalMoves, // future variable "playerLegals"
               final Collection<Move> opponentLegals) {
            this.board = board;
            this.playerKing = establishKing();  // new method for looking up the king
            this.isInCheck = !calculateAttackOnTile(this.playerKing.getPiecePosition(), opponentLegals).isEmpty(); // it outputs a boolean if the king is in check or not
            this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentLegals)));
        }                                                // Player.calculateAttackOnTile() returns a Collection<Move> so we use isEmpty() method to check if it is empty or not


        public Collection<Move> getLegalMoves() { return this.legalMoves;}

        private King getPlayerKing() { return this.playerKing;}

         static Collection<Move> calculateAttackOnTile(int piecePosition, Collection<Move> moves) {
            final Collection<Move> attackMoves = new ArrayList<>();
            for(final Move move : moves) { 
                if(piecePosition == move.getDestinationCoordinate()) {
                    attackMoves.add(move);
                }
            }
            return ImmutableList.copyOf(attackMoves);
        }

        private King establishKing() {
            for(final Piece piece : getActivePieces()) {
                if(piece.getPieceType().isKing()) {
                    return (King) piece;
                }
            }
            throw new RuntimeException("Hey you Idiot! Where is your king? :D");
        }

        public boolean isMoveLegal(final Move move) {
            return this.legalMoves.contains(move);
        }

    
        public boolean isInCheck() {
            return this.isInCheck;
        }

        // TO do more work here
        public boolean isInCheckMate() {
            return isInCheck && !hasEscapeMoves();
        }

        public boolean isInStaleMate() {
            return !isInCheck && !hasEscapeMoves() ;
        }

        protected boolean hasEscapeMoves() {
            for(final Move move: this.legalMoves) {
                final MoveTransition transition = makeMove(move);
                if(transition.getMoveStatus().isDone()) {
                    return true;
                }
            }
            return false;
        }

        public boolean isCastled() {
            return false;
        }

        public MoveTransition makeMove(final Move move) {
            if(!isMoveLegal(move)){
                return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE); 
            }
            final Board transitionBoard = move.execute();
            final Collection<Move> kingAttacks = Player.calculateAttackOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                                                                              transitionBoard.currentPlayer().getLegalMoves());
            if(!kingAttacks.isEmpty()) {
                return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
            }
            
            return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
        }




        // polymorphism - we are going to override this method in the subclasses
        // Either BlackPlayer or WhitePlayer will be returned
        public abstract Collection<Piece> getActivePieces();
        public abstract Alliance getAlliance();
        public abstract Player getOpponent();
        protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);
        
    }
