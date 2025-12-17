package com.gestordecompras.gestorcomprasbackend.model.user;

/**
 * Enum que define os papéis (roles) disponíveis para usuários do sistema.
 *
 * <p><b>Roles disponíveis:</b></p>
 * <ul>
 *   <li><b>ADMIN:</b> Administrador do sistema - acesso total, incluindo gerenciamento de usuários</li>
 *   <li><b>USUARIO:</b> Usuário padrão - acesso básico ao sistema</li>
 *   <li><b>COMPRADOR:</b> Usuário com permissões para criar e gerenciar pedidos de compra</li>
 *   <li><b>APROVADOR:</b> Usuário com permissões para aprovar pedidos de compra</li>
 * </ul>
 *
 * @since 1.0.0
 */
public enum UserRole {
    ADMIN("admin"),
    USUARIO("usuario"),
    COMPRADOR("comprador"),
    APROVADOR("aprovador");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
