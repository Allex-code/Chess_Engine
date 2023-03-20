package com.chess.engine.board;

 public class BoardUtils {

    public static boolean[] FIRST_COLUMN = initColumn(0);
    public static boolean[] SECOND_COLUMN = initColumn(1);
    public static boolean[] SEVENTH_COLUMN = initColumn(6);
    public static boolean[] EIGHTH_COLUMN = initColumn(7);

    public static boolean[] EIGHTH_RANK = initRow(0);
    public static boolean[] SEVENTH_RANK = initRow(8);
    public static boolean[] SIXTH_RANK = initRow(16);
    public static boolean[] FIFTH_RANK = initRow(24);
    public static boolean[] FOURTH_RANK = initRow(32);
    public static boolean[] THIRD_RANK = initRow(40);
    public static boolean[] SECOND_RANK = initRow(48);
    public static boolean[] FIRST_RANK = initRow(56);
      
    
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private BoardUtils(){
        throw new RuntimeException("You cannot instantiate me!");
    }

    private static boolean[] initColumn(int columnNumber){
        final boolean[] column = new boolean[NUM_TILES];
        do{
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;
        }while(columnNumber < NUM_TILES);
        return column;
    }

    private static boolean[] initRow(int rowNumber){
        final boolean[] row = new boolean[NUM_TILES];
        do{
            row[rowNumber] = true;
            rowNumber++;
        }while(rowNumber % NUM_TILES_PER_ROW != 0);
        return row;
    }

    public static boolean isValidTileCoordinate(final int coordinate){
        return coordinate > 0 && coordinate < NUM_TILES;
    }    
}