package cu.edu.unah.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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

    private StreamedContent downloadFile(String endpoint, String filename) {
        try {
            String apiUrl = "http://localhost:8081/api/reportes/" + endpoint;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Verificar el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Reporte no generado",
                                "No se encontraron elementos con los criterios de búsqueda indicados."));
                return null;
            }

            InputStream stream = connection.getInputStream();

            return DefaultStreamedContent.builder()
                    .name("reporte_" + filename + ".pdf")
                    .contentType("application/pdf")
                    .stream(() -> stream)
                    .build();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al descargar el archivo",
                            e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }

    public StreamedContent getDatosCultivos() {
        return downloadFile("todasAreasCultivo", "resumen_areas_cultivo");
    }

    public StreamedContent getPlanProduccion() {
        return downloadFile("planProdBetween/" + init + "/" + end, "cultivos_por_plan_produccion");
    }

    public StreamedContent getProdCultivosPermanenteAfter() {
        return downloadFile("prodCultivosPermanenteAfter/" + init, "cultivos_prod_cultivo_permanente");
    }

    public String getFechaRecogidaString() {
        if (fechaRecogida == null) fechaRecogida = new Date(System.currentTimeMillis());
        return new SimpleDateFormat("dd-MM-yyyy").format(fechaRecogida);
    }

    public StreamedContent getFechaRecogidaBefore() {
        if(fechaRecogida == null)
            fechaRecogida = new Date(System.currentTimeMillis());
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        String date = formateador.format(fechaRecogida);
        return downloadFile("fechaRecogidaBefore/" + date, "cultivos_fecha_recogidaBefore");
    }

    public StreamedContent getCultivosPorVencer() {
        return downloadFile("cultivosPorVencer/" + diasVencer, "cultivos_por_vencer");
    }

    public StreamedContent getAgroquimicosMasUsados() {
        return downloadFile("agroquimicosMasUsados", "agroquimicos_mas_usados");
    }
}
