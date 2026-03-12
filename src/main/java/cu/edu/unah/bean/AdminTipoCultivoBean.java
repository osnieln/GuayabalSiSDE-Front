package cu.edu.unah.bean;

import cu.edu.unah.entity.TipoCultivo;
import cu.edu.unah.rest.RestTipoCultivo;
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
public class AdminTipoCultivoBean implements Serializable{

    private List<TipoCultivo> listTipoCultivo = new ArrayList<TipoCultivo>();
    private String nombre ="";


    private TipoCultivo cultivo = new TipoCultivo();
    private TipoCultivo selectedTipoCultivo;
    RestTipoCultivo restTipoCultivo = new RestTipoCultivo();


    public void init(){
        listTipoCultivo.clear();
        cleanVariables();
        listTipoCultivo = restTipoCultivo.findAllTipoCultivo();
    }

    public void cleanVariables(){
        nombre="";
    }

    public void updateSelectedTipoCultivo(TipoCultivo tipoCultivo) {
        this.setSelectedTipoCultivo(tipoCultivo);
    }

    public void updateSelected_TipoCultivo_toEdit(TipoCultivo tipoCultivo) {
        selectedTipoCultivo = tipoCultivo;
        nombre = tipoCultivo.getNombre();
    }

    public void updateSelectedTipoCultivoToDelete(TipoCultivo tipoCultivo){
        this.selectedTipoCultivo = tipoCultivo;
    }

    public void addTipoCultivo() {
        TipoCultivo cultivoToAdd = TipoCultivo.builder()
                .nombre(nombre)
                .build();
        FacesContext context = FacesContext.getCurrentInstance();

        if (nombre == null || nombre.isBlank()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre del tipo de cultivo es obligatorio.", ""));
            return;
        }
        if(restTipoCultivo.create(cultivoToAdd)){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "TIPO DE CULTIVO ADICIONADO CORRECTAMENTE", ""));
            init();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR EL TIPO DE CULTIVO", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-tipoCultivo");
        PrimeFaces.current().executeScript("PF('addtipoCultivoDialog').hide()");
    }

    public void editTipoCultivo() {
        TipoCultivo cultivoToEdit = TipoCultivo.builder()
                .id(selectedTipoCultivo.getId())
                .nombre(nombre)
                .build();

        FacesContext context = FacesContext.getCurrentInstance();
        if (nombre == null || nombre.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre del tipo de cultivo es obligatorio.", ""));
            return;
        }
        if(restTipoCultivo.update(cultivoToEdit)){
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "TIPO DE CULTIVO EDITADO", ""));
        }
        else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR EL TIPO DE CULTIVO", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-tipoCultivo");
        PrimeFaces.current().executeScript("PF('edittipoCultivoDialog').hide()");
    }

    public void deleteTipoCultivo() {
        FacesContext context = FacesContext.getCurrentInstance();
        if(restTipoCultivo.delete(selectedTipoCultivo.getId())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "TIPO DE CULTIVO ELIMINADO CORRECTAMENTE", ""));
            init();
        }
        else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR EL TIPO DE CULTIVO", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-tipoCultivo");
        PrimeFaces.current().executeScript("PF('deletetipoCultivoDialog').hide()");
    }
}
