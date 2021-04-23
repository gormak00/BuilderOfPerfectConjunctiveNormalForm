package by.bsuir;

import java.util.*;

public class Parser {
    private final static String LOGIC_TRUE = "1";
    private final static String LOGIC_FALSE = "0";
    private final static String IMPLICATION = "->";
    private final static String CONJUNCTION = "/\\";
    private final static String DISJUNCTION = "\\/";
    private final static String NEGATION = "!";
    private final static String EQUIVALENCE = "~";
    private final static String LEFT_BRACKET = "(";
    private final static String RIGHT_BRACKET = ")";
    private final static List<Character> symbol = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
    private final static List<String> binaryBundle = Arrays.asList(IMPLICATION, CONJUNCTION, DISJUNCTION, EQUIVALENCE);
    private final Map<Integer, String> subFormulaMap = new HashMap<>();
    private List<String> subFormulaList = new ArrayList<>();
    private final Map<String, List<Integer>> tableMap = new LinkedHashMap<>();
    private int counter = 0, countOfAtomicAndConstant = 0, countOfConstants = 0;

    public Parser(StringBuilder formula) {

        if (isFormula(formula)) {
            System.out.println("Данная строка является формулой языка логики высказываний");
            subFormulaMap.put(++counter, formula.toString());

            for (Map.Entry entry : subFormulaMap.entrySet()) {
                if (!subFormulaList.contains(entry.getValue().toString())) {
                    subFormulaList.add(entry.getValue().toString());
                }
            }

            //Сортировка, чтобы сперва шли все буквы + константы, а потом уже скобки
            List<String> subListOfAtomicAndConstant = new ArrayList<>();
            List<String> subListOfSecondPart = new ArrayList<>();
            for (String subFormula : subFormulaList) {
                if (subFormula.startsWith(LEFT_BRACKET)) subListOfSecondPart.add(subFormula);
                else subListOfAtomicAndConstant.add(subFormula);
            }

            countOfAtomicAndConstant = subListOfAtomicAndConstant.size();
            for (String subFormula : subListOfAtomicAndConstant) {
                if (subFormula.equals(LOGIC_FALSE) || subFormula.equals(LOGIC_TRUE)) countOfConstants++;
            }

            subListOfAtomicAndConstant.addAll(subListOfSecondPart);
            subFormulaList = subListOfAtomicAndConstant;

            subFormulaList.forEach(subFormula -> tableMap.put(subFormula, new ArrayList<>()));
            /*System.out.println(countOfAtomicAndConstant);
            System.out.println(subFormulaList);
            System.out.println(subFormulaMap);
            System.out.println(tableMap);*/

            TruthTable truthTable = new TruthTable();
            truthTable.generateTruthTable(countOfAtomicAndConstant, countOfConstants, tableMap);

            SKNFConstructor sknfConstructor = new SKNFConstructor();
            sknfConstructor.generateSKNF(countOfAtomicAndConstant, tableMap);

        } else System.out.println("Данная строка не является формулой языка логики высказываний");

    }

    private boolean isFormula(StringBuilder formula) {
        if (formula.length() == 0 || formula.toString().equals("()")) return false;
        if (formula.toString().equals(LOGIC_TRUE) && formula.length() == 1 || formula.toString().equals(LOGIC_FALSE) && formula.length() == 1) {
            subFormulaMap.put(++counter, formula.toString());
            return true;
        }

        if (formula.substring(0, 1).equals(LEFT_BRACKET) && formula.substring(formula.length() - 1, formula.length()).equals(RIGHT_BRACKET) && isAtomicFormula(new StringBuilder(formula.substring(1, formula.length() - 1))))
            return false;
        if (isAtomicFormula(formula)) return true;
        if (isUnaryComplexFormula(formula)) return true;
        if (isBinaryFormula(formula)) return true;
        return false;
    }

    private boolean isAtomicFormula(StringBuilder formula) {
        if (symbol.contains(formula.charAt(0)) && formula.length() == 1) {
            subFormulaMap.put(++counter, formula.toString());
            return true;
        }
        if (!symbol.contains(formula.charAt(0)) && formula.length() == 1) return false;
        return false;
    }

    private boolean isUnaryComplexFormula(StringBuilder formula) {
        StringBuilder substringFormula;

        if (!formula.substring(0, 1).equals(LEFT_BRACKET)) return false;
        if (!formula.substring(formula.length() - 1, formula.length()).equals(RIGHT_BRACKET)) return false;

        if (formula.substring(1, 2).equals(NEGATION)) {
            substringFormula = new StringBuilder(formula.substring(2, formula.length() - 1));
        } else substringFormula = new StringBuilder(formula.substring(1, formula.length() - 1));

        if (isFormula(substringFormula)) {
            subFormulaMap.put(++counter, substringFormula.toString());
            return true;
        }
        return false;
    }

    private boolean isBinaryFormula(StringBuilder formula) {
        StringBuilder firstSubstring = null, secondSubstring = null;
        List<Integer> listOfBinaryBundlesIndexes = new ArrayList<>();
        if (!formula.substring(0, 1).equals(LEFT_BRACKET)) return false;
        if (!formula.substring(formula.length() - 1, formula.length()).equals(RIGHT_BRACKET)) return false;

        binaryBundle.forEach(bundle -> {
            int position = 0;
            while (position != -1) {
                position = formula.indexOf(bundle, position + 1);
                if (position != -1) listOfBinaryBundlesIndexes.add(position);
            }
        });
        Collections.sort(listOfBinaryBundlesIndexes);
        if (listOfBinaryBundlesIndexes.size() != 0) {
            int indexOfBundle = 0;
            int countOfBrackets = 0;
            for (int i = 1; i < formula.length() - 1; i++) {
                if (formula.substring(i, i + 1).equals(LEFT_BRACKET)) {
                    countOfBrackets++;
                } else if (formula.substring(i, i + 1).equals(RIGHT_BRACKET)) {
                    countOfBrackets--;
                }
                if (i == listOfBinaryBundlesIndexes.get(indexOfBundle)) {
                    if (indexOfBundle < listOfBinaryBundlesIndexes.size() - 1) indexOfBundle++;
                    if (countOfBrackets == 0) {
                        firstSubstring = new StringBuilder(formula.substring(1, i));
                        if (formula.substring(i, i + 2).equals(IMPLICATION) || formula.substring(i, i + 2).equals(CONJUNCTION) || formula.substring(i, i + 2).equals(DISJUNCTION))
                            secondSubstring = new StringBuilder(formula.substring(i + 2, formula.length() - 1));
                        else secondSubstring = new StringBuilder(formula.substring(i + 1, formula.length() - 1));
                        break;
                    }
                }
            }
        }
        if (firstSubstring == null && secondSubstring == null) return false;
        if (isFormula(firstSubstring) && isFormula(secondSubstring)) {
            subFormulaMap.put(++counter, firstSubstring.toString());
            subFormulaMap.put(++counter, secondSubstring.toString());
            return true;
        }
        return false;
    }

    public static String getIMPLICATION() {
        return IMPLICATION;
    }

    public static String getCONJUNCTION() {
        return CONJUNCTION;
    }

    public static String getDISJUNCTION() {
        return DISJUNCTION;
    }

    public static String getNEGATION() {
        return NEGATION;
    }

    public static String getEQUIVALENCE() {
        return EQUIVALENCE;
    }

    public static String getLogicTrue() {
        return LOGIC_TRUE;
    }

    public static String getLogicFalse() {
        return LOGIC_FALSE;
    }
}
