/* File: Boggle.java
 I affirm that this program is entirely my own work and
 none of it is the work of any other person.

 ____________________________
 (your signature)


 @author Fernando Campo 1299228 COP 3530 Data Structures MWF 10:45 Summer 2014
 */
package boggle;

//Imports
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Program used to solve the game Boggle. Finding words using adjacent
 * characters. Characters used can not be reuse. May not reuse a character in a
 * given position to form a word, though repeated characters in different
 * positions can be used. Takes a text file of a puzzle in the constructor and
 * uses the dictionary file at the root of the project folder "dict.txt"
 *
 * @author Fernando
 */
public class Boggle
{

    /**
     * Inner class for Position. Used to store a row and a column as a pair.
     */
    public class Position
    {

        //private instance varibles of the Position class.
        private int row;
        private int col;

        /**
         * Position constructor that creates a Position object from a row and
         * column location.
         *
         * @param r Row position.
         * @param c Column position.
         */
        public Position( int r, int c )
        {
            row = r; col = c;
        }

        /**
         * Accessor method to return the row of the Position object.
         *
         * @return Returns the row of the Position object.
         */
        public int getRow()
        {
            return row;
        }

        /**
         * Accessor method to return the column of the Position object.
         *
         * @return Returns the column of the Position object.
         */
        public int getCol()
        {
            return col;
        }

        /**
         * Overridden toString method to return a string of the Position object.
         *
         * @return Returns the string value of the Position object. Row and
         * column.
         */
        @Override
        public String toString()
        {
            return "[" + row + "]" + "[" + col + "]";
        }

        /**
         * Overridden equals method. Check if other is a Position object.
         * Compares the row and the column if they're equal.
         *
         * @param other Position object to be compared.
         * @return Returns true if Positions are equal.
         */
        @Override
        public boolean equals( Object other )
        {
            //Check if other is a Position first.
            if ( !( other instanceof Position ) )
                return false;
            
            //Safe to downcast object to Position.
            Position pos = (Position) other;
            //Compares rows and columns.
            return row == pos.row && col == pos.col;
        }

        /**
         * Returns the character at the Position.
         *
         * @return Return a character of Position object.
         */
        public char getChar()
        {
            return puzzle[row][col];
        }

        /**
         * Method used to return a List of neighbor Positions.
         *
         * @return Returns a List of Position objects that are the neighbors of
         * the this Position object.
         */
        public List<Position> getNeighbors()
        {
            //Finds the rows and columns before and after it with check if its
            //at the edges of the puzzle board.
            int lowRow = ( row == 0 ) ? 0 : ( row - 1 );
            int lowCol = ( col == 0 ) ? 0 : ( col - 1 );
            int highRow = ( row == numRows - 1 ) ? row : ( row + 1 );
            int highCol = ( col == numCols - 1 ) ? col : ( col + 1 );

            List<Position> result = new ArrayList<>();

            for ( int r = lowRow; r <= highRow; ++r )
                for ( int c = lowCol; c <= highCol; ++c )
                    if ( r != row || c != col )
                        //creates and adds the new Position to the List.
                        result.add( new Position( r, c ) );
            return result;
        }//end of getNeighbors() method.
    }//end of the inner Position class

    //private instances varibles of the Boggle class
    private String[] dict;
    private char[][] puzzle;
    private int numRows;
    private int numCols;

    /**
     * Boggle constructor. Constructs a Boggle game from a puzzle text file.
     * Builds the dictionary list and the puzzle 2d array.
     *
     * @param puzzFile
     */
    public Boggle( String puzzFile )
    {
        buildDict();
        buildPuzzle( puzzFile );
    }

    /**
     * Private method used to construct the dictionary list from the "dict.txt"
     * from the root of the project file. Does not add one and two letter 
     * words.
     */
    private void buildDict()
    {
        List<String> lst = new ArrayList<>();
        //try catch. throws error if file not found.
        try
        {
            Scanner fileScan = new Scanner( new File( "dict.txt" ) );

            //while file has words, add to the list only if the word longer
            //than two letter.
            while ( fileScan.hasNext() )
            {
                String word = fileScan.next();
                if ( word.length() > 2 )
                    lst.add( word );
            }
            //sort the dictionary
            Collections.sort( lst );
            //sets the size and copies words over to an array.
            dict = new String[ lst.size() ];
            dict = lst.toArray( dict );
        }
        catch ( FileNotFoundException e )
        {
            System.err.println( "Dictionary file not found." );
        }

    }

    /**
     * Private method used to create the puzzle 2d array board a text file.
     *
     * @param puzzFile Name of text file of input puzzle.
     */
    private void buildPuzzle( String puzzFile )
    {
        //temp ArrayList
        List<String> puzzleLines = new ArrayList<>();
        try
        {
            Scanner fileScan = new Scanner( new File( puzzFile ) );

            //First line of puzzle to determine starting column length.
            String line = fileScan.next();
            numCols = line.length();
            puzzleLines.add( line );
            while ( fileScan.hasNext() )
            {
                line = fileScan.next();
                //Check if following lines of puzle files are the same size.
                if ( line.length() != numCols )
                {
                    //If puzzle is odd shaped, exit the program.
                    System.err.println( "Puzzle is odd shape." );
                    System.exit( 0 );
                }
                else
                    puzzleLines.add( line );
            }
            numRows = puzzleLines.size();
            puzzle = new char[ numRows ][ numCols ];

            //Populates the 2d puzzle from the temp ArrayList
            for ( int i = 0; i < numRows; ++i )
                for ( int j = 0; j < numCols; ++j )
                    puzzle[ i][ j] = puzzleLines.get( i ).charAt( j );
            
        }
        catch ( FileNotFoundException e )
        {
            System.out.println( "No puzzle file found." );
        }

    }

    /**
     * Routine to solve the Boggle game.
     *
     * @return a Map containing the strings as keys, and the positions used to
     * form the string (as a List) as values
     */
    public Map<String, List<Position>> solve()
    {
        //Creates the results Map and Position ArrayList for the path.
        Map<String, List<Position>> results = new TreeMap();
        List<Position> path = new ArrayList();
        String charSequence = "";
        for ( int r = 0; r < numRows; r++ )
            for ( int c = 0; c < numCols; c++ )
                //Recursive call to private solve method
                solve( new Position( r, c ), "", path, results );
        
        //Return the results of words found in the puzzle.
        return results;
    }

    /**
     * Hidden recursive routine.
     *
     * @param thisPos the current position
     * @param charSequence the characters in the potential matching string thus
     * far.
     * @param path the List of positions used to form the potential matching
     * string thus far.
     * @param results the Map that contains the strings that have been found as
     * keys and the positions used to form the string (as a List) as values.
     */
    private void solve( Position thisPos, String charSequence,
            List<Position> path, Map<String, List<Position>> results )
    {
        //First checks if the path contain a position already used.
        if ( path.contains( thisPos ) )
            return;
        
        charSequence += thisPos.getChar();

        //Calls the prefixCheck method whichs perfroms a binary search of the
        //prefix against the dictionary.
        int search = prefixCheck( charSequence );

        //If the prefix is not found at all in the dictionary.
        if ( search == dict.length )
            return;

        //Check if the insertation point word from the binary search starts
        //with the prefix.
        if ( dict[search].startsWith( charSequence ) )
        {
            //Finds a potential match. Adds the position to the path and finds
            //the neighbors of the position.
            path.add( thisPos );
            List<Position> neighbors = thisPos.getNeighbors();
            if ( dict[search].equals( charSequence ) )
            {
                //Binary Search returns an exact match. Adds to the Map and
                //a copy of the current path of the word found.
                results.put( charSequence, ( new ArrayList( path ) ) );
                for ( Position pos : neighbors )
                    //Recursive call for more potential matches.
                    solve( pos, charSequence, path, results );
                
            }
            for ( Position pos : neighbors )
                //Recursive call for potential matches.
                solve( pos, charSequence, path, results );
            
            //Removes (updates) path and returns back to method call.
            path.remove( thisPos );
        }
    }

    /*
     * Private utility method for the Binary Search.
     */
    private int prefixCheck( String prefix )
    {
        int check = Arrays.binarySearch( dict, prefix );

        if ( check < 0 )
            //No match found, (-(insertion point) - 1).
            return ( -check - 1 );
        
        //Index where the match was found.
        return check;
    }

    /**
     * Print results Map of words found. Calculates the score of each length of
     * words found in the puzzle. Outputs each word found, along with the number
     * of points it is worth, the location where the characters in the word can
     * be found, and the total number of points. If the number of words exceeds
     * 200, prints out only the words of length eight or more, and a summary of
     * how many words of each length 3-7 there are and the number of points they
     * account for.
     *
     * @param wordsFound Map of words found to be printed.
     */
    public void printMap( Map<String, List<Position>> wordsFound )
    {
        //Reference array of word length value (score points).
        int[] scores ={0, 0, 0, 1, 2, 3, 4, 6, 10};

        //Various counters for total score and various word length score.
        int totalScore = 0;
        int totalofThree = 0, totalofFour = 0, totalofFive = 0,
                totalofSix = 0, totalofSeven = 0, totalofEight = 0;
        
        String longest = "";
        boolean large = ( wordsFound.size() > 199 );
        
        System.out.println( "Words\t\tPoints\t\tPath Found" );
        System.out.println( "-----\t\t------\t\t----------" );
        
        //Enhance for loop to iterate over the Map of words found in the puzzle.
        for ( Map.Entry<String, List<Position>> e : wordsFound.entrySet() )
        {
            String word = e.getKey();
            List<Position> pos = e.getValue();
            
            //If the words found are over 200, sum the score of each length and
            //only prints words of 8 letters or more.
            if ( large )
            {

                totalofThree += ( word.length() == 3 ) ? 1 : 0;
                totalofFour += ( word.length() == 4 ) ? 2 : 0;
                totalofFive += ( word.length() == 5 ) ? 3 : 0;
                totalofSix += ( word.length() == 6 ) ? 4 : 0;
                totalofSeven += ( word.length() == 7 ) ? 6 : 0;
                totalofEight += ( word.length() == 8 ) ? 10 : 0;
                totalScore += ( word.length() > 8 ) ? 15 : 0;

                if ( word.length() == 8 )
                    System.out.println( word + "\t" + scores[word.length()]
                            + " points\tPath: " + pos );
                
                else if ( word.length() > 8 )
                    System.out.println( word + "\t15 points\tPath: " + pos );
                
            }
            //If words found are less than 200 words, prints each word out with
            // its point value and path where the letter were found.
            else if ( !large )
            {
                //Calcuates the score using the score reference array.
                if ( word.length() < 9 )
                    totalScore += scores[word.length()];
                
                //Any words longer than 9+ are 15 points each.
                else
                    totalScore += 15;
                
                System.out.println( word + "\t\t" + scores[word.length()]
                        + " points\tPath: " + pos );

            }
            //Sanity check for longest word found.
            if ( word.length() > longest.length() )
                longest = word;
            
        }//Finishes for loop over Map results.
        
        //Two print options if words are exceeds 200 or not.
        if ( large )
        {
            //Score Summary of each word length.
            System.out.println( "\nScore Summary:" );
            System.out.println( "--------------" );
            System.out.println( "\nScore of " + ( totalofThree / 1 )
                    + " 3 letters words:\t" + totalofThree + " points" );
            System.out.println( "Score of " + ( totalofFour / 2 )
                    + " 4 letters words:\t" + totalofFour + " points" );
            System.out.println( "Score of " + ( totalofFive / 3 )
                    + " 5 letters words:\t" + totalofFive + " points" );
            System.out.println( "Score of " + ( totalofSix / 4 )
                    + " 6 letters words:\t" + totalofSix + " points" );
            System.out.println( "Score of " + ( totalofSeven / 6 )
                    + " 7 letters words:\t" + totalofSeven + " points" );
            System.out.println( "\nTotal score: " + ( totalofThree
                    + totalofFour + totalofFive + totalofSix + totalofSeven
                    + totalofEight + totalScore + " points" ) );
        }
        else
        {
            //Less than 200 words found, just prints total score.
            System.out.println( "\nScore Summary:\n\nTotal score: " 
                    + totalScore  + " points" );
        }
        System.out.println( "Words found: " + wordsFound.size() );
        System.out.println( "\nThe longest word is " + longest + " and is "
                + longest.length() + " letters long." );
    }

    public void printPuzzle()
    {
        for ( int i = 0; i < numRows; ++i )
            for ( int j = 0; j < numCols; ++j )
                System.out.print( "[ " + puzzle[i][j] + " ]" );
        
            System.out.println();
    }
}//end of Boogle.java
