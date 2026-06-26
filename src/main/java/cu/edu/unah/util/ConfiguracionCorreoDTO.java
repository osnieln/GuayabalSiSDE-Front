package cu.edu.unah.util;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionCorreoDTO {
    private String host;
    private int puerto;
    private String usuario;
    private String contrasena;
    private String remitente;
}
