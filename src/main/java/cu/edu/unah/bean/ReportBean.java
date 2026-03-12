package cu.edu.unah.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

@Named
@Getter
@Setter
@SessionScoped
public class ReportBean implements Serializable {

    private long init = 0L, end = 0L;
    Date fechaRecogida = new Date(System.currentTimeMillis());
    private int diasVencer = 30;

    /**
     * Descarga el PDF del backend. Solo abre conexión HTTP cuando PrimeFaces
     * necesita el contenido real (INVOKE_APPLICATION), no durante el render.
     */
    private StreamedContent fetchPdf(String endpoint, String filename) {
        try {
            URL url = new URL("http://localhost:8081/api/reportes/" + endpoint);
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
                return DefaultStreamedContent.builder()
                        .name("reporte_" + filename + ".pdf")
                        .contentType("application/pdf")
                        .stream(() -> new ByteArrayInputStream(bytes))
                        .build();
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultStreamedContent();
    }

    /** Devuelve contenido vacío durante el render; descarga real en otros fases. */
    private StreamedContent phaseAware(String endpoint, String filename) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null || PhaseId.RENDER_RESPONSE.equals(ctx.getCurrentPhaseId())) {
            return new DefaultStreamedContent();
        }
        return fetchPdf(endpoint, filename);
    }

    public StreamedContent getDatosCultivos() {
        return phaseAware("todasAreasCultivo", "resumen_areas_cultivo");
    }

    public StreamedContent getPlanProduccion() {
        return phaseAware("planProdBetween/" + init + "/" + end, "cultivos_por_plan_produccion");
    }

    public StreamedContent getProdCultivosPermanenteAfter() {
        return phaseAware("prodCultivosPermanenteAfter/" + init, "cultivos_prod_cultivo_permanente");
    }

    public String getFechaRecogidaString() {
        if (fechaRecogida == null) fechaRecogida = new Date(System.currentTimeMillis());
        return new SimpleDateFormat("dd-MM-yyyy").format(fechaRecogida);
    }

    public StreamedContent getFechaRecogidaBefore() {
        if (fechaRecogida == null) fechaRecogida = new Date(System.currentTimeMillis());
        String date = new SimpleDateFormat("dd-MM-yyyy").format(fechaRecogida);
        return phaseAware("fechaRecogidaBefore/" + date, "cultivos_fecha_recogidaBefore");
    }

    public StreamedContent getCultivosPorVencer() {
        return phaseAware("cultivosPorVencer/" + diasVencer, "cultivos_por_vencer");
    }

    public StreamedContent getAgroquimicosMasUsados() {
        return phaseAware("agroquimicosMasUsados", "agroquimicos_mas_usados");
    }
}
