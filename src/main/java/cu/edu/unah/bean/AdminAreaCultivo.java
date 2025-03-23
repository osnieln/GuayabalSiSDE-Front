package cu.edu.unah.bean;

import cu.edu.unah.entity.AreaCultivo;
import cu.edu.unah.rest.RestAreaCultivo;
import cu.edu.unah.util.AreaCultivoResponse;
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
public class AdminAreaCultivo implements Serializable{

    private List<AreaCultivoResponse> listAreaCultivo = new ArrayList<AreaCultivoResponse>();
    private String descripcion ="";

    private AreaCultivoResponse areaCultivo = new AreaCultivoResponse();
    private AreaCultivoResponse selectedAreaCultivo;
    RestAreaCultivo restAreaCultivo = new RestAreaCultivo();
    
    public void init(){
        listAreaCultivo.clear();
        cleanVariables();
        listAreaCultivo = restAreaCultivo.findAllAreaCultivo();
        System.out.println(listAreaCultivo.size());
    }

    public void cleanVariables(){
        descripcion="";
    }

    public void updateSelectedAreaCultivo(AreaCultivoResponse areaCultivoResponse) {
        this.setSelectedAreaCultivo(areaCultivoResponse);
    }

    public void updateSelected_AreaCultivo_toEdit(AreaCultivoResponse areaCultivoResponse) {
        selectedAreaCultivo = areaCultivoResponse;
//        descripcion = areaCultivo.getDescripcion();
    }

    public void updateSelectedAreaCultivoToDelete(AreaCultivoResponse areaCultivoResponse){
        this.selectedAreaCultivo = areaCultivoResponse;
    }

    public void addAreaCultivo() {
        AreaCultivoResponse areaCultivoResponseToAdd = AreaCultivoResponse.builder()
//                        .descripcion(descripcion)
                .build();
        FacesContext context = FacesContext.getCurrentInstance();

        if(restAreaCultivo.create(areaCultivoResponseToAdd)){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PRODUCCION ADICIONADA CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-areaCultivo");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR LA PRODUCCION", ""));
        }
        PrimeFaces.current().executeScript("PF('addareaCultivoDialog').hide()");
    }

    public void editAreaCultivo() {
        AreaCultivoResponse areaCultivoResponseToEdit = AreaCultivoResponse.builder()
//                .id(selectedAreaCultivo.getId())
//                .descripcion(descripcion)
                .build();

        FacesContext context = FacesContext.getCurrentInstance();
        if(restAreaCultivo.update(areaCultivoResponseToEdit)){
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PRODUCCION EDITADA", ""));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-areaCultivo");
        }
        else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR LA PRODUCCION", ""));
        }
        PrimeFaces.current().executeScript("PF('editareaCultivoDialog').hide()");
    }

    public void deleteAreaCultivo() {
        FacesContext context = FacesContext.getCurrentInstance();
        if(restAreaCultivo.delete(selectedAreaCultivo.getAreaCultivoPk())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PRODUCCION ELIMINADA CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-areaCultivo");
        }
        else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR LA PRODUCCION", ""));
        }
        PrimeFaces.current().executeScript("PF('deleteAreaCultivoDialog').hide()");
    }
}
