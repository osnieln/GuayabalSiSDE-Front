package cu.edu.unah.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {

    long totalAreas;
    long cultivosActivos;
    long riegosProximosSemana;
    double produccionMesActual;
}
