package by.bsuir;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        while (true) {
            System.out.println("Введите формулу: ");
            Scanner scannerString = new Scanner(System.in);
            StringBuilder formula = new StringBuilder(scannerString.next());
            new Parser(formula);
        }
    }
}
