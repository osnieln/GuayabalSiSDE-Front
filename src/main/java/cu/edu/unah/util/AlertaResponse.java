package cu.edu.unah.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertaResponse {

    String tipo;
    String prioridad;
    String mensaje;
    String fechaReferencia;
    AreaCultivoResponsePK areaCultivoResponsePk;
    Long agroquimicoId;
}
