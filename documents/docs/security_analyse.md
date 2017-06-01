# Analyse de sécurité de l'application

Une personne malveillante intéressée par attaquer notre application pourrait modifier les valeurs **JSON** envoyées par
un client ou le serveur.

Nous avons identifié deux scénarios possibles.

## 1. Modification d'une partie
En analysant les paquets UDP transitant sur le réseau, le pirate pourrait trouver l'identifiant unique d'une partie et
l'exploiter en forgeant des paquets avec de nouvelles informations mais en utilisant l'identifiant trouvé précédemment.

## 2. Génération de clics
Un pirate pourrait forger des paquets simulant énormément de clics, à des points stratégiques de l'univers de la partie,
afin d'augmenter son score et gagner la partie sans le moindre effort.

## 3. Prendre possession des planètes des autres
Un pirate pourrait récupérer l'identifiant des planètes des joueurs, forger des paquets en envoyant des informations
erronées pour modifier l'issue de la partie.

# Corrections

Une solution pour contrer les problèmes listés ci-dessous serait d'implémenter le protocole **SSL** afin que chaque
donnée envoyée sur le réseau soit cryptée.