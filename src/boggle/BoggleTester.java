/* File: BoggleTester.java
 I affirm that this program is entirely my own work and
 none of it is the work of any other person.

 ____________________________
 (your signature)


 @author Fernando Campo 1299228 COP 3530 Data Structures MWF 10:45 Summer 2014
 */
package boggle;

/**
 * Boggle game tester.
 *
 * @author Fernando Campo 1299228
 */
public class BoggleTester
{

    /**
     * Creates Boggle game object and prints the results
     *
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {
        Boggle game;
        try
        {
            game = new Boggle( args[0] );
        }
        catch ( ArrayIndexOutOfBoundsException e )
        {
            System.err.println( "No files given or files not found." );
            return;
        }
        game.printMap( game.solve() );
    }
}
