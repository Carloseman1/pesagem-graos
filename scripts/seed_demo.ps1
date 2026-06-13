$base = "http://localhost:8080/api/v1"

Write-Host "Cadastrando filial..."
$filial = Invoke-RestMethod -Method Post -Uri "$base/filiais" -ContentType "application/json" -Body '{"nome":"Filial Centro","cidade":"Campinas","estado":"SP"}'

Write-Host "Cadastrando tipo de grao..."
$grao = Invoke-RestMethod -Method Post -Uri "$base/tipos-grao" -ContentType "application/json" -Body '{"nome":"Soja","precoComprarPorTonelada":120.00,"estoqueAtualToneladas":50.00,"estoqueMaximoToneladas":100.00}'

Write-Host "Cadastrando caminhao..."
$caminhao = Invoke-RestMethod -Method Post -Uri "$base/caminhoes" -ContentType "application/json" -Body '{"placa":"ABC1D23","tara":8000.00,"motorista":"Joao Silva"}'

Write-Host "Cadastrando balanca..."
$balanca = Invoke-RestMethod -Method Post -Uri "$base/balancas" -ContentType "application/json" -Body (@{identificador="balanca-01";apiKey="chave-da-balanca-1";idFilial=$filial.id} | ConvertTo-Json)

Write-Host "Abrindo transacao..."
$transacao = Invoke-RestMethod -Method Post -Uri "$base/transacoes" -ContentType "application/json" -Body (@{placaCaminhao="ABC1D23";idTipoGrao=$grao.id;idFilial=$filial.id} | ConvertTo-Json)

Write-Host "Pronto. Transacao id=$($transacao.id) status=$($transacao.status)"
