import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("quit")) {
                break;
            }
            Calculator calculator = new Calculator(input);
        }
    }
}
