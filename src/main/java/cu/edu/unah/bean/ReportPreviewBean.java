package cu.edu.unah.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Getter
@Setter
@SessionScoped
public class ReportPreviewBean implements Serializable {

    private static final String API_BASE = "http://localhost:8081/api/reportes/preview/";
    private final ObjectMapper mapper = new ObjectMapper();

    // ── Datos para vista previa ──────────────────────────────────────────────
    private List<Map<String, Object>> previewAreasCultivo = new ArrayList<>();
    private List<Map<String, Object>> previewCultivosPorVencer = new ArrayList<>();
    private List<Map<String, Object>> previewAgroquimicos = new ArrayList<>();

    // ── Filtros ──────────────────────────────────────────────────────────────
    private int diasVencer = 30;
    private long planMin = 0;
    private long planMax = 10000;
    private double prodPermanenteMin = 0;
    private String fechaRecogidaFiltro = "";

    // ── Métodos de carga ─────────────────────────────────────────────────────

    public void cargarTodasAreasCultivo() {
        previewAreasCultivo = fetchList("todasAreasCultivo");
        if (previewAreasCultivo.isEmpty()) {
            addWarn("Sin resultados", "No hay áreas de cultivo registradas.");
        }
    }

    public void cargarPorPlanProduccion() {
        previewAreasCultivo = fetchList("planProdBetween/" + planMin + "/" + planMax);
        if (previewAreasCultivo.isEmpty()) {
            addWarn("Sin resultados", "No se encontraron registros con ese rango de plan de producción.");
        }
    }

    public void cargarPorProdPermanente() {
        previewAreasCultivo = fetchList("prodCultivosPermanenteAfter/" + prodPermanenteMin);
        if (previewAreasCultivo.isEmpty()) {
            addWarn("Sin resultados", "No se encontraron cultivos permanentes con esa producción mínima.");
        }
    }

    public void cargarPorFechaRecogida() {
        if (fechaRecogidaFiltro == null || fechaRecogidaFiltro.isBlank()) return;
        previewAreasCultivo = fetchList("fechaRecogidaBefore/" + fechaRecogidaFiltro);
        if (previewAreasCultivo.isEmpty()) {
            addWarn("Sin resultados", "No se encontraron cultivos con fecha de recogida anterior a la indicada.");
        }
    }

    public void cargarCultivosPorVencer() {
        previewCultivosPorVencer = fetchList("cultivosPorVencer/" + diasVencer);
        if (previewCultivosPorVencer.isEmpty()) {
            addWarn("Sin resultados", "No hay cultivos que venzan en los próximos " + diasVencer + " días.");
        }
    }

    public void cargarAgroquimicos() {
        previewAgroquimicos = fetchList("agroquimicosMasUsados");
        if (previewAgroquimicos.isEmpty()) {
            addWarn("Sin resultados", "No hay agroquímicos registrados.");
        }
    }

    public void limpiarPreviewAreasCultivo() {
        previewAreasCultivo = new ArrayList<>();
    }

    public void limpiarPreviewVencer() {
        previewCultivosPorVencer = new ArrayList<>();
    }

    public void limpiarPreviewAgroquimicos() {
        previewAgroquimicos = new ArrayList<>();
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private List<Map<String, Object>> fetchList(String endpoint) {
        try {
            URL url = new URL(API_BASE + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) return new ArrayList<>();
            InputStream is = conn.getInputStream();
            return mapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            addError("Error al cargar datos", e.getMessage());
            return new ArrayList<>();
        }
    }

    private void addWarn(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
    }

    private void addError(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }
}
