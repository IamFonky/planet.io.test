package ch.elmootan.ui;

import ch.elmootan.physics.Body;
import ch.elmootan.physics.Constants;

import java.util.TimerTask;

public class Refresher extends TimerTask {

    @Override
    public void run() {
        for (int i = 0; i < allThings.size(); ++i) {
            Body body = allThings.get(i);
            for (int j = i + 1; j < allThings.size(); ++j) {
                Body surrounding = allThings.get(j);

                //On calcule les distances x et y et la distance au carré
                double dX = surrounding.getPosition().getX() - body.getPosition().getX();
                double dY = surrounding.getPosition().getY() - body.getPosition().getY();
                double sqDistance = dX * dX + dY * dY;

                //On calcule la distance réelle (Ground to ground)
                double gTgDistance = Math.sqrt(sqDistance) - body.getRadius() / 2 - surrounding.getRadius() / 2;

                if (gTgDistance < 0) //Collision!
                {
                    if (body.getMass() > surrounding.getMass()) {
                        body.eat(surrounding);
                    } else {
                        surrounding.eat(body);
                    }
                } else //Gravitation et physique
                {
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
                    body.speed.x += bodyAX;
                    body.speed.y += bodyAY;
                    surrounding.speed.x += surrAX;
                    surrounding.speed.y += surrAY;

                }
            }
            body.position.x += body.speed.x;
            body.position.y += body.speed.y;

            //Freinage des corps
//            body.speed.x -= 0.005 * body.speed.x;
//            body.speed.y -= 0.005 * body.speed.y;
        }
        res();
    }

}
