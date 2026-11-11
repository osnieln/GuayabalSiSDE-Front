package cu.edu.unah.bean;

import cu.edu.unah.rest.RestAlertas;
import cu.edu.unah.util.AlertaResponse;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Named("alertasBean")
@ViewScoped
@Data
public class AlertasBean implements Serializable {

    private List<AlertaResponse> alertas;
    private boolean soloNoLeidas = false;

    private final RestAlertas restAlertas = new RestAlertas();

    @PostConstruct
    public void init() {
        cargarAlertas();
    }

    public void cargarAlertas() {
        alertas = restAlertas.findAll(soloNoLeidas);
    }

    public void generarAhora() {
        restAlertas.generar();
        cargarAlertas();
        mensaje("Alertas actualizadas desde el sistema.");
    }

    public void marcarLeida(AlertaResponse alerta) {
        if (restAlertas.marcarLeida(alerta.getId())) {
            cargarAlertas();
        }
    }

    public void marcarResuelta(AlertaResponse alerta) {
        if (restAlertas.marcarResuelta(alerta.getId())) {
            cargarAlertas();
            mensaje("Alerta marcada como resuelta.");
        }
    }

    public long getTotalCultivosPorVencer() {
        if (alertas == null) return 0;
        return alertas.stream().filter(a -> "CULTIVO_POR_VENCER".equals(a.getTipo())).count();
    }

    public long getTotalRiegoPendiente() {
        if (alertas == null) return 0;
        return alertas.stream().filter(a -> "RIEGO_PENDIENTE".equals(a.getTipo())).count();
    }

    public long getTotalBajoStock() {
        if (alertas == null) return 0;
        return alertas.stream().filter(a -> "AGROQUIMICO_BAJO_STOCK".equals(a.getTipo())).count();
    }

    public long getTotalAltas() {
        if (alertas == null) return 0;
        return alertas.stream().filter(a -> "ALTA".equals(a.getPrioridad())).count();
    }

    private void mensaje(String texto) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, texto, null));
    }
}
