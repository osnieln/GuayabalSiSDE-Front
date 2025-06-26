package cu.edu.unah.bean;

import cu.edu.unah.rest.RestUsers;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.List;

@SessionScoped
@Named
@Data
public class TemplateBean implements Serializable {

    RestUsers restUsers = new RestUsers();

    String username;
    boolean role_admin, role_director, role_trabajador, role_tecnico;
    List<String> roles;

    public String translateMessage(String message) {
        if (message == null)
            return "";
        else if (message.equals("Bad credentials"))
            return "El usuario o la contraseña son incorrectos";
        else if (message.equals("User is disabled"))
            return "El usuario se encuentra deshabilitado";
        else
            return "Se ha producido un error al establecer conexión con el servidor de autenticación.";
    }

    public String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public void init() {
        username = getCurrentUser();
        roles = restUsers.findAuthoritiesByUsername(username);
        verifyRol();
    }

    public void verifyRol() {
        for (String l : roles) {
            switch (l) {
                case "ROLE_ADMIN" -> {
                    role_admin = true;
                    break;
                }
                case "ROLE_DIRECTOR" -> {
                    role_director = true;
                    break;
                }
                case "ROLE_TRABAJADOR" -> {
                    role_trabajador = true;
                    break;
                }
                case "ROLE_TECNICO" -> {
                    role_tecnico = true;
                    break;
                }
                default -> {}
            }
        }
    }

    public boolean translateRole_Boolean(boolean... roles) {
        boolean rolesSum = false;
        for (boolean b : roles) {
            rolesSum |= b;
        }
        return rolesSum;
    }

    public String translateRole_visible(boolean... roles) {
        if (translateRole_Boolean(roles))
            return "";
        return "display: none;";
    }
}
