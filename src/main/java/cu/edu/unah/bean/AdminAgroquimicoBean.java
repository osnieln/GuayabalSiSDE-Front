package cu.edu.unah.bean;

import cu.edu.unah.rest.RestAgroquimico;
import cu.edu.unah.util.AgroquimicoResponse;
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
public class AdminAgroquimicoBean implements Serializable{

    private List<AgroquimicoResponse> agroquimicoResponseList = new ArrayList<AgroquimicoResponse>();
    private String nombre ="";


    private AgroquimicoResponse agroquimico = new AgroquimicoResponse();
    private AgroquimicoResponse selectedAgroquimicoResponse;
    RestAgroquimico restAgroquimico = new RestAgroquimico();


    public void init(){
        agroquimicoResponseList.clear();
        cleanVariables();
        agroquimicoResponseList = restAgroquimico.findAllAgroquimico();
    }

    public void cleanVariables(){
        nombre="";
    }

    public void updateSelectedAgroquimico(AgroquimicoResponse agroquimicoResponse) {
        this.setSelectedAgroquimicoResponse(agroquimicoResponse);
    }

    public void updateSelected_Agroquimico_toEdit(AgroquimicoResponse agroquimicoResponse) {
        selectedAgroquimicoResponse = agroquimicoResponse;
        nombre = agroquimicoResponse.getNombre();
    }

    public void updateSelectedAgroquimicoToDelete(AgroquimicoResponse agroquimicoResponse){
        this.setSelectedAgroquimicoResponse(agroquimicoResponse);
    }

    public void addAgroquimico() {
        AgroquimicoResponse agroquimicoResponseToAdd = AgroquimicoResponse.builder()
                .nombre(nombre)
                .build();
        FacesContext context = FacesContext.getCurrentInstance();

        if (nombre == null || nombre.isBlank()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre del agroquímico es obligatorio.", ""));
            return;
        }
        if(restAgroquimico.create(agroquimicoResponseToAdd)){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "AGROQUIMICO ADICIONADO CORRECTAMENTE", ""));
            init();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR EL AGROQUIMICO", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-agroquimico");
        PrimeFaces.current().executeScript("PF('addagroquimicoDialog').hide()");
    }

    public void editAgroquimico() {
        AgroquimicoResponse agroquimicoResponseBd = restAgroquimico.findById(selectedAgroquimicoResponse.getId());
        agroquimicoResponseBd.setNombre(nombre);

        FacesContext context = FacesContext.getCurrentInstance();
        if (nombre == null || nombre.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre del agroquímico es obligatorio.", ""));
            return;
        }
        if(restAgroquimico.update(agroquimicoResponseBd)){
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "AGROQUIMICO EDITADO", ""));
        }
        else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR EL AGROQUIMICO", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-agroquimico");
        PrimeFaces.current().executeScript("PF('editagroquimicoDialog').hide()");
    }

    public void deleteAgroquimico() {
        FacesContext context = FacesContext.getCurrentInstance();
        if(restAgroquimico.delete(selectedAgroquimicoResponse.getId())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "AGROQUIMICO ELIMINADO CORRECTAMENTE", ""));
            init();
        }
        else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR EL AGROQUIMICO", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-agroquimico");
        PrimeFaces.current().executeScript("PF('deleteagroquimicoDialog').hide()");
    }
}
