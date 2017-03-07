#!/bin/sh

# Pour preciser une plage de lignes pour la generation, rajouter
# les arguments (exemple pour generer lignes 10 a 12 uniquement) :
# -Dexcel.first.line=10
# -Dexcel.last.line=12

# D'autres parametres sont modifiables dans le fichier e2t.properties

java -jar lib\e2t.jar
read -p "Appuyez sur une touche pour continuer..."