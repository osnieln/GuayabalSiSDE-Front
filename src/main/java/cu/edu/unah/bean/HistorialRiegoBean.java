package cu.edu.unah.bean;

import cu.edu.unah.rest.RestRiego;
import cu.edu.unah.util.RiegoResponse;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Named("historialRiegoBean")
@ViewScoped
@Data
public class HistorialRiegoBean implements Serializable {

    private Long areaId;
    private List<RiegoResponse> historial;

    public void buscarHistorial() {
        if (areaId != null) {
            RestRiego restRiego = new RestRiego();
            historial = restRiego.findHistorialByArea(areaId);
        }
    }
}
