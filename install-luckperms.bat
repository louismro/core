@echo off
echo ============================================
echo Configuration LuckPerms pour Hyko Practice
echo ============================================
echo.

echo [1/1] Creation du fichier de commandes...

echo creategroup default > luckperms-commands.txt
echo creategroup vip >> luckperms-commands.txt
echo creategroup vip+ >> luckperms-commands.txt
echo creategroup mvp >> luckperms-commands.txt
echo creategroup helper >> luckperms-commands.txt
echo creategroup mod >> luckperms-commands.txt
echo creategroup admin >> luckperms-commands.txt
echo creategroup owner >> luckperms-commands.txt
echo. >> luckperms-commands.txt
echo group default permission set practice.player true >> luckperms-commands.txt
echo group default meta setweight 1 >> luckperms-commands.txt
echo. >> luckperms-commands.txt
echo group vip parent add default >> luckperms-commands.txt
echo group vip permission set practice.vip true >> luckperms-commands.txt
echo group vip meta setprefix "^^^&7[^^^&bVIP^^^&7] " >> luckperms-commands.txt
echo group vip meta setweight 10 >> luckperms-commands.txt
echo. >> luckperms-commands.txt
echo group vip+ parent add vip >> luckperms-commands.txt
echo group vip+ permission set practice.premium true >> luckperms-commands.txt
echo group vip+ meta setprefix "^^^&7[^^^&bVIP^^^&a+^^^&7] " >> luckperms-commands.txt
echo group vip+ meta setweight 20 >> luckperms-commands.txt
echo. >> luckperms-commands.txt
echo group mvp parent add vip+ >> luckperms-commands.txt
echo group mvp permission set practice.premium true >> luckperms-commands.txt
echo group mvp meta setprefix "^^^&7[^^^&6MVP^^^&7] " >> luckperms-commands.txt
echo group mvp meta setweight 30 >> luckperms-commands.txt
echo. >> luckperms-commands.txt
echo group helper parent add mvp >> luckperms-commands.txt
echo group helper permission set practice.staff true >> luckperms-commands.txt
echo group helper meta setprefix "^^^&7[^^^&aHelper^^^&7] " >> luckperms-commands.txt
echo group helper meta setweight 40 >> luckperms-commands.txt
echo. >> luckperms-commands.txt
echo group mod parent add helper >> luckperms-commands.txt
echo group mod permission set practice.admin true >> luckperms-commands.txt
echo group mod meta setprefix "^^^&7[^^^&cMod^^^&7] " >> luckperms-commands.txt
echo group mod meta setweight 50 >> luckperms-commands.txt
echo. >> luckperms-commands.txt
echo group admin parent add mod >> luckperms-commands.txt
echo group admin permission set practice.admin true >> luckperms-commands.txt
echo group admin meta setprefix "^^^&7[^^^&4Admin^^^&7] " >> luckperms-commands.txt
echo group admin meta setweight 100 >> luckperms-commands.txt
echo. >> luckperms-commands.txt
echo group owner parent add admin >> luckperms-commands.txt
echo group owner permission set practice.* true >> luckperms-commands.txt
echo group owner meta setprefix "^^^&7[^^^&4^^^&lOwner^^^&7] " >> luckperms-commands.txt
echo group owner meta setweight 1000 >> luckperms-commands.txt

echo Fichier cree: luckperms-commands.txt
echo.
echo ============================================
echo INSTRUCTIONS:
echo ============================================
echo.
echo 1. Demarrez votre serveur Spigot 1.21.8
echo 2. Attendez que LuckPerms se charge
echo 3. Executez dans la console: lp bulkupdate
echo    Ou copiez/collez les commandes de luckperms-commands.txt
echo.
echo Pour donner un grade: lp user ^<pseudo^> parent set ^<groupe^>
echo Exemple: lp user Louis parent set owner
echo ============================================
pause
