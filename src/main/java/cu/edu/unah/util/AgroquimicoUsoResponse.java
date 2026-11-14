package cu.edu.unah.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgroquimicoUsoResponse {

    Long id;
    String nombre;
    int cantidadUsos;
}
