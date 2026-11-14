package cu.edu.unah.bean;

import cu.edu.unah.rest.RestPlanificacionCosecha;
import cu.edu.unah.util.PlanificacionCosechaResponse;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named
@Getter
@Setter
@ViewScoped
public class PlanificacionCosechaBean implements Serializable {

    private static final DateTimeFormatter FMT_DDMMYYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private List<PlanificacionCosechaResponse> listaEventos = new ArrayList<>();
    private PlanificacionCosechaResponse selectedItem;
    private double nuevaProduccionReal;
    private int pendientes;
    private int completadas;
    private int vencidas;

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    public void cargarDatos() {
        listaEventos = new ArrayList<>();
        pendientes = 0;
        completadas = 0;
        vencidas = 0;

        LocalDate hoy = LocalDate.now();
        String desde = hoy.minusMonths(3).format(FMT_DDMMYYYY);
        String hasta = hoy.plusMonths(9).format(FMT_DDMMYYYY);

        try {
            RestPlanificacionCosecha restPlanificacionCosecha = new RestPlanificacionCosecha();
            List<PlanificacionCosechaResponse> lista = restPlanificacionCosecha.findCalendario(desde, hasta);
            if (lista == null) return;

            for (PlanificacionCosechaResponse item : lista) {
                switch (item.getEstado()) {
                    case "COMPLETADA" -> completadas++;
                    case "VENCIDA" -> vencidas++;
                    case "PLANIFICADA" -> pendientes++;
                    default -> { /* CANCELADA no se contabiliza */ }
                }
                listaEventos.add(item);
            }
        } catch (Exception e) {
            // backend no disponible
        }
    }

    public void seleccionarItem(PlanificacionCosechaResponse item) {
        selectedItem = item;
        nuevaProduccionReal = item.getProduccionReal() != null ? item.getProduccionReal() : 0.0;
    }

    public void registrarCosechaReal() {
        if (selectedItem == null) return;
        RestPlanificacionCosecha restPlanificacionCosecha = new RestPlanificacionCosecha();
        boolean ok = restPlanificacionCosecha.completar(selectedItem.getAreaCultivoResponsePk(), nuevaProduccionReal);
        if (ok) {
            cargarDatos();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Cosecha registrada",
                            "La producción real fue guardada correctamente."));
            PrimeFaces.current().ajax().update("cosechaForm:growl", "cosechaForm:statsPanel", "cosechaForm:cosechaTable");
            PrimeFaces.current().executeScript("PF('eventDlg').hide()");
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudo guardar la producción real."));
            PrimeFaces.current().ajax().update("cosechaForm:growl");
        }
    }

    public void cancelarCosecha(PlanificacionCosechaResponse item) {
        RestPlanificacionCosecha restPlanificacionCosecha = new RestPlanificacionCosecha();
        boolean ok = restPlanificacionCosecha.cancelar(item.getAreaCultivoResponsePk());
        if (ok) {
            cargarDatos();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Cosecha cancelada", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cancelar la cosecha."));
        }
    }

    public String determinarColor(PlanificacionCosechaResponse item) {
        return switch (item.getEstado()) {
            case "COMPLETADA" -> "harvest-done";
            case "VENCIDA" -> "harvest-overdue";
            case "CANCELADA" -> "harvest-cancelled";
            default -> "harvest-planned";
        };
    }

    public String estadoLabel(PlanificacionCosechaResponse item) {
        return switch (item.getEstado()) {
            case "COMPLETADA" -> "Completada";
            case "VENCIDA" -> "Vencida";
            case "CANCELADA" -> "Cancelada";
            default -> "Planificada";
        };
    }

    // ── Excel ─────────────────────────────────────────────────────────────────

    private String buildCalendarioUrl() {
        LocalDate hoy = LocalDate.now();
        String desde = hoy.minusMonths(3).format(FMT_DDMMYYYY);
        String hasta = hoy.plusMonths(9).format(FMT_DDMMYYYY);
        return desde + "/" + hasta;
    }

    public StreamedContent getExcelCosecha() {
        String rango = buildCalendarioUrl();
        return DefaultStreamedContent.builder()
                .name("planificacion_cosecha.xlsx")
                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .stream(() -> {
                    try {
                        URL url = new URL("http://localhost:8081/areaCultivo/excel/calendario/" + rango);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(60000);
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            byte[] bytes;
                            try (InputStream is = conn.getInputStream()) {
                                bytes = is.readAllBytes();
                            }
                            conn.disconnect();
                            return new ByteArrayInputStream(bytes);
                        }
                        conn.disconnect();
                    } catch (Exception e) {
                        System.err.println("[PlanificacionCosechaBean] Error Excel cosecha: " + e.getMessage());
                    }
                    return new ByteArrayInputStream(new byte[0]);
                })
                .build();
    }

}
