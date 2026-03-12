package cu.edu.unah.bean;

import jakarta.enterprise.context.SessionScoped;
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
     * Returns a lazy StreamedContent. The HTTP connection to the backend is opened
     * inside the stream() Supplier, which PrimeFaces only calls during the actual
     * file download GET request — not during page rendering.
     */
    private StreamedContent buildLazyContent(String endpoint, String filename) {
        return DefaultStreamedContent.builder()
                .name("reporte_" + filename + ".pdf")
                .contentType("application/pdf")
                .stream(() -> {
                    try {
                        URL url = new URL("http://localhost:8081/api/reportes/" + endpoint);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(10000);
                        connection.setReadTimeout(60000);
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            try (InputStream stream = connection.getInputStream()) {
                                byte[] bytes = stream.readAllBytes();
                                return new ByteArrayInputStream(bytes);
                            } finally {
                                connection.disconnect();
                            }
                        }
                        connection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new ByteArrayInputStream(new byte[0]);
                })
                .build();
    }

    public StreamedContent getDatosCultivos() {
        return buildLazyContent("todasAreasCultivo", "resumen_areas_cultivo");
    }

    public StreamedContent getPlanProduccion() {
        return buildLazyContent("planProdBetween/" + init + "/" + end, "cultivos_por_plan_produccion");
    }

    public StreamedContent getProdCultivosPermanenteAfter() {
        return buildLazyContent("prodCultivosPermanenteAfter/" + init, "cultivos_prod_cultivo_permanente");
    }

    public String getFechaRecogidaString() {
        if (fechaRecogida == null) fechaRecogida = new Date(System.currentTimeMillis());
        return new SimpleDateFormat("dd-MM-yyyy").format(fechaRecogida);
    }

    public StreamedContent getFechaRecogidaBefore() {
        if (fechaRecogida == null)
            fechaRecogida = new Date(System.currentTimeMillis());
        String date = new SimpleDateFormat("dd-MM-yyyy").format(fechaRecogida);
        return buildLazyContent("fechaRecogidaBefore/" + date, "cultivos_fecha_recogidaBefore");
    }

    public StreamedContent getCultivosPorVencer() {
        return buildLazyContent("cultivosPorVencer/" + diasVencer, "cultivos_por_vencer");
    }

    public StreamedContent getAgroquimicosMasUsados() {
        return buildLazyContent("agroquimicosMasUsados", "agroquimicos_mas_usados");
    }
}
