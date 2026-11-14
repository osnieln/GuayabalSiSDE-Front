package cu.edu.unah.bean;

import cu.edu.unah.rest.RestAgroquimico;
import cu.edu.unah.rest.RestMovimientoAgroquimico;
import cu.edu.unah.rest.RestTrabajador;
import cu.edu.unah.util.AgroquimicoResponse;
import cu.edu.unah.util.DateFormatter;
import cu.edu.unah.util.MovimientoAgroquimicoResponse;
import cu.edu.unah.util.TrabajadorResponse;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class ControlInventarioAgroquimicoBean implements Serializable {

    private List<AgroquimicoResponse> agroquimicoResponseList = new ArrayList<>();
    private List<TrabajadorResponse> trabajadorList = new ArrayList<>();
    private List<MovimientoAgroquimicoResponse> movimientoList = new ArrayList<>();

    private Long agroquimicoId;
    private Double cantidad;
    private Date fecha = new Date();
    private Long trabajadorId;
    private String observaciones = "";

    private MovimientoAgroquimicoResponse selectedMovimiento;

    RestAgroquimico restAgroquimico = new RestAgroquimico();
    RestTrabajador restTrabajador = new RestTrabajador();
    RestMovimientoAgroquimico restMovimientoAgroquimico = new RestMovimientoAgroquimico();

    public void init() {
        cleanVariables();
        agroquimicoResponseList = restAgroquimico.findAllAgroquimico();
        if (agroquimicoResponseList == null) agroquimicoResponseList = new ArrayList<>();
        trabajadorList = restTrabajador.findAllTrabajador();
        if (trabajadorList == null) trabajadorList = new ArrayList<>();
        movimientoList = restMovimientoAgroquimico.findAllMovimientoAgroquimico();
        if (movimientoList == null) movimientoList = new ArrayList<>();
    }

    public void cleanVariables() {
        agroquimicoId = null;
        cantidad = null;
        fecha = new Date();
        trabajadorId = null;
        observaciones = "";
    }

    public void updateSelectedMovimientoToDelete(MovimientoAgroquimicoResponse movimiento) {
        this.selectedMovimiento = movimiento;
    }

    public void registrarEntrada() {
        registrarMovimiento("ENTRADA");
    }

    public void registrarSalida() {
        registrarMovimiento("SALIDA");
    }

    private void registrarMovimiento(String tipo) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (agroquimicoId == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un agroquímico.", ""));
            return;
        }
        if (cantidad == null || cantidad <= 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La cantidad debe ser mayor que cero.", ""));
            return;
        }
        if (fecha == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La fecha del movimiento es obligatoria.", ""));
            return;
        }
        MovimientoAgroquimicoResponse movimiento = MovimientoAgroquimicoResponse.builder()
                .agroquimicoId(agroquimicoId)
                .tipo(tipo)
                .cantidad(cantidad)
                .fecha(DateFormatter.formatUtil(fecha))
                .trabajadorId(trabajadorId)
                .observaciones(observaciones)
                .build();

        boolean esEntrada = "ENTRADA".equals(tipo);
        if (restMovimientoAgroquimico.create(movimiento)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    esEntrada ? "ENTRADA REGISTRADA CORRECTAMENTE" : "SALIDA REGISTRADA CORRECTAMENTE", ""));
            init();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    esEntrada ? "ERROR AL REGISTRAR LA ENTRADA" : "ERROR AL REGISTRAR LA SALIDA. VERIFIQUE EL STOCK DISPONIBLE.", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-stock", "form:dt-movimientos");
        PrimeFaces.current().executeScript(esEntrada ? "PF('entradaDialog').hide()" : "PF('salidaDialog').hide()");
    }

    public void deleteMovimiento() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (restMovimientoAgroquimico.delete(selectedMovimiento.getId())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "MOVIMIENTO ELIMINADO CORRECTAMENTE", ""));
            init();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR EL MOVIMIENTO", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-stock", "form:dt-movimientos");
        PrimeFaces.current().executeScript("PF('deleteMovimientoDialog').hide()");
    }
}
