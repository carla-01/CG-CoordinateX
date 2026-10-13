package core.dispositivo;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

// O dispositivo gráfico real: o monitor do computador.
// ndh e ndv são a resolução lida do sistema.
public class Monitor {

    public static GraphicsDevice getDispositivo() {
        GraphicsEnvironment ambiente = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return ambiente.getDefaultScreenDevice();
    }

    // Número de posições endereçáveis na horizontal
    public static int getNdh() {
        return getDispositivo().getDisplayMode().getWidth();
    }

    // Número de posições endereçáveis na vertical
    public static int getNdv() {
        return getDispositivo().getDisplayMode().getHeight();
    }
}
