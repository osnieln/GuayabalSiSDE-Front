package cu.edu.unah.bean;

import cu.edu.unah.entity.Area;
import cu.edu.unah.entity.Cultivo;
import cu.edu.unah.entity.Riego;
import cu.edu.unah.rest.RestArea;
import cu.edu.unah.rest.RestAreaCultivo;
import cu.edu.unah.rest.RestCultivo;
import cu.edu.unah.rest.RestRiego;
import cu.edu.unah.util.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.timeline.TimelineSelectEvent;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class AdminAreaCultivo implements Serializable {

    private List<AreaCultivoResponse> listAreaCultivo = new ArrayList<AreaCultivoResponse>();
    private String descripcion = "";

    List<AreaResponse> areaResponsesList = new ArrayList<>();
    List<Cultivo> cultivoList = new ArrayList<>();
    List<RiegoResponse> riegoResponseList = new ArrayList<>();

    private AreaCultivoResponse areaCultivo = new AreaCultivoResponse();
    private AreaCultivoResponse selectedAreaCultivo;

    private TimelineModel<String, ?> model;

    String cultivoSelected = "";
    String areaSelected = "";
    RiegoResponse riegoSelected = new RiegoResponse();
    private boolean riegoToEdit = false;

    RestAreaCultivo restAreaCultivo = new RestAreaCultivo();
    RestArea restArea = new RestArea();
    RestCultivo restCultivo = new RestCultivo();
    RestRiego restRiego = new RestRiego();

    Date fechaSiembra = new Date(System.currentTimeMillis());
    Date fechaRecogida = new Date(System.currentTimeMillis());

    Date fechaPlanificada = new Date(System.currentTimeMillis());
    Date fechaReal = new Date(System.currentTimeMillis());
    double planProduccion, prodPermanente, prodTemporal, prodReal;

    public void init() {
        listAreaCultivo.clear();
        cleanVariables();
        areaResponsesList.clear();
        cultivoList.clear();
        listAreaCultivo = restAreaCultivo.findAllAreaCultivo();
        areaResponsesList = restArea.findAllArea();
        cultivoList = restCultivo.findAllCultivo();
        riegoResponseList = new ArrayList<>();
        System.out.println(listAreaCultivo.size());
    }

    public void initAddAreaCultivo() {
        riegoResponseList = new ArrayList<>();
        riegoToEdit = false;
        PrimeFaces.current().ajax().update("form:messages", "dialogs:add-areaCultivo-content");
    }

    public void cleanVariables() {
        descripcion = "";
    }

    public void updateSelectedAreaCultivo(AreaCultivoResponse areaCultivoResponse) throws ParseException {
        this.setSelectedAreaCultivo(areaCultivoResponse);
        this.riegoResponseList = restRiego.findByAreaCultivoPk(areaCultivoResponse.getAreaCultivoResponsePK());
        model = new TimelineModel<>();
        for (int i = 0; i < riegoResponseList.size(); i++) {
            Date fechaPlanificacion = DateFormatter.formatUtil(riegoResponseList.get(i).getFechaPlanificacion());
            Date fechaReal = DateFormatter.formatUtil(riegoResponseList.get(i).getFechaReal());
            model.add(TimelineEvent.<String>builder().data("Riego planificado " + (i + 1)).startDate(fechaPlanificacion.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).build());
            model.add(TimelineEvent.<String>builder().data("Riego real " + (i + 1)).startDate(fechaReal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).build());
        }
    }

    public void onSelect(TimelineSelectEvent<String> e) {
        TimelineEvent<String> timelineEvent = e.getTimelineEvent();
        String data = timelineEvent.getData();
        int elementSelectedPosition = Integer.parseInt(data.split(" ")[2]);
        RiegoResponse elementSelected = riegoResponseList.get(elementSelectedPosition - 1);
        FacesMessage msg = new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Riego " + elementSelectedPosition,
                "<strong>Fecha Planificada:</strong> " + elementSelected.getFechaPlanificacion() +
                        "<br/> <strong>Fecha Real:</strong> " + elementSelected.getFechaReal()
        );
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void updateSelected_AreaCultivo_toEdit(AreaCultivoResponse areaCultivoResponse) {
        selectedAreaCultivo = areaCultivoResponse;
        areaSelected = areaCultivoResponse.getAreaCultivoResponsePK().getAreaId().toString();
        cultivoSelected = areaCultivoResponse.getAreaCultivoResponsePK().getCultivoId().toString();
        fechaSiembra = DateFormatter.format(areaCultivoResponse.getAreaCultivoResponsePK().getFechaSiembra());
        fechaRecogida = DateFormatter.format(areaCultivoResponse.getFechaRecogida());
        planProduccion = areaCultivoResponse.getPlanProd();
        prodPermanente = areaCultivoResponse.getProdCultivosPermanente();
        prodTemporal = areaCultivoResponse.getProdCultivosTemporales();
        prodReal = areaCultivoResponse.getProduccionReal();
        this.riegoResponseList = restRiego.findByAreaCultivoPk(areaCultivoResponse.getAreaCultivoResponsePK());
        riegoToEdit = true;
        PrimeFaces.current().ajax().update("form:messages", "dialogs:edit-areaCultivo-content");
    }

    public void updateSelectedRiegoToEdit(RiegoResponse riegoResponse) {
        riegoSelected = riegoResponse;
        fechaPlanificada = DateFormatter.format(riegoResponse.getFechaPlanificacion());
        fechaReal = DateFormatter.format(riegoResponse.getFechaReal());
    }

    public void updateSelectedAreaCultivoToDelete(AreaCultivoResponse areaCultivoResponse) {
        this.selectedAreaCultivo = areaCultivoResponse;
    }

    public void updateSelectedRiegoToDelete(RiegoResponse riegoResponse) {
        riegoSelected = riegoResponse;
    }

    public void addAreaCultivo() {
        try {
            if (!validarFechas(DateFormatter.formatUtil(DateFormatter.formatUtil(fechaSiembra)), fechaRecogida))
                return;
        } catch (ParseException e) {
            return;
        }
        AreaCultivoResponsePK areaCultivoResponsePK = AreaCultivoResponsePK.builder()
                .areaId(Long.parseLong(areaSelected))
                .cultivoId(Long.parseLong(cultivoSelected))
                .fechaSiembra(DateFormatter.formatUtil(fechaSiembra))
                .build();

        riegoResponseList.forEach(riegoResponse -> {
            riegoResponse.setAreaCultivoResponsePk(areaCultivoResponsePK);
        });
        AreaCultivoResponse areaCultivoResponseToAdd = AreaCultivoResponse.builder()
                .areaCultivoResponsePK(areaCultivoResponsePK)
                .fechaRecogida(DateFormatter.formatUtil(fechaRecogida))
                .prodCultivosPermanente(prodPermanente)
                .prodCultivosTemporales(prodTemporal)
                .produccionReal(prodReal)
                .planProd((long) planProduccion)
                .build();

        FacesContext context = FacesContext.getCurrentInstance();

        if (restAreaCultivo.create(areaCultivoResponseToAdd)) {
            riegoResponseList.forEach(riegoResponse -> {
                restRiego.create(riegoResponse);
            });
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ESPACIAL ADICIONADO CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-areaCultivo");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR ESPACIAL", ""));
        }
        PrimeFaces.current().executeScript("PF('addareaCultivoDialog').hide()");
    }

    public void addRiego() {
        FacesContext context = FacesContext.getCurrentInstance();

        RiegoResponse riegoResponse = RiegoResponse.builder()
                .fechaPlanificacion(DateFormatter.formatUtil(fechaPlanificada))
                .fechaReal(DateFormatter.formatUtil(fechaReal))
                .build();
        if (riegoResponseList.contains(riegoResponse)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "YA EXISTE UN RIEGO EN CON ESAS FECHAS", ""));
        } else {
            riegoResponseList.add(riegoResponse);
            PrimeFaces.current().ajax().update(riegoToEdit ? "dialogs:dt-riego1" : "dialogs:dt-riego");
            PrimeFaces.current().executeScript("PF('addRiegoDialog').hide()");
        }
    }

    public void editRiego() {
        FacesContext context = FacesContext.getCurrentInstance();

        int position = riegoResponseList.indexOf(riegoSelected);
        if (position == -1) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR RIEGO", ""));
            return;
        }
        RiegoResponse riegoResponse = riegoResponseList.get(riegoResponseList.indexOf(riegoSelected));
        riegoResponse.setFechaPlanificacion(DateFormatter.formatUtil(fechaPlanificada));
        riegoResponse.setFechaReal(DateFormatter.formatUtil(fechaReal));
        riegoResponseList.set(position, riegoResponse);

        PrimeFaces.current().ajax().update(riegoToEdit ? "dialogs:dt-riego1" : "dialogs:dt-riego");
        PrimeFaces.current().executeScript("PF('editRiegoDialog').hide()");

    }

    public void deleteRiego() {
        FacesContext context = FacesContext.getCurrentInstance();
        riegoResponseList.remove(riegoSelected);
        PrimeFaces.current().ajax().update(riegoToEdit ? "dialogs:dt-riego1" : "dialogs:dt-riego");
        PrimeFaces.current().executeScript("PF('deleteRiegoDialog').hide()");
    }

    public void editAreaCultivo() {
        try {
            if (!validarFechas(DateFormatter.formatUtil(DateFormatter.formatUtil(fechaSiembra)), fechaRecogida))
                return;
        } catch (ParseException e) {
            return;
        }

        riegoResponseList.forEach(riegoResponse -> {
            riegoResponse.setAreaCultivoResponsePk(selectedAreaCultivo.getAreaCultivoResponsePK());
        });

        AreaCultivoResponse areaCultivoResponseToEdit = AreaCultivoResponse.builder()
                .areaCultivoResponsePK(selectedAreaCultivo.getAreaCultivoResponsePK())
                .fechaRecogida(DateFormatter.formatUtil(fechaRecogida))
                .prodCultivosPermanente(prodPermanente)
                .prodCultivosTemporales(prodTemporal)
                .produccionReal(prodReal)
                .planProd((long) planProduccion)
                .build();

        FacesContext context = FacesContext.getCurrentInstance();
        if (restAreaCultivo.update(areaCultivoResponseToEdit)) {
            List<RiegoResponse> riegoResponseList1 = restRiego.findByAreaCultivoPk(selectedAreaCultivo.getAreaCultivoResponsePK());
            riegoResponseList1.forEach(riegoResponse -> {
                restRiego.delete(riegoResponse.getId());
            });
            riegoResponseList.forEach(riegoResponse -> {
                restRiego.create(riegoResponse);
            });
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ESPACIAL EDITADO", ""));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-areaCultivo");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR ESPACIAL", ""));
        }
        PrimeFaces.current().executeScript("PF('editareaCultivoDialog').hide()");
    }

    public void deleteAreaCultivo() {
        FacesContext context = FacesContext.getCurrentInstance();
        List<RiegoResponse> riegoResponseList1 = restRiego.findByAreaCultivoPk(selectedAreaCultivo.getAreaCultivoResponsePK());
        riegoResponseList1.forEach(riegoResponse -> {
            restRiego.delete(riegoResponse.getId());
        });
        if (restAreaCultivo.delete(selectedAreaCultivo.getAreaCultivoResponsePK())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ESPACIAL ELIMINADO CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-areaCultivo");
        } else {
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

    public boolean validarFechas(Date fechaInicio, Date fechaFinal) {
        if (!fechaInicio.before(fechaFinal)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR: LA FECHA DE SIEMBRA DEBE SER ANTERIOR QUE LA FECHA DE RECOGIDA", ""));
            return false;
        }
        return true;
    }
}
