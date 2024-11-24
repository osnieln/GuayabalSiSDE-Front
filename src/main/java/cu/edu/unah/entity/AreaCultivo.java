package cu.edu.unah.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

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
}
