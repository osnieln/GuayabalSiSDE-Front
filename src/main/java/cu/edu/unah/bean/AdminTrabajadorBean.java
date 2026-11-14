package cu.edu.unah.bean;

import cu.edu.unah.rest.RestArea;
import cu.edu.unah.rest.RestRiego;
import cu.edu.unah.rest.RestTrabajador;
import cu.edu.unah.util.AreaResponse;
import cu.edu.unah.util.RiegoResponse;
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
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class AdminTrabajadorBean implements Serializable {

    private List<TrabajadorResponse> listTrabajador = new ArrayList<TrabajadorResponse>();
    private List<AreaResponse> areaList = new ArrayList<AreaResponse>();
    private List<RiegoResponse> riegoList = new ArrayList<RiegoResponse>();

    private String nombre = "";
    private String identificacion = "";
    private String cargo = "";
    private String telefono = "";
    private boolean activo = true;
    private List<Long> areaIds = new ArrayList<Long>();
    private List<Long> riegoIds = new ArrayList<Long>();

    private TrabajadorResponse selectedTrabajadorResponse;
    RestTrabajador restTrabajador = new RestTrabajador();
    RestArea restArea = new RestArea();
    RestRiego restRiego = new RestRiego();

    public void init() {
        cleanVariables();
        listTrabajador = restTrabajador.findAllTrabajador();
        if (listTrabajador == null) listTrabajador = new ArrayList<>();
        areaList = restArea.findAllArea();
        if (areaList == null) areaList = new ArrayList<>();
        riegoList = restRiego.findAllRiegoResponse();
        if (riegoList == null) riegoList = new ArrayList<>();
    }

    public void cleanVariables() {
        nombre = "";
        identificacion = "";
        cargo = "";
        telefono = "";
        activo = true;
        areaIds = new ArrayList<>();
        riegoIds = new ArrayList<>();
    }

    public String getAreaDescripciones(TrabajadorResponse trabajadorResponse) {
        if (trabajadorResponse == null || trabajadorResponse.getAreaIds() == null || trabajadorResponse.getAreaIds().isEmpty()) return "-";
        List<String> nombres = new ArrayList<>();
        trabajadorResponse.getAreaIds().forEach(id -> areaList.stream()
                .filter(a -> a.getId().equals(id)).findFirst()
                .ifPresent(a -> nombres.add(a.getDescripcion())));
        return String.join(", ", nombres);
    }

    public String getRiegoDescripciones(TrabajadorResponse trabajadorResponse) {
        if (trabajadorResponse == null || trabajadorResponse.getRiegoIds() == null || trabajadorResponse.getRiegoIds().isEmpty()) return "-";
        List<String> fechas = new ArrayList<>();
        trabajadorResponse.getRiegoIds().forEach(id -> riegoList.stream()
                .filter(r -> r.getId().equals(id)).findFirst()
                .ifPresent(r -> fechas.add(r.getFechaPlanificacion())));
        return String.join(", ", fechas);
    }

    public void updateSelectedTrabajador(TrabajadorResponse trabajadorResponse) {
        this.setSelectedTrabajadorResponse(trabajadorResponse);
    }

    public void updateSelected_Trabajador_toEdit(TrabajadorResponse trabajadorResponse) {
        selectedTrabajadorResponse = trabajadorResponse;
        nombre = trabajadorResponse.getNombre();
        identificacion = trabajadorResponse.getIdentificacion();
        cargo = trabajadorResponse.getCargo();
        telefono = trabajadorResponse.getTelefono();
        activo = trabajadorResponse.isActivo();
        areaIds = trabajadorResponse.getAreaIds() != null ? new ArrayList<>(trabajadorResponse.getAreaIds()) : new ArrayList<>();
        riegoIds = trabajadorResponse.getRiegoIds() != null ? new ArrayList<>(trabajadorResponse.getRiegoIds()) : new ArrayList<>();
    }

    public void updateSelectedTrabajadorToDelete(TrabajadorResponse trabajadorResponse) {
        this.setSelectedTrabajadorResponse(trabajadorResponse);
    }

    public void addTrabajador() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (nombre == null || nombre.isBlank()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre del trabajador es obligatorio.", ""));
            return;
        }
        if (identificacion == null || identificacion.isBlank()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La identificación del trabajador es obligatoria.", ""));
            return;
        }
        TrabajadorResponse trabajadorToAdd = TrabajadorResponse.builder()
                .nombre(nombre)
                .identificacion(identificacion)
                .cargo(cargo)
                .telefono(telefono)
                .activo(activo)
                .areaIds(areaIds)
                .riegoIds(riegoIds)
                .build();
        if (restTrabajador.create(trabajadorToAdd)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "TRABAJADOR ADICIONADO CORRECTAMENTE", ""));
            init();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR EL TRABAJADOR", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-trabajador");
        PrimeFaces.current().executeScript("PF('addtrabajadorDialog').hide()");
    }

    public void editTrabajador() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (nombre == null || nombre.isBlank()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre del trabajador es obligatorio.", ""));
            return;
        }
        if (identificacion == null || identificacion.isBlank()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La identificación del trabajador es obligatoria.", ""));
            return;
        }
        TrabajadorResponse trabajadorToEdit = TrabajadorResponse.builder()
                .id(selectedTrabajadorResponse.getId())
                .nombre(nombre)
                .identificacion(identificacion)
                .cargo(cargo)
                .telefono(telefono)
                .activo(activo)
                .areaIds(areaIds)
                .riegoIds(riegoIds)
                .build();
        if (restTrabajador.update(trabajadorToEdit)) {
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "TRABAJADOR EDITADO", ""));
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR EL TRABAJADOR", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-trabajador");
        PrimeFaces.current().executeScript("PF('edittrabajadorDialog').hide()");
    }

    public void deleteTrabajador() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (restTrabajador.delete(selectedTrabajadorResponse.getId())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "TRABAJADOR ELIMINADO CORRECTAMENTE", ""));
            init();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR EL TRABAJADOR", ""));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-trabajador");
        PrimeFaces.current().executeScript("PF('deletetrabajadorDialog').hide()");
    }
}
