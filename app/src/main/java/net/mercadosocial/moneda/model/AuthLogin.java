package net.mercadosocial.moneda.model;

/**
 * Created by julio on 1/02/18.
 */

public class AuthLogin {

    public static String API_KEY;

    private String username; //juliotest
    private String password; //boniatos

    public AuthLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
