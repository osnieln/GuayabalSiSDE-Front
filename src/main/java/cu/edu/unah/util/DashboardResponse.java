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
public class DashboardResponse {

    long totalAreas;
    long cultivosActivos;
    long riegosProximosSemana;
    double produccionMesActual;
    long alertasActivas;
    List<ProduccionMensualResponse> produccionMensual;
    List<RiegoMensualResponse> riegosMensual;
    List<CultivoDistribucionResponse> distribucionCultivos;
}
