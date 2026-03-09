package cu.edu.unah.bean;

import cu.edu.unah.rest.RestRiego;
import cu.edu.unah.util.RiegoResponse;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Named("historialRiegoBean")
@ViewScoped
@Data
public class HistorialRiegoBean implements Serializable {

    private Long areaId;
    private List<RiegoResponse> historial;

    public void buscarHistorial() {
        if (areaId != null) {
            RestRiego restRiego = new RestRiego();
            historial = restRiego.findHistorialByArea(areaId);
        }
    }

    public void limpiar() {
        areaId = null;
        historial = null;
    }

    public long getTotalRealizados() {
        if (historial == null) return 0;
        return historial.stream()
                .filter(r -> r.getFechaReal() != null && !r.getFechaReal().isEmpty())
                .count();
    }

    public long getTotalPendientes() {
        if (historial == null) return 0;
        return historial.stream()
                .filter(r -> r.getFechaReal() == null || r.getFechaReal().isEmpty())
                .count();
    }

    public long getTotalAdvertencias() {
        if (historial == null) return 0;
        return historial.stream()
                .filter(r -> r.getAdvertencia() != null && !r.getAdvertencia().isEmpty())
                .count();
    }

    public void exportarCsv() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset();
        ec.setResponseContentType("text/csv");
        ec.setResponseCharacterEncoding("UTF-8");
        ec.setResponseHeader("Content-Disposition",
                "attachment; filename=\"historial_riego_area_" + areaId + ".csv\"");

        StringBuilder sb = new StringBuilder();
        sb.append("ID,Fecha Planificada,Fecha Real,Estado,Area ID,Cultivo ID,Advertencia\n");
        for (RiegoResponse r : historial) {
            String estado = (r.getFechaReal() != null && !r.getFechaReal().isEmpty()) ? "Realizado" : "Pendiente";
            String advertencia = (r.getAdvertencia() != null) ? r.getAdvertencia() : "";
            sb.append(r.getId()).append(",")
              .append(nvl(r.getFechaPlanificacion())).append(",")
              .append(nvl(r.getFechaReal())).append(",")
              .append(estado).append(",")
              .append(r.getAreaCultivoResponsePk().getAreaId()).append(",")
              .append(r.getAreaCultivoResponsePk().getCultivoId()).append(",")
              .append(advertencia).append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        ec.setResponseContentLength(bytes.length);
        try (OutputStream os = ec.getResponseOutputStream()) {
            os.write(bytes);
        }
        fc.responseComplete();
    }

    private String nvl(String val) {
        return val != null ? val : "";
    }
}
