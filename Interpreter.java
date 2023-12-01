import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        String[] lines = input.split(";");
        Map<String, Integer> variables = new HashMap<String, Integer>();
        for (String line : lines) {
            String[] assignment = line.split("=");
            if (assignment.length != 2) {
                System.out.println("error");
                return;
            }
            String variable = assignment[0].trim();
            String expression = assignment[1].trim();
            if (!isValidVariable(variable)) {
                System.out.println("error");
                return;
            }
            int value = evaluate(expression, variables);
            if (value == Integer.MIN_VALUE) {
                System.out.println("error");
                return;
            }
            variables.put(variable, value);
        }
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    private static boolean isValidVariable(String variable) {
        if (variable.length() == 0) {
            return false;
        }
        if (!Character.isLetter(variable.charAt(0)) && variable.charAt(0) != '_') {
            return false;
        }
        for (int i = 1; i < variable.length(); i++) {
            if (!Character.isLetterOrDigit(variable.charAt(i)) && variable.charAt(i) != '_') {
                return false;
            }
        }
        return true;
    }

    private static int evaluate(String expression, Map<String, Integer> variables) {
        List<String> tokens = getTokens(expression);
        return evalExp(tokens, variables);
    }

    private static List<String> getTokens(String expression) {
        List<String> result = new ArrayList<String>();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '+' || c == '-' || c == '*' || c == '(' || c == ')') {
                if (buffer.length() != 0) {
                    result.add(buffer.toString());
                    buffer = new StringBuffer();
                }
                result.add(c + "");
            } else {
                buffer.append(c);
            }
        }
        if (buffer.length() != 0) {
            result.add(buffer.toString());
        }
        return result;
    }

    private static int evalExp(List<String> tokens, Map<String, Integer> variables) {
        int result = evalTerm(tokens, variables);
        if (result == Integer.MIN_VALUE) {
            return result;
        }
        while (tokens.size() != 0) {
            String op = tokens.remove(0);
            if (!op.equals("+") && !op.equals("-")) {
                tokens.add(0, op);
                return result;
            }
            int value = evalTerm(tokens, variables);
            if (value == Integer.MIN_VALUE) {
                return value;
            }
            if (op.equals("+")) {
                result += value;
            } else {
                result -= value;
            }
        }
        return result;
    }

    private static int evalTerm(List<String> tokens, Map<String, Integer> variables) {
        int result = evalFact(tokens, variables);
        if (result == Integer.MIN_VALUE) {
            return result;
        }
        while (tokens.size() != 0) {
            String op = tokens.remove(0);
            if (!op.equals("*")) {
                tokens.add(0, op);
                return result;
            }
            int value = evalFact(tokens, variables);
            if (value == Integer.MIN_VALUE) {
                return value;
            }
            result *= value;
        }
        return result;
    }

    private static int evalFact(List<String> tokens, Map<String, Integer> variables) {
        String next = tokens.remove(0);
        if (next.equals("(")) {
            int result = evalExp(tokens, variables);
            if (result == Integer.MIN_VALUE) {
                return result;
            }
            if (tokens.size() == 0 || !tokens.remove(0).equals(")")) {
                return Integer.MIN_VALUE;
            }
            return result;
        } else if (next.equals("+") || next.equals("-")) {
            int value = evalFact(tokens, variables);
            if (value == Integer.MIN_VALUE) {
                return value;
            }
            if (next.equals("+")) {
                return value;
            } else {
                return -value;
            }
        } else {
            if (isValidInteger(next)) {
                return Integer.parseInt(next);
            } else if (variables.containsKey(next)) {
                return variables.get(next);
            } else {
                return Integer.MIN_VALUE;
            }
        }
    }

    private static boolean isValidInteger(String s) {
        if (s.charAt(0) == '0' && s.length() > 1) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
