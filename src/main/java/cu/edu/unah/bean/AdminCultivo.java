package cu.edu.unah.bean;

import cu.edu.unah.entity.Cultivo;
import cu.edu.unah.entity.Produccion;
import cu.edu.unah.rest.RestCultivo;
import cu.edu.unah.rest.RestProduccion;
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
public class AdminCultivo implements Serializable{

    private List<Cultivo> listCultivo = new ArrayList<Cultivo>();
    private List<Produccion> listProduccion = new ArrayList<Produccion>();
    private String descripcion ="";
    private Produccion produccion = new Produccion();
    private Long produccionId = 0L;


    private Cultivo cultivo = new Cultivo();
    private Cultivo selectedCultivo;
    RestCultivo restCultivo = new RestCultivo();
    RestProduccion restProduccion = new RestProduccion();


    public void init(){
        listCultivo.clear();
        listProduccion.clear();
        cleanVariables();
        listCultivo = restCultivo.findAllCultivo();
        listProduccion = restProduccion.findAllProduccion();
    }

    public void cleanVariables(){
        descripcion="";
        produccion = new Produccion();
        produccionId = 0L;
    }

    public void updateSelectedCultivo(Cultivo cultivo) {
        this.setSelectedCultivo(cultivo);
    }

    public void updateSelected_Cultivo_toEdit(Cultivo cultivo) {
        selectedCultivo = cultivo;
        descripcion = cultivo.getDescripcion();
        produccion = cultivo.getProduccion();
        produccionId = cultivo.getProduccion().getId();
    }

    public void updateSelectedCultivoToDelete(Cultivo cultivo){
        this.selectedCultivo = cultivo;
    }

    public void addCultivo() {
        Produccion prod = restProduccion.findById(produccionId);
        Cultivo cultivoToAdd = Cultivo.builder()
                .descripcion(descripcion)
                .produccion(prod)
                .build();
        FacesContext context = FacesContext.getCurrentInstance();

        if(restCultivo.create(cultivoToAdd)){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PRODUCCION ADICIONADA CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-cultivo");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR LA PRODUCCION", ""));
        }
        PrimeFaces.current().executeScript("PF('addcultivoDialog').hide()");
    }

    public void editCultivo() {
        Produccion prod = restProduccion.findById(produccionId);
        Cultivo cultivoToEdit = Cultivo.builder()
                .id(selectedCultivo.getId())
                .descripcion(descripcion)
                .produccion(prod)
                .build();

        FacesContext context = FacesContext.getCurrentInstance();
        if(restCultivo.update(cultivoToEdit)){
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PRODUCCION EDITADA", ""));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-cultivo");
        }
        else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR LA PRODUCCION", ""));
        }
        PrimeFaces.current().executeScript("PF('editcultivoDialog').hide()");
    }

    public void deleteCultivo() {
        FacesContext context = FacesContext.getCurrentInstance();
        if(restCultivo.delete(selectedCultivo.getId())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PRODUCCION ELIMINADA CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-cultivo");
        }
        else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR LA PRODUCCION", ""));
        }
        PrimeFaces.current().executeScript("PF('deleteCultivoDialog').hide()");
    }
}
