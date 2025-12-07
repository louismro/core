# =====================================================
# GUIDE RAPIDE - Configuration LuckPerms
# PracticeCore - Spigot 1.21.8
# =====================================================

## 📋 HIÉRARCHIE DES GROUPES

```
OWNER (1000) ─── Accès total
    └── ADMIN (100) ─── Gestion serveur
        └── MOD (50) ─── Modération
            └── HELPER (40) ─── Assistance
                └── MVP (30) ─── VIP Premium
                    └── VIP+ (20) ─── Premium
                        └── VIP (10) ─── Membre VIP
                            └── DEFAULT (1) ─── Joueur de base
```

## 🚀 INSTALLATION RAPIDE

### Méthode 1 : Script automatique (Recommandé)
```bash
# Dans la console du serveur, exécutez :
./install-luckperms.sh
```

### Méthode 2 : Commandes manuelles
Copiez toutes les commandes du fichier `luckperms-config.yml` dans la console.

## 👥 ATTRIBUTION DES GROUPES

### Définir le groupe principal d'un joueur :
```
/lp user <pseudo> parent set <groupe>
```

**Exemples :**
```
/lp user Louis parent set owner
/lp user PlayerTest parent set vip
/lp user ModeratorName parent set mod
```

### Ajouter un groupe secondaire :
```
/lp user <pseudo> parent add <groupe>
```

### Groupe temporaire (30 jours) :
```
/lp user <pseudo> parent addtemp vip 30d
```

## 🎯 PERMISSIONS PAR GROUPE

### 👤 DEFAULT (Joueur de base)
- Toutes les commandes joueur du plugin
- Accès aux queues, parties, stats, profil

### 💎 VIP
- Chat coloré (`practice.vip.color`)
- Bypass queue (`practice.vip.queue.bypass`)
- Chat VIP (`practice.vip.chat`)

### 💠 VIP+
- Effets cosmétiques premium
- Titres exclusifs
- Traînées spéciales
- + Toutes les permissions VIP

### 🌟 MVP
- Accès prioritaire
- Features MVP exclusives
- + Toutes les permissions VIP+

### 🛡️ HELPER
- Freeze joueurs (`practice.freeze`)
- Voir inventaires (`practice.invsee`)
- Chat staff (`practice.staffchat`)
- Vanish (`practice.vanish`)
- Alertes (`practice.alert`)

### ⚔️ MOD (Modérateur)
- Ban/Kick/Mute/Warn
- Téléportations (`practice.tp`, `practice.tphere`)
- + Toutes les permissions HELPER

### 🔧 ADMIN (Administrateur)
- Gestion arènes (`practice.admin.arena`)
- Gestion spawn (`practice.admin.setspawn`)
- Gamemode, fly, god, heal, feed
- Économie (`practice.eco`)
- Événements (`practice.event`)
- Weather, time, sudo
- + Toutes les permissions MOD

### 👑 OWNER (Propriétaire)
- **Toutes les permissions** (`*`)
- Accès complet au serveur
- + Toutes les permissions ADMIN

## 🎨 PERSONNALISATION

### Modifier un préfixe :
```
/lp group <groupe> meta setprefix "<préfixe>"
```

**Exemple :**
```
/lp group vip meta setprefix "&6[VIP] &6"
```

### Modifier un suffixe :
```
/lp group <groupe> meta setsuffix "<suffixe>"
```

### Modifier le poids (ordre d'affichage) :
```
/lp group <groupe> meta setweight <nombre>
```

## 🎨 CODES COULEUR MINECRAFT

| Code | Couleur | Code | Couleur |
|------|---------|------|---------|
| `&0` | Noir | `&8` | Gris foncé |
| `&1` | Bleu foncé | `&9` | Bleu |
| `&2` | Vert foncé | `&a` | Vert |
| `&3` | Cyan foncé | `&b` | Cyan |
| `&4` | Rouge foncé | `&c` | Rouge |
| `&5` | Violet foncé | `&d` | Rose |
| `&6` | Or | `&e` | Jaune |
| `&7` | Gris | `&f` | Blanc |

**Formats :**
- `&l` = **Gras**
- `&o` = *Italique*
- `&n` = Souligné
- `&m` = ~~Barré~~
- `&k` = Aléatoire
- `&r` = Reset

## 📊 COMMANDES UTILES

### Informations
```
/lp user <pseudo> permission info    # Permissions du joueur
/lp user <pseudo> parent info         # Groupes du joueur
/lp group <groupe> permission info    # Permissions du groupe
/lp listgroups                        # Liste des groupes
/lp search <permission>               # Qui a cette permission
```

### Gestion des permissions
```
/lp group <groupe> permission set <permission> true
/lp group <groupe> permission unset <permission>
/lp user <pseudo> permission set <permission> true
```

### Synchronisation
```
/lp sync                              # Synchroniser les permissions
/lp reloadconfig                      # Recharger la config
```

## ⚡ PERMISSIONS IMPORTANTES

### Joueur
- `practice.use` - Utiliser le plugin

### VIP
- `practice.vip` - Statut VIP
- `practice.vip.chat` - Chat VIP
- `practice.vip.color` - Couleurs
- `practice.vip.queue.bypass` - Bypass queue

### Premium
- `practice.premium` - Statut Premium
- `practice.premium.chat` - Chat premium
- `practice.premium.color` - Couleurs premium
- `practice.premium.effects` - Effets premium

### Staff
- `practice.staff` - Statut staff
- `practice.alert` - Alertes
- `practice.staffchat` - Chat staff
- `practice.freeze` - Freeze
- `practice.vanish` - Invisibilité
- `practice.invsee` - Voir inventaires

### Modération
- `practice.ban` - Bannir
- `practice.kick` - Expulser
- `practice.mute` - Mute
- `practice.warn` - Avertir
- `practice.tp` - Téléportation
- `practice.tphere` - TP ici

### Admin
- `practice.admin` - Admin complet
- `practice.admin.setspawn` - Définir spawn
- `practice.admin.arena` - Gérer arènes
- `practice.gamemode` - Gamemode
- `practice.fly` - Voler
- `practice.god` - Invincibilité
- `practice.heal` - Soin
- `practice.feed` - Nourrir
- `practice.eco` - Économie
- `practice.event` - Événements

### Global
- `practice.*` - Toutes les permissions Practice
- `*` - Toutes les permissions serveur

## 🔗 INTÉGRATION AVEC D'AUTRES PLUGINS

### Chat (EssentialsChat, ChatControl, etc.)
Les préfixes/suffixes s'afficheront automatiquement si vous avez un plugin de chat compatible.

### PlaceholderAPI
LuckPerms fournit des placeholders :
- `%luckperms_prefix%` - Préfixe
- `%luckperms_suffix%` - Suffixe
- `%luckperms_primary_group_name%` - Nom du groupe

## 📝 EXEMPLES D'UTILISATION

### Promouvoir un joueur VIP :
```
/lp user PlayerVIP parent set vip
```

### Donner VIP temporaire (7 jours) :
```
/lp user PlayerTest parent addtemp vip 7d
```

### Nommer un modérateur :
```
/lp user NewMod parent set mod
```

### Donner une permission spécifique :
```
/lp user PlayerName permission set practice.premium.effects true
```

### Retirer un groupe :
```
/lp user PlayerName parent remove vip
```

## ⚠️ NOTES IMPORTANTES

1. **Redémarrez** le serveur après l'installation
2. Utilisez `/lp sync` après des modifications
3. Les weights déterminent la priorité (plus haut = prioritaire)
4. Le groupe "default" doit être défini dans `config.yml` de LuckPerms
5. Sauvegardez régulièrement votre base de données LuckPerms

## 🆘 SUPPORT

- **Wiki LuckPerms** : https://luckperms.net/wiki/
- **Discord** : https://discord.gg/luckperms
- **Documentation** : https://luckperms.net/wiki/Command-Usage

## ✅ VÉRIFICATION

Après installation, vérifiez que tout fonctionne :
```
/lp listgroups                    # Doit afficher tous les groupes
/lp group default permission info # Doit afficher les permissions
/lp user VotrePseudo parent info  # Doit afficher votre groupe
```

## 🎯 CONFIGURATION RECOMMANDÉE

Dans `config.yml` de LuckPerms, assurez-vous d'avoir :
```yaml
default-assignments:
  default: true
```

Cela attribue automatiquement le groupe "default" aux nouveaux joueurs.
