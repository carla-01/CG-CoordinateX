package app;

import java.util.Scanner;

// Lê os dados digitados pelo usuário no console.
public class LeitorConsole {

    private Scanner scanner;

    public LeitorConsole() {
        scanner = new Scanner(System.in);
    }

    // Lê um número. Aceita "2.5" ou "2,5".
    public double lerNumero(String mensagem) {
        System.out.print(mensagem);
        String texto = scanner.nextLine();
        texto = texto.trim();
        texto = texto.replace(',', '.');
        return Double.parseDouble(texto);
    }

    // Lê a opção do cenário (1 ou 2)
    public int lerCenario() {
        System.out.println("1 - NDC [0,1] x [0,1]");
        System.out.println("2 - NDC [-1,1] x [-1,1]");
        int opcao = (int) lerNumero("Escolha: ");

        while (opcao != 1 && opcao != 2) {
            System.out.println("Escolha 1 ou 2.");
            opcao = (int) lerNumero("Escolha: ");
        }
        return opcao;
    }
}