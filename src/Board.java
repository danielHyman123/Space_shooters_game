import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**  Board GUI for implementation with various games
 *   Author: Kirill Levin, Troy Vasiga, Chris Ingram
 *   Modified by Mr. Benum, Daniel Hyman, Peter Frach, John Yoon to handle Keyboard-Interactive Space Shooter game
 */

// =============================MUST IMPLEMENT KEYLISTENER ====================
public class Board extends JPanel implements KeyListener
{
    private static final int X_DIM = 40;
    private static final int Y_DIM = 40;
    private static final int X_OFFSET = 30;
    private static final int Y_OFFSET = 30;
    private static final double MIN_SCALE = 0.25;
    private static final int GAP = 10;
    private static final int FONT_SIZE = 16;
    // THIS VARIABLE STORES THE UNICODE VALUE OF THE KEY THAT WAS PRESSED =======
    private static char key;

    // Grid colours
//    private static final Color GRID_COLOR_A = new Color(200,250, 250);
//    private static final Color GRID_COLOR_B = new Color(200,250, 250);
    private static final Color GRID_COLOR_A = new Color(0, 0, 0);
    private static final Color GRID_COLOR_B = new Color(0, 0, 0);

    // Preset colours for pieces
    private static final Color[] COLOURS =
            {Color.YELLOW, Color.BLUE, Color.CYAN, Color.GREEN,
                    Color.PINK, Color.WHITE, Color.RED, Color.ORANGE };

    // String used to indicate each colour
    private static final String[] COLOUR_NAMES =
            {"yellow", "blue", "cyan", "green", "pink", "white", "red", "orange"};

    // Colour to use if a match is not found
    private static final Color DEFAULT_COLOUR = Color.BLACK;

    //private Color[][] grid;
    private static final ImageIcon DEFAULT_OBJECT = new ImageIcon("Asteroid.png");
    private static final ImageIcon[] OBJECTS = new ImageIcon[4];
    private static final String[] OBJECT_NAMES = {"spaceship", "asteroid", "rainbowStar", "boss"};
    private ImageIcon[][] grid;
    private Coordinate lastClick;  // How the mouse handling thread communicates
    // to the board where the last click occurred
    private String message = "";
    private int numLines = 0;
    private int[][] line = new int[4][400];  // maximum number of lines is 100
    private int columns, rows;

    private boolean first = true;
    private int originalWidth;
    private int originalHeight;
    private double scale;

    /** A constructor to build a 2D board.
     */
    public Board (int rows, int cols)
    {
        super( true );
        // RENAME THE WINDOW HERE =================================================
        JFrame f = new JFrame( "Spaceshooter" );

        this.columns = cols;
        this.rows = rows;
        originalWidth = 2*X_OFFSET+X_DIM*cols;
        originalHeight = 2*Y_OFFSET+Y_DIM*rows+GAP+FONT_SIZE;

        this.setPreferredSize( new Dimension( originalWidth, originalHeight ) );

        f.setResizable(true);

        //this.grid = new Color[cols][rows];
        this.grid = new ImageIcon[cols][rows];
        this.setFocusable(true);
        // MUST ADD THE KEYLISTENER TO THE BOARD (JPANEL) =========================
        addKeyListener(this);

        this.addMouseListener(
                new MouseInputAdapter()
                {
                    /** A method that is called when the mouse is clicked
                     */
                    public void mouseClicked(MouseEvent e)
                    {
                        int x = (int)e.getPoint().getX();
                        int y = (int)e.getPoint().getY();

                        // We need to by synchronized to the parent class so we can wake
                        // up any threads that might be waiting for us
                        synchronized(Board.this)
                        {
                            int curX = (int)Math.round(X_OFFSET*scale);
                            int curY = (int)Math.round(Y_OFFSET*scale);
                            int nextX = (int)Math.round((X_OFFSET+X_DIM*grid.length)*scale);
                            int nextY = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale);

                            // Subtract one from high end so clicks on the black edge
                            // don't yield a row or column outside of board because of
                            // the way the coordinate is calculated.
                            if (x >= curX && y >= curY && x < nextX && y < nextY)
                            {
                                lastClick = new Coordinate(y,x);
                                // Notify any threads that would be waiting for a mouse click
                                Board.this.notifyAll() ;
                            } /* if */
                        } /* synchronized */
                    } /* mouseClicked */
                } /* anonymous MouseInputAdapater */
        );
        OBJECTS [0] = new ImageIcon("Spaceship.png");
        OBJECTS [1] = new ImageIcon("Asteroid.png");
        OBJECTS [2] = new ImageIcon("RainbowStar.png");
        OBJECTS [3] = new ImageIcon("LegendZeldaMoon.png");

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane( this );
        f.pack();
        f.setVisible(true);
    }

    /** A constructor to build a 1D board.
     */
    public Board (int cols)
    {
        this(1, cols);
    }

    // ADD THE KEYLISTENER EVENT METHODS HERE ====================================
    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {
        key = e.getKeyChar();
    }

    // ACCESSOR METHOD THAT RETURNS THE KEY CHAR PRESSED ======================
    public char getKey(){
        return key;
    }

    private void paintText(Graphics g)
    {
        g.setColor( this.getBackground() );
        g.setFont(new Font(g.getFont().getFontName(), Font.ITALIC+Font.BOLD, (int)(Math.round(FONT_SIZE*scale))));

        int x = (int)Math.round(X_OFFSET*scale);
        int y = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale + GAP  ) ;

        g.fillRect(x,y, this.getSize().width, (int)Math.round(GAP+FONT_SIZE*scale) );
        g.setColor( Color.black );
        g.drawString(message, x, y + (int)Math.round(FONT_SIZE*scale));
    }

    private void paintGrid(Graphics g)
    {
        super.paintComponent(g); //fixes ghosting problem
        for (int i = 0; i < this.grid.length; i++)
        {
            for (int j = 0; j < this.grid[i].length; j++)
            {
                if ((i%2 == 0 && j%2 != 0) || (i%2 != 0 && j%2 == 0))
                    g.setColor(GRID_COLOR_A);
                else
                    g.setColor(GRID_COLOR_B);
                int curX = (int)Math.round((X_OFFSET+X_DIM*i)*scale);
                int curY = (int)Math.round((Y_OFFSET+Y_DIM*j)*scale);
                int nextX = (int)Math.round((X_OFFSET+X_DIM*(i+1))*scale);
                int nextY = (int)Math.round((Y_OFFSET+Y_DIM*(j+1))*scale);
                int deltaX = nextX-curX;
                int deltaY = nextY-curY;

                g.fillRect( curX, curY, deltaX, deltaY );
//                Color curColour = this.grid[i][j];
//                if (curColour != null) // Draw pegs if they exist
//                {
//                    g.setColor(curColour);
//                    g.fillRect(curX + 7, curY + 7, deltaX, deltaY);
//                }
                ImageIcon curImage = this.grid[i][j];
                if (curImage != null) {
                    curImage.paintIcon(this, g, curX, curY);
                }
            }
        }

        g.setColor(Color.BLACK);
        int curX = (int)Math.round(X_OFFSET*scale);
        int curY = (int)Math.round(Y_OFFSET*scale);
        int nextX = (int)Math.round((X_OFFSET+X_DIM*grid.length)*scale);
        int nextY = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale);
        g.drawRect(curX, curY, nextX-curX, nextY-curY);
    }

    private void drawLine(Graphics g1)
    {
        g1.setColor(Color.red);
        for (int i =0; i < numLines; i++ )
        {
            ((Graphics2D) g1).setStroke(new BasicStroke(5.0f*(float)scale));
            g1.drawLine( (int)Math.round((X_OFFSET-6+X_DIM/2.0+line[0][i]*X_DIM)*scale),
                    (int)Math.round((Y_OFFSET-15+Y_DIM/2.0+line[1][i]*Y_DIM)*scale),
                    (int)Math.round((X_OFFSET-6+X_DIM/2.0+line[2][i]*X_DIM)*scale),
                    (int)Math.round((Y_OFFSET-15+Y_DIM/2.0+line[3][i]*Y_DIM)*scale) );
        }
    }

    /**
     * Convert a String to the corresponding Color defaulting to Black
     * with an invald input
     */
    private ImageIcon convertImageIcon( String object )
    {
        for( int i=0; i<OBJECTS.length; i++ )
        {
            if( OBJECT_NAMES[i].equalsIgnoreCase( object ) )
                return OBJECTS[i];
        }

        return DEFAULT_OBJECT;
    }

    private Color convertColour( String theColour )
    {
        for( int i=0; i<COLOUR_NAMES.length; i++ )
        {
            if( COLOUR_NAMES[i].equalsIgnoreCase( theColour ) )
                return COLOURS[i];
        }

        return DEFAULT_COLOUR;
    }


    /** The method that draws everything
     */
    public void paintComponent( Graphics g )
    {
        this.setScale();
        this.paintGrid(g);
        this.drawLine(g);
        this.paintText(g);
    }

    public void setScale()
    {
        double width = (0.0+this.getSize().width) / this.originalWidth;
        double height = (0.0+this.getSize().height) / this.originalHeight;
        this.scale = Math.max( Math.min(width,height), MIN_SCALE );
    }

    /** Sets the message to be displayed under the board
     */
    public void displayMessage(String theMessage)
    {
        message = theMessage;
        this.repaint();
    }

    //new function get displayedMessage in the Board by John
    public String getDisplayedMessage() {
        return message;
    }


    /** This method will save the value of the colour of the peg in a specific
     * spot.  theColour is restricted to
     *   "yellow", "blue", "cyan", "green", "pink", "white", "red", "orange"
     * Otherwise the colour black will be used.
     */
    public void putPeg(String object, int row, int col)
    {
        this.grid[col][row] = this.convertImageIcon( object );
        this.repaint();
    }

    /** Same as putPeg above but for 1D boards
     */
    public void putPeg(String theColour, int col)
    {
        this.putPeg( theColour, 0, col );
    }

    /** Remove a peg from the gameboard.
     */
    public void removePeg(int row, int col)
    {
        this.grid[col][row] = null;
        repaint();
    }

    /** Same as removePeg above but for 1D boards
     */
    public void removePeg(int col)
    {
        this.grid[col][0] = null;
        repaint();
    }

    /** Draws a line on the board using the given co-ordinates as endpoints
     */
    public void drawLine(int row1, int col1, int row2, int col2)
    {
        this.line[0][numLines]=col1;
        this.line[1][numLines]=row1;
        this.line[2][numLines]=col2;
        this.line[3][numLines]=row2;
        this.numLines++;
        repaint();
    }

    /** Removes one line from a board given the co-ordinates as endpoints
     * If there is no such line, nothing happens
     * If multiple lines, all copies are removed
     */

    public void removeLine(int row1, int col1, int row2, int col2)
    {
        int curLine = 0;
        while (curLine < this.numLines)
        {
            // Check for either endpoint being specified first in our line table
            if ( (line[0][curLine] == col1 && line[1][curLine] == row1 &&
                    line[2][curLine] == col2 && line[3][curLine] == row2)   ||
                    (line[2][curLine] == col1 && line[3][curLine] == row1 &&
                            line[0][curLine] == col2 && line[1][curLine] == row2) )
            {
                // found a matching line: overwrite with the last one
                numLines--;
                line[0][curLine] = line[0][numLines];
                line[1][curLine] = line[1][numLines];
                line[2][curLine] = line[2][numLines];
                line[3][curLine] = line[3][numLines];
                curLine--; // perhaps the one we copied is also a match
            }
            curLine++;

        }
        repaint();
    }

    /** Waits for user to click somewhere and then returns the click.
     */
    public Coordinate getClick()
    {
        Coordinate returnedClick = null;
        synchronized(this) {
            lastClick = null;
            while (lastClick == null)
            {
                try {
                    this.wait();
                } catch(Exception e) {
                    // We'll never call Thread.interrupt(), so just consider
                    // this an error.
                    e.printStackTrace();
                    System.exit(-1) ;
                } /* try */
            }

            int col = (int)Math.floor((lastClick.getCol()-X_OFFSET*scale)/X_DIM/scale);
            int row = (int)Math.floor((lastClick.getRow()-Y_OFFSET*scale)/Y_DIM/scale);

            // Put this into a new object to avoid a possible race.
            returnedClick = new Coordinate(row,col);
        }
        return returnedClick;
    }

    /** Same as getClick above but for 1D boards
     */
    public int getPosition()
    {
        return this.getClick().getCol();
    }

    public int getColumns()
    {
        return this.grid.length;
    }

    public int getRows()
    {
        return this.grid[0].length;
    }

    public void resetLines() {
        this.numLines = 0;
    }
}