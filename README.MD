# planet.io

[insert general description here]

## Description

L'application se présente sous la forme de plusieurs clients et d'un serveur. Chaque client se connecte au serveur, renseigne un pseudonyme et commence à jouer.

Un client contrôle une planète. Celle-ci peut se déplacer dans l'univers. Chaque planète génère une force gravitationnelle qui attire les corps célestes environnants vers elle.
Lorsqu'il y a collision entre deux corps, il y a plusieurs cas de figure :  
    - le corps le plus imposant *mange* l'autre et grossit (ce qui amplifie son attraction).
    - les deux corps on une masse trop proche, le plus gros mange le plus petit et finalement explose en fragments.
    - Le corps à tellement mangé sur une courte période qu'il se transforme en soleil / trou noir / autre.

En plus des planètes, des bonus se baladent dans l'univers. Ceux-ci octroient des bonus ou des malus. La liste des possibilités est disponible plus bas.

## Fonctionnalités

1. Déplacement de la planète grâce à la souris: lorsque le joueur fait un geste de frottement à l'aide de sa souris, cela génère une forte gravité par rapport à la planète du joueur (*idée*: attire **tout** sauf les planètes des autres joueurs).

2. Score se basant sur la masse de la planète du joueur. Mise à jour en temps réel du score de chaque joueur, visible par chaque joueur.

3. Sauvegarde des scores des joueurs dans une base de données.

4. Bonus/malus: Des objets célestes qui octroient des bonus ou des malus comme:

    - Un anneau protecteur [bonus, temporaire]
    - Amplification de la masse de la planète [bonus, temporaire]
    - Zone de pluie de météorites [zone malus, temporaire]
