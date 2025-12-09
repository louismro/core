# PracticeCore - Serveur Practice Professionnel

<div align="center">

**Un plugin Minecraft Practice ultra-complet et moderne pour Spigot 1.21.8**  
Inspiré de Kohi, Minemen Club et les meilleurs serveurs Practice du marché

[![Java](https://img.shields.io/badge/Java-21_LTS-orange.svg)](https://openjdk.java.net/)
[![Spigot](https://img.shields.io/badge/Spigot-1.21.8-yellow.svg)](https://www.spigotmc.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-4.11.1-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-Private-red.svg)](LICENSE)

</div>

---

## 📋 Table des Matières

- [Aperçu](#-aperçu)
- [Fonctionnalités Principales](#-fonctionnalités-principales)
  - [⚔️ Combat & Matchmaking](#️-système-de-combat--matchmaking)
  - [👥 Systèmes Sociaux](#-systèmes-sociaux)
  - [🎯 Modes de Jeu](#-modes-de-jeu)
  - [✨ Cosmétiques & Personnalisation](#-cosmétiques--personnalisation)
  - [📊 Progression & Économie](#-progression--économie)
  - [🛡️ Modération & Administration](#️-modération--administration)
  - [🏠 Téléportation & Navigation](#-téléportation--navigation)
- [Installation](#-installation)
- [Configuration](#️-configuration)
- [Commandes](#-commandes)
- [Architecture](#️-architecture)
- [API & Développement](#-api--développement)
- [Changelog](#-changelog)
- [Support](#-support)

---

## 🌟 Aperçu

**PracticeCore** est un plugin Minecraft Practice de niveau professionnel, développé en Java 21 LTS, offrant une expérience PvP complète et immersive. Conçu pour être performant, extensible et facile à administrer, il intègre tous les systèmes attendus d'un serveur practice moderne.

### 🎯 Points Clés

- **⚡ Performance** : Optimisé pour gérer des centaines de joueurs simultanés
- **🔧 Extensible** : Architecture modulaire et API complète pour les développeurs
- **🎨 Moderne** : Interface utilisateur riche avec scoreboard dynamique, menus GUI et effets visuels
- **📦 Complet** : 40+ managers, 80+ commandes, 50+ modèles de données
- **💾 Persistant** : Sauvegarde automatique MongoDB avec système de cache intelligent
- **🌐 Multi-langues** : Support prévu pour plusieurs langues

---

## ✨ Fonctionnalités Principales

### ⚔️ Système de Combat & Matchmaking

#### 🥊 Combat Avancé
- **Knockback Personnalisé** : Profils Kohi/Minemen avec configuration fine
- **Système de Combos** : Affichage en temps réel avec titres et effets sonores
- **Pearl Cooldown** : 16 secondes configurables avec barre de progression
- **Combat Tag** : 15 secondes empêchant la déconnexion en combat
- **Hit Detection** : Compensation de ping avec rollback jusqu'à 5 ticks
- **W-Tap & Sprint** : Mécanique de sprint authentique Kohi/Minemen
- **Statistiques Live** : Hits, combos max, précision potions/flèches

#### 🏆 Système d'ELO & Ranking
- **ELO Dynamique** : Système Elo avec K-factor configurable (16-40)
- **7 Rangs Compétitifs** :
  - 🥉 Bronze (0-799 ELO)
  - 🥈 Silver (800-999 ELO)
  - 🥇 Gold (1000-1199 ELO)
  - 💎 Platinum (1200-1399 ELO)
  - 💠 Diamond (1400-1599 ELO)
  - 👑 Master (1600-1799 ELO)
  - 🏆 Champion (1800+ ELO)
- **Matchmaking Intelligent** : Recherche par ELO avec expansion progressive
- **Statistiques Complètes** : K/D, Winrate, Victoires/Défaites, Killstreaks par kit
- **Peak ELO** : Suivi du meilleur ELO atteint par saison

#### 🎯 Kits de Combat
- **NoDebuff** : PvP classique avec potions Speed II et Health
- **Debuff** : Variante avec potions de status négatifs
- **BuildUHC** : Kit UHC avec blocs et crafting
- **Combo** : Kit optimisé pour l'entraînement aux combos
- **Sumo** : Sans arme, basé uniquement sur le knockback
- **Archer** : Arc, flèches et armure spécialisée
- **Gapple** : Golden apples et armure diamant

#### 🔄 Files d'Attente (Queues)
- **Queues Classées** : Avec calcul d'ELO et matchmaking strict
- **Queues Non-Classées** : Pour l'entraînement sans pression
- **Auto-Matchmaking** : Recherche automatique d'adversaires
- **Expansion de Range** : Élargissement progressif après 10s, 30s, 60s
- **Statistiques en Temps Réel** : Temps d'attente, joueurs en queue, range actuel

### 👥 Systèmes Sociaux

#### 🎉 Système de Parties (Party)
- **Création & Gestion** : Parties jusqu'à 10 joueurs
- **Système d'Invitations** : Invitations avec acceptation/refus
- **Rôles** : Leader avec permissions de gestion (kick, disband)
- **Chat Privé** : Canal de discussion réservé à la partie
- **Matchs en Équipe** : Support des matchs 2v2, 3v3, 4v4, 5v5
- **Téléportation de Groupe** : TP tous les membres ensemble

#### 👫 Système d'Amis
- **Liste d'Amis** : Jusqu'à 100 amis par joueur
- **Demandes d'Amis** : Système de demande avec acceptation/refus
- **Statut en Ligne** : Voir qui est connecté en temps réel
- **Messages Privés** : Chat privé entre amis
- **Notifications** : Alerte à la connexion/déconnexion d'un ami

#### 🏰 Système de Clans
- **Création de Clans** : Nom, tag (6 caractères max), jusqu'à 20 membres
- **Hiérarchie** : Chef, Modérateurs, Membres
- **ELO de Clan** : Classement compétitif entre clans
- **Statistiques** : Victoires, défaites, winrate, membres actifs
- **Chat de Clan** : Canal de communication privé
- **Gestion Avancée** : Invite, kick, promote, settings personnalisés

### 🎯 Modes de Jeu

#### 🎪 Duels
- **Défis Personnalisés** : Défier n'importe quel joueur connecté
- **Choix du Kit** : Sélection libre du kit pour le duel
- **Système de Demande** : Acceptation/refus avec expiration (30s)
- **Rematch** : Possibilité de revanche après un match
- **Spectateurs** : Autoriser/bloquer les spectateurs

#### ⚡ Free-For-All (FFA)
- **Événements FFA** : Jusqu'à 100 joueurs simultanés
- **Kits Variés** : Support de tous les kits disponibles
- **Classement Live** : Top 3 killers en temps réel
- **Récompenses** : Coins basés sur la position finale
- **Spawn Aléatoires** : Points de réapparition multiples

#### 🏆 Tournois
- **Système de Brackets** : Élimination simple/double
- **Inscription** : Nombre de joueurs configurables (8, 16, 32, 64)
- **Rounds Automatiques** : Gestion automatique des matchs
- **Récompenses** : Prize pool distribué aux vainqueurs
- **Spectateur Global** : Tous les matchs spectables

#### 🌍 Événements Globaux
- **Multiplicateurs** : Bonus XP/Coins temporaires (x1.5, x2, x3)
- **Types d'Événements** :
  - 💰 **Double Coins** : Coins x2 pendant X heures
  - ⭐ **Double XP** : Expérience x2
  - 🎁 **Drop Event** : Crates gratuites
  - 🔥 **Happy Hour** : Tous les bonus activés
- **Annonces** : Broadcast à tous les joueurs avec effets sonores
- **Durée Configurable** : De 30 minutes à 24 heures

### ✨ Cosmétiques & Personnalisation

#### 💀 Kill Effects
- ⚡ **Lightning Strike** : Éclair au sol
- 🔥 **Flames** : Particules de feu
- 💥 **Explosion** : Effet d'explosion
- ❤️ **Hearts** : Cœurs qui montent
- 🩸 **Blood** : Particules rouges
- ⭐ **Stars** : Étoiles dorées
- 💧 **Water Splash** : Éclaboussures d'eau
- 🌪️ **Tornado** : Tourbillon de particules
- ✨ **Magic** : Particules enchantement
- 👻 **Ghost** : Effet fantôme

#### 🌟 Trails (Traînées)
- 🔥 **Flame Trail** : Flammes derrière le joueur
- 💨 **Smoke Trail** : Fumée
- 💧 **Water Trail** : Particules d'eau
- ❤️ **Heart Trail** : Cœurs
- ⚡ **Lightning Trail** : Électricité
- 🌈 **Rainbow Trail** : Arc-en-ciel
- ✨ **Enchant Trail** : Particules d'enchantement
- 💎 **Crystal Trail** : Cristaux

#### 🔊 Hit Sounds
- 🔔 **Ding** : Son de cloche
- 💥 **Pop** : Éclatement
- 🔨 **Crack** : Coup sec
- ⚔️ **Sword** : Lame qui tranche
- 🥁 **Drum** : Percussion
- 🎵 **Note** : Note musicale
- 💎 **Glass** : Verre qui casse
- 🌟 **Sparkle** : Étincelle

#### 👑 Titres & Rangs
- **Titres Débloquables** : 20+ titres avec couleurs et raretés
- **Système de Rareté** :
  - ⬜ Commun
  - 🟢 Inhabituel
  - 🔵 Rare
  - 🟣 Épique
  - 🟡 Légendaire
- **Affichage** : Préfixe dans le chat, tab et au-dessus du joueur
- **Exemples** : Légende, Guerrier, Maître, Champion, etc.

#### 🛒 Boutique (Shop)

- **Achats avec Coins** : Économie interne du serveur
- **Catégories** : Kill Effects, Trails, Hit Sounds, Titles, Boosts
- **Preview** : Aperçu avant achat
- **Débloquages Permanents** : Une fois acheté, toujours disponible
- **GUI Interactive** : Interface intuitive avec filtres

### 📊 Progression & Économie

#### 💰 Système de Coins

- **Gains après Match** : Coins basés sur victoire/défaite et performance
- **Calcul Intelligent** : Bonus pour killstreaks, combos, précision
- **Daily Rewards** : Récompenses quotidiennes avec série
- **Système de Série** : Jusqu'à 7 jours consécutifs avec bonus progressif
- **Transferts** : `/pay` pour donner des coins à d'autres joueurs
- **BalTop** : Classement des joueurs les plus riches
- **Boosts Temporaires** : Multiplicateurs x2, x3 activables

#### ⭐ Système d'Expérience

- **XP par Match** : Expérience basée sur les actions en jeu
- **Système de Niveaux** : Progression jusqu'au niveau 100+
- **Récompenses par Niveau** : Débloquages de cosmétiques et coins
- **Multiplicateurs** : Boosts VIP et événements

#### 🏆 Achievements (Succès)

- **40+ Succès** : Défis variés à compléter
- **Catégories** :
  - 🎯 **Combat** : Kills, Killstreaks, Combos
  - 🏆 **Victoires** : Wins, Win Streaks
  - 👥 **Social** : Amis, Clans
  - 💎 **ELO** : Atteindre certains ELO
  - 🌟 **Spécial** : Événements, Daily Streaks
- **Récompenses** : Coins et cosmétiques exclusifs
- **Tracking** : Progression affichée dans le profil

#### 📅 Daily Quests (Quêtes Journalières)

- **3 Quêtes par Jour** : Renouvellement automatique à minuit
- **Types de Quêtes** :
  - Gagner X matchs en [kit]
  - Tuer X joueurs
  - Atteindre un killstreak de X
  - Jouer X matchs
  - Faire un combo de X hits
- **Récompenses** : Coins basés sur la difficulté
- **GUI Interactive** : Suivi de progression en temps réel
- **Expiration** : 24 heures après création

#### 🎁 Système de Crates (Coffres)

- **5 Raretés** : Commun, Inhabituel, Rare, Épique, Légendaire
- **Récompenses** :
  - 💰 Coins (100 à 5000)
  - ✨ Cosmétiques exclusifs
  - 👑 Titres rares
  - 🚀 Boosts temporaires
  - 🌟 Items spéciaux
- **Animation** : Ouverture avec suspense et effets visuels
- **Obtention** : Daily rewards, achievements, événements, achats

#### 🚀 Système de Boosts

- **Types de Boosts** :
  - 💰 **Coin Boost** : Multiplie les gains de coins
  - ⭐ **XP Boost** : Multiplie les gains d'XP
  - 🏆 **Kill Boost** : Bonus sur les kills
  - 🎯 **Win Boost** : Bonus sur les victoires
- **Durées** : 1h, 3h, 12h, 24h, 7 jours
- **Multiplicateurs** : x1.5, x2, x2.5, x3
- **Cumul** : Les boosts se combinent avec les événements globaux

#### 📊 Système de Saisons

- **Durée** : 3 mois par saison
- **Réinitialisation** : ELO reset avec placement matches
- **Leaderboards** : Classements conservés historiquement
- **Récompenses de Fin** : Basées sur le classement final
- **Rangs Saisonniers** : Badges exclusifs par saison

### 🛡️ Modération & Administration

#### 🛠️ Commandes Admin - Gestion Serveur

- `/setspawn` - Définir le spawn principal
- `/arena create/edit/delete <nom>` - Gestion des arènes
- `/kit create/edit <nom>` - Création de kits custom
- `/event start/stop <type>` - Lancer des événements globaux
- `/sudo <joueur> <commande>` - Exécuter commande pour un joueur
- `/broadcast <message>` - Annonce globale stylisée

#### 🚨 Commandes Admin - Modération

- `/freeze <joueur>` - Geler un joueur en place
- `/vanish` - Mode invisible pour staff
- `/invsee <joueur>` - Voir l'inventaire d'un joueur
- `/staffmode` - Mode modération complet avec outils
- `/staffchat <message>` - Chat réservé au staff

#### ⚖️ Système de Punitions

- **Types de Punitions** :
  - 🚫 **Ban** : Bannissement permanent ou temporaire
  - ⚠️ **Kick** : Expulsion du serveur
  - 🔇 **Mute** : Mute permanent ou temporaire
  - ⚡ **Warning** : Avertissement (3 = ban temporaire)
- **Commandes** :
  - `/ban <joueur> <raison> [durée]`
  - `/kick <joueur> <raison>`
  - `/mute <joueur> <raison> [durée]`
  - `/warn <joueur> <raison>`
  - `/punishhistory <joueur>` - Historique des sanctions
- **Durées** : 5m, 1h, 3h, 1d, 7d, 30d, perm
- **Historique Complet** : Toutes les punitions enregistrées en MongoDB
- **Appels** : Système de conteste prévu

#### 📋 Système de Reports (Signalements)

- **Catégories** :
  - 🎮 **Cheat** : Utilisation de hacks
  - 💬 **Toxic** : Comportement toxique
  - 🐛 **Bug** : Exploitation de bugs
  - 🎯 **Teaming** : Collaboration interdite
  - 🔒 **Autre** : Autres infractions
- **Commandes** :
  - `/report create <joueur> <raison> [description]`
  - `/report list` - Vos reports
  - `/report handle <id>` - Prendre en charge (Staff)
  - `/report resolve <id> [note]` - Résoudre (Staff)
  - `/report reject <id> [raison]` - Rejeter (Staff)
- **File d'Attente** : Système de gestion pour staff
- **Notifications** : Alertes en temps réel pour le staff
- **Cooldown** : 30 secondes entre chaque report

#### 👮 Staff Mode

- **Outils Inclus** :
  - 🔍 **Inspect** : Examiner joueur (stats, inventaire)
  - 📊 **Random TP** : TP aléatoire pour surveillance
  - 👻 **Vanish** : Invisibilité totale
  - 🚫 **Freeze** : Geler joueur suspect
  - 📋 **Reports** : Accès rapide aux reports
  - ⚖️ **Punish** : Menu de punition rapide
- **Désactivation Auto** : Lors de la déconnexion
- **Inventaire Séparé** : Items conservés

### 🏠 Téléportation & Navigation

#### 🏡 Système de Homes

- **3 Homes par Défaut** : 10 pour VIP
- **Commandes** :
  - `/sethome [nom]` - Définir un home
  - `/home [nom]` - TP au home
  - `/delhome <nom>` - Supprimer un home
  - `/homes` - Liste de vos homes
- **Noms Personnalisés** : Donnez des noms à vos homes
- **Cooldown** : 5 secondes (désactivable en combat)

#### 🌐 Système de Warps

- **Warps Publics** : Accessibles à tous
- **Warps VIP** : Réservés aux joueurs VIP
- **Commandes** :
  - `/warp <nom>` - TP au warp
  - `/warp list` - Liste des warps
  - `/setwarp <nom>` - Créer warp (Admin)
  - `/delwarp <nom>` - Supprimer warp (Admin)
- **Permissions** : Système de permissions par warp

#### 🚀 Téléportation Joueur à Joueur

- **TPA System** :
  - `/tpa <joueur>` - Demande de TP vers un joueur
  - `/tpahere <joueur>` - Demande qu'un joueur vienne à vous
  - `/tpaccept` - Accepter une demande
  - `/tpdeny` - Refuser une demande
- **Expiration** : Demandes expirent après 30 secondes
- **Cooldown** : 3 minutes entre chaque TP
- **Bloquage Combat** : Impossible en combat tag

#### ⬅️ Back & Retour

- `/back` - Retourner à la position précédente
- **Sauvegarde Auto** : Position avant TP, mort, etc.
- **Limite** : Une seule position sauvegardée

---

---

## 📦 Installation

### Prérequis

- **Spigot/LightSpigot** 1.21.8-R0.1-SNAPSHOT
- **Java 21 LTS** (Migration complète depuis Java 17)
- **MongoDB** 4.11.1+ (Base de données)
- **Maven** 3.9.9+ (Pour compilation)

### Compilation

```bash
git clone https://github.com/louismro/core.git
cd core
mvn clean package
```

Le fichier JAR sera généré dans `target/PracticeCore-1.0.0.jar`

### Installation

1. **Arrêtez votre serveur**
2. **Placez le JAR** dans le dossier `plugins/`
3. **Installez MongoDB** et créez une base de données
4. **Démarrez le serveur** pour générer les fichiers de configuration
5. **Configurez** :
   - `config.yml` - Configuration principale
   - `arenas.yml` - Définition des arènes
   - `kits.yml` - Configuration des kits
6. **Redémarrez le serveur**

---

## ⚙️ Configuration

### config.yml - Configuration Principale

```yaml
# Paramètres généraux
general:
  server-name: "<gradient:#00d4ff:#0066ff><bold>Hyko Practice</bold></gradient>"
  max-party-size: 10
  spawn-location: # Coordonnées du spawn

# Système de knockback (Kohi/Minemen)
knockback:
  enabled: true
  profile: "kohi" # kohi, minemen, vanilla
  horizontal: 0.4
  vertical: 0.385

# Système d'ELO
elo:
  starting-elo: 1000
  k-factor: 32 # Vitesse de changement d'ELO
  
# Base de données MongoDB
database:
  enabled: true
  connection-string: "mongodb://localhost:27017"
  database-name: "practice"
```

### arenas.yml - Définition des Arènes

```yaml
arenas:
  arena1:
    display-name: "&eArena 1"
    pos1: # Coin 1 de la zone
      world: "world"
      x: 0
      y: 64
      z: 0
    pos2: # Coin 2 de la zone
      world: "world"
      x: 50
      y: 100
      z: 50
    spawns: # Points de spawn des joueurs
      - world: "world"
        x: 10
        y: 65
        z: 10
      - world: "world"
        x: 40
        y: 65
        z: 40
```

### kits.yml - Configuration des Kits

```yaml
kits:
  nodebuff:
    name: "§6NoDebuff"
    icon: DIAMOND_SWORD
    armor:
      helmet: DIAMOND_HELMET
      chestplate: DIAMOND_CHESTPLATE
      leggings: DIAMOND_LEGGINGS
      boots: DIAMOND_BOOTS
    items:
      - "DIAMOND_SWORD:0:1:16"
      - "ENDER_PEARL:0:16"
      - "POTION:8226:1:0-8" # Speed II
      - "POTION:16421:1:9-35" # Health II
```

---

## 🎮 Commandes

### Commandes Joueur - PvP

| Commande | Description | Permission |
|----------|-------------|------------|
| `/queue join <kit>` | Rejoindre une queue | `practice.queue` |
| `/queue leave` | Quitter la queue | `practice.queue` |
| `/duel <joueur> [kit]` | Défier en duel | `practice.duel` |
| `/duel accept` | Accepter un duel | `practice.duel` |
| `/spectate <joueur>` | Spectater un match | `practice.spectate` |
| `/stopspec` | Arrêter de spectater | `practice.spectate` |
| `/rematch accept` | Accepter un rematch | `practice.rematch` |
| `/inventory <joueur>` | Voir inventaire post-match | `practice.inventory` |

### Commandes Joueur - Social

| Commande | Description | Permission |
|----------|-------------|------------|
| `/party create` | Créer une partie | `practice.party` |
| `/party invite <joueur>` | Inviter à la partie | `practice.party` |
| `/party accept` | Accepter invitation | `practice.party` |
| `/party leave` | Quitter la partie | `practice.party` |
| `/party kick <joueur>` | Exclure un membre | `practice.party` |
| `/friend add <joueur>` | Ajouter un ami | `practice.friend` |
| `/friend remove <joueur>` | Retirer un ami | `practice.friend` |
| `/friend list` | Liste des amis | `practice.friend` |
| `/clan create <nom> <tag>` | Créer un clan | `practice.clan` |
| `/clan invite <joueur>` | Inviter au clan | `practice.clan` |
| `/clan info [nom]` | Info du clan | `practice.clan` |

### Commandes Joueur - Stats & Progression

| Commande | Description | Permission |
|----------|-------------|------------|
| `/stats [joueur]` | Voir statistiques | `practice.stats` |
| `/leaderboard [kit]` | Classements | `practice.leaderboard` |
| `/profile [joueur]` | Profil complet | `practice.profile` |
| `/achievements` | Vos succès | `practice.achievements` |
| `/quests` | Quêtes journalières | `practice.quests` |
| `/daily` | Récompense quotidienne | `practice.daily` |
| `/season` | Info saison actuelle | `practice.season` |
| `/ping [joueur]` | Voir le ping | `practice.ping` |

### Commandes Joueur - Cosmétiques

| Commande | Description | Permission |
|----------|-------------|------------|
| `/shop` | Ouvrir la boutique | `practice.shop` |
| `/killeffect <effet>` | Changer effet de kill | `practice.cosmetic` |
| `/trail <trail>` | Changer traînée | `practice.cosmetic` |
| `/hitsound <son>` | Changer son de hit | `practice.cosmetic` |
| `/title <titre>` | Changer titre | `practice.cosmetic` |
| `/crate open <rareté>` | Ouvrir une crate | `practice.crate` |

### Commandes Joueur - Navigation

| Commande | Description | Permission |
|----------|-------------|------------|
| `/spawn` | Retour au spawn | `practice.spawn` |
| `/home [nom]` | TP au home | `practice.home` |
| `/sethome [nom]` | Définir un home | `practice.home` |
| `/delhome <nom>` | Supprimer un home | `practice.home` |
| `/homes` | Liste des homes | `practice.home` |
| `/warp <nom>` | TP au warp | `practice.warp` |
| `/tpa <joueur>` | Demande de TP | `practice.tpa` |
| `/tpaccept` | Accepter TP | `practice.tpa` |
| `/back` | Retour position | `practice.back` |

### Commandes Admin - Gestion

| Commande | Description | Permission |
|----------|-------------|------------|
| `/setspawn` | Définir le spawn | `practice.admin.setspawn` |
| `/arena create <nom>` | Créer une arène | `practice.admin.arena` |
| `/arena setpos1/2 <nom>` | Définir zone | `practice.admin.arena` |
| `/arena addspawn <nom>` | Ajouter spawn | `practice.admin.arena` |
| `/kit create <nom>` | Créer un kit | `practice.admin.kit` |
| `/event start <type>` | Lancer événement | `practice.admin.event` |
| `/broadcast <msg>` | Annonce globale | `practice.admin.broadcast` |

### Commandes Admin - Modération

| Commande | Description | Permission |
|----------|-------------|------------|
| `/freeze <joueur>` | Geler un joueur | `practice.staff.freeze` |
| `/vanish` | Mode invisible | `practice.staff.vanish` |
| `/invsee <joueur>` | Voir inventaire | `practice.staff.invsee` |
| `/staffmode` | Mode staff | `practice.staff.mode` |
| `/staffchat <msg>` | Chat staff | `practice.staff.chat` |
| `/tp <joueur>` | TP admin | `practice.staff.tp` |
| `/tphere <joueur>` | TP joueur à soi | `practice.staff.tp` |

### Commandes Admin - Punitions

| Commande | Description | Permission |
|----------|-------------|------------|
| `/ban <joueur> <raison> [durée]` | Bannir | `practice.staff.punish` |
| `/kick <joueur> <raison>` | Kick | `practice.staff.punish` |
| `/mute <joueur> <raison> [durée]` | Mute | `practice.staff.punish` |
| `/warn <joueur> <raison>` | Avertir | `practice.staff.punish` |
| `/punishhistory <joueur>` | Historique | `practice.staff.punish` |
| `/report handle <id>` | Gérer report | `practice.staff.reports` |

---

## 🏗️ Architecture

### Vue d'Ensemble

Le plugin suit une architecture modulaire avec séparation claire des responsabilités :

```
PracticeCore
├── 📂 Commands (80+)     → Interface utilisateur
├── 📂 Listeners (13)     → Gestion des événements
├── 📂 Managers (40+)     → Logique métier
├── 📂 Models (50+)       → Modèles de données
├── 📂 GUI (5)            → Interfaces graphiques
└── 📂 Utils (1)          → Utilitaires
```

### Managers Principaux

#### Core Managers

- **PlayerManager** : Gestion des joueurs en mémoire avec cache
- **MatchManager** : Création, démarrage, terminaison des matchs
- **QueueManager** : Files d'attente avec matchmaking automatique
- **ArenaManager** : Attribution et libération des arènes
- **KitManager** : Gestion des kits et loadouts

#### Combat Managers

- **CombatManager** : Combat tags, pearl cooldowns
- **ComboManager** : Détection et affichage des combos
- **EloManager** : Calcul des changements d'ELO
- **KillstreakManager** : Suivi des séries de kills
- **RematchManager** : Système de revanche

#### Social Managers

- **PartyManager** : Gestion des parties
- **FriendManager** : Système d'amis
- **ClanManager** : Système de clans
- **ChatManager** : Chat global et privé
- **ChatFormatManager** : Formatage des messages

#### Progression Managers

- **AchievementManager** : Succès et déblocages
- **QuestManager** : Quêtes journalières
- **SeasonManager** : Gestion des saisons
- **StatisticsManager** : Tracking des statistiques
- **LeaderboardManager** : Classements

#### Economy Managers

- **ShopManager** : Boutique de cosmétiques
- **CosmeticManager** : Activation des cosmétiques
- **CrateManager** : Système de coffres
- **BoostManager** : Boosts temporaires
- **DailyRewardManager** : Récompenses quotidiennes

#### Moderation Managers

- **ReportManager** : Système de signalement
- **PunishmentManager** : Gestion des sanctions
- **StaffModeManager** : Mode modération

#### Navigation Managers

- **HomeManager** : Système de homes
- **WarpManager** : Warps publics
- **TeleportManager** : TPA et téléportations

### Flux de Match Typique

```
1. Joueur → QueueCommand → QueueManager.joinQueue()
2. QueueManager trouve un adversaire compatible (ELO)
3. MatchManager.createMatch() → Réserve une Arena
4. Téléportation → Countdown 5s → Match starts
5. Combat → Tracking stats en temps réel
6. Mort → Match ends → EloManager calcule changements
7. Résultats affichés → Inventaires sauvegardés
8. Arena libérée → Joueurs retournent au spawn
```

### Base de Données MongoDB

#### Collections

- **players** : Données joueurs (ELO, stats, coins, XP, settings)
- **matches** : Historique des matchs
- **clans** : Données des clans
- **punishments** : Historique des sanctions
- **reports** : Signalements
- **achievements** : Progression des succès
- **cosmetics** : Possessions cosmétiques

#### Système de Cache

- Cache en mémoire pour les données fréquemment accédées
- Sauvegarde automatique toutes les 5 minutes
- Sauvegarde immédiate sur déconnexion
- CompletableFuture pour opérations asynchrones

---

## 🔧 API & Développement

### Dépendances Maven

```xml
<dependencies>
    <!-- Spigot API -->
    <dependency>
        <groupId>org.spigotmc</groupId>
        <artifactId>spigot-api</artifactId>
        <version>1.21.8-R0.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- MongoDB Driver -->
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>4.11.1</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.42</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Utiliser l'API

```java
// Récupérer le plugin
PracticeCore plugin = PracticeCore.getInstance();

// Accéder aux managers
PlayerManager playerManager = plugin.getPlayerManager();
MatchManager matchManager = plugin.getMatchManager();

// Récupérer un joueur
PracticePlayer practicePlayer = playerManager.getPlayer(uuid);

// Créer un match
Match match = matchManager.createMatch(MatchType.SOLO, "nodebuff", arena, ranked);

// Ajouter un joueur à une queue
queueManager.joinQueue(player, queue);
```

### Événements Custom

```java
// MatchStartEvent
@EventHandler
public void onMatchStart(MatchStartEvent event) {
    Match match = event.getMatch();
    // Votre code
}

// MatchEndEvent
@EventHandler
public void onMatchEnd(MatchEndEvent event) {
    Match match = event.getMatch();
    UUID winner = event.getWinner();
    // Votre code
}
```

---

## 🚀 Changelog

### Version 1.1.0 - Décembre 2025

#### ✨ Migration Java 21 LTS

- ✅ Migration complète vers Java 21 LTS depuis Java 17
- ✅ Syntaxe moderne : Switch expressions, Pattern matching, Records
- ✅ 0 erreur de compilation, 0 warning IDE
- ✅ 100+ améliorations de qualité de code appliquées

#### 📦 Mises à Jour Techniques

- ✅ **MongoDB 4.11.1** : Driver synchrone moderne pour persistence
- ✅ **Spigot 1.21.8-R0.1-SNAPSHOT** : Support de la dernière version
- ✅ **Maven 3.9.9** : Build system optimisé
- ✅ **Compiler Plugin 3.14.1** : Configuration optimale

#### 🎯 Fonctionnalités Ajoutées

- ✅ Système de cosmétiques complet (Kill Effects, Trails, Hit Sounds, Titles)
- ✅ Système de crates avec 5 raretés
- ✅ Achievements avec 40+ succès
- ✅ Daily quests (3 par jour)
- ✅ Système de clans avec ELO
- ✅ Tournois avec brackets
- ✅ Événements FFA
- ✅ Boosts temporaires
- ✅ Système de punitions complet
- ✅ Reports avec gestion staff

### Version 1.0.0 - Novembre 2025

- 🎉 Release initiale avec système de practice complet
- ⚔️ Combat avec knockback Kohi/Minemen
- 🏆 Système d'ELO et 7 rangs
- 🎯 5 kits de combat
- 👥 Systèmes de parties, amis, clans
- 📊 Scoreboard dynamique
- 💾 MongoDB pour persistence

---

## 🐛 Dépannage

### Les joueurs ne trouvent pas de matchs

- ✅ Vérifiez qu'il y a au moins **2 arènes disponibles**
- ✅ Augmentez `search-range-max` dans config.yml (recommandé: 500)
- ✅ Réduisez `search-range-initial` (recommandé: 100)
- ✅ Vérifiez que les queues sont bien activées dans config.yml

### La base de données ne se connecte pas

- ✅ Vérifiez que **MongoDB est démarré** : `sudo systemctl status mongod`
- ✅ Testez la connexion : `mongo --host localhost --port 27017`
- ✅ Vérifiez `connection-string` dans config.yml
- ✅ Regardez les logs du serveur pour les erreurs MongoDB

### Le scoreboard ne s'affiche pas

- ✅ Vérifiez `scoreboard.enabled: true` dans config.yml
- ✅ Les joueurs peuvent le désactiver dans `/settings`
- ✅ Vérifiez les conflits avec d'autres plugins de scoreboard
- ✅ Redémarrez le serveur après modification

### Les cosmétiques ne fonctionnent pas

- ✅ Vérifiez que le joueur a bien **acheté** le cosmétique
- ✅ Utilisez `/shop` puis `/killeffect`, `/trail`, etc.
- ✅ Vérifiez les permissions dans LuckPerms
- ✅ Les cosmétiques sont sauvegardés dans MongoDB

---

## 📝 TODO / Améliorations Futures

### ⚡ Performance

- [ ] Optimisation des requêtes MongoDB
- [ ] Cache Redis pour les leaderboards
- [ ] Profiling et optimisation mémoire

### 🎮 Gameplay

- [ ] Système d'events (brackets FFA, tournois ladder)
- [ ] Éditeur de kits custom en jeu
- [ ] Système de spectateur avancé avec caméra libre
- [ ] Replay system (enregistrement et lecture des matchs)
- [ ] Système de paris entre joueurs
- [ ] Ranked Teams (2v2, 3v3, 5v5 classés)

### 🛠️ Administration

- [ ] Panel web d'administration
- [ ] API REST pour intégrations externes
- [ ] Webhook Discord pour événements
- [ ] Logs avancés avec ElasticSearch

### 🌐 Internationalisation

- [ ] Support multi-langues (FR, EN, ES, DE)
- [ ] Messages configurables par langue
- [ ] Détection automatique de la langue du client

### 🔐 Sécurité

- [ ] Anti-cheat intégré (killaura, velocity, etc.)
- [ ] Rate limiting sur les commandes
- [ ] Détection d'exploitation de bugs

---

## 👨‍💻 Développement

### Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. **Fork** le projet
2. **Créez** une branche (`git checkout -b feature/AmazingFeature`)
3. **Commit** vos changements (`git commit -m 'Add AmazingFeature'`)
4. **Push** vers la branche (`git push origin feature/AmazingFeature`)
5. **Ouvrez** une Pull Request

### Standards de Code

- ✅ **Java 21 LTS** requis
- ✅ Suivre les conventions de nommage Java
- ✅ Documenter les méthodes publiques avec Javadoc
- ✅ Tests unitaires pour la logique critique
- ✅ Code review obligatoire avant merge

---

## 📄 Licence

Ce projet est sous **licence privée**. Tous droits réservés.

**⚠️ Utilisation strictement interdite sans autorisation**

---

## 🙏 Crédits

### Inspiration

- 🏆 **Kohi** - Pour le système de knockback et l'expérience PvP
- 🏆 **Minemen Club** - Pour le matchmaking et les kits
- 🏆 **Lunar Client** - Pour l'interface utilisateur moderne

### Technologies

- ☕ **Java 21 LTS** - Oracle/OpenJDK
- 🎮 **Spigot 1.21.8** - Bukkit team
- 💾 **MongoDB 4.11.1** - MongoDB Inc.
- 🛠️ **Lombok 1.18.42** - Project Lombok
- 🎨 **MiniMessage** - Kyori Adventure

### Développement

- 💻 **Développeur Principal** : Louis
- 🎨 **Design & UX** : Communauté Practice
- 🐛 **Beta Testers** : Communauté Hyko

---

## 📞 Support

### Besoin d'aide ?

- 📧 **Email** : support@hyko-practice.com
- 💬 **Discord** : [discord.gg/hyko](https://discord.gg/hyko)
- 📚 **Wiki** : [wiki.hyko-practice.com](https://wiki.hyko-practice.com)
- 🐛 **Bug Reports** : [GitHub Issues](https://github.com/louismro/core/issues)

### Questions Fréquentes

**Q: Compatible avec quelle version de Minecraft ?**  
R: Spigot 1.21.8 (Java 21 requis côté serveur)

**Q: Fonctionne avec Paper/Purpur ?**  
R: Oui, compatible avec tous les forks de Spigot

**Q: Besoin de plugins supplémentaires ?**  
R: Non, entièrement standalone. MongoDB requis.

**Q: Support multi-serveur ?**  
R: Pas encore, prévu pour v2.0

---

<div align="center">

**⭐ Si vous aimez ce projet, donnez-lui une étoile sur GitHub ! ⭐**

**Version:** 1.1.0  
**Auteur:** Louis  
**Date:** Décembre 2025  
**Java:** 21 LTS  
**Spigot:** 1.21.8-R0.1-SNAPSHOT

---

*Made with ❤️ for the Practice community*

</div>
