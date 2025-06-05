package cu.edu.unah.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaCultivo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @NonNull
    AreaCultivoPk areaCultivoPk;
    Date fechaRecogida;
    Long planProd;
    Double prodCultivosPermanente;
    Double prodCultivosTemporales;
    Double produccionReal;

    @JoinColumn(name = "cultivoId", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Cultivo cultivo;

    @JoinColumn(name = "areaId", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Area area;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "area_cultivo_agroquimico",
            joinColumns = {
                    @JoinColumn(name = "area_cultivo_areaid", referencedColumnName = "areaId"),
                    @JoinColumn(name = "area_cultivo_cultivoid", referencedColumnName = "cultivoId"),
                    @JoinColumn(name = "area_cultivo_fecha_siembra", referencedColumnName = "fechaSiembra")
            },
            inverseJoinColumns = @JoinColumn(name = "agroquimicoid")
    )
    private List<Agroquimico> agroquimicos;
}
