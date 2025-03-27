package cu.edu.unah.bean;

import cu.edu.unah.entity.Area;
import cu.edu.unah.rest.RestArea;
import cu.edu.unah.util.AreaResponse;
import cu.edu.unah.util.ColorGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class AdminArea implements Serializable {

    private List<AreaResponse> listArea = new ArrayList<AreaResponse>();
    private String descripcion = "";

    private AreaResponse area = new AreaResponse();
    private AreaResponse selectedArea;
    RestArea restArea = new RestArea();
    ColorGenerator colorGenerator = new ColorGenerator();

    String centerCoords = "";

    private MapModel<Long> polygonModel;
    private MapModel<Long> polygonModelInfo;

    @PostConstruct
    public void initMap() {
        listArea = restArea.findAllArea();
        polygonModel = new DefaultMapModel<>();
        long i = 0L;
        for (AreaResponse areaResponse : listArea) {
            String[] points = areaResponse.getUbicacion().replace("POLYGON ((", "").replace("))", "").split(", ");
            Polygon<Long> polygon = new Polygon<>();
            polygon.setData(1L);
            for (String point : points) {
                String[] coords = point.split(" ");
                LatLng latLng = new LatLng(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]));
                polygon.getPaths().add(latLng);
            }
            polygon.setData(i++);
            polygon.setStrokeOpacity(0.7);
            polygon.setFillOpacity(0.7);
            colorGenerator.generateRandomColorSet();
            polygon.setStrokeColor(colorGenerator.getDarkColor());
            polygon.setFillColor(colorGenerator.getLightColor());
            polygonModel.addOverlay(polygon);
        }

    }

    public void onPolygonSelect(OverlaySelectEvent<Long> event) {
        Overlay<Long> overlay = event.getOverlay();
        AreaResponse areaResponse = listArea.get(Math.toIntExact((overlay.getData())));
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Area: "
                        + areaResponse.getDescripcion() + " ",
                        null));
    }

    public void init() {
        listArea.clear();
        cleanVariables();
        listArea = restArea.findAllArea();
    }

    public void cleanVariables() {
        descripcion = "";
    }

    public void updateSelectedArea(AreaResponse area) {
        this.setSelectedArea(area);
        polygonModelInfo = new DefaultMapModel<>();
        String[] points = area.getUbicacion().replace("POLYGON ((", "").replace("))", "").split(", ");
        centerCoords = "";
        Polygon<Long> polygon = new Polygon<>();
        for (String point : points) {
            String[] coords = point.split(" ");
            if (centerCoords.isEmpty()) centerCoords = coords[1] + "," + coords[0];
            LatLng latLng = new LatLng(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]));
            polygon.getPaths().add(latLng);
        }
        polygon.setStrokeOpacity(0.7);
        polygon.setFillOpacity(0.7);
        colorGenerator.generateRandomColorSet();
        polygon.setStrokeColor(colorGenerator.getDarkColor());
        polygon.setFillColor(colorGenerator.getLightColor());
        polygonModelInfo.addOverlay(polygon);
    }

    public void updateSelected_Area_toEdit(AreaResponse area) {
        selectedArea = area;
        descripcion = area.getDescripcion();
    }

    public void updateSelectedAreaToDelete(AreaResponse area) {
        this.selectedArea = area;
    }

    public void addArea() {
        AreaResponse areaToAdd = AreaResponse.builder()
                .descripcion(descripcion)
                .ubicacion("")
                .build();
        FacesContext context = FacesContext.getCurrentInstance();

        if (restArea.create(areaToAdd)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "AREA ADICIONADA CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-area");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR EL AREA", ""));
        }
        PrimeFaces.current().executeScript("PF('addareaDialog').hide()");
    }

    public void editArea() {
        AreaResponse areaToEdit = AreaResponse.builder()
                .id(selectedArea.getId())
                .descripcion(descripcion)
                .ubicacion("")
                .build();

        FacesContext context = FacesContext.getCurrentInstance();
        if (restArea.update(areaToEdit)) {
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "AREA EDITADA", ""));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-area");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR EL AREA", ""));
        }
        PrimeFaces.current().executeScript("PF('editareaDialog').hide()");
    }

    public void deleteArea() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (restArea.delete(selectedArea.getId())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "AREA ELIMINADA CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-area");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR EL AREA", ""));
        }
        PrimeFaces.current().executeScript("PF('deleteAreaDialog').hide()");
    }
}
