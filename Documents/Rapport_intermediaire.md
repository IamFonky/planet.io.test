# Rapport intermédiaire

## Protocole d'échange entre le client et le serveur

Chaque donnée envoyée ou reçue sera sous la forme JSON.

Commande | Description
:-----------------: | ----------------------------------------
`PLANET_IO_SUCCESS:<data_nullable>` | Demande acceptée avec possibilité de retourner des données
`PLANET_IO_FAILURE:<error>` | Demande refusée avec description de l'erreur
`PLANET_IO_HELLO` | Demande de connexion au serveur
`PLANET_IO_LOGIN:<user_id>:<password_hash>` | Demande de connexion à un compte
`PLANET_IO_REGISTER:<user_id>:<password_hash>` | Demande de création de compte
`PLANET_IO_GET_SCORES` | Demande de récupération des scores
`PLANET_IO_GET_UNIVERSE` | Demande des données relatives à l'univers
`PLANET_IO_SEND_POSITION:<position_data>` | Envoi de la position de la planète au serveur
