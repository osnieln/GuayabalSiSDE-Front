package cu.edu.unah.bean;

import cu.edu.unah.rest.RestAreaCultivo;
import cu.edu.unah.util.AreaCultivoResponse;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named
@Getter
@Setter
@ViewScoped
public class PlanificacionCosechaBean implements Serializable {

    private static final DateTimeFormatter FMT_DDMMYYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FMT_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private List<AreaCultivoResponse> listaEventos = new ArrayList<>();
    private AreaCultivoResponse selectedAreaCultivo;
    private double nuevaProduccionReal;
    private int pendientes;
    private int completadas;
    private int vencidas;

    private RestAreaCultivo restAreaCultivo = new RestAreaCultivo();

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
            List<AreaCultivoResponse> lista = restAreaCultivo.findByFechaRecogidaBetween(desde, hasta);
            if (lista == null) return;

            for (AreaCultivoResponse ac : lista) {
                if (ac.getFechaRecogida() == null || ac.getFechaRecogida().isEmpty()) continue;
                String styleClass = determinarColor(ac);
                if ("harvest-done".equals(styleClass)) completadas++;
                else if ("harvest-overdue".equals(styleClass)) vencidas++;
                else pendientes++;
                listaEventos.add(ac);
            }
        } catch (Exception e) {
            // backend no disponible
        }
    }

    public void seleccionarItem(AreaCultivoResponse item) {
        selectedAreaCultivo = item;
        nuevaProduccionReal = item.getProduccionReal() != null ? item.getProduccionReal() : 0.0;
    }

    public void registrarCosechaReal() {
        if (selectedAreaCultivo == null) return;
        if (selectedAreaCultivo.getAgroquimicos() == null) {
            selectedAreaCultivo.setAgroquimicos(Collections.emptyList());
        }
        selectedAreaCultivo.setProduccionReal(nuevaProduccionReal);
        boolean ok = restAreaCultivo.update(selectedAreaCultivo);
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

    public String determinarColor(AreaCultivoResponse ac) {
        if (ac.getProduccionReal() != null && ac.getProduccionReal() > 0) return "harvest-done";
        LocalDateTime fecha = parseDate(ac.getFechaRecogida());
        LocalDate hoy = LocalDate.now();
        if (fecha != null && fecha.toLocalDate().isBefore(hoy) && Boolean.TRUE.equals(ac.getActivo())) return "harvest-overdue";
        return "harvest-planned";
    }

    public String estadoLabel(AreaCultivoResponse ac) {
        return switch (determinarColor(ac)) {
            case "harvest-done"    -> "Completada";
            case "harvest-overdue" -> "Vencida";
            default                -> "Planificada";
        };
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr, FMT_DDMMYYYY).atStartOfDay();
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(dateStr, FMT_YYYYMMDD).atStartOfDay();
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}
