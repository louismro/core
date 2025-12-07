# 🎮 PracticeCore - Plugin Minecraft Practice Professionnel

## 📊 **STATISTIQUES DU PROJET**

```
📦 Fichiers Java    : 190 fichiers
📝 Lignes de code   : 19,394 lignes
💾 Taille JAR       : 2.75 MB
🎯 Version Spigot   : 1.21.8-R0.1-SNAPSHOT
☕ Java             : 17
🗄️  Base de données : MongoDB 4.11.1
```

## 🏗️ **ARCHITECTURE DU PLUGIN**

### Structure complète (190 fichiers)
```
├── 📂 Commands (78 fichiers)
│   ├── Commandes joueur (50+)
│   └── Commandes admin (28)
├── 📂 Managers (47 fichiers)
│   ├── Gestion des matchs
│   ├── Gestion des joueurs
│   ├── Économie & Progression
│   └── Systèmes avancés
├── 📂 Models (49 fichiers)
│   ├── Classes de données
│   ├── Entités MongoDB
│   └── Structures de jeu
├── 📂 GUI (5 fichiers)
│   └── Interfaces graphiques
└── 📂 Listeners (10 fichiers)
    └── Événements Bukkit
```

---

## 🎯 **DESCRIPTION COMPLÈTE**

**PracticeCore** est un plugin Minecraft Practice **ultra-complet** inspiré des plus grands serveurs PvP (Kohi, Minemen, MMC). Il transforme votre serveur en une **plateforme de combat compétitive** avec tous les systèmes modernes attendus par les joueurs PvP.

---

## ⚔️ **SYSTÈME DE COMBAT & MATCHMAKING**

### 🏆 **Système de Matchs**
- **Duels 1v1** classés et non-classés
- **Matchs de party** (2v2, 3v3, etc.)
- **FFA (Free For All)** avec arènes dédiées
- **Tournois automatisés** avec brackets
- **File d'attente intelligente** avec ELO matching
- **Spectateur** en temps réel
- **Rematch** instantané après match
- **Historique des matchs** complet

### 🎭 **Système de Kits**
- **Gestion complète des kits PvP**
- **Kit Editor** en jeu (inventaire personnalisable)
- **Loadouts** (plusieurs configurations par kit)
- **NoDebuff, BuildUHC, Soup, Combo** et plus
- **Statistiques par kit** (ELO, W/L ratio)
- **Exportation/Importation** de kits

### 🏟️ **Gestion des Arènes**
- **Arènes dynamiques** avec spawn points
- **Système de duplication** d'arène
- **Sélection automatique** selon disponibilité
- **Build mode** pour construction d'arènes
- **Restauration automatique** après match

---

## 👥 **SYSTÈME SOCIAL**

### 🎉 **Parties (Party System)**
- **Création/Gestion de party**
- **Invitations** et acceptations
- **Chat de party** privé
- **Matchs en équipe**
- **Système de leader**
- **Téléportation de groupe**

### 👫 **Système d'Amis**
- **Liste d'amis persistante**
- **Demandes d'amis**
- **Statut en ligne/hors ligne**
- **Messages privés**
- **Notification de connexion**

### 🏰 **Système de Clans**
- **Création de clans**
- **Gestion des membres** (ranks)
- **Chat de clan**
- **Statistiques de clan**
- **Wars entre clans** (à venir)

---

## 📊 **PROGRESSION & ÉCONOMIE**

### 💰 **Système de Coins**
- **Économie complète** avec transactions
- **Gains après match** (victoire/défaite)
- **Daily rewards** (récompenses quotidiennes)
- **Boosts** de gains (temporaires)
- **Transferts entre joueurs** (/pay)
- **Baltop** (classement richesse)

### 🏆 **Système de Saisons**
- **Réinitialisation saisonnière**
- **Récompenses de fin de saison**
- **Leaderboards persistants**
- **Progression ELO**
- **Rangs saisonniers**

### ⭐ **Système d'EXP & Niveaux**
- **Expérience par match**
- **Système de niveaux**
- **Récompenses par niveau**
- **Multiplicateurs VIP**

### 📈 **Statistiques Complètes**
- **Global** : Kills, Deaths, W/L, KD ratio
- **Par kit** : ELO, Matchs joués, Win rate
- **Streaks** : Killstreak, Win streak
- **Records personnels**

---

## 🎨 **SYSTÈME COSMÉTIQUE**

### 🎆 **Effets de Kill**
- **12 effets différents** (LIGHTNING, EXPLOSION, HEART, etc.)
- **Particles personnalisés** (1.21.8 API)
- **Sons de victoire**
- **Achat avec coins**

### 🎵 **Sons de Hit**
- **9 sons personnalisés**
- **Configuration individuelle**
- **Preview en GUI**

### ✨ **Traînées (Trails)**
- **Traînées de particules** en mouvement
- **Animations** personnalisées
- **Activation/désactivation** dynamique
- **Différents matériaux**

### 🏷️ **Titres & Préfixes**
- **Titres cosmétiques** déblocables
- **Système de rareté**
- **Affichage en chat**
- **Collection complète**

---

## 🎁 **SYSTÈME DE RÉCOMPENSES**

### 📦 **Crates (Coffres)**
- **Crates avec récompenses aléatoires**
- **Animations d'ouverture**
- **Raretés** (Common, Rare, Epic, Legendary)
- **Récompenses** : Coins, Cosmétiques, Boosts

### 🎯 **Quêtes Journalières**
- **3-5 quêtes par jour**
- **Réinitialisation automatique**
- **Progression trackée**
- **Récompenses coins/XP**
- **Interface GUI dédiée**

### 🏅 **Succès (Achievements)**
- **50+ succès** différents
- **Catégories** : Combat, Social, Collection
- **Récompenses progressives**
- **Suivi en temps réel**

---

## 🤖 **SYSTÈMES AVANCÉS**

### 🤺 **Bots d'Entraînement**
- **IA de combat** configurée
- **3 niveaux de difficulté** (Easy, Medium, Hard)
- **Simulation de combat réaliste**
- **Pas de perte d'ELO**
- **Entraînement illimité**

### 📸 **Snapshots d'Inventaire**
- **Capture automatique** après match
- **Consultation post-match**
- **Voir l'inventaire de l'adversaire**
- **Historique conservé**

### 🔥 **Killstreaks**
- **Système de combo**
- **Notifications**
- **Récompenses progressives**
- **Effets visuels**

### 📺 **Spectateur Avancé**
- **Mode spectateur** complet
- **Téléportation entre joueurs**
- **Interface de sélection**
- **Statistiques en temps réel**

---

## 🛡️ **SYSTÈME DE MODÉRATION**

### 👮 **Commandes Staff**
- **/freeze** - Geler un joueur
- **/vanish** - Mode invisible
- **/invsee** - Voir inventaire
- **/staffchat** - Chat modérateur
- **/staffmode** - Mode modération complet

### ⚖️ **Punitions**
- **/ban** - Bannissement
- **/kick** - Expulsion
- **/mute** - Mute
- **/warn** - Avertissement
- **Historique** des sanctions
- **Raisons prédéfinies**
- **Durées temporaires**

### 📊 **Reports**
- **Système de signalement**
- **Catégories** : Cheat, Toxic, Bug
- **File d'attente** pour staff
- **Notifications** en temps réel

---

## 🌐 **SYSTÈME D'ÉVÉNEMENTS**

### 🎊 **Événements Globaux**
- **Double XP** weekend
- **Double Coins** events
- **Tournois automatisés**
- **FFA Events** spéciaux
- **Notifications serveur-wide**

### 🏆 **Tournois**
- **Brackets automatiques**
- **Single/Double elimination**
- **Récompenses configurables**
- **Spectateurs autorisés**
- **Annonces en temps réel**

---

## 💬 **SYSTÈME DE CHAT**

### 💬 **Chat Avancé**
- **Filtres anti-spam**
- **Anti-insultes** configurable
- **Cooldowns par grade**
- **Formats personnalisables**
- **Mentions** (@pseudo)

### 📢 **Canaux de Chat**
- **Global** - Chat public
- **Party** - Chat de groupe
- **Clan** - Chat de clan
- **Staff** - Chat modération
- **Tournoi** - Chat événement

---

## 🏠 **SYSTÈME DE TÉLÉPORTATION**

### 🏡 **Homes & Warps**
- **/home** - Téléportation maison
- **/sethome** - Définir point de spawn
- **/delhome** - Supprimer home
- **/homes** - Liste des homes
- **/warp** - Warps publics

### 🚀 **Téléportations Joueur**
- **/tpa** - Demande de TP
- **/tpahere** - TP ici
- **/tpaccept** - Accepter
- **/tpdeny** - Refuser
- **/back** - Retour position précédente

---

## 👑 **SYSTÈME DE RANGS & PERMISSIONS**

### 8 Groupes Hiérarchiques
```
👑 OWNER    (1000) ─── Accès total
🔧 ADMIN    (100)  ─── Administration
⚔️  MOD      (50)   ─── Modération
🛡️  HELPER   (40)   ─── Assistance
🌟 MVP      (30)   ─── VIP Premium
💠 VIP+     (20)   ─── Premium
💎 VIP      (10)   ─── Membre VIP
👤 DEFAULT   (1)    ─── Joueur de base
```

### Intégration LuckPerms
- **40+ permissions** configurées
- **Préfixes/Suffixes** automatiques
- **Héritage** de permissions
- **Permissions temporaires**
- **Configuration complète** fournie

---

## 🗄️ **SYSTÈME DE PERSISTANCE**

### 💾 **MongoDB**
- **Base de données MongoDB 4.11.1**
- **Sauvegarde automatique** des données
- **Collections** :
  - Players (profils joueurs)
  - Matches (historique matchs)
  - Stats (statistiques)
  - Clans
  - Punishments
  - Transactions

### 📊 **Leaderboards**
- **Top ELO** par kit
- **Top Kills**
- **Top Win Rate**
- **Top Richesse**
- **Top Niveaux**
- **Mise à jour automatique**

---

## ⚙️ **COMMANDES ADMIN**

### 🔧 **Gestion Serveur**
```bash
/setspawn          # Définir spawn
/arena             # Gérer arènes
/gamemode          # Changer gamemode
/fly               # Mode vol
/god               # Invincibilité
/heal              # Soigner
/feed              # Nourrir
/tp / tphere       # Téléportations
/tpall             # TP tous
/clear             # Vider inventaire
/broadcast         # Annonce globale
/vanish            # Invisibilité
/speed             # Vitesse
/eco               # Gestion économie
/time / weather    # Monde
/sudo              # Forcer commande
/killall           # Supprimer entités
/give              # Donner items
/enderchest        # Ouvrir EC
/kickall           # Expulser tous
/rename / lore     # Modifier items
```

### 🎮 **Gestion Événements**
```bash
/event start       # Lancer event
/event stop        # Arrêter event
/event info        # Infos event
/tournament        # Gérer tournois
```

---

## 📱 **INTERFACES GRAPHIQUES**

### 🖥️ **GUIs Disponibles**
1. **Kit Editor** - Personnalisation de kit
2. **Shop** - Boutique cosmétiques
3. **Quests** - Quêtes journalières
4. **Stats** - Statistiques détaillées
5. **Queue Selector** - Sélection de queue

Toutes les GUIs sont **modernes**, **intuitives** et **optimisées** pour 1.21.8.

---

## 🔥 **FONCTIONNALITÉS AVANCÉES**

### ⚡ **Performance**
- **Architecture asynchrone** pour MongoDB
- **Caching intelligent** des données
- **Optimisation des calculs**
- **Gestion mémoire efficace**

### 🛡️ **Sécurité**
- **Anti-cheat** de base
- **Validation** des données
- **Protection** contre exploits
- **Logs** complets

### 🎨 **Personnalisation**
- **Configuration YAML** complète
- **Messages** personnalisables
- **Couleurs** configurables
- **Système de tags**

---

## 🚀 **PRÊT POUR LA PRODUCTION**

### ✅ **100% Fonctionnel**
- ✅ **0 erreur de compilation**
- ✅ **Compatible Spigot 1.21.8**
- ✅ **Toutes les API migrées**
- ✅ **Build Maven réussi**

### 📦 **Livré Avec**
- ✅ **JAR compilé** (2.75 MB)
- ✅ **Configuration LuckPerms**
- ✅ **Guide d'installation**
- ✅ **Documentation complète**

---

## 🎯 **COMMANDES JOUEUR PRINCIPALES**

```bash
# Combat
/queue              # Rejoindre queue
/duel <joueur>      # Défier en duel
/spectate <joueur>  # Spectater
/rematch            # Revanche
/ffa                # Free For All

# Social
/party              # Gérer party
/friend             # Gérer amis
/clan               # Gérer clan

# Progression
/stats              # Voir stats
/profile            # Profil joueur
/leaderboard        # Classements
/season             # Saison actuelle

# Économie
/coins              # Voir coins
/shop               # Boutique
/pay <joueur>       # Envoyer argent
/baltop             # Top richesse
/daily              # Récompense quotidienne

# Cosmétiques
/killeffect         # Effets de kill
/hitsound           # Sons de hit
/trail              # Traînées
/title              # Titres

# Divers
/achievements       # Succès
/quests             # Quêtes
/settings           # Paramètres
/ping               # Voir ping
/spawn              # Retour spawn
```

---

## 💡 **POINTS FORTS DU PLUGIN**

### 🏆 **Compétitivité**
- Système ELO professionnel
- Matchmaking équilibré
- Statistiques détaillées
- Leaderboards en temps réel

### 🎮 **Expérience Joueur**
- Interface moderne
- Réactivité optimale
- Systèmes intuitifs
- Progression satisfaisante

### 🛠️ **Qualité du Code**
- **19,394 lignes** bien structurées
- **190 fichiers** organisés
- Architecture MVC respectée
- Code maintenable

### 🔧 **Administration**
- Outils complets
- Configuration flexible
- Modération efficace
- Logs détaillés

---

## 🌟 **RÉSUMÉ**

**PracticeCore** est un plugin Practice **complet et professionnel** offrant :

- ⚔️ **Système de combat** complet (Duels, FFA, Tournois)
- 👥 **Système social** avancé (Party, Amis, Clans)
- 📊 **Progression** riche (ELO, Niveaux, Saisons)
- 💰 **Économie** complète (Coins, Shop, Boosts)
- 🎨 **Cosmétiques** variés (Effets, Sons, Traînées)
- 🎁 **Récompenses** multiples (Quêtes, Succès, Crates)
- 🛡️ **Modération** robuste (Bans, Freeze, Reports)
- 🗄️ **Persistance** MongoDB
- 👑 **LuckPerms** intégré

**19,394 lignes de code** pour créer l'expérience Practice ultime ! 🚀
