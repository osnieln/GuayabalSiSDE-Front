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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@Named
@Getter
@Setter
@ViewScoped
public class PlanificacionCosechaBean implements Serializable {

    private static final DateTimeFormatter FMT_DDMMYYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FMT_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ScheduleModel model;
    private ScheduleEvent<?> selectedEvent;
    private AreaCultivoResponse selectedAreaCultivo;
    private double nuevaProduccionReal;
    private int pendientes;
    private int completadas;
    private int vencidas;

    private RestAreaCultivo restAreaCultivo = new RestAreaCultivo();

    @PostConstruct
    public void init() {
        buildModel();
    }

    public void buildModel() {
        model = new DefaultScheduleModel();
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
                LocalDateTime fecha = parseDate(ac.getFechaRecogida());
                if (fecha == null) continue;

                String titulo = "Área " + ac.getAreaCultivoResponsePK().getAreaId()
                        + " — Cultivo " + ac.getAreaCultivoResponsePK().getCultivoId();
                String styleClass = determinarColor(ac, hoy);

                if ("harvest-done".equals(styleClass)) completadas++;
                else if ("harvest-overdue".equals(styleClass)) vencidas++;
                else pendientes++;

                DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder()
                        .title(titulo)
                        .startDate(fecha)
                        .endDate(fecha.plusHours(1))
                        .data(ac)
                        .styleClass(styleClass)
                        .allDay(true)
                        .build();
                model.addEvent(event);
            }
        } catch (Exception e) {
            // backend unavailable — show empty calendar
        }
    }

    public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
        selectedEvent = selectEvent.getObject();
        selectedAreaCultivo = (AreaCultivoResponse) selectedEvent.getData();
        nuevaProduccionReal = selectedAreaCultivo.getProduccionReal() != null
                ? selectedAreaCultivo.getProduccionReal() : 0.0;
    }

    public void registrarCosechaReal() {
        if (selectedAreaCultivo == null) return;
        if (selectedAreaCultivo.getAgroquimicos() == null) {
            selectedAreaCultivo.setAgroquimicos(Collections.emptyList());
        }
        selectedAreaCultivo.setProduccionReal(nuevaProduccionReal);
        boolean ok = restAreaCultivo.update(selectedAreaCultivo);
        if (ok) {
            buildModel();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Cosecha registrada",
                            "La producción real fue guardada correctamente."));
            PrimeFaces.current().ajax().update("cosechaForm:growl", "cosechaForm:statsPanel", "cosechaForm:cosechaSchedule");
            PrimeFaces.current().executeScript("PF('eventDlg').hide()");
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudo guardar la producción real."));
            PrimeFaces.current().ajax().update("cosechaForm:growl");
        }
    }

    private String determinarColor(AreaCultivoResponse ac, LocalDate hoy) {
        if (ac.getProduccionReal() != null && ac.getProduccionReal() > 0) return "harvest-done";
        LocalDateTime fecha = parseDate(ac.getFechaRecogida());
        if (fecha != null && fecha.toLocalDate().isBefore(hoy) && Boolean.TRUE.equals(ac.getActivo())) return "harvest-overdue";
        return "harvest-planned";
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
