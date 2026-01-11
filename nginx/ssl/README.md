# ==============================================================================
# README - Configuração SSL/HTTPS
# ==============================================================================
#
# Para habilitar HTTPS, você precisa de certificados SSL. Existem duas opções:
#
# OPÇÃO 1: Let's Encrypt (Gratuito e Recomendado)
# ----------------------------------------------
# 1. Instale o Certbot no servidor:
#    sudo apt-get update
#    sudo apt-get install certbot python3-certbot-nginx
#
# 2. Obtenha o certificado (substitua seu-dominio.com):
#    sudo certbot --nginx -d seu-dominio.com -d www.seu-dominio.com
#
# 3. Certbot irá configurar automaticamente o Nginx
#
# 4. Renovação automática (já configurada pelo Certbot):
#    sudo certbot renew --dry-run
#
# OPÇÃO 2: Certificado Auto-assinado (Apenas para testes)
# --------------------------------------------------------
# 1. Gere o certificado:
#    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
#      -keyout ./nginx/ssl/key.pem \
#      -out ./nginx/ssl/cert.pem \
#      -subj "/C=BR/ST=State/L=City/O=Organization/CN=45.55.186.12"
#
# 2. Descomente a seção HTTPS no arquivo conf.d/default.conf
#
# 3. Reinicie o Nginx:
#    docker-compose -f docker-compose.prod.yml restart nginx-proxy
#
# NOTA: Certificados auto-assinados exibirão aviso de segurança no navegador.
# Use apenas para testes. Para produção, use Let's Encrypt.
#
# ==============================================================================
