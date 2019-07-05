import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.Timer.*;
import javax.swing.border.Border;

public class FallingBallsGame extends JFrame implements ActionListener{
    public static final int NCOLORS = 7;
    private final static int frameSizeX=200;
    private final static int nXRows=7, nYRows=20;
    private int r=frameSizeX/nXRows;//the diameter of the balls
    private final static int frameSizeY=nYRows*(frameSizeX/nXRows);
    private final static int nBalls=3;
    private int x, y;//the startpositions for the balls to fall down
    private static final Color BROWN = new Color(102,51,0);
    private static final Color PINK = new Color(255,153,203);
    public static final Color[] col = new Color[] {Color.blue,Color.green,Color.red,Color.yellow,BROWN,Color.cyan,PINK};
    public Color[] currentColor;//what colors that are moving down
    private int score;
    private static final int startInterval=800;
    private int interval=startInterval;
    private JLabel scoreLabel;
    private JButton newGame;
    private JPanel gamePanel;
    private Color[][] balls;//shows which positions are occupied
    private boolean[][] scores;//help arrays for seeing if we have scored
    private Timer timer;

    public FallingBallsGame(){
      currentColor=new Color[nBalls];
      JPanel bigPanel = new JPanel();
      gamePanel = new FallingBallsPanel();
      JPanel subPanel = new JPanel();
      scoreLabel = new JLabel();
      newGame = new JButton("Nytt spel");
      init();
      gamePanel.setBackground(Color.white);
      gamePanel.setPreferredSize(new Dimension(frameSizeX,frameSizeY));
      gamePanel.setBackground(Color.white);
      Border border = BorderFactory.createLineBorder(Color.black);
      gamePanel.setBorder(border);
      newGame.addActionListener(this);
      subPanel.add(scoreLabel);
      subPanel.add(newGame);
      bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));
      bigPanel.add(gamePanel);
      bigPanel.add(subPanel);
      add(bigPanel);
    }

    public void init(){//resets for a new game
      balls = new Color[nXRows][nYRows];
      scores = new boolean[nXRows][nYRows];
      score=0;
      reset();
    }

    public void reset(){//resets between every new ball-triples
      x=((nXRows-1)/2)*r;
      y=0;
      scoreLabel.setText("Score: " + Integer.toString(score));
      for (int i=0;i<nBalls;i++){
        currentColor[i]=col[(int)(NCOLORS*Math.random())];
      }
    }

    class FallingBallsPanel extends JPanel implements ActionListener, KeyListener{//for the game area
      public FallingBallsPanel(){
        repaint();
        timer = new Timer(interval, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);
      }

      public void paintComponent(Graphics g){
        super.paintComponent(g);
        for (int i=0;i<nXRows;i++){//paints the already existing balls
          for (int j=0;j<nYRows;j++){
            if (balls[i][j]!=null){
              g.setColor(balls[i][j]);
              g.fillOval(i*r,j*r,r,r);
            }
          }
        }
        if (balls[(nXRows-1)/2][nBalls-1]==null){//repaints the falling balls
          for (int i=0;i<nBalls;i++){
            g.setColor(currentColor[i]);
            g.fillOval(x,y+i*r,r,r);
          }
        }
        else{//if the mid column is full, stop the game!
          timer.stop();
        }
      }

      public void checkScoring(){//checks if we have three in a row
        int oldScore=score;
        for (int i=0;i<nXRows-(nBalls-1);i++){
          for (int j=0;j<nYRows-(nBalls-1);j++){
            checkScoring2(i,i+1,i+2,j,j,j);
            checkScoring2(i,i,i,j,j+1,j+2);
            checkScoring2(i,i+1,i+2,j,j+1,j+2);
          }
        }
        for (int i=nBalls-1;i<nXRows;i++){
          for (int j=0;j<nYRows-(nBalls-1);j++){
            checkScoring2(i,i-1,i-2,j,j+1,j+2);
          }
        }
        for (int i=nXRows-(nBalls-1);i<nXRows;i++){
          for (int j=0;j<nYRows-(nBalls-1);j++){
            checkScoring2(i,i,i,j,j+1,j+2);
          }
        }
        for (int i=0;i<nXRows-(nBalls-1);i++){
          for (int j=nYRows-(nBalls-1);j<nYRows;j++){
            checkScoring2(i,i+1,i+2,j,j,j);
          }
        }
        for (int i=0;i<nXRows;i++){//removes the balls which have given us scores
          for (int j=0;j<nYRows;j++){
            if(scores[i][j]){
              try{
                for (int k=j;k>0;k--){
                  balls[i][k]=balls[i][k-1];
                }
              }
              catch(NullPointerException ex){
                balls[i][j]=null;
              }
              scores[i][j]=false;
            }
          }
        }
        if (score > oldScore){//speeds it up once we reached 50 points
          if (score/50>oldScore/50){
            interval-=interval/10;
            timer.setDelay(interval);
          }
          checkScoring();
        }
      }

      public void checkScoring2(int i1,int i2,int i3,int j1,int j2,int j3){//help method for checkScoring
        try{
          if (balls[i1][j1].equals(balls[i2][j2]) && balls[i2][j2].equals(balls[i3][j3])){
            scores[i1][j1]=true;
            scores[i2][j2]=true;
            scores[i3][j3]=true;
            score++;
          }
        }
        catch(NullPointerException ex){}//we don't care if there is a null object here
      }

      public void actionPerformed(ActionEvent e) {//updates constantly with the timer
          if (y+(1+nBalls)*r <= frameSizeY && balls[x/r][y/r+nBalls]==null) {
            y+=r;
          }
          else {
            for (int i=0;i<nBalls;i++){
              balls[x/r][y/r+i]=currentColor[i];
            }
            checkScoring();
            reset();
          }
          repaint();
      }

      public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        try{
          if (key == KeyEvent.VK_LEFT) {//the ball moves left
            if (x >= r && balls[x/r-1][y/r+(nBalls-1)]==null) {
              x -= r;
            }
          }
          else if (key == KeyEvent.VK_RIGHT) {//the ball moves right
            if (x+2*r <= frameSizeX && balls[x/r+1][y/r+(nBalls-1)]==null) {
              x += r;
            }
          }
          else if (key == KeyEvent.VK_DOWN) {//move faster down
            if (y+(1+nBalls)*r <= frameSizeY && balls[x/r][y/r+nBalls]==null) {
              y += r;
            }
          }
          else if (key == KeyEvent.VK_UP) {//switches the order of the balls
            Color tempCol = currentColor[0];
            for (int i=0;i<nBalls-1;i++){
              currentColor[i] = currentColor[i+1];
            }
            currentColor[nBalls-1]=tempCol;
          }
        }
        catch(ArrayIndexOutOfBoundsException ex){}
        repaint();
      }
      public void keyReleased(KeyEvent e){}
      public void keyTyped(KeyEvent e){}
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
              JFrame frame = new FallingBallsGame();
              frame.pack();
              frame.setVisible(true);
              frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(newGame)){//starts a new game
        init();
        interval=startInterval;
        timer.setDelay(interval);
        if (!timer.isRunning()){
          timer.restart();
        }
        gamePanel.repaint();
        gamePanel.requestFocus();
      }
    }
}
