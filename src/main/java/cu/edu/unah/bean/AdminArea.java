package cu.edu.unah.bean;

import cu.edu.unah.rest.RestArea;
import cu.edu.unah.rest.RestAreaImport;
import cu.edu.unah.util.AreaImportDTO;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Named
@Getter
@Setter
@SessionScoped
public class AdminArea implements Serializable {

    private List<AreaResponse> listArea = new ArrayList<AreaResponse>();
    private List<String> areaImport = new ArrayList<>();
    private String selectedAreaToImport = "";
    private String descripcion = "";

    private AreaResponse area = new AreaResponse();
    private AreaResponse selectedArea;
    RestArea restArea = new RestArea();
    RestAreaImport restAreaImport = new RestAreaImport();
    ColorGenerator colorGenerator = new ColorGenerator();

    String centerCoords = "";

    private MapModel<Long> polygonModel;
    private MapModel<Long> polygonModelInfo;
    private MapModel<Long> polygonModelToImport;
    List<String> legend = new ArrayList<>();
    Map<String, String> legendColor = new HashMap<>();
    boolean isValid = false;
    List<AreaImportDTO> dataImport = new ArrayList<>();

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
        PrimeFaces.current().ajax().update("form:gmap");
    }

    public void getAreasToImport(){
        areaImport = restAreaImport.listAreaImport();
        List<String> capaImportedList = restArea.findDistinctCapa();
        areaImport.removeAll(Arrays.asList("weather", "spatial_ref_sys"));
        areaImport.removeAll(capaImportedList);
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
        cleanVariables();
        listArea = restArea.findAllArea();
        if (listArea == null) listArea = new ArrayList<>();
        initMap();
    }

    public void clearImportDataVariables(){
        dataImport = new ArrayList<>();
        isValid = false;
        areaImport = new ArrayList<>();
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
        AtomicInteger added = new AtomicInteger();
        StringBuilder areasError = new StringBuilder();
        dataImport.forEach(area -> {
            AreaResponse areaToAdd = AreaResponse.builder()
                    .descripcion(area.getDescription())
                    .ubicacion(area.getGeometry())
                    .capa(selectedAreaToImport)
                    .build();
            if (restArea.create(areaToAdd)) added.getAndIncrement();
            else areasError.append(areaToAdd.getDescripcion()).append(", ");
        });

        FacesContext context = FacesContext.getCurrentInstance();

        if (added.get() == dataImport.size()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "TODAS LAS AREAS HAN SIDO ADICIONADAS CORRECTAMENTE", ""));
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "AL MENOS UN AREA NO SE IMPORTÓ CORRECTAMENTE", "Áreas no importadas (posibilidad de duplicados): " + areasError.toString()));
        }
        init();
        clearImportDataVariables();
        PrimeFaces.current().ajax().update("form:messages", "form:dt-area", "dialogs:btnAddModal");
        PrimeFaces.current().executeScript("PF('importAreaDialog').hide()");
        PrimeFaces.current().executeScript("PF('addareaDialog').hide()");
    }

    public void editArea() {
        AreaResponse areaToEdit = AreaResponse.builder()
                .id(selectedArea.getId())
                .descripcion(descripcion)
                .ubicacion(selectedArea.getUbicacion())
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

    public void getAreaInfo(){
        isValid = false;
        if(selectedAreaToImport!=null && !selectedAreaToImport.isEmpty()) {
            isValid = true;
            dataImport = restAreaImport.getAreaImport(selectedAreaToImport);
            polygonModelToImport = new DefaultMapModel<>();
            legend = new ArrayList<>();
            long i = 0L;
            for (AreaImportDTO areaImportDTO : dataImport) {
                String[] points = areaImportDTO.getGeometry().replace("POLYGON((", "").replace("))", "").split(", ");

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
                legend.add(areaImportDTO.getDescription());
                legendColor.put(areaImportDTO.getDescription(), colorGenerator.getLightColor());
                polygonModelToImport.addOverlay(polygon);
            }
            PrimeFaces.current().ajax().update("form:messages", "dialogs:add-area-content", "dialogs:btnAddModal");
        }
    }

    public String translateLegendColor(String legendLabel){
        return legendColor.get(legendLabel);
    }
}
