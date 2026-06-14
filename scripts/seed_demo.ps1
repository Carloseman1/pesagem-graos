$base = "http://localhost:8080/api/v1"

Write-Host "Cadastrando filial..."
$filial = Invoke-RestMethod -Method Post -Uri "$base/filiais" -ContentType "application/json" -Body '{"nome":"Filial Norte","cidade":"Ribeirao Preto","estado":"SP"}'

Write-Host "Cadastrando tipo de grao..."
$grao = Invoke-RestMethod -Method Post -Uri "$base/tipos-grao" -ContentType "application/json" -Body '{"nome":"Milho","precoComprarPorTonelada":85.00,"estoqueAtualToneladas":10.00,"estoqueMaximoToneladas":100.00}'

Write-Host "Cadastrando caminhao..."
$caminhao = Invoke-RestMethod -Method Post -Uri "$base/caminhoes" -ContentType "application/json" -Body '{"placa":"XYZ9A87","tara":9500.00,"motorista":"Carlos Souza"}'

Write-Host "Cadastrando balanca..."
$balanca = Invoke-RestMethod -Method Post -Uri "$base/balancas" -ContentType "application/json" -Body (@{identificador="balanca-02";apiKey="chave-da-balanca-2";idFilial=$filial.id} | ConvertTo-Json)

Write-Host "Abrindo transacao..."
$transacao = Invoke-RestMethod -Method Post -Uri "$base/transacoes" -ContentType "application/json" -Body (@{placaCaminhao="XYZ9A87";idTipoGrao=$grao.id;idFilial=$filial.id} | ConvertTo-Json)

Write-Host "Pronto. Transacao id=$($transacao.id) status=$($transacao.status)"