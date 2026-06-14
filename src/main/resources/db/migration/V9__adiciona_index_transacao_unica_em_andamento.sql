CREATE UNIQUE INDEX uk_caminhao_em_andamento
ON transacao_transporte (caminhao_id)
WHERE status = 'EM_ANDAMENTO';