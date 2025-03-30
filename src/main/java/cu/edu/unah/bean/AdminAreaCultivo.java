package cu.edu.unah.bean;

import cu.edu.unah.entity.Area;
import cu.edu.unah.entity.Cultivo;
import cu.edu.unah.rest.RestArea;
import cu.edu.unah.rest.RestAreaCultivo;
import cu.edu.unah.rest.RestCultivo;
import cu.edu.unah.util.AreaCultivoResponse;
import cu.edu.unah.util.AreaCultivoResponsePK;
import cu.edu.unah.util.AreaResponse;
import cu.edu.unah.util.DateFormatter;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class AdminAreaCultivo implements Serializable{

    private List<AreaCultivoResponse> listAreaCultivo = new ArrayList<AreaCultivoResponse>();
    private String descripcion ="";

    List<AreaResponse> areaResponsesList = new ArrayList<>();
    List<Cultivo> cultivoList = new ArrayList<>();

    private AreaCultivoResponse areaCultivo = new AreaCultivoResponse();
    private AreaCultivoResponse selectedAreaCultivo;

    String cultivoSelected = "";
    String areaSelected = "";

    RestAreaCultivo restAreaCultivo = new RestAreaCultivo();
    RestArea restArea = new RestArea();
    RestCultivo restCultivo = new RestCultivo();

    Date fechaSiembra = new Date(System.currentTimeMillis());
    Date fechaRecogida = new Date(System.currentTimeMillis());
    double planProduccion, prodPermanente, prodTemporal, prodReal;

    public void init(){
        listAreaCultivo.clear();
        cleanVariables();
        areaResponsesList.clear();
        cultivoList.clear();
        listAreaCultivo = restAreaCultivo.findAllAreaCultivo();
        areaResponsesList = restArea.findAllArea();
        cultivoList = restCultivo.findAllCultivo();
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
        areaSelected = areaCultivoResponse.getAreaCultivoResponsePK().getAreaId().toString();
        cultivoSelected = areaCultivoResponse.getAreaCultivoResponsePK().getCultivoId().toString();
        fechaSiembra = DateFormatter.format(areaCultivoResponse.getAreaCultivoResponsePK().getFechaSiembra());
        fechaRecogida= DateFormatter.format(areaCultivoResponse.getFechaRecogida());
        planProduccion = areaCultivoResponse.getPlanProd();
        prodPermanente =areaCultivoResponse.getProdCultivosPermanente();
        prodTemporal = areaCultivoResponse.getProdCultivosTemporales();
        prodReal = areaCultivoResponse.getProduccionReal();
    }

    public void updateSelectedAreaCultivoToDelete(AreaCultivoResponse areaCultivoResponse){
        this.selectedAreaCultivo = areaCultivoResponse;
    }

    public void addAreaCultivo() {
        AreaCultivoResponse areaCultivoResponseToAdd = AreaCultivoResponse.builder()
                .areaCultivoResponsePK(
                        AreaCultivoResponsePK.builder()
                                .areaId(Long.parseLong(areaSelected))
                                .cultivoId(Long.parseLong(cultivoSelected))
                                .fechaSiembra(DateFormatter.formatUtil(fechaSiembra))
                                .build()
                )
                .fechaRecogida(DateFormatter.formatUtil(fechaRecogida))
                .prodCultivosPermanente(prodPermanente)
                .prodCultivosTemporales(prodTemporal)
                .produccionReal(prodReal)
                .planProd((long) planProduccion)
                .build();

        try {
            if(!validarFechas(DateFormatter.formatUtil(DateFormatter.formatUtil(fechaSiembra)), fechaRecogida))
                return;
        } catch (ParseException e) {
            return;
        }

        FacesContext context = FacesContext.getCurrentInstance();

        if(restAreaCultivo.create(areaCultivoResponseToAdd)){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ESPACIAL ADICIONADO CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-areaCultivo");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR ESPACIAL", ""));
        }
        PrimeFaces.current().executeScript("PF('addareaCultivoDialog').hide()");
    }

    public void editAreaCultivo() {
        AreaCultivoResponse areaCultivoResponseToEdit = AreaCultivoResponse.builder()
                .areaCultivoResponsePK(selectedAreaCultivo.getAreaCultivoResponsePK())
                .fechaRecogida(DateFormatter.formatUtil(fechaRecogida))
                .prodCultivosPermanente(prodPermanente)
                .prodCultivosTemporales(prodTemporal)
                .produccionReal(prodReal)
                .planProd((long) planProduccion)
                .build();

        try {
            if(!validarFechas(DateFormatter.formatUtil(selectedAreaCultivo.getAreaCultivoResponsePK().getFechaSiembra()), fechaRecogida))
                return;
        } catch (ParseException e) {
            return;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        if(restAreaCultivo.update(areaCultivoResponseToEdit)){
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ESPACIAL EDITADO", ""));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-areaCultivo");
        }
        else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR ESPACIAL", ""));
        }
        PrimeFaces.current().executeScript("PF('editareaCultivoDialog').hide()");
    }

    public void deleteAreaCultivo() {
        FacesContext context = FacesContext.getCurrentInstance();
        if(restAreaCultivo.delete(selectedAreaCultivo.getAreaCultivoResponsePK())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ESPACIAL ELIMINADO CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-areaCultivo");
        }
        else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR ESPACIAL", ""));
        }
        PrimeFaces.current().executeScript("PF('deleteAreaCultivoDialog').hide()");
    }

    public AreaResponse findAreaById(long id) {
        return restArea.findById(id);
    }

    public Cultivo findCultivoById(long id) {
        return restCultivo.findById(id);
    }

    public boolean validarFechas(Date fechaInicio, Date fechaFinal){
        if (!fechaInicio.before(fechaFinal)){
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR: LA FECHA DE SIEMBRA DEBE SER ANTERIOR QUE LA FECHA DE RECOGIDA", ""));
            return false;
        }
        return true;
    }
}
