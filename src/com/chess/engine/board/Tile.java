package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;



// Abstract class Tile (parent class of EmptyTile and OccupiedTile)
public abstract class Tile { 

    // Tile coordinate (0-63) // protected because it is used in the child classes
    protected final int tileCoordinate;

    // Creates a map of all possible empty tiles
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    // private because it is only used in this class
    // Static because it is only called once
    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }

    // Returns the tile based on the tile coordinate and the piece on the tile
    public static Tile createTile(final int tileCoordinate, final Piece piece) {
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
        // return piece != null ? // means if piece is not null then return new OccupiedTile(tileCoordinate, piece)   
    }
    // Constructor for Tile class
    private Tile(final int tileCoordinate) { 
        this.tileCoordinate = tileCoordinate;
    }

    public abstract boolean isTileOccupied();
    
    public abstract Piece getPiece();

    public int getTileCoordinate() {
        return this.tileCoordinate;
    }

    public static final class EmptyTile extends Tile {

        private EmptyTile(final int coordinate) {
            super(coordinate);
        }

        @Override
        public String toString() { 
            return "-";
        }
        
        @Override   
        public boolean isTileOccupied() { 
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }

    public static final class OccupiedTile extends Tile {
        
        private final Piece pieceOnTile;

        private OccupiedTile(int tileCoordinate,final Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString() {
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                   getPiece().toString(); 
        }

        // Returns true because the tile is occupied
        @Override
        public boolean isTileOccupied() {
            return true;
        }

        // Returns the piece on the tile
        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }
    }
}
