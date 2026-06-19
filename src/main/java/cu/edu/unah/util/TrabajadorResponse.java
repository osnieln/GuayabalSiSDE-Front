package cu.edu.unah.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrabajadorResponse {

    private Long id;

    private String nombre;

    private String identificacion;

    private String cargo;

    private String telefono;

    private boolean activo;

    private List<Long> areaIds;

    private List<Long> riegoIds;
}
