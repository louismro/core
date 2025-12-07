# PracticeCore - Core Practice Moderne pour LightSpigot

Un plugin Minecraft practice complet et moderne pour Spigot 1.8.8, optimisé pour LightSpigot avec une expérience PvP inspirée de Kohi et Minemen.

## 🎮 Fonctionnalités

### ⚔️ Système de Combat
- **Knockback personnalisé** : Géré par LightSpigot avec profils Kohi/Minemen
- **Système de combos** : Affichage en temps réel avec messages et titres
- **Pearl cooldown** : 16 secondes par défaut, configurable
- **Combat tag** : 15 secondes empêchant la déconnexion
- **Statistiques de match** : Hits, combos, précision des potions/flèches

### 🏆 Système d'ELO et Ranking
- **ELO dynamique** : Système K-factor avec plages configurables
- **7 Rangs** : Bronze, Silver, Gold, Platinum, Diamond, Master, Champion
- **Matchmaking intelligent** : Recherche par ELO avec expansion de range
- **Statistiques détaillées** : K/D, Winrate, Victoires/Défaites par kit

### 🎯 Kits Disponibles
- **NoDebuff** : Épée diamant, potions de speed et health, perles
- **Debuff** : NoDebuff + potions de poison, slowness, weakness
- **BuildUHC** : Kit UHC complet avec build et golden apples
- **Combo** : Kit simple pour pratiquer les combos
- **Sumo** : Sans items, knockback uniquement

### 🔄 Files d'Attente (Queues)
- **Queues classées** : Avec ELO et matchmaking
- **Queues non-classées** : Pour s'entraîner sans pression
- **Auto-matchmaking** : Recherche automatique d'adversaires
- **Expansion de range** : Augmentation progressive après 10 secondes

### 👥 Système de Parties
- **Création de parties** : Jusqu'à 10 joueurs
- **Invitations** : Système d'invitation avec acceptation
- **Gestion** : Kick, leave, disband
- **Matchs d'équipe** : Support des matchs 2v2, 3v3, etc.

### 🎪 Système de Duels
- **Défis personnalisés** : Défier n'importe quel joueur
- **Choix du kit** : Sélection du kit pour le duel
- **Accept/Deny** : Système de demande avec expiration

### 📊 Scoreboard Dynamique
- **États différents** : Spawn, Queue, Match, Spectating
- **Informations en temps réel** :
  - ELO par kit
  - Temps de recherche en queue
  - Stats du match en cours
  - Ping des joueurs
  - Compteur de potions
  - Combo actuel

### 🗺️ Gestion des Arènes
- **Arènes multiples** : Support illimité d'arènes
- **Gestion automatique** : Attribution et libération
- **Spawn points** : Multiple spawn points par arène
- **Configuration YAML** : Facile à configurer

### 💾 Base de Données
- **SQLite** : Base de données locale par défaut
- **MySQL** : Support MySQL pour multi-serveurs
- **Sauvegarde automatique** : Toutes les 5 minutes
- **Données sauvegardées** :
  - ELO par kit
  - Statistiques détaillées
  - Préférences joueurs
  - Killstreaks

### 🛠️ Intégrations
- **LuckPerms** : Gestion des permissions
- **AdvancedEnchantments** : Support des enchantements custom
- **TownPractice** : Compatible avec la configuration existante
- **LightSpigot** : Optimisé pour le knockback custom

## 📦 Installation

### Prérequis
- Spigot/LightSpigot 1.8.8
- Java 21
- Maven pour la compilation

### Compilation
```bash
cd /chemin/vers/core
mvn clean package
```

Le JAR sera généré dans `target/PracticeCore-1.0.0.jar`

### Installation
1. Placez le JAR dans `plugins/`
2. Démarrez le serveur pour générer les fichiers de configuration
3. Configurez `config.yml`, `arenas.yml` et `kits.yml`
4. Redémarrez le serveur

## ⚙️ Configuration

### config.yml
Configuration principale avec :
- Paramètres généraux (spawn, taille des parties)
- Système d'ELO (K-factor, rangs)
- Queues (ranked/unranked pour chaque kit)
- Combat (pearl cooldown, combat tag)
- Scoreboard (lignes personnalisables)
- Base de données (SQLite ou MySQL)

### arenas.yml
Définition des arènes :
```yaml
arenas:
  arena1:
    display-name: "&eArena 1"
    pos1: # Coin 1
    pos2: # Coin 2
    spawns: # Points de spawn
```

### kits.yml
Configuration des kits avec items, armure et effets

## 🎮 Commandes

### Joueur
- `/queue join <queue>` - Rejoindre une queue
- `/queue leave` - Quitter la queue
- `/party create` - Créer une partie
- `/party invite <joueur>` - Inviter à la partie
- `/party accept` - Accepter une invitation
- `/party leave` - Quitter la partie
- `/party kick <joueur>` - Exclure un joueur
- `/duel <joueur> [kit]` - Défier en duel
- `/duel accept` - Accepter un duel
- `/stats` - Voir ses statistiques
- `/spawn` - Retourner au spawn
- `/ping` - Voir son ping

### Admin
- `/setspawn` - Définir le spawn (à implémenter)
- `/arena create <nom>` - Créer une arène (à implémenter)
- `/kit create <nom>` - Créer un kit (à implémenter)

## 🏗️ Architecture

### Structure du Projet
```
src/main/java/fr/louis/practice/
├── PracticeCore.java           # Classe principale
├── commands/                   # Commandes
│   ├── QueueCommand.java
│   ├── PartyCommand.java
│   ├── DuelCommand.java
│   ├── StatsCommand.java
│   ├── SpawnCommand.java
│   └── PingCommand.java
├── listeners/                  # Événements
│   ├── PlayerConnectionListener.java
│   ├── CombatListener.java
│   ├── DeathListener.java
│   ├── InteractionListener.java
│   ├── MiscListener.java
│   └── BuildListener.java
├── managers/                   # Gestionnaires
│   ├── PlayerManager.java
│   ├── MatchManager.java
│   ├── QueueManager.java
│   ├── PartyManager.java
│   ├── EloManager.java
│   ├── ArenaManager.java
│   ├── KitManager.java
│   ├── CombatManager.java
│   ├── DuelManager.java
│   ├── CustomScoreboardManager.java
│   ├── DatabaseManager.java
│   └── InventoryManager.java
└── models/                     # Modèles de données
    ├── PracticePlayer.java
    ├── Match.java
    ├── Queue.java
    ├── Party.java
    ├── Arena.java
    ├── Kit.java
    ├── PlayerStats.java
    └── ...
```

### Managers Principaux

#### PlayerManager
Gère tous les joueurs connectés avec leurs données en mémoire.

#### MatchManager
Crée, démarre et termine les matchs. Calcule les changements d'ELO.

#### QueueManager
Gère les files d'attente et le matchmaking automatique.

#### EloManager
Calcule les changements d'ELO selon le système Elo standard.

#### CombatManager
Gère les combos, pearl cooldowns et combat tags.

## 🔧 Personnalisation

### Ajouter un Kit
1. Définir dans `kits.yml`
2. Créer la méthode dans `KitManager`
3. Ajouter à la queue dans `config.yml`

### Ajouter une Arène
1. Définir dans `arenas.yml`
2. Le système charge automatiquement

### Modifier l'ELO
Ajustez dans `config.yml` :
- `k-factor` : Vitesse de changement (16-40)
- `starting-elo` : ELO de départ
- Rangs et leurs plages

## 🐛 Dépannage

### Les joueurs ne trouvent pas de matchs
- Vérifiez qu'il y a au moins une arène disponible
- Augmentez le `search-range-max` dans la config
- Réduisez le `search-range-initial`

### La base de données ne sauvegarde pas
- Vérifiez les permissions du dossier `plugins/PracticeCore/`
- Pour MySQL, vérifiez les identifiants de connexion

### Le scoreboard ne s'affiche pas
- Vérifiez `scoreboard.enabled: true`
- Les joueurs peuvent le désactiver dans leurs settings

## 📝 TODO / Améliorations Futures

- [ ] Système d'events (brackets, FFA)
- [ ] Éditeur de kits custom
- [ ] Système de spectateur avancé
- [ ] Leaderboards
- [ ] Replay system
- [] Anti-cheat intégré
- [ ] API pour développeurs
- [ ] Support de parties custom
- [ ] Système de cosmétiques

## 👨‍💻 Développement

### Dépendances
- Spigot API 1.8.8
- Lombok (annotations)
- SQLite/MySQL (base de données)

### Contribution
Les contributions sont les bienvenues ! N'hésitez pas à :
- Reporter des bugs
- Proposer des fonctionnalités
- Soumettre des pull requests

## 📄 Licence

Ce projet est sous licence privée. Tous droits réservés.

## 🙏 Crédits

- Inspiré par Kohi et Minemen Club
- Développé pour LightSpigot
- Compatible avec TownPractice, AdvancedEnchantments, LuckPerms

## 📞 Support

Pour toute question ou problème :
- Créez une issue sur GitHub
- Contactez le développeur

---

**Version:** 1.0.0  
**Auteur:** Louis  
**Date:** Décembre 2025
