INSERT INTO professionals (
  public_id, name, business_name, description, phone, whatsapp, email,
  instagram, city, state, active, created_at, updated_at
) VALUES
  ('Pro000000000000000001', 'Carlos Elétrica Residencial', 'Carlos Elétrica Demo', 'Serviços fictícios de instalações e reparos elétricos residenciais.', '+55 00 00000-0001', '5500000000001', 'eletrica@example.com', 'https://instagram.com/bloquinho_demo_eletrica', 'Campinas', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Pro000000000000000002', 'Lumen Instalações', 'Lumen Instalações Demo', 'Demonstração de serviços elétricos e instalação de climatização.', '+55 00 00000-0002', '5500000000002', 'lumen@example.com', 'https://instagram.com/bloquinho_demo_lumen', 'Campinas', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Pro000000000000000003', 'HidroCamp Serviços', 'HidroCamp Demo', 'Serviços fictícios de manutenção hidráulica residencial.', '+55 00 00000-0003', '5500000000003', 'hidrocamp@example.com', 'https://instagram.com/bloquinho_demo_hidrocamp', 'Campinas', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Pro000000000000000004', 'Pintura Nova Casa', 'Nova Casa Pinturas Demo', 'Demonstração de pintura de ambientes internos e externos.', '+55 00 00000-0004', '5500000000004', 'pintura@example.com', 'https://instagram.com/bloquinho_demo_pintura', 'Valinhos', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Pro000000000000000005', 'Madeira e Forma', 'Madeira e Forma Demo', 'Projetos fictícios de marcenaria e pequenos reparos em madeira.', '+55 00 00000-0005', '5500000000005', 'marcenaria@example.com', 'https://instagram.com/bloquinho_demo_madeira', 'Vinhedo', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Pro000000000000000006', 'Mármores Modelo', 'Mármores Modelo Demo', 'Demonstração de bancadas e revestimentos em pedras.', '+55 00 00000-0006', '5500000000006', 'marmores@example.com', 'https://instagram.com/bloquinho_demo_marmores', 'Campinas', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Pro000000000000000007', 'Piso Certo Campinas', 'Piso Certo Demo', 'Instalação fictícia de pisos e revestimentos para demonstração.', '+55 00 00000-0007', '5500000000007', 'pisos@example.com', 'https://instagram.com/bloquinho_demo_pisos', 'Campinas', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Pro000000000000000008', 'Reforma Ideal', 'Reforma Ideal Demo', 'Demonstração de construção, reforma, pintura e acabamento.', '+55 00 00000-0008', '5500000000008', 'reforma@example.com', 'https://instagram.com/bloquinho_demo_reforma', 'Hortolândia', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Pro000000000000000009', 'ClimaLar Instalações', 'ClimaLar Demo', 'Serviços fictícios de instalação e manutenção de ar-condicionado.', '+55 00 00000-0009', '5500000000009', 'climalar@example.com', NULL, 'Campinas', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Pro000000000000000010', 'Jardim Vivo Paisagismo', 'Jardim Vivo Demo', 'Planejamento fictício de jardins e manutenção de áreas verdes.', '+55 00 00000-0010', '5500000000010', 'jardim@example.com', 'https://instagram.com/bloquinho_demo_jardim', 'Paulínia', 'SP', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP
FROM professionals p
JOIN professional_categories c ON c.slug = 'eletrica'
WHERE p.public_id IN ('Pro000000000000000001', 'Pro000000000000000002');

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP FROM professionals p
JOIN professional_categories c ON c.slug = 'hidraulica'
WHERE p.public_id = 'Pro000000000000000003';

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP FROM professionals p
JOIN professional_categories c ON c.slug = 'pintura'
WHERE p.public_id IN ('Pro000000000000000004', 'Pro000000000000000008');

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP FROM professionals p
JOIN professional_categories c ON c.slug = 'marcenaria'
WHERE p.public_id = 'Pro000000000000000005';

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP FROM professionals p
JOIN professional_categories c ON c.slug = 'marmoraria'
WHERE p.public_id = 'Pro000000000000000006';

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP FROM professionals p
JOIN professional_categories c ON c.slug = 'pisos-e-revestimentos'
WHERE p.public_id IN ('Pro000000000000000007', 'Pro000000000000000008');

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP FROM professionals p
JOIN professional_categories c ON c.slug = 'construcao-e-reforma'
WHERE p.public_id = 'Pro000000000000000008';

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP FROM professionals p
JOIN professional_categories c ON c.slug = 'telhados-e-calhas'
WHERE p.public_id = 'Pro000000000000000008';

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP FROM professionals p
JOIN professional_categories c ON c.slug = 'ar-condicionado'
WHERE p.public_id IN ('Pro000000000000000002', 'Pro000000000000000009');

INSERT INTO professional_category_links (professional_id, category_id, created_at)
SELECT p.id, c.id, CURRENT_TIMESTAMP FROM professionals p
JOIN professional_categories c ON c.slug = 'paisagismo'
WHERE p.public_id = 'Pro000000000000000010';
