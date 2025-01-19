import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    Stack<Integer> operands = new Stack<>();
    Stack<Character> operators = new Stack<>();
    
    final Map<Character, Integer> PRECEDENCE = Map.of(
            '+', 1,
            '-', 1,
            '*', 2,
            '/', 2,
            '^', 3
    );
    
    
    Calculator(String input) {
        input = input.replaceAll("\\s", "");
        if (validateInput(input)) {
            input = processImplicitMultiplication(input);
            
            tokenize(input);
        
            // finish off the rest of the operators
            while (!this.operators.isEmpty()) {
                processOperator();
            }
            
            // display the result, hopefully the result is the last one in the operands stack
            System.out.println("Answer: " + this.operands.pop());
        }
    }
    
    private void tokenize(String input) {
        String regex = "\\d+|[+\\-*/()^]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        
        while (matcher.find()) {
            String token = matcher.group();
            
            if (token.matches("\\d+")) {
                this.operands.push(Integer.parseInt(token));
            } else if (token.matches("[+\\-*/^]")) {
                Character operator = token.charAt(0);
                
                // handle the operators that have high precedences first
                while (!this.operators.isEmpty() && this.operators.peek() != '(' &&
                        PRECEDENCE.get(this.operators.peek()) >= PRECEDENCE.get(operator)) {
                    processOperator();
                }
                operators.push(operator);
            } else if (token.equals("(")) {
                this.operators.push('(');
            } else if (token.equals(")")) {
                while (!this.operators.isEmpty() && this.operators.peek() != '(') {
                    processOperator();
                }
                if (!this.operators.isEmpty() && this.operators.peek() == '(') {
                    this.operators.pop();
                }
            }
        }
    }
    
    private String processImplicitMultiplication(String input) {
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            
            result.append(current);
            
            if (i < chars.length - 1) {
                char next = chars[i + 1];
                
                if (Character.isDigit(current) && next == '(') {
                    result.append('*');
                }
                else if (current == ')' && Character.isDigit(next)) {
                    result.append('*');
                }
                else if (current == ')' && next == '(') {
                    result.append('*');
                }
            }
        }
        
        return result.toString();
    }
    
    private boolean validateInput(String input) {
        if (input.trim().isEmpty()) {
            System.out.println("Error, cannot be empty");
            return false;
        }
        if (!input.matches("[0-9+\\-*/()^]+")) {
            System.out.println("Error, invalid input in expression");
            return false;
        }
        
        int parenthesisBalance = 0;
        boolean expectingOperand = true;
        for (char ch : input.toCharArray()) {
            if (Character.isDigit(ch)) {
                expectingOperand = false;
            }
            else if (ch == '(') {
                parenthesisBalance++;
                expectingOperand = false;
            }
            else if (ch == ')') {
                if (expectingOperand) {
                    System.out.println("Error, misplaced closing parenthesis");
                    return false;
                }
                parenthesisBalance--;
            }
            else {
                if (expectingOperand) {
                    System.out.println("Error, misplaced operand: " + ch);
                    return false;
                }
                expectingOperand = true;
            }
            
            
            if (parenthesisBalance < 0) {
                System.out.println("Error, unmatched closing parenthesis");
                return false;
            }
        }
        
        if (expectingOperand) {
            System.out.println("Error, expression ends with an operand");
            return false;
        }
        if (parenthesisBalance != 0) {
            System.out.println("Error, unmatched opening parenthesis");
            return false;
        }
        
        // passed all validations
        return true;
    }
    
    // TODO: add case for division by zero
    private void processOperator() {
        Character operator = this.operators.pop();
        int b = this.operands.pop();
        int a = this.operands.pop();
        
        int result = 1;
        switch (operator) {
            case '+': result = a + b; break;
            case '-': result = a - b; break;
            case '*': result = a * b; break;
            case '/': result = a / b; break;
            case '^':  {
                while (b != 0) {
                    result = result * a;
                    b--;
                }
                break;
            }
            default: throw new IllegalStateException("Error, unexpected operator");
        }
        
        
        this.operands.push(result);
    }
}
