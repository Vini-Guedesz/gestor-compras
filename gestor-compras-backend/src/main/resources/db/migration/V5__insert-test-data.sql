-- Migration para inserir dados de teste
-- Autor: Sistema
-- Data: 2025-10-10
-- Descrição: Insere dados de teste para demonstração do sistema

-- 1. Inserir Endereços
INSERT INTO endereco (cep, estado, cidade, bairro, rua, numero, complemento) VALUES
('65020-000', 'MA', 'São Luís', 'Centro', 'Rua do Comércio', '123', NULL),
('65030-000', 'MA', 'São Luís', 'Renascença', 'Av. dos Bandeirantes', '456', 'Sala 201'),
('65040-000', 'MA', 'São Luís', 'Jardim Renascença', 'Rua das Flores', '789', NULL),
('65050-000', 'MA', 'São Luís', 'Cohama', 'Rua da Tecnologia', '321', 'Galpão 5'),
('65060-000', 'MA', 'São Luís', 'Anil', 'Av. São Luís Rei de França', '555', NULL),
('65070-000', 'MA', 'São Luís', 'Turu', 'Rua Grande', '888', 'Loja A'),
('65075-000', 'MA', 'São Luís', 'Vila Palmeira', 'Rua dos Prazeres', '111', NULL),
('65080-000', 'MA', 'São Luís', 'São Francisco', 'Av. dos Portugueses', '200', 'Galpão 3'),
('65085-000', 'MA', 'São Luís', 'Vinhais', 'Rua do Empresário', '777', 'Sala 305'),
('65090-000', 'MA', 'São Luís', 'Calhau', 'Av. Litorânea', '999', NULL);

-- 2. Inserir Contatos
INSERT INTO contato (email, telefone_fixo, celular) VALUES
('contato@papelariasaoluis.com.br', '(98) 3234-5678', '(98) 98765-4321'),
('vendas@digitalinfo.com.br', '(98) 3245-9876', '(98) 99876-5432'),
('contato@moveisnordeste.com.br', '(98) 3256-1234', '(98) 91234-5678'),
('comercial@techsolutions.com.br', '(98) 3267-4567', '(98) 92345-6789'),
('vendas@officepro.com.br', '(98) 3278-9012', '(98) 93456-7890'),
('atendimento@superlimpa.com.br', '(98) 3289-0123', '(98) 94567-8901'),
('comercial@construmax.com.br', '(98) 3290-1234', '(98) 95678-9012'),
('vendas@eletroparts.com.br', '(98) 3201-2345', '(98) 96789-0123'),
('contato@megasuprimentos.com.br', '(98) 3212-3456', '(98) 97890-1234'),
('sac@distribuidoranorte.com.br', '(98) 3223-4567', '(98) 98901-2345');

-- 3. Inserir Fornecedores de Produto
INSERT INTO fornecedor_de_produto (razao_social, cnpj, endereco_id, contato_id, inscricao_estadual) VALUES
('Papelaria São Luís LTDA', '12345678000190', 1, 1, '123456789'),
('Informática Digital LTDA', '98765432000111', 2, 2, '987654321'),
('Móveis e Equipamentos Nordeste LTDA', '11222333000144', 3, 3, '112233445'),
('Tech Solutions Brasil LTDA', '22333444000155', 4, 4, '223334455'),
('Office Pro Distribuidora LTDA', '33444555000166', 5, 5, '334445556'),
('Super Limpa Produtos de Limpeza LTDA', '44555666000177', 6, 6, '445556667'),
('Construmax Materiais LTDA', '55666777000188', 7, 7, '556667778'),
('Eletro Parts Componentes LTDA', '66777888000199', 8, 8, '667778889'),
('Mega Suprimentos Corporativos LTDA', '77888999000100', 9, 9, '778889990'),
('Distribuidora Norte Comércio LTDA', '88999000000111', 10, 10, '889990001');

-- 4. Inserir Solicitações de Pedido
INSERT INTO solicitacao_de_pedido (observacao, data_criacao, status) VALUES
('Material de escritório para departamento administrativo', '2025-09-15 10:00:00', 'APROVADO'),
('Equipamentos de informática para setor de TI', '2025-09-20 14:30:00', 'APROVADO'),
('Mobiliário para nova sala de reuniões', '2025-09-25 09:15:00', 'EM_ANALISE'),
('Material de limpeza e higiene', '2025-10-01 11:00:00', 'PENDENTE'),
('Equipamentos de segurança e EPI', '2025-10-03 08:45:00', 'APROVADO'),
('Material elétrico para manutenção', '2025-10-05 13:20:00', 'EM_ANALISE'),
('Suprimentos de informática diversos', '2025-10-06 16:00:00', 'APROVADO'),
('Materiais de construção e ferramentas', '2025-10-07 10:30:00', 'PENDENTE'),
('Equipamentos audiovisuais para auditório', '2025-10-08 14:15:00', 'EM_ANALISE'),
('Material de copa e cozinha', '2025-10-09 09:00:00', 'APROVADO');

-- 5. Inserir Itens de Pedido
INSERT INTO item_pedido (nome, quantidade, descricao, observacao, solicitacao_de_pedido_id) VALUES
-- Itens da Solicitação 1 (Material de escritório)
('Papel A4 - Resma 500 folhas', 50, 'Papel sulfite branco A4, resma com 500 folhas, gramatura 75g/m²', 'Preferência para marca reconhecida no mercado', 1),
('Canetas esferográficas azuis', 200, 'Canetas esferográficas cor azul, ponta média 1.0mm', 'Solicitar embalagem com 50 unidades', 1),
('Pastas suspensas', 100, 'Pastas suspensas tamanho ofício, cor kraft', NULL, 1),
('Grampeadores', 20, 'Grampeador de mesa, capacidade 25 folhas', NULL, 1),

-- Itens da Solicitação 2 (Equipamentos de TI)
('Notebook Dell Inspiron 15', 10, 'Notebook Dell Inspiron 15, Intel Core i7, 16GB RAM, SSD 512GB', 'Incluir mouse sem fio e case de transporte', 2),
('Monitor LG 24 polegadas', 15, 'Monitor LG LED 24 polegadas, Full HD, HDMI', 'Preferência por modelos com regulagem de altura', 2),
('Teclado e Mouse sem fio Logitech', 20, 'Kit teclado e mouse sem fio Logitech', NULL, 2),
('Impressora multifuncional HP', 3, 'Impressora HP LaserJet multifuncional, colorida', 'Com WiFi e impressão duplex', 2),

-- Itens da Solicitação 3 (Mobiliário)
('Mesa para reunião 8 lugares', 2, 'Mesa de reunião em MDF, capacidade para 8 pessoas, cor amadeirada', 'Dimensões aproximadas: 2,40m x 1,20m', 3),
('Cadeiras giratórias executivas', 16, 'Cadeiras giratórias executivas com braços, assento e encosto em courino', 'Preferência cor preta, com regulagem de altura', 3),
('Armário de escritório', 4, 'Armário alto com 2 portas, cor branca, 4 prateleiras', NULL, 3),

-- Itens da Solicitação 4 (Material de limpeza)
('Papel higiênico', 100, 'Papel higiênico folha dupla, pacote com 4 rolos', NULL, 4),
('Sabonete líquido', 50, 'Sabonete líquido para mãos, 500ml', 'Preferência fragrância suave', 4),
('Álcool em gel', 30, 'Álcool em gel 70%, frasco 500ml', NULL, 4),
('Detergente neutro', 40, 'Detergente neutro 500ml', NULL, 4),
('Desinfetante', 35, 'Desinfetante bactericida 2L', 'Preferência fragrância lavanda', 4),

-- Itens da Solicitação 5 (Equipamentos de segurança e EPI)
('Capacetes de segurança', 30, 'Capacete de segurança tipo aba frontal, com carneira', 'Cor branca, conforme NR-6', 5),
('Luvas de proteção', 100, 'Luvas de proteção em raspa, tamanho G', 'Para trabalhos pesados', 5),
('Óculos de proteção', 50, 'Óculos de proteção incolor, anti-embaçante', NULL, 5),
('Botas de segurança', 25, 'Bota de segurança PVC cano médio', 'Diversos tamanhos', 5),
('Extintores de incêndio', 10, 'Extintor de incêndio ABC 6kg', 'Com suporte de parede', 5),

-- Itens da Solicitação 6 (Material elétrico)
('Lâmpadas LED 12W', 100, 'Lâmpada LED bulbo 12W luz branca E27', NULL, 6),
('Tomadas 10A', 50, 'Tomada 10A padrão novo (NBR 14136)', 'Cor branca', 6),
('Interruptores simples', 40, 'Interruptor simples 10A', 'Cor branca', 6),
('Disjuntores 20A', 20, 'Disjuntor monopolar 20A curva C', NULL, 6),
('Cabos elétricos 2,5mm', 10, 'Cabo de cobre flexível 2,5mm² (rolo 100m)', 'Cor azul', 6),

-- Itens da Solicitação 7 (Suprimentos de informática)
('Cartuchos HP 664 preto', 15, 'Cartucho de tinta HP 664 preto original', NULL, 7),
('Cartuchos HP 664 colorido', 15, 'Cartucho de tinta HP 664 colorido original', NULL, 7),
('Pen drives 32GB', 50, 'Pen drive USB 3.0 32GB', 'Preferencialmente Kingston ou Sandisk', 7),
('HDs externos 1TB', 10, 'HD externo portátil 1TB USB 3.0', NULL, 7),
('Cabos HDMI 2m', 30, 'Cabo HDMI 2.0 de 2 metros', 'Alta velocidade', 7),
('Mouse pad', 40, 'Mouse pad ergonômico com apoio', NULL, 7),

-- Itens da Solicitação 8 (Materiais de construção)
('Cimento CP-II 50kg', 40, 'Cimento Portland CP-II-F-32', NULL, 8),
('Areia média (m³)', 5, 'Areia média lavada para construção', NULL, 8),
('Tijolos cerâmicos', 2000, 'Tijolo cerâmico 6 furos 9x14x19cm', NULL, 8),
('Furadeira de impacto', 5, 'Furadeira de impacto 1/2 pol 650W', 'Com maleta e kit de brocas', 8),
('Parafusadeira', 8, 'Parafusadeira/Furadeira a bateria 12V', 'Com 2 baterias', 8),

-- Itens da Solicitação 9 (Equipamentos audiovisuais)
('Projetor Full HD', 2, 'Projetor multimídia Full HD 3500 lumens', 'Com controle remoto e cabos', 9),
('Tela de projeção 100"', 2, 'Tela de projeção retrátil 100 polegadas', 'Fixação em parede', 9),
('Caixas de som amplificadas', 4, 'Caixa de som amplificada 150W', 'Com suporte', 9),
('Microfones sem fio', 6, 'Microfone sem fio UHF profissional', 'Com base carregadora', 9),
('Mesa de som', 1, 'Mesa de som 12 canais com efeitos', 'Com case de transporte', 9),

-- Itens da Solicitação 10 (Material de copa e cozinha)
('Copos descartáveis 200ml', 50, 'Copo descartável transparente 200ml (pacote com 100 unidades)', NULL, 10),
('Guardanapos', 30, 'Guardanapos de papel folha dupla (pacote com 50 unidades)', NULL, 10),
('Café em pó', 20, 'Café torrado e moído tradicional 500g', 'Preferência marcas premium', 10),
('Açúcar', 15, 'Açúcar cristal 1kg', NULL, 10),
('Filtro de papel', 40, 'Filtro de papel nº 103 (pacote com 30 unidades)', NULL, 10),
('Garrafa térmica', 10, 'Garrafa térmica 1 litro em inox', NULL, 10);

-- 6. Inserir Cotações
INSERT INTO cotacao (item_pedido_id, fornecedor_produto_id, preco, prazo_entrega, data_cotacao) VALUES
-- Cotações para Papel A4 (item 1)
(1, 1, 28.50, '2025-09-21', '2025-09-16'),
(1, 5, 27.90, '2025-09-23', '2025-09-16'),
(1, 9, 26.80, '2025-09-22', '2025-09-16'),

-- Cotações para Canetas (item 2)
(2, 1, 0.85, '2025-09-19', '2025-09-16'),
(2, 5, 0.79, '2025-09-21', '2025-09-16'),
(2, 9, 0.82, '2025-09-20', '2025-09-16'),

-- Cotações para Pastas (item 3)
(3, 1, 2.50, '2025-09-21', '2025-09-16'),
(3, 5, 2.35, '2025-09-22', '2025-09-16'),

-- Cotações para Grampeadores (item 4)
(4, 1, 15.90, '2025-09-23', '2025-09-17'),
(4, 5, 14.50, '2025-09-24', '2025-09-17'),
(4, 9, 15.20, '2025-09-23', '2025-09-17'),

-- Cotações para Notebooks (item 5)
(5, 2, 3850.00, '2025-10-05', '2025-09-21'),
(5, 4, 3720.00, '2025-10-10', '2025-09-21'),
(5, 9, 3680.00, '2025-10-08', '2025-09-21'),

-- Cotações para Monitores (item 6)
(6, 2, 890.00, '2025-10-01', '2025-09-21'),
(6, 4, 850.00, '2025-10-03', '2025-09-21'),
(6, 8, 835.00, '2025-10-02', '2025-09-21'),

-- Cotações para Teclado e Mouse (item 7)
(7, 2, 145.00, '2025-09-28', '2025-09-21'),
(7, 4, 138.00, '2025-10-01', '2025-09-21'),
(7, 9, 142.00, '2025-09-29', '2025-09-21'),

-- Cotações para Impressora (item 8)
(8, 2, 2450.00, '2025-10-05', '2025-09-22'),
(8, 4, 2380.00, '2025-10-09', '2025-09-22'),
(8, 9, 2420.00, '2025-10-07', '2025-09-22'),

-- Cotações para Mesa de Reunião (item 9)
(9, 3, 2450.00, '2025-10-20', '2025-09-26'),
(9, 10, 2350.00, '2025-10-22', '2025-09-26'),

-- Cotações para Cadeiras (item 10)
(10, 3, 580.00, '2025-10-15', '2025-09-26'),
(10, 10, 555.00, '2025-10-17', '2025-09-26'),

-- Cotações para Armário (item 11)
(11, 3, 950.00, '2025-10-17', '2025-09-26'),
(11, 10, 920.00, '2025-10-18', '2025-09-26'),

-- Cotações para Papel Higiênico (item 12)
(12, 1, 8.90, '2025-10-06', '2025-10-02'),
(12, 6, 8.50, '2025-10-07', '2025-10-02'),
(12, 10, 8.70, '2025-10-06', '2025-10-02'),

-- Cotações para Sabonete (item 13)
(13, 1, 12.50, '2025-10-06', '2025-10-02'),
(13, 6, 11.90, '2025-10-07', '2025-10-02'),

-- Cotações para Álcool em Gel (item 14)
(14, 1, 15.80, '2025-10-04', '2025-10-02'),
(14, 6, 14.90, '2025-10-08', '2025-10-02'),
(14, 10, 15.20, '2025-10-06', '2025-10-02'),

-- Cotações para Detergente (item 15)
(15, 6, 3.50, '2025-10-05', '2025-10-02'),
(15, 10, 3.40, '2025-10-06', '2025-10-02'),

-- Cotações para Desinfetante (item 16)
(16, 6, 8.90, '2025-10-05', '2025-10-02'),
(16, 10, 8.70, '2025-10-06', '2025-10-02'),

-- Cotações para Capacetes (item 17)
(17, 7, 45.00, '2025-10-10', '2025-10-04'),
(17, 10, 42.50, '2025-10-12', '2025-10-04'),

-- Cotações para Luvas (item 18)
(18, 7, 8.50, '2025-10-10', '2025-10-04'),
(18, 10, 7.90, '2025-10-11', '2025-10-04'),

-- Cotações para Óculos (item 19)
(19, 7, 12.00, '2025-10-10', '2025-10-04'),
(19, 10, 11.50, '2025-10-11', '2025-10-04'),

-- Cotações para Botas (item 20)
(20, 7, 85.00, '2025-10-12', '2025-10-04'),
(20, 10, 82.00, '2025-10-13', '2025-10-04'),

-- Cotações para Extintores (item 21)
(21, 7, 180.00, '2025-10-15', '2025-10-04'),

-- Cotações para Lâmpadas LED (item 22)
(22, 8, 12.50, '2025-10-10', '2025-10-06'),
(22, 10, 11.90, '2025-10-11', '2025-10-06'),

-- Cotações para Tomadas (item 23)
(23, 8, 8.50, '2025-10-10', '2025-10-06'),
(23, 10, 8.20, '2025-10-11', '2025-10-06'),

-- Cotações para Interruptores (item 24)
(24, 8, 6.50, '2025-10-10', '2025-10-06'),
(24, 10, 6.20, '2025-10-11', '2025-10-06'),

-- Cotações para Disjuntores (item 25)
(25, 8, 25.00, '2025-10-12', '2025-10-06'),

-- Cotações para Cabos elétricos (item 26)
(26, 8, 280.00, '2025-10-15', '2025-10-06'),

-- Cotações para Cartuchos HP preto (item 27)
(27, 2, 85.00, '2025-10-12', '2025-10-07'),
(27, 4, 82.00, '2025-10-14', '2025-10-07'),
(27, 9, 83.50, '2025-10-13', '2025-10-07'),

-- Cotações para Cartuchos HP colorido (item 28)
(28, 2, 95.00, '2025-10-12', '2025-10-07'),
(28, 4, 92.00, '2025-10-14', '2025-10-07'),
(28, 9, 93.50, '2025-10-13', '2025-10-07'),

-- Cotações para Pen drives (item 29)
(29, 2, 28.00, '2025-10-11', '2025-10-07'),
(29, 4, 26.50, '2025-10-13', '2025-10-07'),
(29, 9, 27.00, '2025-10-12', '2025-10-07'),

-- Cotações para HDs externos (item 30)
(30, 2, 380.00, '2025-10-15', '2025-10-07'),
(30, 4, 365.00, '2025-10-17', '2025-10-07'),

-- Cotações para Cabos HDMI (item 31)
(31, 2, 25.00, '2025-10-11', '2025-10-07'),
(31, 8, 22.00, '2025-10-12', '2025-10-07'),
(31, 9, 23.50, '2025-10-11', '2025-10-07'),

-- Cotações para Mouse pad (item 32)
(32, 2, 18.00, '2025-10-11', '2025-10-07'),
(32, 9, 16.50, '2025-10-12', '2025-10-07'),

-- Cotações para Cimento (item 33)
(33, 7, 32.00, '2025-10-12', '2025-10-08'),

-- Cotações para Areia (item 34)
(34, 7, 120.00, '2025-10-12', '2025-10-08'),

-- Cotações para Tijolos (item 35)
(35, 7, 0.85, '2025-10-15', '2025-10-08'),

-- Cotações para Furadeira (item 36)
(36, 7, 280.00, '2025-10-14', '2025-10-08'),
(36, 10, 270.00, '2025-10-15', '2025-10-08'),

-- Cotações para Parafusadeira (item 37)
(37, 7, 320.00, '2025-10-14', '2025-10-08'),
(37, 10, 310.00, '2025-10-15', '2025-10-08'),

-- Cotações para Projetor (item 38)
(38, 2, 2850.00, '2025-10-18', '2025-10-09'),
(38, 4, 2750.00, '2025-10-20', '2025-10-09'),

-- Cotações para Tela de projeção (item 39)
(39, 2, 580.00, '2025-10-18', '2025-10-09'),
(39, 9, 550.00, '2025-10-19', '2025-10-09'),

-- Cotações para Caixas de som (item 40)
(40, 2, 450.00, '2025-10-17', '2025-10-09'),
(40, 8, 420.00, '2025-10-18', '2025-10-09'),

-- Cotações para Microfones (item 41)
(41, 2, 380.00, '2025-10-17', '2025-10-09'),
(41, 8, 365.00, '2025-10-18', '2025-10-09'),

-- Cotações para Mesa de som (item 42)
(42, 2, 1850.00, '2025-10-20', '2025-10-09'),
(42, 8, 1750.00, '2025-10-21', '2025-10-09'),

-- Cotações para Copos descartáveis (item 43)
(43, 1, 5.50, '2025-10-13', '2025-10-10'),
(43, 6, 5.20, '2025-10-14', '2025-10-10'),
(43, 10, 5.30, '2025-10-13', '2025-10-10'),

-- Cotações para Guardanapos (item 44)
(44, 1, 3.80, '2025-10-13', '2025-10-10'),
(44, 10, 3.60, '2025-10-14', '2025-10-10'),

-- Cotações para Café (item 45)
(45, 10, 18.50, '2025-10-13', '2025-10-10'),

-- Cotações para Açúcar (item 46)
(46, 10, 4.50, '2025-10-13', '2025-10-10'),

-- Cotações para Filtro de papel (item 47)
(47, 1, 6.80, '2025-10-13', '2025-10-10'),
(47, 10, 6.50, '2025-10-14', '2025-10-10'),

-- Cotações para Garrafa térmica (item 48)
(48, 3, 85.00, '2025-10-15', '2025-10-10'),
(48, 10, 78.00, '2025-10-16', '2025-10-10');
