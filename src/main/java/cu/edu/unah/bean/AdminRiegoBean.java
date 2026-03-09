package cu.edu.unah.bean;

import cu.edu.unah.rest.RestRiego;
import cu.edu.unah.util.AreaCultivoResponsePK;
import cu.edu.unah.util.RiegoResponse;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class AdminRiegoBean implements Serializable {

    private List<RiegoResponse> listRiego = new ArrayList<>();
    private RiegoResponse selectedRiego;

    // Campos para crear/editar
    private Long areaId;
    private Long cultivoId;
    private String fechaSiembra;
    private String fechaPlanificacion;
    private String fechaReal;
    private String advertencia;

    RestRiego restRiego = new RestRiego();

    public void init() {
        listRiego = restRiego.findAllRiegoResponse();
        if (listRiego == null) listRiego = new ArrayList<>();
        limpiarCampos();
    }

    private void limpiarCampos() {
        areaId = null;
        cultivoId = null;
        fechaSiembra = "";
        fechaPlanificacion = "";
        fechaReal = "";
        advertencia = "";
        selectedRiego = null;
    }

    public void prepararEdicion(RiegoResponse riego) {
        this.selectedRiego = riego;
        this.areaId = riego.getAreaCultivoResponsePk().getAreaId();
        this.cultivoId = riego.getAreaCultivoResponsePk().getCultivoId();
        this.fechaSiembra = riego.getAreaCultivoResponsePk().getFechaSiembra();
        this.fechaPlanificacion = riego.getFechaPlanificacion();
        this.fechaReal = riego.getFechaReal();
        this.advertencia = riego.getAdvertencia();
    }

    public void prepararEliminacion(RiegoResponse riego) {
        this.selectedRiego = riego;
    }

    public void addRiego() {
        FacesContext context = FacesContext.getCurrentInstance();
        RiegoResponse nuevo = RiegoResponse.builder()
                .areaCultivoResponsePk(AreaCultivoResponsePK.builder()
                        .areaId(areaId)
                        .cultivoId(cultivoId)
                        .fechaSiembra(fechaSiembra)
                        .build())
                .fechaPlanificacion(fechaPlanificacion)
                .fechaReal(fechaReal)
                .advertencia(advertencia)
                .build();

        if (restRiego.create(nuevo)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Riego planificado correctamente", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-riego");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al planificar el riego", ""));
        }
        PrimeFaces.current().executeScript("PF('addRiegoDialog').hide()");
    }

    public void editRiego() {
        FacesContext context = FacesContext.getCurrentInstance();
        RiegoResponse editado = RiegoResponse.builder()
                .id(selectedRiego.getId())
                .areaCultivoResponsePk(AreaCultivoResponsePK.builder()
                        .areaId(areaId)
                        .cultivoId(cultivoId)
                        .fechaSiembra(fechaSiembra)
                        .build())
                .fechaPlanificacion(fechaPlanificacion)
                .fechaReal(fechaReal)
                .advertencia(advertencia)
                .build();

        if (restRiego.update(editado)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Riego actualizado correctamente", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-riego");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar el riego", ""));
        }
        PrimeFaces.current().executeScript("PF('editRiegoDialog').hide()");
    }

    public void deleteRiego() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (restRiego.delete(selectedRiego.getId())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Riego eliminado correctamente", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-riego");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar el riego", ""));
        }
        PrimeFaces.current().executeScript("PF('deleteRiegoDialog').hide()");
    }
}
