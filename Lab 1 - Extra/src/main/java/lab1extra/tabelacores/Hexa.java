package lab1extra.tabelacores;

// Funções pra mostrar números em hexadecimal e binário.
public class Hexa {

    // Coloca zeros à esquerda até o texto ter o tamanho pedido
    public static String completarComZeros(String texto, int tamanho) {
        while (texto.length() < tamanho) {
            texto = "0" + texto;
        }
        return texto;
    }

    // Índice de 12 bits = 3 dígitos hex. Ex: 384 -> "0x180"
    public static String indice(int indice) {
        String hex = Integer.toHexString(indice).toUpperCase();
        return "0x" + completarComZeros(hex, 3);
    }

    // Cor de 24 bits = 6 dígitos hex. Ex: 1148928 -> "#118800"
    public static String cor(int cor) {
        String hex = Integer.toHexString(cor).toUpperCase();
        return "#" + completarComZeros(hex, 6);
    }

    // Um canal de 8 bits = 2 dígitos hex. Ex: 136 -> "88"
    public static String canal(int valor) {
        String hex = Integer.toHexString(valor).toUpperCase();
        return completarComZeros(hex, 2);
    }

    // Índice em binário, separado de 4 em 4 (1 dígito hex = 4 bits)
    // Ex: 384 -> "0001 1000 0000"
    public static String binario(int indice) {
        String bits = completarComZeros(Integer.toBinaryString(indice), 12);
        return bits.substring(0, 4) + " " + bits.substring(4, 8) + " " + bits.substring(8, 12);
    }

    // Converte o texto digitado em número.
    // Aceita decimal ("384") ou hexadecimal com 0x ("0x180").
    // Retorna -1 se não for um número válido.
    public static int lerIndice(String texto) {
        texto = texto.trim().toUpperCase();

        int base = 10;
        if (texto.startsWith("0X")) {
            texto = texto.substring(2);
            base = 16;
        }

        if (texto.length() == 0) {
            return -1;
        }

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            boolean ehDigito = c >= '0' && c <= '9';
            boolean ehLetraHex = c >= 'A' && c <= 'F';

            if (base == 10 && ehDigito == false) {
                return -1;
            }
            if (base == 16 && ehDigito == false && ehLetraHex == false) {
                return -1;
            }
        }

        if (texto.length() > 5) {
            return -1; // grande demais, com certeza passa de 4095
        }

        return Integer.parseInt(texto, base);
    }
}