package cu.edu.unah.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanificacionCosechaResponse {

    AreaCultivoResponsePK areaCultivoResponsePk;
    String areaDescripcion;
    String cultivoDescripcion;
    String fechaEstimada;
    String estado;
    Long planProd;
    Double produccionReal;
    Double cumplimientoPct;
    String fechaCompletado;
}
