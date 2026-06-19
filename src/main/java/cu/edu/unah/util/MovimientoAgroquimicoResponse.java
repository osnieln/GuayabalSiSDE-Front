package cu.edu.unah.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoAgroquimicoResponse {

    private Long id;
    private Long agroquimicoId;
    private String agroquimicoNombre;
    private String tipo;
    private Double cantidad;
    private String fecha;
    private Long trabajadorId;
    private String trabajadorNombre;
    private String observaciones;
}
