package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.AttackMove;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Knight extends Piece {


    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17,-15,-10,-6,6,10,15,17};

    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.KNIGHT, pieceAlliance, piecePosition, true);
    }     

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

         
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidate : CANDIDATE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidate;

            // if the candidateDestinationCoordinate is valid (on the board) then do the following
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) { 

                // if the knight is on the first column and the candidateDestinationCoordinate is -17 or -10 or 6 or 15 then do the following
                if(isFirstColumnExclusion(this.piecePosition, currentCandidate) ||
                    isSecondColumnExclusion(this.piecePosition, currentCandidate) ||
                    isSeventhColumnExclusion(this.piecePosition, currentCandidate) ||
                    isEighthColumnExclusion(this.piecePosition, currentCandidate)) {
                    continue;
                }
                
                // get the tile of the candidateDestinationCoordinate 
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                // if the tile is not occupied then do the following 
                if(!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    
                    if(this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }                               // if the tile is occupied then do atacking move
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }


  

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }


    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
         return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset) == -17) || (candidateOffset == -10) 
               || (candidateOffset == 6) || (candidateOffset == 15);        
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.SECOND_COLUMN[currentPosition] && ((candidateOffset) == -10) || (candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && ((candidateOffset) == -6) || (candidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset) == -15) || (candidateOffset == -6) 
               || (candidateOffset == 10) || (candidateOffset == 17);
    }

    @Override
    public int computeHashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
