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

\title{%
  Planet.IO \vskip0.4em
  \large Slogan claqué à trouver}

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
Le but principal du projet était de nous familiariser avec la gestion et le suivi de projet, notions vues en cours. Il s'agissait aussi d'entrainer le travail en groupe et de faire face aux problèmes qui peuvent en découler.

# Analyse
Ce chapitre se concentre sur la présentation de notre jeu, les responsabilités client/serveur et la phase d'analyse du projet.
## Planet.IO
Cette section va présenter notre jeu, ses règles et ses fonctionnalités.

### Description
L'application se présente sous la forme de plusieurs clients et d'un serveur. Chaque client se connecte au serveur, renseigne un pseudonyme et arrive dans le lobby du jeu.
Dans le lobby, il a une vision sur toutes les parties actuellement créées et peut en rajouter dans la limite offerte par le serveur.
Il peut joindre une partie en cours. Lorsque il joint une partie, il se retrouve dans un univers en 2D représentant l'espace et a une visibilité sur tous les corps présents dans celui-ci.
Le client contrôle donc planète. Il peut déplacer celle-ci grâce à sa souris. Chaque planète génère une force gravitationnelle qui attire les corps célestes environnants vers elle. Lorsqu'il y a collision entre deux corps, il y a plusieurs cas de figure :
- Le corps le plus imposant mange l'autre et grossit (ce qui amplifie sa masse et donc son attraction).
- Les deux corps on une masse trop proche, le plus gros mange le plus petit et finalement explose en fragments.
En plus des planètes, des bonus se baladent dans l'univers. Ceux-ci octroient des pouvoirs sur une courte durée. La liste des bonus est disponible plus bas.

### Contrôles
Le but de notre jeu étant de proposer une mini-simulation de gravité, en plus d'être amusant, nous somme parti du fait que même les contrôles devaient suivre le thème de l'espace/gravité. Pour déplacer sa planète, il faut donc cliquer sur la fenêtre, ce qui va générer une force de gravité proportionnelle à la taille de notre planète et qui va donc interagir avec cette dernière.

### Règles du jeu
Il n'y a pas de victoire à proprement parler, ni de fin de partie. Les joueurs ont des scores qui correspondent à la masse de leur planète et leur partie se termine lorsque ils se font manger par un autre joueur.

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
    - Etre spectateur d'une partie en ayant la visibilité sur tous les joueurs.

- Choix d'une texture visuelle de la planète contrôlée.

- Gestion concurrente du maximum de joueurs possibles (si possible, pas de limite).

- Sons.

Fonctionnalités optionnelles

Composition atomique du corps (% de chaque éléments fondamentaux : Fe, C, O, He, H, N, U, etc..) --> permettrait de définir la densité.
Variation de la densité en fonction de la composition et de la force G appliquée à soi-même. Une planète de masse m sera logiquement toujours moins dense qu'une planète de masse m+1 car la masse va augmenter la force de gravité et donc la "compression" de la planète sur elle-même.
Vie sur les planètes (en fonction de la chaleur et du taux de radiations --> zones de vie autour des soleils).
Nouveau corps : Nuages de gaz, comètes, spécialisation des planètes (gazeuse, rocheuse, océanique, magmatique, plasmatique, etc... )
Management du serveur depuis telnet.

## Partage des responsabilités client-serveur
Une des contraintes de ce projet étant de faire une application de type client-serveur, il nous à fallu réfléchir aux différentes responsabilités que l'une et l'autre des parties devaient avoir.
Nous sommes partis du principe que les calculs devaient se faire du côté serveur et que les clients n'auraient qu'à mettre à jour leur affichage avec les données envoyées par le serveur.

![Modèle de domaine client]
