package pathfinding;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;

public class Graph {
    //Notice none of this are labeled final ;D

    //Graph Frame
    JFrame frame; //Initialize frame

    //General stuff for a graph
    private int cellNumber = 20; //Number of cells default
    private int delays = 30;

    //Graph density values
    private double denseValue = 0.5;
    private double density = (cellNumber * cellNumber) * denseValue;

    //Initial X and Y values on graph
    private int firstX = -1;
    private int firstY = -1;

    //Final X and Y values on graph
    private int lastX = -1;
    private int lastY = -1;

    //Number of tools that will be used when making a graph
    private int numTool = 0;

    private int check = 0; //Checking each block
    private int length = 0;
    private int currentAlgo = 0;

    //General L x W x H for graph
    private final int WIDTH = 850; //Pixels
    private final int HEIGHT = 650;
    private final int MAZESIZE = 600;
    private int CELLSIZE = MAZESIZE / cellNumber;

    //Arrays that will be used for a menu and tools
    private String[] Type = {
            "Dijkstra's",
            "A*"
    };
    private String[] Toolbox = {
            "Finish",
            "Start",
            "Block",
            "Undoifier"
    };

    //The only boolean that will check of the maze was solved
    private boolean solved = false; //Always set it to false because only when it is solved it can be reassigned as true

    //This will be used as the general map options
    Node[][] maps;
    Algorithm algo = new Algorithm();
    Random randomN = new Random();

    //This will be the menu sliders for the size of the graph, its density, and the delay/speed to start the operation
    JSlider mapSize = new JSlider(1, 5, 2); //Slider(integer minimum, integer max, integer value) :create a new slider with horizontal orientation and max, minimum value and the slider Value specified
    JSlider graphSpeed = new JSlider(0, 500, delays);
    JSlider numberOfB = new JSlider(1, 100, 50);

    //Titles for each menu type
    JLabel Algo = new JLabel("Algorithm");
    JLabel numberOfTools = new JLabel("Tools n' stuff");
    JLabel graphSize = new JLabel("Sizes: ");
    JLabel cellN = new JLabel(cellNumber + "x" + cellNumber);
    JLabel msDelay = new JLabel("MS Delay:");
    JLabel ms = new JLabel(delays + "ms");
    JLabel numberBlocks = new JLabel("Density: ");
    JLabel densityLabel = new JLabel(numberOfB.getValue() + "%");
    JLabel checkLabel = new JLabel("Check number: " + check);
    JLabel lengths = new JLabel("Length of thy path: " + length); //This will show the path length

    //Buttons for said menus!
    JButton start = new JButton("Start Pathfinding");
    JButton resets = new JButton("Reset Graph");
    JButton randomGen = new JButton("Randomly generate map");
    JButton mapClear = new JButton("Clear the map");


    //Fancy drop down menus :D
    JComboBox algoBox = new JComboBox(Type);
    JComboBox toolBox = new JComboBox(Toolbox);

    //Overall application panel
    JPanel panel = new JPanel();

    //The canvas!
    Map canvas;

    //And do finish this overall complex graph headass, a border lmao
    Border lower = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    public static void main(String[] args) {    //MAIN METHOD
        new Graph();
    }


    public Graph() { //The only constructor that will bind everything
        clear();
        startPro();
    }

    public void clear() { //This will clear the map
        lastX = -1; //Reset all the starting and finishing nodes
        lastY = -1;
        firstX = -1;
        firstY = -1;

        maps = new Node[cellNumber][cellNumber]; //Create a new map with all new nodes
        for (int x = 0; x < cellNumber; x++) {
            for (int y = 0; y < cellNumber; y++) {
                maps[x][y] = new Node(3, x, y); //Set every single node to basically empty
            }
        }
        reset(); //Just resets variables
    }

    public void genMaps() { //EXTERMELY REUSASBLE
        clear(); //Clear the map first

        for (int i = 0; i < density; i++) {
            Node current;

            do { //Do is just another way of saying while or if this is just shorter lmao

                int x = randomN.nextInt(cellNumber);
                int y = randomN.nextInt(cellNumber);
                current = maps[x][y]; //Find any random node on the grid
            } while (current.getType() == 2); //If it is a wall, then go find a new wall
            current.setType(2); //Set this node to now be a block

        }
    }

    public void resetTheGraph() { //Resets the whole damn thing
        for (int x = 0; x < cellNumber; x++) { //Reset graph and clear graph codes can be VERY reusable and would just need a bit of adjustment in terms of what variables you have, most likely will reuse this function on some other thing idk
            for (int y = 0; y < cellNumber; y++) {
                Node current = maps[x][y];
                if (current.getType() == 4 || current.getType() == 5) //This will check if the current node is either already checked or is part of the final path
                    maps[x][y] = new Node(3, x, y); //Just reset it if the node is empty
            }
        }
        if (firstX > -1 && firstY > -1) { //This will reset the start and finish points
            maps[firstX][firstY] = new Node(0, firstX, firstY);
            maps[firstX][firstY].setHops(0);
        }
        if (lastX > -1 && lastY > -1) {
            maps[lastX][lastY] = new Node(1, lastX, lastY);
            reset(); //Reset variables
        }

    }

    private void startPro() { //Start all the UI stuff, this is ESSENTIAL when creating programs that contain a UI
        frame = new JFrame(); //Create window
        frame.setVisible(true); //Make sure it appears on the screen
        frame.setResizable(false); //Can't expand the window
        frame.setSize(WIDTH, HEIGHT); //Set the windows size based off variable
        frame.setTitle("Cr3's Pathfinding!"); //Window title
        frame.setLocationRelativeTo(null); //When started it will create a window in the middle of the screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Click magic red X to make the fancy stuff go bye bye
        frame.getContentPane().setLayout(null); //Stuffs in the middle

        //The rest will now set the locations to all the menus, sliders, etc, which means all of this is hard coded and takes the majority of time to make and just a crap load of lines, atleast it ain't complicated

        panel.setBorder(BorderFactory.createTitledBorder(lower, "Controls"));
        int spaceing = 25; //Remember this in CSS kids?
        int buffering = 45;

        panel.setLayout(null);
        panel.setBounds(10, 10, 210, 600);
        //Now we are adding everything to the tools panel
        start.setBounds(40, spaceing, 120, 25);
        panel.add(start);
        spaceing += buffering;

        resets.setBounds(40, spaceing, 120, 25);
        panel.add(resets);
        spaceing += buffering;

        randomGen.setBounds(40, spaceing, 120, 25);
        panel.add(randomGen);
        spaceing += buffering;

        mapClear.setBounds(40, spaceing, 120, 25);
        panel.add(mapClear);
        spaceing += buffering;


        Algo.setBounds(40, spaceing, 120, 25);
        panel.add(Algo);
        spaceing += 25;

        algoBox.setBounds(40, spaceing, 120, 25);
        panel.add(algoBox);
        spaceing += 40;

        toolBox.setBounds(40, spaceing, 120, 25);
        panel.add(toolBox);
        spaceing += buffering;

        numberOfTools.setBounds(45, spaceing, 15, 30);
        panel.add(numberOfTools);
        spaceing += 25;

        graphSize.setBounds(15, spaceing, 40, 25);
        panel.add(graphSize);

        mapSize.setMajorTickSpacing(10);
        mapSize.setBounds(50, spaceing, 100, 25);
        panel.add(mapSize);

        cellN.setBounds(160, spaceing, 40, 25);
        panel.add(cellN);
        spaceing += buffering;

        msDelay.setBounds(15, spaceing, 50, 25);
        panel.add(msDelay);

        graphSpeed.setMajorTickSpacing(5);
        graphSpeed.setBounds(50, spaceing, 100, 25);
        panel.add(graphSpeed);

        ms.setBounds(160, spaceing, 40, 25);
        panel.add(ms);
        spaceing += buffering;

        numberBlocks.setBounds(15, spaceing, 100, 25);
        panel.add(numberBlocks);

        numberOfB.setMajorTickSpacing(5);
        numberOfB.setBounds(50, spaceing, 100, 25);
        panel.add(numberOfB);

        densityLabel.setBounds(160, spaceing, 100, 25);
        panel.add(densityLabel);
        spaceing += buffering;

        checkLabel.setBounds(15, spaceing, 100, 25);
        panel.add(checkLabel);
        spaceing += buffering;

        lengths.setBounds(15, spaceing, 100, 25);
        panel.add(lengths);
        spaceing += buffering;

        frame.getContentPane().add(panel);

        canvas = new Map();
        canvas.setBounds(230, 10, MAZESIZE + 1, MAZESIZE + 1);
        frame.getContentPane().add(canvas);

        //ALL THE ACTION LISTENERS, Basically registering each button to respond to mouse clicks, in the basic sense lol
        start.addActionListener(new ActionListener() {
            @Override
            //This basically means, whatever button is pressed will just be that button and no other, triggering a response when clicked
            public void actionPerformed(ActionEvent e) {
                reset();
                if ((firstX > -1 && firstY > -1) && (lastX > -1 && lastY > -1))
                    solved = true;
            }
        });

        //Action listeners are usually the best way of triggering a response or just registering clicks in general, highly REUSABLE CODE
        resets.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                resetTheGraph();
                Update();
            }
        });
        randomGen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genMaps();
                Update();
            }
        });
        mapClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
                Update();
            }
        });
        algoBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                currentAlgo = algoBox.getSelectedIndex();
                Update();
            }
        });
        toolBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                numTool = toolBox.getSelectedIndex();
            }
        });
        mapSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                cellNumber = mapSize.getValue() * 10;
                clear();
                reset();
                Update();
            }
        });
        graphSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                delays = graphSpeed.getValue();
                Update();
            }
        });
        numberOfB.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                denseValue = (double) numberOfB.getValue() / 100;
                Update();
            }
        });

        searching(); //Start whatever algorithm is selected
    }

    public void searching() { //What type of path-finding you shall use?
        if (solved) {
            switch (currentAlgo) {
                case 0:
                    algo.Dijkstra(); //algorithm class
                    break;
                case 1:
                    algo.AStar();
                    break;
                default:
                    algo.AStar();


            }
        }
        pause(); //Set a pause command for obvious reasons I guess
    }

    public void pause() {
        int s = 0;
        while (!solved) {
            s++;
            if (s > 500)
                s = 0;

            try {
                Thread.sleep(1); //set a timer
            } catch (Exception e) {
            }
        }
        searching();
    }

    public void Update() { //A class like this is usually always needed, designed to update everything that is related to the UI
        density = (cellNumber * cellNumber) * denseValue;
        CELLSIZE = MAZESIZE / cellNumber;
        canvas.repaint();
        cellN.setText(cellNumber + "x" + cellNumber);
        ms.setText(delays + "ms");
        lengths.setText("Length of Path: " + length);
        densityLabel.setText(numberOfB.getValue() + "%");
        checkLabel.setText("Checking: " + check);

    }

    public void reset() { //So that everything works as intended when stuffs resets
        solved = false;
        length = 0;
        check = 0;
    }

    public void delays() { //Delay method

        try {
            Thread.sleep(delays); //Set delay timer (It makes the animation)
        } catch (Exception e) {
        }

    }

    class Map extends JPanel implements MouseListener, MouseMotionListener { //Basically will register the mouse being held and dragged when holding mouse 1
        public Map() { //New constructor

            addMouseListener(this);
            addMouseMotionListener(this);

        }

        public void paintComponent(Graphics g) { //There are a number of factors that determine when a component needs to be re-painted, ranging from moving, re-sizing, changing focus, being hidden by other frames, and so on and so forth. Many of these events are detected auto-magically, and paintComponent is called internally when it is determined that that operation is necessary.
            super.paintComponent(g);
            for (int x = 0; x < cellNumber; x++) { //Now you can repaint or re-graphic each node on the grid
                for (int y = 0; y < cellNumber; y++) {
                    switch (maps[x][y].getType()) {
                        case 0:
                            g.setColor(Color.BLUE); //Now we set each color for different types of nodes and switch between them using the easy switch method rather than just a shit-ton of if methods
                            break;

                        case 1:
                            g.setColor(Color.GREEN);
                            break;

                        case 2:

                            g.setColor(Color.BLACK);
                            break;

                        case 3:
                            g.setColor(Color.lightGray);
                            break;

                        case 4:
                            g.setColor(Color.YELLOW);
                            break;

                        case 5:
                            g.setColor(Color.PINK);
                            break;
                    }
                    g.fillRect(x * CELLSIZE, y * CELLSIZE, CELLSIZE, CELLSIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x * CELLSIZE, y * CELLSIZE, CELLSIZE, CELLSIZE);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            try {
                int x = e.getX() / CELLSIZE;
                int y = e.getY() / CELLSIZE;
                Node current = maps[x][y];
                if ((numTool == 2 || numTool == 3) && (current.getType() != 0 && current.getType() != 1))
                    current.setType(numTool);
                Update();
            } catch (Exception m) {
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            reset(); //NOW, FINALLY FOR THE LOVE OF GOD, RESET WHEN CLICKED!

            try { //This is this code for setting the start node
                int x = e.getX() / CELLSIZE; //Get the x and y cords of the mouse click in terms of the size of the grid
                int y = e.getY() / CELLSIZE;
                Node current = maps[x][y];

                switch (numTool) {
                    case 0: { //The start node
                        if (current.getType() != 2) { //If not walls
                            if (firstX > -1 && firstY > -1) {
                                maps[firstX][firstY].setType(3);
                                maps[firstX][firstY].setHops(-1);
                            }
                            current.setHops(0);
                            firstX = x; //Set the start x and y stuffs
                            firstY = y;
                            current.setType(0); //Set the Node clicked to be start

                        }
                        break;
                    }
                    case 1: {
                        if (current.getType() != 2) {
                            if (lastX > -1 && lastY > -1) //If it is not a wall
                                maps[lastX][lastY].setType(3);

                            lastX = x; //Set the new last x and y cords
                            lastY = y;
                            current.setType(1); //The node click will now be the ending node


                        }
                        break;
                    }
                    default:
                        if (current.getType() != 0 && current.getType() != 1)
                            current.setType(numTool);

                        break;

                }
                Update();
            } catch (Exception m) {
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }


    }

    //Dijkstra works by propagating outwards until it finds the finish and then working its way back to get the path
    //it uses a priority que to keep track of nodes that it needs to explore
    //each node in the priority que is explored and all of its neighbors are added to the que
    //once a node is explored it is deleted from the que
    //an array list is used to represent the priority que
    //a separate array list is returned from a method that explores a nodes neighbors
    //this array list contains all the nodes that were explored, it is then added to the que
    //a hops variable in each node represents the number of nodes traveled from the start
    //Description credit to

    class Algorithm {

        public void Dijkstra() {
            ArrayList<Node> priority = new ArrayList<Node>(); //CREATE A PRIORITY QUE
            priority.add(maps[firstX][firstY]); //ADD THE START TO THE QUE
            while (solved) {
                if (priority.size() <= 0) { //IF THE QUE IS 0 THEN NO PATH CAN BE FOUND
                    solved = false;
                    break;
                }
                int hops = priority.get(0).getHops() + 1; //INCREMENT THE HOPS VARIABLE
                ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops); //CREATE AN ARRAYLIST OF NODES THAT WERE EXPLORED
                if (explored.size() > 0) {
                    priority.remove(0); //REMOVE THE NODE FROM THE QUE
                    priority.addAll(explored); //ADD ALL THE NEW NODES TO THE QUE
                    Update();
                    delays();
                } else { //IF NO NODES WERE EXPLORED THEN JUST REMOVE THE NODE FROM THE QUE
                    priority.remove(0);
                }
            }

        }

        //a star works essentially the same as dijkstra creating a priority que and propagating outwards until it finds the end
        //however a star builds in a heuristic of distance from any node to the finish
        //this means that nodes that are closer to the finish will be explored first
        //this heuristic is built in by sorting the que according to hops plus distance until the finish
        public void AStar() {
            // TODO Auto-generated method stub
            ArrayList<Node> priority = new ArrayList<Node>();
            priority.add(maps[firstX][firstY]);
            while (solved) {
                if (priority.size() <= 0) {
                    solved = false;
                    break;
                }
                int hops = priority.get(0).getHops() + 1;
                ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);
                if (explored.size() > 0) {
                    priority.remove(0);
                    priority.addAll(explored);
                    Update();
                    delays();
                } else {
                    priority.remove(0);
                }
                sortQue(priority); //SORT THE PRIORITY QUE
            }
        }

    }


    public ArrayList<Node> sortQue(ArrayList<Node> sort) { //SORT PRIORITY QUE
        int c = 0;
        while (c < sort.size()) {
            int sm = c;
            for (int i = c + 1; i < sort.size(); i++) {
                if (sort.get(i).getEuclidDist() + sort.get(i).getHops() < sort.get(sm).getEuclidDist() + sort.get(sm).getHops())
                    sm = i;
            }
            if (c != sm) {
                Node temp = sort.get(c);
                sort.set(c, sort.get(sm));
                sort.set(sm, temp);
            }
            c++; //Get it?
        }
        return sort;
    }

    public ArrayList<Node> exploreNeighbors(Node current, int hops) { //EXPLORE NEIGHBORS
        ArrayList<Node> explored = new ArrayList<Node>(); //LIST OF NODES THAT HAVE BEEN EXPLORED
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                int xbound = current.getX() + a;
                int ybound = current.getY() + b;
                if ((xbound > -1 && xbound < cellNumber) && (ybound > -1 && ybound < cellNumber)) { //MAKES SURE THE NODE IS NOT OUTSIDE THE GRID
                    Node neighbor = maps[xbound][ybound];
                    if ((neighbor.getHops() == -1 || neighbor.getHops() > hops) && neighbor.getType() != 2) { //CHECKS IF THE NODE IS NOT A WALL AND THAT IT HAS NOT BEEN EXPLORED
                        explore(neighbor, current.getX(), current.getY(), hops); //EXPLORE THE NODE
                        explored.add(neighbor); //ADD THE NODE TO THE LIST
                    }
                }
            }
        }
        return explored;
    }

    public void explore(Node current, int lastx, int lasty, int hops) { //EXPLORE A NODE
        if (current.getType() != 0 && current.getType() != 1) //CHECK THAT THE NODE IS NOT THE START OR FINISH
            current.setType(4); //SET IT TO EXPLORED
        current.setLastNode(lastx, lasty); //KEEP TRACK OF THE NODE THAT THIS NODE IS EXPLORED FROM
        current.setHops(hops); //SET THE HOPS FROM THE START
        check++;
        if (current.getType() == 1) { //IF THE NODE IS THE FINISH THEN BACKTRACK TO GET THE PATH
            backtrack(current.getLastX(), current.getLastY(), hops);
        }
    }

    public void backtrack(int lx, int ly, int hops) { //BACKTRACK
        length = hops;
        while (hops > 1) { //BACKTRACK FROM THE END OF THE PATH TO THE START
            Node current = maps[lx][ly];
            current.setType(5);
            lx = current.getLastX();
            ly = current.getLastY();
            hops--;
        }
        solved = false;
    }

    class Node {

        // 0 = start, 1 = finish, 2 = wall, 3 = empty, 4 = checked, 5 = finalpath
        private int cellType = 0;
        private int hops;
        private int x;
        private int y;
        private int lastX;
        private int lastY;
        private double dToEnd = 0;

        public Node(int type, int x, int y) { //CONSTRUCTOR
            cellType = type;
            this.x = x;
            this.y = y;
            hops = -1;
        }

        public double getEuclidDist() { //CALCULATES THE EUCLIDIAN DISTANCE TO THE FINISH NODE
            int xdif = Math.abs(x - lastX);
            int ydif = Math.abs(y - lastY);
            dToEnd = Math.sqrt((xdif * xdif) + (ydif * ydif));
            return dToEnd;
        }

        public int getX() {
            return x;
        } //GET METHODS

        public int getY() {
            return y;
        }

        public int getLastX() {
            return lastX;
        }

        public int getLastY() {
            return lastY;
        }

        public int getType() {
            return cellType;
        }

        public int getHops() {
            return hops;
        }

        public void setType(int type) {
            cellType = type;
        } //SET METHODS

        public void setLastNode(int x, int y) {
            lastX = x;
            lastY = y;
        }

        public void setHops(int hops) {
            this.hops = hops;
        }
    }
}
