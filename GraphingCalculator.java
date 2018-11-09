import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

/**
 *
 * @author jamie lopez Description: A graphing calculator that can evaluate
 * expressions and graph functions
 */
public class GraphingCalculator
{
    static int pressedX;
    static int pressedY;
    static int releasedX;
    static int releasedY;
    static double currentX;
    static double currentY;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setSize(700, 700);
        frame.setTitle("Graphing Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Graph graph = new Graph(500, 1, 10);

        JPanel panelButtons = new JPanel();
        JPanel graphButtonsPanel = new JPanel();

        JTextField expression = new JTextField("");
        expression.setColumns(20);
        JTextField x1TF = new JTextField("-10");
        JTextField x2TF = new JTextField("10");
        JTextField rsTF = new JTextField("5");
        rsTF.setColumns(5);
        x1TF.setColumns(5);
        x2TF.setColumns(5);
        JButton zoomInButton = new JButton("+");
        JButton zoomOutButton = new JButton("-");
        JButton graphButton = new JButton("Graph it!");
        JButton removeGraphButton = new JButton("Remove Graph");
        JButton showRiemannSumButton = new JButton("Show Riemann Sum");

        graphButtonsPanel.add(expression);
        graphButtonsPanel.add(graphButton);
        graphButtonsPanel.add(x1TF);
        graphButtonsPanel.add(x2TF);
        graphButtonsPanel.add(removeGraphButton);
        graphButtonsPanel.add(zoomInButton);
        graphButtonsPanel.add(zoomOutButton);
        graphButtonsPanel.add(showRiemannSumButton);
        graphButtonsPanel.add(rsTF);

        JLabel graphLabel = new JLabel();
        graphLabel.setText(" ");

        class zoomInButtonListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                graph.setMagnify(graph.getMagnifyX() + 10);
                graph.generateGraph();
                graph.repaint();
            }
        }
        class zoomOutButtonListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                graph.setMagnify(graph.getMagnifyX() - 10);
                graph.generateGraph();
                graph.repaint();
            }
        }
        class graphButtonListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                graph.addInput(expression.getText());
                graph.setX1(Double.parseDouble(x1TF.getText()));
                graph.setX2(Double.parseDouble(x2TF.getText()));
                graph.generateGraph();
                graph.repaint(); 
                graphLabel.setText("");
                for (int i = 0; i < graph.getInputListSize(); i++)
                    graphLabel.setText(graphLabel.getText() + "f(x)=" + graph.getInputListAt(i) + ", "); 
            }
        }
        class removeGraphButtonListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                expression.setText(graph.removeInput(graph.getInputListSize() - 1));
                graph.generateGraph();
                graph.repaint();

                graphLabel.setText(" ");
                for (int i = 0; i < graph.getInputListSize(); i++)
                    graphLabel.setText(graphLabel.getText() + "f(x)=" + graph.getInputListAt(i) + ", ");
            }
        }

        zoomInButton.addActionListener(new zoomInButtonListener());
        zoomOutButton.addActionListener(new zoomOutButtonListener());
        graphButton.addActionListener(new graphButtonListener());
        removeGraphButton.addActionListener(new removeGraphButtonListener());
        showRiemannSumButton.addActionListener(new ActionListener()
        {
           @Override
           public void actionPerformed(ActionEvent event)
           {
               if ( graph.isRiemannSum() )
                   graph.clearRiemannSum();
               else
                   graph.showRiemannSum(Integer.parseInt(rsTF.getText()));
           }
        });

        graph.addMouseListener(new MouseListener()
        {
            @Override
            public void mousePressed(MouseEvent event)
            {
                pressedX = event.getX();
                pressedY = event.getY();
                currentX = graph.getOriginX();
                currentY = graph.getOriginY();
            }
            @Override
            public void mouseReleased(MouseEvent event){}
            @Override
            public void mouseClicked(MouseEvent event){}
            @Override
            public void mouseEntered(MouseEvent event){}
            @Override
            public void mouseExited(MouseEvent event){}
        });
        
        graph.addMouseMotionListener( new MouseMotionListener()
        {
            @Override
            public void mouseDragged(MouseEvent event)
            {
                releasedX = event.getX();
                releasedY = event.getY();
                graph.setOrigin((releasedX - pressedX) + currentX, currentY - (releasedY - pressedY));
                graph.generateGraph();
            }

            @Override
            public void mouseMoved(MouseEvent event){}
        });
        
        graph.addComponentListener(new ComponentListener()
        {
            @Override
            public void componentResized(ComponentEvent event){if (frame.isResizable()) graph.repaint();}
            @Override
            public void componentHidden(ComponentEvent event){}
            @Override
            public void componentShown(ComponentEvent event){}
            @Override
            public void componentMoved(ComponentEvent event){}
        });

        frame.add(graphLabel, "North");
        frame.add(graph);
        panelButtons.add(graphButtonsPanel, BorderLayout.EAST);
        frame.add(panelButtons, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
