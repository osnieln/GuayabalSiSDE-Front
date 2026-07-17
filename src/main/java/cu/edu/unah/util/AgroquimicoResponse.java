package cu.edu.unah.util;

import cu.edu.unah.entity.Agroquimico;
import cu.edu.unah.entity.AreaCultivo;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgroquimicoResponse {

    @Id
    private Long id;

    private String nombre;

    private Double stockActual;

    private Double stockMinimo;

    private List<AreaCultivoResponsePK> areaCultivoResponsePKListList;

    public static AgroquimicoResponse map(Agroquimico agroquimico) {
        if(agroquimico == null) return null;
        if(agroquimico.getAreaCultivos() == null) agroquimico.setAreaCultivos(new ArrayList<>());
        List<AreaCultivo> areaCultivoList = agroquimico.getAreaCultivos().stream().toList();
        List<AreaCultivoResponsePK> areaCultivoResponsePKList = new ArrayList<>();
        areaCultivoList.forEach(areaCultivo -> {
            areaCultivoResponsePKList.add(AreaCultivoResponsePK.map(areaCultivo.getAreaCultivoPk()));
        });
        return AgroquimicoResponse.builder()
                .id(agroquimico.getId())
                .nombre(agroquimico.getNombre())
                .stockActual(agroquimico.getStockActual())
                .stockMinimo(agroquimico.getStockMinimo())
                .areaCultivoResponsePKListList(areaCultivoResponsePKList)
                .build();
    }

    public static Agroquimico map(AgroquimicoResponse agroquimicoResponse) {
        if(agroquimicoResponse == null) return null;
        if(agroquimicoResponse.getAreaCultivoResponsePKListList() == null) agroquimicoResponse.setAreaCultivoResponsePKListList(new ArrayList<>());
        List<AreaCultivo> areaCultivoList = new ArrayList<>();
        agroquimicoResponse.getAreaCultivoResponsePKListList().forEach(areaCultivoResponsePK -> {
            areaCultivoList.add(AreaCultivo.builder()
                    .areaCultivoPk(AreaCultivoResponsePK.map(areaCultivoResponsePK))
                    .build());
        });

        return Agroquimico.builder()
                .id(agroquimicoResponse.getId())
                .areaCultivos(areaCultivoList)
                .nombre(agroquimicoResponse.getNombre())
                .stockActual(agroquimicoResponse.getStockActual())
                .stockMinimo(agroquimicoResponse.getStockMinimo())
                .build();

    }
}