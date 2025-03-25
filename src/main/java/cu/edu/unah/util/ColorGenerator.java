package cu.edu.unah.util;

import lombok.Data;

import java.util.concurrent.ThreadLocalRandom;

@Data
public class ColorGenerator {

    String color, lightColor, darkColor;

    private static final double DARK_FACTOR = 0.7;
    private static final double LIGHT_FACTOR = 0.3;

    // Genera color principal y sus variantes en una sola llamada
    public void generateRandomColorSet() {
        color = generateRandomColor();
        String [] colors = generateColorVariants(color);
        lightColor = colors[1];
        darkColor = colors[0];
    }

    // Genera un color hexadecimal aleatorio (#RRGGBB)
    private String generateRandomColor() {
        // Uso de ThreadLocalRandom para mejor rendimiento en multi-hilo
        int rgb = ThreadLocalRandom.current().nextInt(0xFFFFFF + 1);
        return String.format("#%06X", rgb);
    }

    // Genera ambas variantes (oscura y clara) en una sola operación
    public String[] generateColorVariants(String hexColor) {
        // Parseo rápido eliminando el #
        int color = Integer.parseInt(hexColor.substring(1), 16);

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // Calcula variantes
        String darker = formatColor(
                (int)(r * DARK_FACTOR),
                (int)(g * DARK_FACTOR),
                (int)(b * DARK_FACTOR)
        );

        String lighter = formatColor(
                r + (int)((255 - r) * (1 - LIGHT_FACTOR)),
                g + (int)((255 - g) * (1 - LIGHT_FACTOR)),
                b + (int)((255 - b) * (1 - LIGHT_FACTOR))
        );

        return new String[]{darker, lighter};
    }

    // Método auxiliar para formatear componentes RGB a hexadecimal
    private String formatColor(int r, int g, int b) {
        // Asegura que los valores estén en el rango correcto
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        return String.format("#%02X%02X%02X", r, g, b);
    }
}
