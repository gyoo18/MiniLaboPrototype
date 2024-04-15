## MiniLabo - prototype

Ceci est le dépôt du prototype du projet MiniLabo. Ce projet vise à développer un programme simple d'usage afin de simuler une variété de réactions chimiques simples. 

Nous nous basons sur un modèle de dynamique moléculaire dans lequel les atomes sont représentés par des sphères rigides qui évoluent dans un système newtonnien. Ils sont alors influencés par des forces interatomiques et peuvent créer et briser des liens entre eux pour former des molécules et des réactions chimiques.

Comme mentionné plus haut, ceci est un prototype, il ne sert qu'à bâtir le coeur de la simulation. Pour voir le véritable projet allez voir https://github.com/gyoo18/MiniLabo.

## Simulation

Nos atomes sont influencés par des forces et elles peuvent se décliner en deux catégories : les forces interatomiques et les forces de liaisons.

Dans les forces interatomiques, nous utilisons la force électrostatique, suivant la formule de Coulomb. Les deux autres suivent le potentiel de Lennard-Jones 6-12 dont le premier terme, attractif, représente les forces de Van der Walls et le deuxième la répulsion de Pauli.

Dans les forces de liaisons, nous commençons par utiliser le potentiel de Morse pour assurer le maintien d'un lien, puis pour assurer le maintien de la géométrie moléculaire, nous ajoutons des ressorts angulaires sur les liens qui maintiendrons la forme désirée.

Pour plus de détails, veuillez consulter le rapport intitulé Rapport_Recherche.pdf

## Programme

Le programme est écrit en Java. Il se divise en trois parties : App.java, Atome.java et Intégrateur.java. Les trois fichiers se trouvent dans le dossier src/com.MiniLabo.protoype/.

App.java contient le programme. Il initialise la simulation, fait jouer la boucle de mise à jour et dessine le résultat à l'écran.

Atome.java contient toutes les informations et les fonctions nécessaires à l'atome. Vous y retrouverez notamment la fonction ÉvaluerForces qui contient toutes les forces appliqués sur l'atome à un moment donné.

Intégrateur.java contient diverses méthodes d'intégration numérique. Elles sont toutes intercompatibles. Les plus notables sont la méthode RK4 et la méthode de Verlet. Il est reccomandé d'utiliser une variante de Verlet, car elle conserve l'énergie du système.

Veuillez noter que les dernières modifications sont disponibles dans la branche développement, mais qu'elle n'est pas garantie de fonctionner, tandis que la branche main est garantie de fonctionner, mais peut se retrouver en retard par rapport à l'avancement du projet.
