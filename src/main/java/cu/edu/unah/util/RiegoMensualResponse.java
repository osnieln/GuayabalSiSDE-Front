package cu.edu.unah.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiegoMensualResponse {

    private String mes;
    private long planificados;
    private long ejecutados;
}
