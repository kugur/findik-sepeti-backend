package com.kolip.findiksepeti.user;

public enum Roles {
    User("ROLE_USER"),
    Pre_User("ROLE_PRE_USER"),
    Admin("ROLE_ADMIN");

    private String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
