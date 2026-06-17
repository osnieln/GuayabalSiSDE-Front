package cu.edu.unah.bean;

import cu.edu.unah.rest.RestAlertas;
import cu.edu.unah.util.AlertaResponse;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        cargarAlertas();
    }

    public void cargarAlertas() {
        RestAlertas restAlertas = new RestAlertas();
        alertas = restAlertas.findAll();
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
}
