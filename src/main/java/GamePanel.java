import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class GamePanel extends JPanel implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(MyKeyAdapter.class);
    static final int SCREEN_WIDTH = 1300;
    static final int SCREEN_HEIGHT = 750;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS =(SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 175;

    final int[] x = new int [GAME_UNITS];
    final int[] y = new int [GAME_UNITS];

    int bodyParts = 6;
    int appleEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;

    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();

        logger.info("Game started");
    }

    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent (Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw (Graphics g){

        if (running){

            g.setColor(Color.red);
            g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++){
                if (i == 0){
                    g.setColor(new Color(90, 168, 196));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(50, 124, 184));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + appleEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + appleEaten))/2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }

    public void newApple(){
        appleX = random.nextInt((SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;

        logger.debug("New apple created at x: {}, y: {}", appleX, appleY);
    }

    public void move(){

        for(int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            appleEaten++;
            newApple();
        }
    }

    public void checkCollisions(){
        //проверка на удар головой в стену
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        //левая стена
        if(x[0] < 0){
            running = false;
        }
        //правая стена
        if(x[0] > SCREEN_WIDTH){
            running = false;
        }
        //верхняя стена
        if(y[0] < 0){
            running = false;
        }
        //нижняя стена
        if(y[0] > SCREEN_HEIGHT){
            running = false;
        }
        if (!running){
            timer.stop();

            logger.info("Game over");
        }
    }

    public void gameOver(Graphics g){
        //Число съеденных яблок должно отображаться при гейм овере
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metricsScore = getFontMetrics(g.getFont());
        g.drawString("Score: " + appleEaten, (SCREEN_WIDTH - metricsScore.stringWidth("Score: " + appleEaten))/2, g.getFont().getSize());

        //Отображение гейм овера
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsGameOver = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metricsGameOver.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {

            direction = (e.getKeyCode() == KeyEvent.VK_LEFT && direction != 'R') ? 'L' :
                        (e.getKeyCode() == KeyEvent.VK_RIGHT && direction != 'L') ? 'R' :
                        (e.getKeyCode() == KeyEvent.VK_UP && direction != 'D') ? 'U' :
                        (e.getKeyCode() == KeyEvent.VK_DOWN && direction != 'U') ? 'D' :
                                direction;

            logger.info("User pressed key: {}", KeyEvent.getKeyText(e.getKeyCode()));
        }
    }

}
