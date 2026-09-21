package org.input;

import java.util.Scanner;

public class LeitorEntrada {

    private final Scanner scanner;

    public LeitorEntrada() {
        scanner = new Scanner(System.in);
    }

    public double lerDouble(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextDouble();
    }

    public int lerInt(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextInt();
    }

    public void fechar() {
        scanner.close();
    }
}