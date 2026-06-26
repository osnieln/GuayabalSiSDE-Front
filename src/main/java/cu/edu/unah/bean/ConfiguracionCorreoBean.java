package cu.edu.unah.bean;

import cu.edu.unah.rest.RestConfiguracionCorreo;
import cu.edu.unah.util.ConfiguracionCorreoDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.Map;

@Named
@Getter
@Setter
@ViewScoped
public class ConfiguracionCorreoBean implements Serializable {

    private String host = "smtp.gmail.com";
    private int puerto = 587;
    private String usuario = "";
    private String contrasena = "";
    private String remitente = "";

    private final RestConfiguracionCorreo rest = new RestConfiguracionCorreo();

    @PostConstruct
    public void init() {
        try {
            ConfiguracionCorreoDTO cfg = rest.getConfig();
            if (cfg != null) {
                host = cfg.getHost();
                puerto = cfg.getPuerto() > 0 ? cfg.getPuerto() : 587;
                usuario = cfg.getUsuario();
                contrasena = cfg.getContrasena();
                remitente = cfg.getRemitente();
            }
        } catch (Exception e) {
            // backend no disponible al cargar — se usan los valores por defecto
        }
    }

    public void guardar() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            ConfiguracionCorreoDTO dto = new ConfiguracionCorreoDTO(host, puerto, usuario, contrasena, remitente);
            Map<String, String> r = rest.saveConfig(dto);
            if (r != null && "ok".equals(r.get("status"))) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Configuración guardada", r.get("mensaje")));
                init();
            } else {
                String msg = r != null ? r.get("mensaje") : "No se pudo conectar con el servidor.";
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error al guardar", msg));
            }
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al guardar", e.getMessage()));
        }
        PrimeFaces.current().ajax().update("correoForm:growl");
    }

    public void probarConexion() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            ConfiguracionCorreoDTO dto = new ConfiguracionCorreoDTO(host, puerto, usuario, contrasena, remitente);
            Map<String, String> r = rest.testConexion(dto);
            if (r != null && "ok".equals(r.get("status"))) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Conexión exitosa", r.get("mensaje")));
            } else {
                String msg = r != null ? r.get("mensaje") : "No se pudo conectar al servidor SMTP.";
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Conexión fallida", msg));
            }
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error de conexión", e.getMessage()));
        }
        PrimeFaces.current().ajax().update("correoForm:growl");
    }
}
