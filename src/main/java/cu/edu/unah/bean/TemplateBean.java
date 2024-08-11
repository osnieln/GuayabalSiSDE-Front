package cu.edu.unah.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

@SessionScoped
@Named
@Data
public class TemplateBean implements Serializable {

    String username;
    String nombreApp;
    String nombreAppToShow;

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
//        nombreAppToShow = "noName";
//        nombreApp = "";
    }

    public void changeName(){
        nombreAppToShow = nombreApp;
    }
}
