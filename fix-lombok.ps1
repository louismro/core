# Script pour supprimer @Data et ajouter getters/setters
$files = Get-ChildItem "src/main/java/fr/louis/practice/models/*.java" -Recurse

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    
    # Ignorer les fichiers déjà corrigés (sans @Data)
    if ($content -notmatch '@Data') {
        continue
    }
    
    Write-Host "Processing: $($file.Name)"
    
    # Supprimer les imports Lombok
    $content = $content -replace 'import lombok\.Data;\r?\n', ''
    $content = $content -replace 'import lombok\.Getter;\r?\n', ''
    $content = $content -replace 'import lombok\.Setter;\r?\n', ''
    
    # Supprimer @Data, @Getter, @Setter
    $content = $content -replace '@Data\r?\n', ''
    $content = $content -replace '@Getter\r?\n', ''
    $content = $content -replace '@Setter\r?\n', ''
    
    Set-Content $file.FullName -Value $content
    Write-Host "  Removed Lombok annotations from $($file.Name)"
}

Write-Host "Done! Lombok annotations removed from all model files."
Write-Host "Note: You need to manually add getters/setters where needed."
