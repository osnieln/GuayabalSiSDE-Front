package cu.edu.unah.bean;

import cu.edu.unah.rest.RestAreaCultivo;
import cu.edu.unah.util.RendimientoResponse;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Named("rendimientoBean")
@ViewScoped
@Data
public class RendimientoBean implements Serializable {

    private List<RendimientoResponse> rendimientos;

    @PostConstruct
    public void init() {
        cargarRendimiento();
    }

    public void cargarRendimiento() {
        RestAreaCultivo restAreaCultivo = new RestAreaCultivo();
        rendimientos = restAreaCultivo.getRendimiento();
    }
}
