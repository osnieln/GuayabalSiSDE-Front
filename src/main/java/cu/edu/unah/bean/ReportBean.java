package cu.edu.unah.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private String emailDestinatario;

    // ── PDF ──────────────────────────────────────────────────────────────────

    private StreamedContent lazyPdf(String endpoint, String filename) {
        return DefaultStreamedContent.builder()
                .name("reporte_" + filename + ".pdf")
                .contentType("application/pdf")
                .stream(() -> {
                    try {
                        URL url = new URL("http://localhost:8081/api/reportes/" + endpoint);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(60000);
                        int code = conn.getResponseCode();
                        if (code == HttpURLConnection.HTTP_OK) {
                            byte[] bytes;
                            try (InputStream is = conn.getInputStream()) {
                                bytes = is.readAllBytes();
                            }
                            conn.disconnect();
                            System.err.println("[ReportBean] PDF OK: " + bytes.length + " bytes — " + endpoint);
                            return new ByteArrayInputStream(bytes);
                        }
                        String errorBody = "";
                        try (InputStream err = conn.getErrorStream()) {
                            if (err != null) errorBody = new String(err.readAllBytes());
                        } catch (Exception ignored) {}
                        System.err.println("[ReportBean] Backend devolvió HTTP " + code + " para: " + endpoint
                                + (errorBody.isEmpty() ? "" : " → " + errorBody));
                        conn.disconnect();
                    } catch (Exception e) {
                        System.err.println("[ReportBean] Error PDF '" + endpoint + "': " + e.getMessage());
                        e.printStackTrace();
                    }
                    return new ByteArrayInputStream(new byte[0]);
                })
                .build();
    }

    public StreamedContent getDatosCultivos() {
        return lazyPdf("todasAreasCultivo", "resumen_areas_cultivo");
    }

    public StreamedContent getPlanProduccion() {
        return lazyPdf("planProdBetween/" + init + "/" + end, "cultivos_por_plan_produccion");
    }

    public StreamedContent getProdCultivosPermanenteAfter() {
        return lazyPdf("prodCultivosPermanenteAfter/" + init, "cultivos_prod_cultivo_permanente");
    }

    public String getFechaRecogidaString() {
        if (fechaRecogida == null) fechaRecogida = new Date(System.currentTimeMillis());
        return new SimpleDateFormat("dd-MM-yyyy").format(fechaRecogida);
    }

    public StreamedContent getFechaRecogidaBefore() {
        if (fechaRecogida == null) fechaRecogida = new Date(System.currentTimeMillis());
        String date = new SimpleDateFormat("dd-MM-yyyy").format(fechaRecogida);
        return lazyPdf("fechaRecogidaBefore/" + date, "cultivos_fecha_recogidaBefore");
    }

    public StreamedContent getCultivosPorVencer() {
        return lazyPdf("cultivosPorVencer/" + diasVencer, "cultivos_por_vencer");
    }

    public StreamedContent getAgroquimicosMasUsados() {
        return lazyPdf("agroquimicosMasUsados", "agroquimicos_mas_usados");
    }

    // ── Excel ─────────────────────────────────────────────────────────────────

    private StreamedContent lazyExcel(String endpoint, String filename) {
        return DefaultStreamedContent.builder()
                .name(filename + ".xlsx")
                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .stream(() -> {
                    try {
                        URL url = new URL("http://localhost:8081/api/reportes/excel/" + endpoint);
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
                        System.err.println("[ReportBean] Error Excel '" + endpoint + "': " + e.getMessage());
                    }
                    return new ByteArrayInputStream(new byte[0]);
                })
                .build();
    }

    public StreamedContent getExcelTodasAreasCultivo() {
        return lazyExcel("todasAreasCultivo", "resumen_areas_cultivo");
    }

    public StreamedContent getExcelPlanProduccion() {
        return lazyExcel("planProdBetween/" + init + "/" + end, "cultivos_plan_produccion");
    }

    public StreamedContent getExcelProdCultivosPermanenteAfter() {
        return lazyExcel("prodCultivosPermanenteAfter/" + init, "cultivos_prod_permanente");
    }

    public StreamedContent getExcelFechaRecogidaBefore() {
        if (fechaRecogida == null) fechaRecogida = new Date(System.currentTimeMillis());
        String date = new SimpleDateFormat("dd-MM-yyyy").format(fechaRecogida);
        return lazyExcel("fechaRecogidaBefore/" + date, "cultivos_fecha_recogida");
    }

    public StreamedContent getExcelCultivosPorVencer() {
        return lazyExcel("cultivosPorVencer/" + diasVencer, "cultivos_por_vencer");
    }

    public StreamedContent getExcelAgroquimicosMasUsados() {
        return lazyExcel("agroquimicosMasUsados", "agroquimicos_mas_usados");
    }

    // ── Correo ────────────────────────────────────────────────────────────────

    private void enviarCorreo(String endpoint, FacesContext ctx) {
        if (emailDestinatario == null || emailDestinatario.isBlank()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Correo requerido", "Ingrese el correo del destinatario."));
            return;
        }
        try {
            String enc = URLEncoder.encode(emailDestinatario.trim(), StandardCharsets.UTF_8);
            URL url = new URL("http://localhost:8081/api/reportes/email/" + endpoint + "?destinatario=" + enc);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                conn.disconnect();
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Correo enviado", "Reporte enviado a " + emailDestinatario));
            } else {
                String errorMsg = leerMensajeError(conn, code);
                conn.disconnect();
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error al enviar", errorMsg));
            }
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al enviar", "No se pudo conectar con el servidor: " + e.getMessage()));
        }
    }

    private String leerMensajeError(HttpURLConnection conn, int code) {
        try {
            java.io.InputStream err = conn.getErrorStream();
            if (err == null) err = conn.getInputStream();
            if (err != null) {
                String body = new String(err.readAllBytes(), StandardCharsets.UTF_8);
                String extraido = extraerMensajeJson(body);
                if (extraido != null && !extraido.isBlank()) return extraido;
            }
        } catch (Exception ignored) {}
        return "El servidor devolvió código " + code + ".";
    }

    private String extraerMensajeJson(String json) {
        if (json == null) return null;
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\"mensaje\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                .matcher(json);
        if (m.find()) return m.group(1).replace("\\n", "\n").replace("\\\"", "\"");
        m = java.util.regex.Pattern
                .compile("\"errorDescription\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                .matcher(json);
        return m.find() ? m.group(1).replace("\\n", "\n").replace("\\\"", "\"") : null;
    }

    public void enviarCorreoTodasAreasCultivo() {
        enviarCorreo("todasAreasCultivo", FacesContext.getCurrentInstance());
    }

    public void enviarCorreoPlanProduccion() {
        enviarCorreo("planProdBetween/" + init + "/" + end, FacesContext.getCurrentInstance());
    }

    public void enviarCorreoProdCultivosPermanenteAfter() {
        enviarCorreo("prodCultivosPermanenteAfter/" + init, FacesContext.getCurrentInstance());
    }

    public void enviarCorreoFechaRecogidaBefore() {
        if (fechaRecogida == null) fechaRecogida = new Date(System.currentTimeMillis());
        String date = new SimpleDateFormat("dd-MM-yyyy").format(fechaRecogida);
        enviarCorreo("fechaRecogidaBefore/" + date, FacesContext.getCurrentInstance());
    }

    public void enviarCorreoCultivosPorVencer() {
        enviarCorreo("cultivosPorVencer/" + diasVencer, FacesContext.getCurrentInstance());
    }

    public void enviarCorreoAgroquimicosMasUsados() {
        enviarCorreo("agroquimicosMasUsados", FacesContext.getCurrentInstance());
    }
}
