# Fix UUIDs in JasperReport files
$files = @(
    "src/main/resources/relatorios/dashboard_executivo.jrxml",
    "src/main/resources/relatorios/itens_mais_solicitados.jrxml",
    "src/main/resources/relatorios/comparativo_cotacao.jrxml",
    "src/main/resources/relatorios/solicitacoes_abertas.jrxml",
    "src/main/resources/relatorios/pedidos_fechados.jrxml"
)

foreach ($file in $files) {
    Write-Host "Processing $file..."
    $content = Get-Content $file -Raw -Encoding UTF8

    # Replace all invalid UUIDs (those ending in -uuid) with new valid UUIDs
    $content = $content -replace 'uuid="[^"]*-uuid"', {
        param($match)
        $newUuid = [guid]::NewGuid().ToString()
        return "uuid=`"$newUuid`""
    }

    # Save the file
    Set-Content $file -Value $content -Encoding UTF8 -NoNewline
    Write-Host "Fixed $file"
}

Write-Host "Done!"
