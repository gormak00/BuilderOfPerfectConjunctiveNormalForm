package by.bsuir;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SKNFConstructor {
    private int countOfAtomicAndConstant;
    private Map<String, List<Integer>> tableMap;
    private List<String> listOfKeys;
    private final StringBuilder formulaSKNF = new StringBuilder("");
    private final StringBuilder additionsBrackets = new StringBuilder("");

    public void generateSKNF(int countOfAtomicAndConstant, Map<String, List<Integer>> tableMap) {
        this.countOfAtomicAndConstant = countOfAtomicAndConstant;
        this.tableMap = tableMap;
        this.listOfKeys = new ArrayList<>(tableMap.keySet());
        constructFormula();
    }

    private void constructFormula() {
        int counter = 0;
        for (int indexOfRow = 0; indexOfRow < tableMap.get(listOfKeys.get(0)).size(); indexOfRow++) {
            System.out.println("Строка: " + indexOfRow + " из " + tableMap.get(listOfKeys.get(0)).size());
            if (tableMap.get(listOfKeys.get(listOfKeys.size() - 1)).get(indexOfRow) == 1) continue;
            for (int i = 0; i < countOfAtomicAndConstant; i++) {
                if (i == 0) {
                    for (int a = 0; a < countOfAtomicAndConstant - 1; a++) {
                        formulaSKNF.append("(");
                    }
                }
                if (tableMap.get(listOfKeys.get(i)).get(indexOfRow) == 0) {
                    formulaSKNF.append(listOfKeys.get(i));
                } else {
                    formulaSKNF.append("(!").append(listOfKeys.get(i)).append(")");
                }
                if (i > 0) formulaSKNF.append(")\\/");
                else formulaSKNF.append("\\/");
                if (i == countOfAtomicAndConstant - 1) {
                    formulaSKNF.delete(formulaSKNF.length() - 2, formulaSKNF.length());
                }
            }
            if (counter > 0) formulaSKNF.append(")/\\");
            else formulaSKNF.append("/\\");
            counter++;
        }
        for (int a = 0; a < counter - 1; a++) {
            additionsBrackets.append("(");
            System.out.println("Строка: " + a + " из " + (counter - 1));
        }
        formulaSKNF.insert(0, additionsBrackets);
        if (formulaSKNF.length() == 0) System.out.println("\nДля данной формулы не существует СКНФ");
        else {
            formulaSKNF.delete(formulaSKNF.length() - 2, formulaSKNF.length());
            System.out.println("\nФормула СКНФ: " + formulaSKNF);
        }
    }
}
