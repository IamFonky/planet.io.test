<!---
  Quelques commandes utiles:

  1. Pour compiler le document, il faut que vous ayiez installé Pandoc et XeLaTeX (ou XeTeX).
      La commande pour compiler le document est la suivate:

      pandoc --latex-engine=xelatex --listings Rapport_final.md -o Rapport_final.pdf
-->

---
lang: fr

numbersections: true

papersize: a4

geometry: margin=2cm

header-includes:
    - \usepackage{etoolbox}
    - \usepackage{fancyhdr}
    - \usepackage[T1]{fontenc}
    - \usepackage{xcolor}
    - \usepackage{graphicx}
    - \usepackage{tikz}
    - \usepackage{hyperref}
    - \usepackage{caption}

    # Some beautiful colors.
    - \definecolor{pblue}{rgb}{0.13, 0.13, 1.0}
    - \definecolor{pgray}{rgb}{0.46, 0.45, 0.48}
    - \definecolor{pgreen}{rgb}{0.0, 0.5, 0.0}
    - \definecolor{pred}{rgb}{0.9, 0.0, 0.0}

    - \renewcommand{\ttdefault}{pcr}

    # 'fancyhdr' settings.
    - \pagestyle{fancy}
    - \fancyhead[CO,CE]{}
    - \fancyhead[LO,LE]{Commusica}
    - \fancyhead[RO,RE]{HEIG-VD - PRO 2017}

    # Redefine TOC style.
    - \setcounter{tocdepth}{2}

    # 'listings' settings.
    - \lstset{breaklines = true}
    - \lstset{backgroundcolor = \color{black!10}}
    - \lstset{basicstyle = \ttfamily}
    - \lstset{breakatwhitespace = true}
    - \lstset{columns = fixed}
    - \lstset{commentstyle = \color{pgreen}}
    - \lstset{extendedchars = true}
    - \lstset{frame = trbl}
    - \lstset{frameround = none}
    - \lstset{framesep = 2pt}
    - \lstset{keywordstyle = \bfseries}
    - \lstset{keywordsprefix = {@}}                           # Java annotations.
    - \lstset{language = Java}
    - \lstset{numbers=left,xleftmargin=2em,xrightmargin=0.25em}
    - \lstset{numberstyle = \small\ttfamily}
    - \lstset{showstringspaces = false}
    - \lstset{stringstyle = \color{pred}}
    - \lstset{tabsize = 2}

    # 'listings' not page breaking.
    - \BeforeBeginEnvironment{lstlisting}{\begin{minipage}{\textwidth}}
    - \AfterEndEnvironment{lstlisting}{\end{minipage}}

    # Set links colors
    - \hypersetup{colorlinks,citecolor=black,filecolor=black,linkcolor=black,urlcolor=black}
---
\makeatletter
\renewcommand{\@maketitle}{%
\newpage
\null
\vfil
\begingroup
\let\footnote\thanks
\begin{center}
{\Huge\@title}\vskip1.5em
{\LARGE Rapport final}\vskip1.5em
{

    \begin{tabular}{ll}
        \underline{Membres} & Pierre-Benjamin Monaco \\
        & Lucas Elisei \\
        & Gaëtan Othenin-Girard \\
        & David Truan \\
        \\
        \underline{Professeur} & Eric Lefrançois \\
        \underline{Assistant} & Christophe Greppin
    \end{tabular}}\vskip1.5em
{\large\@date}
\end{center}
\endgroup
\vfil
}
\makeatother

\title{Planet.io}

\date{HEIG-VD - Semestre d'été 2017}

\maketitle

\begin{tikzpicture}[remember picture,overlay]
   \node[anchor=north east,inner sep=0.25cm] at (current page.north east)
              {\includegraphics[width=5cm]{images/heig-vd.png}};
\end{tikzpicture}

\newpage

\newpage

\tableofcontents

\listoffigures

\newpage

# Introduction

Ce projet a été développé dans le cadre du cours de Génie Logiciel lors du 4ème semestre à la HEIG-VD. Il s'agit d'un jeu sous la forme d'une application client-serveur.  
Le but principal du projet était de nous familiariser avec la gestion et le suivi de projet en appliquant la méthode UP. Il s'agissait aussi d'entrainer le travail en groupe et de faire face aux problèmes qui peuvent en découler.

# Analyse

Ce chapitre se concentre sur la présentation de notre application, les responsabilités client/serveur et la phase d'analyse du projet.

## Règles du jeu

Cette section va présenter notre jeu, ses règles et ses fonctionnalités.

### Description

L'application se présente sous la forme de plusieurs clients et d'un serveur. Chaque client se connecte au serveur, renseigne un pseudonyme et arrive dans le lobby du jeu.

Dans le lobby, le client a une vision sur toutes les parties actuellement créées et peut en rajouter dans la limite offerte par le serveur.
Il peut joindre une partie en cours. Lorsque il joint une partie, il se retrouve dans un univers en 2D représentant l'espace et a une visibilité sur tous les corps présents dans celui-ci.
Le client contrôle donc une planète. Il peut déplacer celle-ci grâce à sa souris. Chaque planète génère une force gravitationnelle qui attire les corps célestes environnants vers elle.

Lorsqu'il y a collision entre deux corps, il y a plusieurs cas de figure :

- Le corps le plus imposant mange l'autre et grossit (ce qui amplifie sa masse et donc son attraction).
- Les deux corps ont une masse trop proche, le plus gros mange le plus petit et finalement explose en fragments.

En plus des planètes, des bonus se baladent dans l'univers. Ceux-ci octroient des pouvoirs sur une courte durée. La liste des bonus est disponible plus bas.

### Contrôles

Le but de notre jeu étant de proposer une mini-simulation de gravité, en plus d'être amusant, nous somme partis du fait que même les contrôles devaient suivre le thème de l'espace/gravité. Pour déplacer sa planète, il faut donc cliquer sur la fenêtre, ce qui va générer une force de gravité proportionnelle à la taille de notre planète et qui va donc intéragir avec cette dernière.

### Victoire/défaite

Il n'y a pas de victoire à proprement parler, ni de fin de partie. Les joueurs ont un score qui correspond à la masse de leur planète et leur partie se termine lorsqu'ils se font manger par un autre joueur.

### Fonctionnalités disponibles

- Déplacement de la planète grâce à la souris: lorsque le joueur fait un geste de frottement à l'aide de sa souris, cela génère une forte gravité par rapport à la planète du joueur.
- Score se basant sur la masse de la planète du joueur. Mise à jour en temps réel du score des 5 meilleurs joueurs, visible par chaque joueur.
- **Sauvegarde des scores des joueurs dans une base de données**.
- Bonus offrant des pouvoirs:
    - Lune amassant des fragments pour nous.
    - Atmosphère protectrice.
- Zoom/De-zoom avec les touches **A** et **D** .
- **Déplacement de la caméra avec les flèches directionnelles.**
- Administration. Au niveau du serveur, un administrateur doit pouvoir effectuer certaines tâches :
    - **Vider la base de données (reset des scores par utilisateur ou pour tout le monde)**
    - Gérer le nombre de parties max du lobby.
    - Être spectateur d'une partie en ayant la visibilité sur tous les joueurs.
- Choix d'une texture visuelle de la planète contrôlée.
- Gestion concurrente du maximum de joueurs possibles (si possible, pas de limite).
- Sons.

## Partage des responsabilités client-serveur

Une des contraintes de ce projet étant de faire une application de type client-serveur, il nous a fallu réfléchir aux différentes responsabilités que l'une ou l'autre des parties devaient avoir.

Nous sommes partis du principe que les calculs devaient se faire du côté serveur et que les clients n'auraient qu'à mettre à jour leur affichage avec les données envoyées par le serveur.

### Diagramme d'activité

### Cas d'utilisation

### Modèles de domaine

\begin{minipage}{\linewidth}
    \centering
    \includegraphics[width=\linewidth]{Schemas/domaine/domaine_serveur_final.png}
    \captionof{figure}{Modèle de domaine serveur}
\end{minipage}

\begin{minipage}{\linewidth}
    \centering
    \includegraphics[width=\linewidth]{Schemas/domaine/domaine_client.png}
    \captionof{figure}{Modèle de domaine client}
\end{minipage}

### Base de données

\begin{minipage}{\linewidth}
    \centering
    \includegraphics{images/db.png}
    \captionof{figure}{Schéma de la base de données}
\end{minipage}

# Conception du projet

## Protocole d'échange

Le tableau ci-dessous liste les différentes commandes du protocole et leur rôle:

| Commande | Description |
| --- | --- |
| `PLANET_IO_SUCCESS` | Retourné par le serveur en cas de réussite. |
| `PLANET_IO_FAILURE` | Retourné par le serveur en cas d'erreur. |
| `LOBBY_UPDATED` | Envoyé par le serveur lorsque le lobby a été mis à jour. |
| `GAME_UPDATED` | Envoyé par le serveur lorsque la partie a été mise à jour. |
| `PLANET_IO_LOBBY_JOINED` | Le client au accède au lobby. |
| `PLANET_IO_HELLO` | Découverte du serveur. |
| `PLANET_IO_LOGIN`| Le client se connecte au lobby. |
| `NB_GAME_MAX_UPDATE` | Le client demande le nombre max de parties. |
| `PLANET_IO_CREATE_PLANET` | Le client crée sa planète. |
| `PLANET_IO_SET_PLANET` | |
| `PLANET_IO_KILL_PLANET` | |
| `CMD_CREATE_GAME` | Le client crée une partie. |
| `CMD_JOIN_GAME` | Le client rejoint une partie. |
| `CMD_DISCONNECT` | Le client se déconnecte. |
| `CMD_SEPARATOR` | Séparateur <argument>:<commande> |
| `PLANET_IO_LEAVING_GAME` | Le client quitte la partie. |
| `END_OF_COMMAND` | Signal de fin de commande. |

Les échanges multicast se font à l'adresse `239.192.0.2`, sur le port `9898`. Les échanges unicast se font sur le port `8585`.

## Diagrammes de classes

# Implémentation du projet

## Technologies utilisées

Planet.io a été développé en Java 8 pour le client comme pour le serveur.

Maven a été utilisé pour la gestion de modules, l'exécution des tests unitaires et la compilation.

Notre base de données s'appuie sur SQLite3.

Pour la sérialisation/désarialisation des données, nous avons utilisé Jackson.

Un dépôt Git a été ouvert sur Bitbucket afin que tout le monde puisse travailler de son côté. L'outil Travis a été ajouté au dépôt afin que chaque push ne casse pas la compilation du projet.

# Gestion du projet

## Rôles

- Pierre-Benjamin Monaco
    Chef de projet, responsable des normes et procédures, responsable des tests, développeur.

- Lucas Elisei
    Architecte, concepteur en chef, développeur.

- David Truan
    Représentant des utilisateur, développeur.

- Gaëtan Othenin-Girard
    Responsable de la configuration, développeur.

## Plan d'itération initial

### Itération 1

#### Objectifs

Modélisation des schémas UML du client et serveur et prototype :

 - Schéma UML du client.
 - Schéma UML du serveur.
 - Schéma UML des modules externes.
 - Création d'un prototype simple de client.

#### Temps escompté

 - 2 semaines (12.04 au 27.04).

#### Rôles et effort escompté

 - P-B. Monaco : Mise en place de JUnit et implémentation des tests du prototype, 2h.
 - Lucas Elisei : Développement du schéma UML du serveur, 2h.
 - David Truan : Développement du schéma UML du client, 2h.
 - Gaëtan Othenin-Girard : Mise en place du projet Maven, du système de contrôle de version et mise en place du système d'intégration continue, 2h.
 - Tout le monde : Développement du prototype (travail collaboratif : bonne technique pour être opérationnel avec git le moment venu).

### Itération 2

#### Objectifs

Système de jeu fonctionnel en standalone :

 - Création partie.
 - Contrôle d'une planète.
 - Gestion des évènements (collisions, changements d'états).
 - Design graphique (simple).

#### Temps escompté

 - 1 semaine (27.04 au 04.05).

#### Rôles et effort escompté

 - P-B. Monaco : Tests unitaires et revue de code, 4h.
 - Lucas Elisei : Gestion des propriétés physiques, 6h.
 - David Truan : Design graphique, validation du design auprès d'utilisateurs alpha, 5h.
 - Gaëtan Othenin-Girard : Gestion évènementielle, 6h.

### Itération 3

#### Objectifs

L'objectif de cette itération est l'amélioration de l'application créée lors de la première itération mais en fonctionnant de manière client/serveur. Sur cette version, le serveur contiendra les informations concernant les objets du jeu qu'il enverra au client. Le client va uniquement afficher les objets aux coordonnées reçues.

#### Temps escompté

- 1 semaine (04.05 au 11.05).

#### Rôles et efforts escomptés

- P-B. Monaco : Tests unitaires et revue de code, 2h.
- Lucas Elisei : Conception et implémentation de l'API, 3h.
- David Truan : Interface client générale (différentes zones du GUI), 3h.
- Gaëtan Othenin-Girard : Création et gestion de la connexion au serveur, 3h.

### Itération 4

#### Objectifs

Implémentation du concept de joueurs avec la création de comptes stockés dans la base de données et connexion au serveur via l'interface du client.

#### Temps escompté

- 1 semaine (11.05 au 18.05).

#### Rôles et efforts escomptés

- P-B. Monaco : Création de la base de données et ajout de la partie compte utilisateur, 3h.
- Lucas Elisei : Connexion à la base de données et affichage du pseudo sur la planète, 3h.
- David Truan : Analyse et recherche des besoins au niveau sécuritaire et tests unitaires, 2h.
- Responsable de configuration (Gaëtan Othenin-Girard) : Interface de connexion au compte utilisateur sur le client, 4h.

### Itération 5

#### Objectifs

Management du serveur :

 - Implémentation du système de score.
 - Étude des échanges entre client et serveur (adaptation de la taille et du type de données partagées).
 - Étude des flux entre les threads du serveur et optimisation.
 - Tests unitiares de l'intéraction client/serveur.

#### Temps escompté

 - 1 semaine (18.05 au 25.05).

#### Rôles et effort escompté

 - P-B. Monaco : Implémentation des scores dans l'application, 3h.
 - Lucas Elisei : Tests unitaires, test de fonctionnement et revue de code, 4h.
 - David Truan : Étude et optimisation des flux entre client et serveur, validation auprès des utilisateurs alpha, 4h.
 - Gaëtan Othenin-Girard : Implémentation du système de score dans la base de données, 3h.


### Itération 6

#### Objectifs

Management du serveur :

 - Implémentation des fonctions de contrôle des joueurs (ban kick, allow, etc...).
 - Essais d'attaques depuis le client, vérification qu'il n'est pas possible de contrer un bannisement.
 - Études des différentes techniques de triches qui pourraient être utilisées (glitches et exploits).

#### Temps escompté

 - 1 semaine (25.05 au 01.06).

#### Rôles et effort escompté

 - P-B. Monaco : Implémentation des fonctions d'administration, 3h.
 - Lucas Elisei : Étude des problèmes/risques de sécurité. Écriture d'un rapport de sécurité, 3h.
 - David Truan : Étude des problèmes/risques de sécurité. Écriture d'un rapport de sécurité, 3h.
 - Gaëtan Othenin-Girard : Test unitaires, vérification et validation du fonctionnement et revue de code, 2h.

### Itération 7

#### Objectifs

Implémentation des modifications administrateurs en pleine partie avec l'ajout de compte "modérateur". Par exemple :

- Ajout de bonus à un endroit.
- Explosion d'une planète sélectionnée.
- Application instantanée d'un bonus sur un joueur.
- Déplacement d'un joueur en drag and drop.
- Mettre la partie en pause.

#### Temps escompté

- 1 semaine (01.06 au 08.06).

#### Rôles et efforts escomptés

- P-B. Monaco : Apparition d'objets aux endroits sélectionnés et tests unitaires, 3h.
- Lucas Elisei : Implémentation du compte spécial "modérateur" sur la base de données et en jeu, 3h.
- David Truan : Intéraction directe avec la planète du joueur (déplacement et explosion), 3h.
- Gaëtan Othenin-Girard : Interface de sélection de la liste des joueurs et ajout de bonus sur le joueur sélectionné, 3h.

### Itération 8

#### Objectifs

- Finalisation du projet, récupération du retard.

#### Temps escompté

- 1 semaine (08.06 au 15.06).

#### Rôles et efforts escomptés

- P-B. Monaco : Finalisation, revue de code en profondeur, 2h.
- Lucas Elisei : Finalisation, revue de code en profondeur, 2h.
- David Truan : Finalisation, revue de code en profondeur, 2h.
- Gaëtan Othenin-Girard : Finalisation, revue de code en profondeur, 2h.

## Suivi du projet

### Itération 1

#### Bilan

> Tout bon! Revoir simplement les cas d'utilisation et compléter les itérations 3 à 8.

#### Problèmes rencontrés

Aucun.

#### Replanification

Non.

### Itération 2

#### Bilan

> Revoir la description avec la notion de gestion, infra et UC (fait partiellement ou complétement). Faire de même après en dessous avec puces. Sinon pour tout le reste, c'est ok.
> Gestion de la planète à compléter. Manque lorsque la souris bouge. Mettre une description plus complète pour expliquer ce que "gérer" veut dire.

#### Problèmes rencontrés

Aucun.

#### Replanification

Non.

### Itération 3

#### Bilan

> Faire une séparation entre le scénario principale et échec. Mettre le/les scénarios au niveau de checkbox. Reprendre dans la description les points qui sont traités et dire dans le cas d'un point "orange", qu'est ce qui est visible dans l'ensemble.

#### Problèmes rencontrés

Aucun.

#### Replanification

Non.

### Itération 4

#### Bilan

> Revoir la présentation du partage de travail, compléter les itérations jusqu'à la fin. Faire la distinction entre Gestion, fonctionnalité et infrastructure. A faire d'ici lundi 22 soir.s
> Créer une BDD pour répondre au cahier des charges.

#### Problèmes rencontrés

Aucun.

#### Replanification

Non.

### Itération 5

#### Bilan

> Planification à faire pour rattraper le retard.

#### Problèmes rencontrés

Aucun.

#### Replanification

Le temps par les autres matières étant trop conséquent, nous avons préféré mettre cette itération de côté afin de pouvoir tenir la cadence. Le retard sera rattrapé les semaines suivantes.

### Itération 6

#### Bilan

> UC Actions sur une partie à re-planifier. Pour le reste tout est ok. A planifier aussi les points qui sont faits partiellement. Rattrapage du retard pris sur l'itération précédente.

#### Problèmes rencontrés

Aucun.

#### Replanification

Non.

### Itération 7

#### Bilan

- La création de bonus est presque terminée.
- Reste quelques bugs au niveau de la communication client/serveur pour les calculs mais la séparation est bien mieux définie maintenant.
- Les fonctionnalités admin ont été revues : plus de possibilité de ban. Un admin ne peut pour l'instant que modifier le nombre max de parties dans un lobby et accéder aux parties en tant que spectateur en voyant tous les joueurs plutôt que le top 5.
- La base de donnée est presque implémentée.

#### Problèmes rencontrés

Problème de communication multicast.

#### Replanification

Non.

### Itération 8

#### Bilan

- Correction de bugs majeurs.
- Rédaction du rapport.

#### Problèmes rencontrés

Le fait que nous ayons été mis au courant au dernier moment qu'un rapport était à rédiger, une charge de travail conséquente est venue s'ajouter à celle déjà existante mais la replanification est cette fois-ci impossible.

#### Replanification

Malheureusement non.

## Stratégie de tests

Les tests ont été effectués par toute l'équipe, dans la mesure du possible. Ces derniers ont été réalisés dans leur intégralité grâce à la libraire JUnit. Quant aux résultats des tests, tous ont été validés par Travis lors du push sur les différentes branches.

## Stratégie d'intégration

Comme expliqué précédemment, Git a été utilisé pour intégrer le travail de chacun des membres du groupe. Chaque membre avait sa propre branche. Le merge d'une branche sur *master* était fait à chaque fois qu'une nouvelle fonctionnalité était implémentée et fonctionnelle. Travis nous permettait de savoir si le merge allait casser la compilation de la branche principale, au quel cas nous résolvions les conflits avant de merge.

# État des lieux
