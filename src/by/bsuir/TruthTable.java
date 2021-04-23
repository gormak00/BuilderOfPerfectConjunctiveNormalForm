package by.bsuir;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TruthTable {
    private int countOfAtomicAndConstant, rows = 0, countOfConstants, numberForRows;
    private Map<String, List<Integer>> tableMap;
    private List<String> listOfKeys;

    public void generateTruthTable(int countOfAtomicAndConstant, int countOfConstants, Map<String, List<Integer>> tableMap) {
        this.countOfAtomicAndConstant = countOfAtomicAndConstant;
        this.countOfConstants = countOfConstants;   //Для корректного создания строк относительно констант
        this.tableMap = tableMap;
        this.listOfKeys = new ArrayList<>(tableMap.keySet());

        //System.out.println(listOfKeys);
        startGenerateTruthTable();
        for (int indexOfCurrentEl = countOfAtomicAndConstant; indexOfCurrentEl < listOfKeys.size(); indexOfCurrentEl++) {
            for (int rowNumber = 0; rowNumber < rows; rowNumber++) {
                generateValueToCurrentPosition(indexOfCurrentEl, rowNumber);
            }
        }
        printTruthTable();
    }

    private void startGenerateTruthTable() {
        rows = (int) Math.pow(2, countOfAtomicAndConstant - countOfConstants);

        /*for (int i=0; i<rows; i++) {  //Отобразить создание таблицы истинности для констант и атомарных формул
            for (int j=countOfAtomicAndConstant-1; j>=0; j--) {
                System.out.print((i/(int) Math.pow(2, j))%2 + " ");
            }
            System.out.println();
        }*/

        for (int i = 0; i < rows; i++) {
            for (int j = countOfAtomicAndConstant - 1; j >= 0; j--) {
                if (listOfKeys.get(j).equals(Parser.getLogicTrue())) {
                    tableMap.get(listOfKeys.get(j)).add(1);
                } else if (listOfKeys.get(j).equals(Parser.getLogicFalse())){
                    tableMap.get(listOfKeys.get(j)).add(0);
                } else tableMap.get(listOfKeys.get(j)).add((i / (int) Math.pow(2, j)) % 2);
            }
        }
    }

    private void generateValueToCurrentPosition(int indexOfCurrentEl, int numberOfRow) {
        String substring, method = "";
        int digit, firstSubstringIndex = -1, secondSubstringIndex = -1, lastPositionOfFirstSb = 0;
        substring = listOfKeys.get(indexOfCurrentEl).substring(1, listOfKeys.get(indexOfCurrentEl).length() - 1);
        if (substring.substring(0, 1).equals(Parser.getNEGATION())) {
            for (int i = indexOfCurrentEl - 1; i >= 0; i--) {
                if (substring.indexOf(listOfKeys.get(i)) == 1) {
                    negationMethod(i, indexOfCurrentEl, numberOfRow); //метод отрицания
                    return;
                }
            }
        }

        for (int i = indexOfCurrentEl - 1; i >= 0; i--) {
            digit = substring.indexOf(listOfKeys.get(i), lastPositionOfFirstSb);
            if (digit == 0) {
                firstSubstringIndex = i;
                lastPositionOfFirstSb = listOfKeys.get(i).length() - 1;
            } else if (digit != -1 && digit > lastPositionOfFirstSb) {
                secondSubstringIndex = i;
                method = substring.substring(digit - 2, digit);
            }
            if (firstSubstringIndex != -1 && secondSubstringIndex != -1) break;
        }

        if(firstSubstringIndex != -1 && secondSubstringIndex == -1){
            secondSubstringIndex = firstSubstringIndex;
            method = substring.substring(lastPositionOfFirstSb + 1, substring.indexOf(listOfKeys.get(firstSubstringIndex), lastPositionOfFirstSb + 1));
        }

        if (method.equals(Parser.getCONJUNCTION())) {
            conjunctionMethod(firstSubstringIndex, secondSubstringIndex, indexOfCurrentEl, numberOfRow);//метод конъюнкции
        } else if (method.equals(Parser.getDISJUNCTION())) {
            disjunctionMethod(firstSubstringIndex, secondSubstringIndex, indexOfCurrentEl, numberOfRow);//метод дизъюнкции
        } else if (method.equals(Parser.getIMPLICATION())) {
            implicationMethod(firstSubstringIndex, secondSubstringIndex, indexOfCurrentEl, numberOfRow);//метод импликации
        } else {
            equivalenceMethod(firstSubstringIndex, secondSubstringIndex, indexOfCurrentEl, numberOfRow);//метод эквиваленции
        }
    }

    private void negationMethod(int positionOfSubFormula, int positionOfFormula, int numberOfRow) {
        int result;
        int valueOfSubFormula = tableMap.get(listOfKeys.get(positionOfSubFormula)).get(numberOfRow);
        if (valueOfSubFormula == 1) result = 0;
        else result = 1;
        tableMap.get(listOfKeys.get(positionOfFormula)).add(result);
    }

    private void conjunctionMethod(int firstSubstringIndex, int secondSubstringIndex, int positionOfFormula, int numberOfRow) {
        int result;
        int valueOfFirstSubstring = tableMap.get(listOfKeys.get(firstSubstringIndex)).get(numberOfRow);
        int valueOfSecondSubstring = tableMap.get(listOfKeys.get(secondSubstringIndex)).get(numberOfRow);
        if (valueOfFirstSubstring == 0 || valueOfSecondSubstring == 0) result = 0;
        else result = 1;
        tableMap.get(listOfKeys.get(positionOfFormula)).add(result);
    }

    private void disjunctionMethod(int firstSubstringIndex, int secondSubstringIndex, int positionOfFormula, int numberOfRow) {
        int result;
        int valueOfFirstSubstring = tableMap.get(listOfKeys.get(firstSubstringIndex)).get(numberOfRow);
        int valueOfSecondSubstring = tableMap.get(listOfKeys.get(secondSubstringIndex)).get(numberOfRow);
        if (valueOfFirstSubstring == 0 && valueOfSecondSubstring == 0) result = 0;
        else result = 1;
        tableMap.get(listOfKeys.get(positionOfFormula)).add(result);
    }

    private void implicationMethod(int firstSubstringIndex, int secondSubstringIndex, int positionOfFormula, int numberOfRow) {
        int result;
        int valueOfFirstSubstring = tableMap.get(listOfKeys.get(firstSubstringIndex)).get(numberOfRow);
        int valueOfSecondSubstring = tableMap.get(listOfKeys.get(secondSubstringIndex)).get(numberOfRow);
        if ((valueOfFirstSubstring == 1 && valueOfSecondSubstring == 1)
                || (valueOfFirstSubstring == 0 && valueOfSecondSubstring == 1)
                || (valueOfFirstSubstring == 0 && valueOfSecondSubstring == 0)) result = 1;
        else result = 0;
        tableMap.get(listOfKeys.get(positionOfFormula)).add(result);
    }

    private void equivalenceMethod(int firstSubstringIndex, int secondSubstringIndex, int positionOfFormula, int numberOfRow) {
        int result;
        int valueOfFirstSubstring = tableMap.get(listOfKeys.get(firstSubstringIndex)).get(numberOfRow);
        int valueOfSecondSubstring = tableMap.get(listOfKeys.get(secondSubstringIndex)).get(numberOfRow);
        if ((valueOfFirstSubstring == 1 && valueOfSecondSubstring == 1)
                || (valueOfFirstSubstring == 0 && valueOfSecondSubstring == 0)) result = 1;
        else result = 0;
        tableMap.get(listOfKeys.get(positionOfFormula)).add(result);
    }

    private void printTruthTable() {
        System.out.println("Таблица истинности:");
        for (String subFormula :
                listOfKeys) {
            System.out.print(subFormula + "  |  ");
        }
        for (int numberOfRow = 0; numberOfRow < tableMap.get(listOfKeys.get(0)).size(); numberOfRow++) {
            System.out.println("");
            for (String listOfKey : listOfKeys) {
                System.out.print(tableMap.get(listOfKey).get(numberOfRow));
                for (int lengthOfKey = 0; lengthOfKey < listOfKey.length() - 1; lengthOfKey++) System.out.print(" ");
                System.out.print("  |  ");

            }
        }
        //System.out.println(tableMap);
    }
}
