<h1> Whac a quokka application</h1>

<h2> Auteurs </h2>

* Berney Alec
* Forestier Quentin
* Herzig Melvyn

<h2> Context </h2>

Ce projet a été réalisé dans le cadre du cours "développement d'applications Android" à la HEIG-VD. L'application est inspirée du [jeu de la taupe](https://fr.wikipedia.org/wiki/Jeu_de_la_taupe). Par simple choix artistique du groupe, les taupes ont été remplacées par des quokkas.

![hole_with_quokka](https://user-images.githubusercontent.com/34660483/173238518-4aefad25-f159-4e97-9ffc-846a52c69875.png)

L'application fait s'affronter deux joueurs. L'un doit faire apparaître des quokkas, tandis que l'autre doit leur tapper dessus. 

<h2> But du jeu </h2>

Chaque joueur a pour objectif de marquer 10 points avant son opposant.

Pour marquer des points, le joueur qui fait apparaître les quokkas doit réussir à en montrer un plus de 400ms sans qu'il se fasse cogner par l'autre joueur. En d'autres termes, tant que le quokka n'est pas frappé, le joueur qui endosse ce rôle gagne 1 point par tranche de 400ms à la surface. 

Pour marquer des points, le joueur qui frappe les quokkas doit réussir à les tapper avant qu'ils ne rentrent se cacher dans les trou. En d'autres termes, il gagne 1 point par quokka qu'il arrive a toucher.

Lorsque un quokka est frappé, il rentre automatiquement dans son trou.

<h2> Point technique </h2>

Pour connecter les téléphones des joueurs, nous avons utilisé l'API [Nearby Connections API](https://developers.google.com/nearby/connections/overview). Les téléphones sont appairés le temps de la partie. Sur l'activité d'accueil, les joueurs choisissent un rôle entre faire apparaître les quokkas ou les frapper. Les joueurs qui veulent faire apparaître les quokkas effectuent le "bluetooth discovery" et les joueurs qui veulent les frapper font du "bluetooth advertising".

Le projet et divisé en trois activités:
* MainActivity: qui est l'activité d'accueil qui permet aux joueurs de choisir leur rôle.
* QuokkaGameActivity: qui est l'activité qui permet de faire apparaître les quokkas durant une partie après s'être connecté à un adversaire.
* WhackGameActivity: qui est l'activité qui permet de faire frapper les quokkas durant une partie après s'être connecté à un adversaire.

<h2> Organisation des fichiers </h2>

À la [racine](https://github.com/MelvynHerzig/AND_Project/tree/main/WhacAQuokkaApplication/app/src/main/java/com/and/whacaquokkaapplication) du projet, les fichiers sont organisés comme suit:

* Les dossiers:
  * bluetoothmanager: qui contient les classes nécessaires à la gestion du bluetooth.
  * gamelogic: qui contient les classes nécessaires à l'implémentation de la logique de jeu.
  * models: qui contient les classes qui représentent les messages envoyés entre les joueurs.

* Les fichiers:  
  * Permission.kt : qui permet aux activités de demander les permissions nécessaires pour s'exécuter à l'utilisateur.
  * Main/QuokkaGame/WhackGame activity.kt: qui représentent les trois activités de l'application qui mettent en place les listeners, les appels à l'implémentation du bluetooth et de la logique de jeu.  

<h2> Utilisation </h2>

Au lancement de l'application, les joueurs arrivent sur l'écran d'accueil:

![Sans titre](https://user-images.githubusercontent.com/34660483/173237699-60b43f94-69b0-4999-a822-311c7cb4859a.png)

Lors de la première utilisation les joueurs doivent accepter les permissions nécessaires à l'application. Dans le cas où ils refusent, il ne se passe rien et ils restent sur l'activité d'accueil. S'ils acceptent, le processus de connexion bluetooth démarre.

Lorsque deux opposants sont trouvés, ils se voient afficher un dialogue pour accepter la connexion à l'adversaire. Si l'un d'eux refuse, la recherche continue.

En jeu, le joueur qui fait apparaître les quokkas voit ceci:

![Sans titre2](https://user-images.githubusercontent.com/34660483/173238216-e3a6cec3-3add-4d92-a34c-3564e8ec6542.png)

En jeu, le joueur qui tappe les quokkas voit ceci:

![Sans titre3](https://user-images.githubusercontent.com/34660483/173238754-0813b323-df34-49d5-b121-3b811be6b26f.png)

Quitter une partie déconnecte les joueurs.

Lorsqu'un des joueurs atteint 10 points, la partie se termine et un dialogue annonce le résultat.

<h2> Requirements </h2>

Ce projet a été testé et développé en utilisant des téléphones sous SDK 31+
En conséquences, nous ne pouvons garantir le bon fonctionnement de l'application sous des versions plus anciennes.
