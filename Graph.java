import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JComponent;
/**
 *
 * @author Jamie
 * Description: this class was made and used in the last project. I just added the ability
 *              to display the graphs of multiple functions
 */
public class Graph extends JComponent {
    protected ArrayList<String> inputList = new ArrayList<>();
    
    private int nR; // number of riemann rectangles
    protected int n; // number of points to calculate
    protected double x1, x2; // interval
    protected ArrayList<Point2D.Double> pointList = new ArrayList<>(); // holds all the points in the graph
    protected ArrayList<Line2D.Double> lineList = new ArrayList<>(); // holds all the lines in the graph
    
    protected ArrayList<ArrayList<Point2D.Double>> pointListList = new ArrayList<>(); // holds a collection of pointLists
    protected ArrayList<ArrayList<Line2D.Double>> lineListList = new ArrayList<>(); // holds a collection of lineLists
    
    static protected double magnifyX =1, magnifyY =1; // these values are used to inscrease the size of the graph
    
    protected double windowWidth; // window size
    protected double windowHeight;
    
    static protected Point2D.Double origin = new Point2D.Double( 0,0 );
    static protected Line2D.Double xAxis = new Line2D.Double();
    static protected Line2D.Double yAxis = new Line2D.Double();
    
    boolean initialOrigin = true;
    
    /**
     * @param n is the number of points to calculate
     * @param x1 the first interval
     * @param x2 the second interval 
     */
    public Graph ( int n, double x1, double x2)
    {
        if ( n > 0 )
            this.n = n;
        else
            n = 1;
        this.x1 = x1;
        this.x2 = x2;
    }
    
    public void generateGraph( )
    {
        pointListList.clear();
        lineListList.clear();
        
        for ( int j = 0; j < inputList.size(); j++ )
        {
            pointList.clear();
            lineList.clear();
            double deltaX = ( x2 - x1 ) / n;
            double x = x1;
            Point2D.Double tempPoint;
            
            for ( int i = 0; i < n; i++)
            {
                tempPoint = getPointFromFunction( x, j );

                pointList.add( new Point2D.Double( ( tempPoint.getX()*magnifyX ) + origin.getX(), (windowHeight - (tempPoint.getY()*magnifyY)) - origin.getY() ) );
                x+=deltaX;
            }
            for ( int i = 0; i < pointList.size()-1; i++)
            {
                lineList.add( new Line2D.Double( pointList.get(i), pointList.get(i+1)));
            }
            pointListList.add( new ArrayList<>(pointList) );
            lineListList.add( new ArrayList<>(lineList) );
        }
        
        xAxis.setLine( 0, windowHeight - origin.getY(), windowWidth, windowHeight - origin.getY());
        yAxis.setLine( origin.getX(), 0, origin.getX(), windowHeight);
    }
    
    /**
     * @param currX current x value
     * @param i the index for the inputList
     * @return 
     */
    protected Point2D.Double getPointFromFunction( double currX, int i )
    {
        Evaluator ev = new Evaluator( inputList.get( i ) );
        ev.setX(currX);
        return new Point2D.Double(currX, ev.getExpressionValue());
    }

    public void setN( int n )
    {
        if ( n > 0)
            this.n = n;
        else
            n = 1;
    }
    public int getN()
    {
        return n;
    }

    public double getOriginX ()
    {
        return origin.getX();
    }
    public double getOriginY ()
    {
        return origin.getY();
    }
    
    public void setOrigin( double x, double y )
    {
        origin.setLocation( new Point2D.Double( x, y ) );
        repaint();
    }    
    
    public void setMagnify( double n )
    {
        if ( n > 0 )
        {
            magnifyX = n; 
            magnifyY = n;
        }
    }
    public double getMagnifyX()
    {
        return magnifyX;
    }
    public double getMagnifyY()
    {
        return magnifyY;
    }

    @Override
    public void paintComponent( Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        
        windowHeight = g2.getClipBounds().height;
        windowWidth = g2.getClipBounds().width;
        
        if ( initialOrigin )
        {
            origin.setLocation(new Point2D.Double(windowWidth/2, windowHeight/2));
            initialOrigin = false;
        }
        
        xAxis.setLine( 0, windowHeight - origin.getY(), windowWidth, windowHeight - origin.getY());
        yAxis.setLine( origin.getX(), 0, origin.getX(), windowHeight);
        g2.draw(xAxis);
        g2.draw(yAxis);
        
        if ( showRiemannSum ) 
        {
            showRiemannSum(nR);
            paintRectangles(g2);
        }
        g2.setColor(Color.RED);
        for ( int j = 0; j < lineListList.size(); j++ )
        {
            for ( int i = 0; i < lineListList.get(j).size(); i++ )
            {
                g2.draw(lineListList.get(j).get(i));
            }
        }
    }
    
    public void setX1( double x1 )
    {
        this.x1 = x1;
    }
    public void setX2( double x2 )
    {
        this.x2 = x2;
    }
    
    public void addInput( String input )
    {
        inputList.add(input);
    }
    
    public String removeInput( int i )
    {
        if ( !inputList.isEmpty() )
        {
            pointListList.remove(i);
            lineListList.remove(i);
            if ( inputList.size() == 1 ) clearRiemannSum();
            return inputList.remove( i );
        }
        else return "";
    }
    
    public int getInputListSize()
    {
        return inputList.size();
    }
    
    public String getInputListAt( int i )
    {
        return inputList.get(i);
    }
    
    private ArrayList<Rectangle2D.Double> rectangleList = new ArrayList<Rectangle2D.Double>();
    private boolean showRiemannSum = false;
    
    public double showRiemannSum( int n )
    {
        nR = n;
        clearRiemannSum();
        double deltaX = (x2 - x1)/n;
        double area = 0;
        double x = x1;
        
        for ( int i = 0; i < n; i++ )
        {
            double y = getPointFromFunction(x+deltaX/2, 0).getY();
            if ( y > 0 )
            {
                rectangleList.add( new Rectangle2D.Double( x*magnifyX + origin.getX(), windowHeight - y*magnifyY - origin.getY(), Math.abs(deltaX*magnifyX), Math.abs(y*magnifyY) ) );
                area += rectangleList.get(i).height*rectangleList.get(i).width/magnifyX/magnifyY;
            }
            else
            {
                rectangleList.add( new Rectangle2D.Double( x*magnifyX + origin.getX(), windowHeight - y*magnifyY - origin.getY() - Math.abs(y*magnifyY), Math.abs(deltaX*magnifyX), Math.abs(y*magnifyY) ) );
                area -= rectangleList.get(i).height*rectangleList.get(i).width/magnifyX/magnifyY;
            }
            x+=deltaX;
        }
        repaint();
        showRiemannSum = true;
        return area;
    }
    
    public void clearRiemannSum()
    {
        rectangleList.clear();
        showRiemannSum = false;
        repaint();
    }
    
    public boolean isRiemannSum()
    {
        return showRiemannSum;
    }
    
    public void paintRectangles( Graphics2D g2)
    {
        int red = 255, green=0, blue=0;
        for ( int i = 0; i < rectangleList.size(); i++ )
        {
            g2.setColor(Color.BLACK);
            if ( green < 250 && i < (double)(rectangleList.size()/3) )
            {
                green+=5;
                if ( red > 5) red-=5;
            }
            else if ( blue < 250 && i < (double)(2*rectangleList.size()/3))
            {
                blue+=5;
                if ( green > 5 ) green-=5;
            }
            else 
            {
                if ( red < 250) red+=5;
                if ( blue > 5 ) blue-=5;
            }
            g2.setPaint(new Color(red, green, blue) );
            g2.fill(rectangleList.get(i));
            g2.setColor(Color.BLACK);
            g2.draw(rectangleList.get(i));
        }
        g2.draw(xAxis);
        g2.draw(yAxis);
        g2.setColor(Color.RED);
        for ( int i = 0; i < lineList.size(); i++ ) g2.draw(lineList.get(i));
    }
}
