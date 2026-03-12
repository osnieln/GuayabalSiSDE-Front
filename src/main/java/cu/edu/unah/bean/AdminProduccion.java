package cu.edu.unah.bean;

import cu.edu.unah.entity.Authorities;
import cu.edu.unah.entity.AuthoritiesPK;
import cu.edu.unah.entity.Produccion;
import cu.edu.unah.entity.Produccion;
import cu.edu.unah.rest.RestAuthorities;
import cu.edu.unah.rest.RestProduccion;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class AdminProduccion implements Serializable{

    private List<Produccion> listProduccion = new ArrayList<Produccion>();
    private String descripcion ="";

    private Produccion produccion = new Produccion();
    private Produccion selectedProduccion;
    RestProduccion restProduccion = new RestProduccion();
    
    public void init(){
        cleanVariables();
        listProduccion = restProduccion.findAllProduccion();
        if (listProduccion == null) listProduccion = new ArrayList<>();
    }

    public void cleanVariables(){
        descripcion="";
    }

    public void updateSelectedProduccion(Produccion produccion) {
        this.setSelectedProduccion(produccion);
    }

    public void updateSelected_Produccion_toEdit(Produccion produccion) {
        selectedProduccion = produccion;
        descripcion = produccion.getDescripcion();
    }

    public void updateSelectedProduccionToDelete(Produccion produccion){
        this.selectedProduccion = produccion;
    }

    public void addProduccion() {
        Produccion produccionToAdd = Produccion.builder()
                        .descripcion(descripcion)
                .build();
        FacesContext context = FacesContext.getCurrentInstance();

        if(restProduccion.create(produccionToAdd)){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PRODUCCION ADICIONADA CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-produccion");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR LA PRODUCCION", ""));
        }
        PrimeFaces.current().executeScript("PF('addproduccionDialog').hide()");
    }

    public void editProduccion() {
        Produccion produccionToEdit = Produccion.builder()
                .id(selectedProduccion.getId())
                .descripcion(descripcion)
                .build();

        FacesContext context = FacesContext.getCurrentInstance();
        if(restProduccion.update(produccionToEdit)){
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PRODUCCION EDITADA", ""));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-produccion");
        }
        else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR LA PRODUCCION", ""));
        }
        PrimeFaces.current().executeScript("PF('editproduccionDialog').hide()");
    }

    public void deleteProduccion() {
        FacesContext context = FacesContext.getCurrentInstance();
        if(restProduccion.delete(selectedProduccion.getId())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PRODUCCION ELIMINADA CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-produccion");
        }
        else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR LA PRODUCCION", ""));
        }
        PrimeFaces.current().executeScript("PF('deleteProduccionDialog').hide()");
    }
}
