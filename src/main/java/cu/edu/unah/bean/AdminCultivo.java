package cu.edu.unah.bean;

import cu.edu.unah.entity.Cultivo;
import cu.edu.unah.entity.Produccion;
import cu.edu.unah.entity.TipoCultivo;
import cu.edu.unah.rest.RestCultivo;
import cu.edu.unah.rest.RestProduccion;
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
public class AdminCultivo implements Serializable{

    private List<Cultivo> listCultivo = new ArrayList<Cultivo>();
    private List<Produccion> listProduccion = new ArrayList<Produccion>();
    private List<TipoCultivo> tipoCultivoList = new ArrayList<TipoCultivo>();
    private String descripcion ="";
    private Produccion produccion = new Produccion();
    private TipoCultivo tipoCultivo = new TipoCultivo();
    private Long produccionId = 0L;
    private Long tipoCultivoId = 0L;


    private Cultivo cultivo = new Cultivo();
    private Cultivo selectedCultivo;
    RestCultivo restCultivo = new RestCultivo();
    RestProduccion restProduccion = new RestProduccion();
    RestTipoCultivo restTipoCultivo = new RestTipoCultivo();


    public void init(){
        listCultivo.clear();
        listProduccion.clear();
        tipoCultivoList.clear();
        cleanVariables();
        listCultivo = restCultivo.findAllCultivo();
        listProduccion = restProduccion.findAllProduccion();
        tipoCultivoList = restTipoCultivo.findAllTipoCultivo();
    }

    public void cleanVariables(){
        descripcion="";
        produccion = new Produccion();
        tipoCultivo = new TipoCultivo();
        produccionId = 0L;
        tipoCultivoId = 0L;
    }

    public void updateSelectedCultivo(Cultivo cultivo) {
        this.setSelectedCultivo(cultivo);
    }

    public void updateSelected_Cultivo_toEdit(Cultivo cultivo) {
        selectedCultivo = cultivo;
        descripcion = cultivo.getDescripcion();
        produccion = cultivo.getProduccion();
        tipoCultivo = cultivo.getTipoCultivo();
        tipoCultivoId = cultivo.getTipoCultivo().getId();
        produccionId = cultivo.getProduccion().getId();
    }

    public void updateSelectedCultivoToDelete(Cultivo cultivo){
        this.selectedCultivo = cultivo;
    }

    public void addCultivo() {
        Produccion prod = restProduccion.findById(produccionId);
        TipoCultivo tcult = restTipoCultivo.findById(tipoCultivoId);
        Cultivo cultivoToAdd = Cultivo.builder()
                .descripcion(descripcion)
                .produccion(prod)
                .tipoCultivo(tcult)
                .build();
        FacesContext context = FacesContext.getCurrentInstance();

        if(restCultivo.create(cultivoToAdd)){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "CULTIVO ADICIONADO CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-cultivo");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR EL CULTIVO", ""));
        }
        PrimeFaces.current().executeScript("PF('addcultivoDialog').hide()");
    }

    public void editCultivo() {
        Produccion prod = restProduccion.findById(produccionId);
        TipoCultivo tcult = restTipoCultivo.findById(tipoCultivoId);
        Cultivo cultivoToEdit = Cultivo.builder()
                .id(selectedCultivo.getId())
                .descripcion(descripcion)
                .produccion(prod)
                .tipoCultivo(tcult)
                .build();

        FacesContext context = FacesContext.getCurrentInstance();
        if(restCultivo.update(cultivoToEdit)){
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "CULTIVO EDITADO", ""));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-cultivo");
        }
        else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR EL CULTIVO", ""));
        }
        PrimeFaces.current().executeScript("PF('editcultivoDialog').hide()");
    }

    public void deleteCultivo() {
        FacesContext context = FacesContext.getCurrentInstance();
        if(restCultivo.delete(selectedCultivo.getId())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "CULTIVO ELIMINADO CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-cultivo");
        }
        else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR EL CULTIVO", ""));
        }
        PrimeFaces.current().executeScript("PF('deleteCultivoDialog').hide()");
    }
}
