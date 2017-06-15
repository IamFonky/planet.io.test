package ch.elmootan.core.universe;

import ch.elmootan.core.physics.*;
import ch.elmootan.core.sharedObjects.GameCreator;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import static java.lang.Math.*;

//import com.zenjava.javafx.maven.plugin.*;

public class GUniverse extends JFrame {

    private final ArrayList<Body> allThings = new ArrayList<>();
    private ArrayList<Body> statBodies = new ArrayList<>();

    private double zoom = 500.0;
    private int dx = 0;
    private int dy = 0;
    private boolean followMode = false;

    private boolean asAdmin;

    private TheEndFrame theEndFrame = new TheEndFrame();
    private JPanel rootPane;
    private final double controlPlanetMass = 2E+24;
    private InvisiblePlanet clickedPlanet;
    private Planet myPlanet;
    private boolean mousePressed = false;

    private ArrayList<BufferedImage> planets = new ArrayList<>();
    private ArrayList<BufferedImage> invisibles = new ArrayList<>();

    private BufferedImage atmospher;
    private BufferedImage bonus;

    private MediaPlayer mediaPlayer;

    private float bonusCounter = 0;

    private BufferedReader rd;
    private PrintWriter wr;

    private int gameId;
    private boolean playerAlive = true;
    private boolean playerIsWatching = false;
    private boolean playerWantQuit = false;

    private final ObjectMapper mapper = new ObjectMapper();

    private BufferedImage backgroundImage;
    private TexturePaint backgroundTexture;

    private void setDx(int dx) {
        this.dx = (dx > 0) ? min(dx, (int)10e5 / (int)zoom) : max(dx, (int)-10e5 / (int)zoom);
    }

    private void setDy(int dy) {
        this.dy = (dy > 0) ? min(dy, (int)10e5 / (int)zoom) : max(dy, (int)-10e5 / (int)zoom);
    }

    public GUniverse(PrintWriter wr, BufferedReader rd, MulticastSocket udpSocket, int gameId, Planet myPlanet, boolean asAdmin) {

        super("Mon univers");
        this.rd = rd;
        this.wr = wr;
        this.gameId = gameId;
        this.myPlanet = myPlanet;
        this.asAdmin = asAdmin;

        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/skins/universe.jpg"));
            backgroundTexture = new TexturePaint(
                    backgroundImage,
                    new Rectangle(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight())
            );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        try {
            for (int i = 1; i <= 8; i++)
                planets.add(ImageIO.read(getClass().getResourceAsStream("/skins/planet" + i + "_64x64.png")));
            for (int i = 1; i <= 4; i++)
                invisibles.add(ImageIO.read(getClass().getResourceAsStream("/skins/invisible" + i + "_64x64.png")));
            atmospher = ImageIO.read(getClass().getResourceAsStream("/skins/atmospher.png"));
            bonus = ImageIO.read(getClass().getResourceAsStream("/skins/bonus.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() == 1)
                {
                    zoom += zoom * 0.1;
                }
                else if(e.getWheelRotation() == -1)
                {
                    zoom -= zoom * 0.1;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                if (!playerIsWatching && clickedPlanet != null && mousePressed) {
                    setInvisiblePlanet(e);
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                if (!playerIsWatching && (clickedPlanet == null || !mousePressed)) {
                    createInvisiblePlanet(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (clickedPlanet != null && mousePressed) {
                    killInvisiblePlanet(e);
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ADD:
                    case KeyEvent.VK_PLUS:
                    case KeyEvent.VK_Q:
                        zoom -= zoom * 0.1;
                        break;
                    case KeyEvent.VK_MINUS:
                    case KeyEvent.VK_SUBTRACT:
                    case KeyEvent.VK_E:
                        zoom += zoom * 0.1;
                        break;

                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        followMode = false;
                        setDx(dx + (int)Math.sqrt(zoom));
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        followMode = false;
                        setDx(dx - (int)Math.sqrt(zoom));
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        followMode = false;
                        setDy(dy + (int)Math.sqrt(zoom));
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        followMode = false;
                        setDy(dy - (int)Math.sqrt(zoom));
                        break;

                    case KeyEvent.VK_SPACE:
                        followMode = !followMode;
                        break;

                    case KeyEvent.VK_ENTER:
                        followMode = false;
                        setDx(0);
                        setDy(0);
                        break;

                    default:
                        break;
                }
                System.out.println(e.getKeyChar());

            }
        });

        setSize(1000, 1000);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  wr.println(Protocol.PLANET_IO_LEAVING_GAME + Protocol.CMD_SEPARATOR + gameId + Protocol.CMD_SEPARATOR + myPlanet.getName());
                                  wr.flush();
                                  dispose();
                                  //System.exit(0);
                              }
                          }
        );


        rootPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                final Graphics2D g2d = (Graphics2D) g;

                backgroundTexture = new TexturePaint(
                        backgroundImage,
                        new Rectangle(dx, dy, backgroundImage.getWidth(), backgroundImage.getHeight())
                );

                g2d.setPaint(backgroundTexture);
                g2d.fill(new Rectangle(0, 0, getWidth(), getHeight()));

                g.setColor(Color.WHITE);

                synchronized (allThings) {
                    if (GUniverse.this.asAdmin) {
                        g2d.drawString("Player List:", 0, 10);
                        for (int i = 0; i < allThings.size(); i++) {
                            Body bodyScore = allThings.get(i);
                            if (bodyScore.getClass() == Planet.class) {
                                g2d.drawString(bodyScore.getName() + " : " + (int) bodyScore.getRadius(), 0, 15 * (i + 2));
                            }
                        }

                    } else {
//                        allThings.sort(Comparator.comparingDouble(Body::getRadius));
//                        Collections.reverse(allThings);
//                        int nbScores = allThings.size() > 5 ? 5 : allThings.size();
//                        g2d.drawString("Top 5:", 0, 10);
//                        int i = 0, j = 1;
//                        while (i != nbScores) {
//                            Body bodyScore = allThings.get(i);
//                            if (bodyScore instanceof Planet && !(bodyScore instanceof InvisiblePlanet)) {
//                                if(bodyScore.getName().indexOf("MOON") != 0)
//                                    g2d.drawString(j + ". " + bodyScore.getName() + " : " + (int) bodyScore.getRadius(), 0, 15 * (++j) + 10);
//                            } else if (allThings.size() > 5) {
//                                nbScores++;
//                            }
//                            if (++i >= allThings.size())
//                                break;
//                        }


                        for (Body body : allThings) {
                            int radius = (int) (body.getRadius() / zoom);
                            radius = radius > 0 ? radius : 1;


                            int x = (getWidth() / 2) + ((int) ((body.getPosition().getX() - (body.getRadius() / 2)) / zoom)) + dx;
                            int y = (getHeight() / 2) + ((int) ((body.getPosition().getY() - (body.getRadius() / 2)) / zoom)) + dy;

                            if (!asAdmin && body.getClass() == InvisiblePlanet.class && body.getId() == myPlanet.getId()) {
                                g2d.drawImage(invisibles.get(((InvisiblePlanet) body).getIdSkin()).getScaledInstance(radius, radius, 0), x, y, this);
                            } else if (body.getClass() == Bonus.class) {
                                g2d.drawImage(bonus.getScaledInstance(radius + radius, radius + radius, 0), x - radius / 2, y - radius / 2, this);
                            } else if (body.getClass() == Planet.class) {
                                if(body.getName().indexOf("MOON") != 0)
                                g2d.drawString(body.getName(), x - (body.getName().length() / 2) * 5 + radius / 2, y - 10);
                                g2d.drawImage(planets.get(((Planet) body).getIdSkin()).getScaledInstance(radius, radius, 0), x, y, this);

                                switch (((Planet) body).getActiveBonus()) {
                                    case Bonus.MOON:
                                        Double xB = new Double(x + radius / 4 + (radius * (Math.cos(Math.toRadians(bonusCounter)))));
                                        Double yB = new Double(y + radius / 4 + (radius * (Math.sin(Math.toRadians(bonusCounter)))));
                                        int xp = xB.intValue();
                                        int yp = yB.intValue();

                                        g.drawOval(xp, yp, radius / 5, radius / 5);

                                        bonusCounter += 0.1f;
                                        if (bonusCounter > 360)
                                            bonusCounter = 0;
                                        break;
                                    case Bonus.ATMOSPHER:
                                        g2d.drawImage(atmospher.getScaledInstance(radius + radius, radius + radius, 0), x - radius / 2, y - radius / 2, this);
                                        break;
                                }
                            } else if (body.getClass() == Fragment.class) {
                                g.drawRect(x, y, radius, radius);

                            }

                            if (body.equals(myPlanet)) {
                                myPlanet.setPosition(body.getPosition());
                                if (followMode) {
                                    setDx((int) -((myPlanet.getPosition().getX() - (body.getRadius() / 2)) / zoom));
                                    setDy((int) -((myPlanet.getPosition().getY() - (body.getRadius() / 2)) / zoom));

                                }
                            }

                        }
                    }
                }

                        if(statBodies != null) {
                            synchronized (allThings)
                            {
                                statBodies = (ArrayList<Body>)allThings.clone();
                            }
                            statBodies.sort(Comparator.comparingDouble(Body::getRadius));
                            Collections.reverse(statBodies);
                            int nbScores = statBodies.size() > 5 ? 5 : statBodies.size();
                            g2d.drawString("Top 5:", 0, 10);
                            int i = 0, j = 1;
                            while (i != nbScores) {
                                Body bodyScore = statBodies.get(i);
                                if (bodyScore instanceof Planet && !(bodyScore instanceof InvisiblePlanet)) {
                                    if (bodyScore.getName().indexOf("MOON") != 0)
                                        g2d.drawString(j + ". " + bodyScore.getName() + " : " + (int) bodyScore.getRadius(), 0, 15 * (++j) + 10);
                                } else if (statBodies.size() > 5) {
                                    nbScores++;
                                }
                                if (++i >= statBodies.size())
                                    break;
                            }
                        }


//                (new Thread()
//                {
//                    @Override
//                    public void run() {
//                        ArrayList<Body> statBodies = (ArrayList<Body>)allThings.clone();
//                        statBodies.sort(Comparator.comparingDouble(Body::getRadius));
//                        Collections.reverse(statBodies);
//                        int nbScores = statBodies.size() > 5 ? 5 : statBodies.size();
//                        g2d.drawString("Top 5:", 0, 10);
//                        int i = 0, j = 1;
//                        while (i != nbScores) {
//                            Body bodyScore = statBodies.get(i);
//                            if (bodyScore instanceof Planet && !(bodyScore instanceof InvisiblePlanet)) {
//                                if(bodyScore.getName().indexOf("MOON") != 0)
//                                    g2d.drawString(j + ". " + bodyScore.getName() + " : " + (int) bodyScore.getRadius(), 0, 15 * (++j) + 10);
//                            } else if (statBodies.size() > 5) {
//                                nbScores++;
//                            }
//                            if (++i >= statBodies.size())
//                                break;
//                        }
//                    }
//                }).start();

            }

        };

        //ScorePane scorePane = new ScorePane();
        // rootPane.add(scorePane);

        ActionListener repaintLol = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //scorePane.setScores();
                refreshBodies();
                rootPane.repaint();
            }
        };


        javax.swing.Timer displayTimer = new javax.swing.Timer(10, repaintLol);
        displayTimer.start();

        add(rootPane);

        setSize(1000, 1000);

        setLocationRelativeTo(null);

        setVisible(true);

        /*setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitProcedure();
            }
        });*/

    }


    public void showUI() {
        setVisible(true);
    }

    private void refreshBodies() {
        //WAIT FOR UNIVERSE INFOS
    }

    private int getControlForce(MouseEvent e) {
        if (e.isShiftDown() && e.isControlDown()) {
            clickedPlanet.setIdSkin(3);
            return 10;
        } else if (e.isShiftDown()) {
            clickedPlanet.setIdSkin(2);
            return 2;
        } else if (e.isControlDown()) {
            clickedPlanet.setIdSkin(1);
            return 3;
        } else {
            clickedPlanet.setIdSkin(0);
            return 1;
        }
    }

    private boolean createInvisiblePlanet(MouseEvent e) {
        try {
            wr.println(Protocol.PLANET_IO_CREATE_PLANET + Protocol.CMD_SEPARATOR + gameId);
            wr.flush();
            if (rd.readLine().equals(Protocol.PLANET_IO_SUCCESS)) {
                generatePlanetFromClick(e.getX(), e.getY());
                clickedPlanet.setMass(controlPlanetMass * getControlForce(e));
                //clickedPlanet.setRadius(clickedPlanet.getRadius()*nbClicks);
                mousePressed = true;
                wr.println(mapper.writeValueAsString(clickedPlanet));
                wr.flush();
                String pressedPlanet = rd.readLine();
                if (!pressedPlanet.equals(Protocol.PLANET_IO_FAILURE)) {
                    clickedPlanet = mapper.readValue(pressedPlanet, InvisiblePlanet.class);
                    mousePressed = true;
                } else {
                    clickedPlanet = null;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean setInvisiblePlanet(MouseEvent e) {
        try {
            mousePressed = false;
            //Demande des modification de la planete au serveur
            wr.println(Protocol.PLANET_IO_SET_PLANET + Protocol.CMD_SEPARATOR + gameId);
            wr.flush();
            if (rd.readLine().equals(Protocol.PLANET_IO_SUCCESS)) {
                //Récupération de la masse de la planete cliquée
                clickedPlanet.setMass(controlPlanetMass * getControlForce(e));
                clickedPlanet.setPosition(convertXYToPosition(e.getX(), e.getY()));

                wr.println(mapper.writeValueAsString(clickedPlanet));
                wr.flush();
                if (rd.readLine().equals(Protocol.PLANET_IO_SUCCESS)) {
                    mousePressed = true;
                } else {
                    clickedPlanet = null;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return mousePressed;
    }

    private boolean killInvisiblePlanet(MouseEvent e) {
        try {
            wr.println(Protocol.PLANET_IO_KILL_PLANET + Protocol.CMD_SEPARATOR + gameId);
            wr.flush();
            if (rd.readLine().equals(Protocol.PLANET_IO_SUCCESS)) {
                wr.println(mapper.writeValueAsString(clickedPlanet));
                wr.flush();
                mousePressed = false;
                clickedPlanet = null;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return mousePressed;
    }

    private Position convertXYToPosition(double x, double y) {
        double bodyX = ((x - (getWidth() / 2) - dx) * zoom);
        double bodyY = ((y - (getHeight() / 2) - dy) * zoom);
        return new Position(bodyX, bodyY);
    }

    private void generatePlanetFromClick(double x, double y) {
        double bodyRadius = 30000;
        clickedPlanet = new InvisiblePlanet(
                "Invisible-" + myPlanet.getName(),
                convertXYToPosition(x, y),
                controlPlanetMass,
                bodyRadius,
                myPlanet.getId());
    }

    public void hollySong(String soundFile, double volume) {
        new JFXPanel();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media hit = new Media(getClass().getResource("/sounds/" + soundFile).toString());
        mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.setVolume(volume);
        mediaPlayer.play();
    }

    public synchronized ArrayList<Body> getAllThings() {
        return allThings;
    }

    public void setAllThings(ArrayList<Body> bodies) {
        synchronized (allThings) {
            allThings.clear();
            allThings.addAll(bodies);
        }
    }

    public class TheEndFrame extends JFrame {
        private TheEndFrame() {
            super();
            setSize(500, 100);
            JPanel theEndPannel = new JPanel();
            JLabel theEndLabel = new JLabel();
            JButton theEndWatch = new JButton();
            JButton theEndQuit = new JButton();
            setTitle("This is the end, my friend.");


            //End label
            theEndLabel.setText("Sorry bro, you died...");

            //Watch button
            theEndWatch.setText("Keep watching");
            theEndWatch.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    playerIsWatching = true;
                    setVisible(false);
                }
            });

            //Quit button
            theEndQuit.setText("Go to lobby");
            theEndQuit.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    playerWantQuit = true;
                    setVisible(false);
                }
            });


            //Adding content to pannel
            theEndPannel.add(theEndLabel);
            theEndPannel.add(theEndWatch);
            theEndPannel.add(theEndQuit);
            add(theEndPannel);
        }
    }
}
