package cu.edu.unah.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RendimientoResponse {

    Long areaId;
    String areaDescripcion;
    Long cultivoId;
    String cultivoDescripcion;
    String fechaSiembra;
    Long planProd;
    Double produccionReal;
    Double rendimientoPorcentaje;
}
