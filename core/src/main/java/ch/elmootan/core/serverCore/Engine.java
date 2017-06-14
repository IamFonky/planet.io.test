package ch.elmootan.core.serverCore;

import ch.elmootan.core.physics.*;
import ch.elmootan.core.universe.*;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class Engine {
    private final ArrayList<Body> allThings = new ArrayList<>();
    private final ArrayList<Body> userPlanets = new ArrayList<>();
    private ServerMulticast multicastServer;
    private int engineId;

    private Random randomGenerator;

    private int nextBonusTime = 0;
    private int bonusTime = 0;

    private int nextMoonTime = 0;
    private int moonTime = 0;


    private static final long TIME_BONUS_STAY = 21000;
    private static final int MIN_TIME_BONUS_APPEARS = 20000;
    private static final int MAX_TIME_BONUS_APPEARS = 60000;

    private static final int MIN_TIME_MOON_APPEARS = 5000;
    private static final int MAX_TIME_MOON_APPEARS = 15000;
    private static final int MAX_BODIES = 50;


    private static final int SERVER_SPEED = 20;

    public Engine(ServerMulticast udpServer, int serverId) {
        engineId = serverId;
        multicastServer = udpServer;
        ActionListener repaintLol = evt -> calculateBodies();
        javax.swing.Timer displayTimer = new javax.swing.Timer(SERVER_SPEED, repaintLol);
        displayTimer.start();
        randomGenerator = new Random();
        nextBonusTime = randomGenerator.nextInt(MAX_TIME_BONUS_APPEARS/SERVER_SPEED) + MIN_TIME_BONUS_APPEARS/SERVER_SPEED;
    }

    private void calculateBodies() {
        for (int i = 0; i < allThings.size(); ++i) {
            Body body = allThings.get(i);
            if (body instanceof Bonus) {
                long timeAlive = System.currentTimeMillis() - ((Bonus) body).getCreationTime();
                if (timeAlive >= TIME_BONUS_STAY) {
                    removeBody(body);
                    continue;
                }
            }
            if (body != null) {
                for (int j = i + 1; j < allThings.size(); ++j) {
                    Body surrounding = allThings.get(j);
                    if (surrounding != null) {
                        double gTgDistance = 0;
                        double sqDistance = 0;
                        double dX = 0;
                        double dY = 0;
                        //On calcule les distances x et y et la distance au carré
                        synchronized (body) {
                            dX = surrounding.getPosition().getX() - body.getPosition().getX();
                            dY = surrounding.getPosition().getY() - body.getPosition().getY();
                            sqDistance = dX * dX + dY * dY;

                            //On calcule la distance réelle (Ground to ground)
                            gTgDistance = Math.sqrt(sqDistance) - body.getRadius() / 2 - surrounding.getRadius() / 2;

                        }

                        if (gTgDistance < 0) //Collision!
                        {
                            // Si la planète du joueur et la planète cliquée rentrent en collision, la planète cliquée disparait
                            // (on évite ainsi les valeurs limites).
                            if (body.getId() == surrounding.getId()) {
                                if (InvisiblePlanet.class.isInstance(body)) {
                                    removeBody(body);
                                } else if (InvisiblePlanet.class.isInstance(surrounding)) {
                                    removeBody(surrounding);
                                }
                            }
                            // Sinon, si une des deux planète est une planète cliquée, saute cette comparaison car la planète
                            // est invisible et n'interragit pas avec celle des autres joueurs.
                            else if ((body instanceof InvisiblePlanet) || (surrounding instanceof InvisiblePlanet)) {
                                continue;
                            } else {
                                BodyState eatState;
                                synchronized (allThings) {
                                    if (isProtected(body) || isProtected(surrounding))
                                        continue;
                                    if (body.getMass() > surrounding.getMass()) {
                                        eatState = body.eat(surrounding);
                                        allThings.remove(surrounding);
                                    } else {
                                        eatState = surrounding.eat(body);
                                        allThings.remove(body);
                                    }

                                    switch (eatState) {
                                        case EXPLODE:
                                            explode(body);
                                            break;
                                        //On peut imaginer ici un case SUN ou BLACKHOLE
                                    }
                                }
                            }
                        }

                        // On applique la gravité et la physique uniquement dans 2 cas :
                        //   - Si c'est la planète cliquée et la planète du joueur.
                        //   - Si aucune des deux planète n'est une planète cliquée.
                        else if ((!(body instanceof InvisiblePlanet) && !(surrounding instanceof InvisiblePlanet)) ||
                                (body.getId() == surrounding.getId())) {
                            //On calcule le ratio des composantes de distance x et y (règle de 3, Thalès)
                            double rDX = dX / Math.sqrt(sqDistance);
                            double rDY = dY / Math.sqrt(sqDistance);

                            //Loi de gravité : F [N] = G [N*m2*kg-2] * mA [kg] * mB [kg] / d2 [m2]
                            //un Newton = 1 [kg*m*s-2]. Plus simplement [kg*a] ou a est l'accélération
                            //donc G [kg*m*s-2*m2*kg-2] ==> G [m3*s-2*kg-1]
                            //On peut donc calculer simplement l'accélération aN sur chaque corps avec :
                            //aA [m*s-2] = G [m3*s-2*kg-1] * mB [kg] / d2 [m2]
                            double bodyA = Constants.GRAVITATION.valeur * surrounding.getMass() / sqDistance;
                            //A cette étape nous avons un vecteur d'accélération mais pas de direction
                            //Il décomposer en deux composantes x et y
                            double bodyAX = bodyA * rDX;
                            double bodyAY = bodyA * rDY;

                            //Même chose pour les deuxième corps
                            double surrA = Constants.GRAVITATION.valeur * body.getMass() / sqDistance;
                            double surrAX = surrA * -rDX;
                            double surrAY = surrA * -rDY;

                            //Il faut maintenant appliquer accélérations x et y aux vitesses x et y
                            //Pour le moment la cadence du processeur règle la vitesse du programme
                            synchronized (body) {
                                body.getSpeed().setX(body.getSpeed().getX() + bodyAX);
                                body.getSpeed().setY(body.getSpeed().getY() + bodyAY);

                                surrounding.getSpeed().setX(surrounding.getSpeed().getX() + surrAX);
                                surrounding.getSpeed().setY(surrounding.getSpeed().getY() + surrAY);
                            }
                        }
                    }
                }
            }

            synchronized (body) {
                body.getPosition().setX(body.getPosition().getX() + body.getSpeed().getX());
                body.getPosition().setY(body.getPosition().getY() + body.getSpeed().getY());

                //Débug
//            System.out.print(body);

            }

            // Freinage des corps
            // body.speed.x -= 0.005 * body.speed.x;
            // body.speed.y -= 0.005 * body.speed.y;

            bonusTime++;

            if (bonusTime >= nextBonusTime) {
                generateBonus();
                bonusTime = 0;
                nextBonusTime = randomGenerator.nextInt(MAX_TIME_BONUS_APPEARS/SERVER_SPEED) + MIN_TIME_BONUS_APPEARS/SERVER_SPEED;
            }

            moonTime++;

            if (moonTime >= nextMoonTime && allThings.size() < MAX_BODIES) {
                generateMoon();
                moonTime = 0;
                nextMoonTime = randomGenerator.nextInt(MIN_TIME_MOON_APPEARS/SERVER_SPEED) + MAX_TIME_MOON_APPEARS/SERVER_SPEED;
            }


            sendInfos();
        }
    }

    private boolean isProtected(Body body) {
        if (!(body instanceof Planet))
            return false;

        switch (((Planet) body).getActiveBonus()) {
            case Bonus.ATMOSPHER:
                return true;
            case Bonus.MOON:
                ((Planet) body).setActiveBonus(Bonus.NONE);
                return true;
        }
        return false;
    }

    public void explode(Body body) {
        Random rand = new Random();
        double dThis = body.getMass() / (body.getRadius() * body.getRadius() * PI);
        double oldMass = body.getMass();


        while (body.getMass() > 0) {
            double fragMass = oldMass * rand.nextDouble() / 2;
            double fragRadius = sqrt(fragMass / (dThis * PI));
            Body frag = addNewFragment(
                    body.getId(),
                    "FRAG" + body.getName(),
                    body.getPosition().getX() + rand.nextDouble() * body.getRadius() * 10 - 5,
                    body.getPosition().getY() + rand.nextDouble() * body.getRadius() * 10 - 5,
                    fragMass,
                    fragRadius,
                    Color.RED
            );

            double newDirection = rand.nextDouble() * 2 * PI;
            double newVX = cos(newDirection) * body.getSpeed().speedVector();
            double newVY = sin(newDirection) * body.getSpeed().speedVector();

            frag.setSpeed(new Speed(newVX, newVY));

            if (body.getMass() - fragMass < 0) {
                body.setMass(0);
            } else {
                body.setMass(body.getMass() - fragMass);
            }
        }
        allThings.remove(body);
//        hollySong("boom",0.001);
    }

    private static class ColorSerializer extends JsonSerializer<Color> {
        @Override
        public void serialize(Color value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeFieldName("argb");
            gen.writeString(Integer.toHexString(value.getRGB()));
            gen.writeEndObject();
        }
    }

    private void sendInfos() {
        if (multicastServer != null) {
            ObjectMapper mapper = new ObjectMapper();

            SimpleModule module = new SimpleModule();
            module.addSerializer(Color.class, new ColorSerializer());

            mapper.registerModule(module);

            String infosJson = "";
            try {
                synchronized (allThings) {
                    infosJson = mapper.writeValueAsString(allThings);
                }
                String command = Protocol.GAME_UPDATE + "\n" +
                        engineId + "\n" +
                        infosJson + "\n" +
                        Protocol.END_OF_COMMAND;
                multicastServer.send(command);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public Planet addNewPlanet(String name, double x, double y, double mass, double radius, int skin, int id) {
        return addNewPlanet(new Planet(name, new Position(x, y), mass, radius, skin, id));
    }

    public Planet addNewPlanet(Planet newP) {
        return (Planet) addNewBody(newP);
    }

    public InvisiblePlanet addNewInvisiblePlanet(InvisiblePlanet newP) {
        return (InvisiblePlanet) addNewBody(newP);
    }

    public Body addNewBody(Body body) {
        allThings.add(body);
        return body;
    }

    public void removeBody(Body body) {
        allThings.remove(body);
    }

    public void removeAUserByName(String name) {
        Body bodyToRemove = allThings.stream().filter(b -> b.getName().equals(name)).collect(Collectors.toList()).get(0);
        allThings.remove(bodyToRemove);
    }

    private Fragment addNewFragment(int id, String name, double x, double y, double mass, double radius, Color couleur) {
        Fragment newP = new Fragment(name, new Position(x, y), mass, radius, couleur);
        newP.setId(id);
        allThings.add(newP);
        return newP;
    }

    public Planet generateUserPlanet(Planet planet) {
        Random rand = new Random();
        Planet newPlanet = this.addNewPlanet(
                planet.getName(),
                rand.nextDouble() * 400000 + -200000,
                rand.nextDouble() * 400000 + -200000,
                rand.nextDouble() * 1E+22 + 1E+21,
                rand.nextDouble() * 1000 + 4000,
                planet.getIdSkin(),
                userPlanets.size());
        newPlanet.setSpeed(new Speed(rand.nextDouble() * 100 - 50,
                rand.nextDouble() * 100 - 50));
        userPlanets.add(newPlanet);
        return newPlanet;
    }

    public void removeUserPlanet(int idPlanet) {
        allThings.remove(userPlanets.get(idPlanet));
        userPlanets.remove(idPlanet);
    }

    private void generateBonus() {
        Random rand = new Random();
        Bonus bonus = new Bonus(
                "CENA" + rand.nextDouble() * 10000,
                new Position(rand.nextDouble() * 400000 + -200000,
                        rand.nextDouble() * 400000 + -200000),
                1,
                6666,
                Color.WHITE,
                1,
                System.currentTimeMillis()
        );
        allThings.add(bonus);

        sendPlayMusic();
    }

    private void generateMoon() {
            Planet moon = new Planet(
                "MOON" + randomGenerator.nextDouble() * 10000,
                    new Position(
                        randomGenerator.nextDouble() * 400000 + -200000,
                        randomGenerator.nextDouble() * 400000 + -200000),
                    randomGenerator.nextDouble() * 1E+21 + 1E+20,
                    randomGenerator.nextDouble() * 4000 + 500,
                7,
                    allThings.size()
            );
        allThings.add(moon);
    }

    private void sendPlayMusic() {
        if (multicastServer != null) {
            multicastServer.send(Protocol.PLAY_MUSIC + '\n' + Protocol.END_OF_COMMAND);
        }
    }

    public synchronized ArrayList<Body> getAllThings() {
        return allThings;
    }

    public synchronized Body getBodyById(Body body) {
        for (Body listBody : allThings) {
            if (listBody.getId() == body.getId()) {
                return listBody;
            }
        }
        return body;
    }

    public synchronized Body getBodyByName(Body body) {
        for (Body listBody : allThings) {
            if (listBody.getName().equals(body.getName())) {
                return listBody;
            }
        }
        return body;
    }

    public synchronized boolean killBody(Body body) {
        try {
            for (int i = 0; i < allThings.size(); ++i) {
                if (allThings.get(i).getName().equals(body.getName())) {
                    allThings.remove(i);
                    return true;
                }

            }
            return false;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return false;
        }
    }
}