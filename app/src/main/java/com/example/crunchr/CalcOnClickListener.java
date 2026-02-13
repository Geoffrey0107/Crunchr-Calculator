package com.example.crunchr;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CalcOnClickListener implements View.OnClickListener{

    private final TextView display;
    private String infix = "";
    private boolean justEvaluated = false;

    public CalcOnClickListener(TextView display) {
        this.display = display;
    }
    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        String label = b.getText().toString();

        if (label.equals("Clear")) {
            display.setText("0");
            infix = "";
            justEvaluated = false;
            return;
        }
        String current = display.getText().toString();

        if (label.matches("[0-9]")) {
            if (justEvaluated) {
                infix = label;
                justEvaluated = false;
            } else if (infix.equals("0")) {
                infix = label;
            } else {
                infix += label;
            }
            display.setText(infix);
            return;
        }

        if (label.equals(".")) {
            if (justEvaluated) {
                current = "0.";
                infix = "0.";
                justEvaluated = false;
            } else {
                int lastOp = Math.max(
                        Math.max(current.lastIndexOf("+"), current.lastIndexOf("-")),
                        Math.max(current.lastIndexOf("*"), current.lastIndexOf("/"))
                );

                String currentNumber = current.substring(lastOp + 1);

                if (currentNumber.contains(".")) {
                    return;
                }
                if (currentNumber.isEmpty()) {
                    current += "0.";
                    infix += "0.";
                } else {
                    current += ".";
                    infix += ".";
                }
                display.setText(current);
                return;
            }
        }
        if (label.matches("[+\\-*/^]")) {
            if (justEvaluated) {
                current = display.getText().toString();
                infix = current;
                justEvaluated = false;
            }
            if (infix.isEmpty()){
                return;
            }
            if (infix.substring(infix.length() - 1).matches("[+\\-*/^]")) {
                infix = infix.substring(0, infix.length() - 1) + label;
            } else {
                infix += label;
            }
            current = infix;
            display.setText(infix);

            return;
        }

        if(label.equals("Enter")){
            if (infix.isEmpty() || infix.matches(".*[+\\-*/^]$")) {
                Toast.makeText(display.getContext(), "Invalid equation", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double result = evaluate(infix);
                String resultStr = String.valueOf(result);

                CalcHistory.add(infix, resultStr);

                display.setText(resultStr);
                infix = resultStr;

                justEvaluated = true;

            } catch (IllegalArgumentException e) {
                Toast.makeText(display.getContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private double evaluate(String infixExpression) throws IllegalArgumentException {
        String[] tokens = tokenize(infixExpression);
        String[] postfix = infixToPostfix(tokens);
        return evaluatePostfix(postfix);
    }

    private String[] tokenize(String infix) throws IllegalArgumentException {
        java.util.ArrayList<String> tokens = new java.util.ArrayList<>();
        int i = 0;
        while(i<infix.length()){
            char c = infix.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < infix.length() && (Character.isDigit(infix.charAt(i)) || infix.charAt(i) == '.')) {
                    num.append(infix.charAt(i));
                    i++;
                }
                tokens.add(num.toString());
                continue;  // don't increment i again
            }
            if ("+-*/^".indexOf(c) != -1) {
                tokens.add(String.valueOf(c));
                i++;
                continue;
            }
            throw new IllegalArgumentException("Invalid character: " + c);
        }
        return tokens.toArray(new String[0]);
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(String token) {
        return token.equals("+") ||
                token.equals("-") ||
                token.equals("*") ||
                token.equals("/") ||
                token.equals("^");
    }
    private int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }
    private boolean isLeftAssociative(String op) {
        return !op.equals("^");     // ^ is right-associative, others are left
    }

    private String[] infixToPostfix(String[] tokens) throws IllegalArgumentException {
        java.util.ArrayList<String> output = new java.util.ArrayList<>();
        java.util.Stack<String> operators = new java.util.Stack<>();

        for (String token : tokens) {
            // We'll handle three cases: number, operator, (invalid for now)

            // Case 1: Number (integer or decimal)
            if (isNumber(token)) {
                output.add(token);
                continue;
            }

            if (isOperator(token)) {
                while (!operators.isEmpty()) {
                    String top = operators.peek();
                    if (precedence(top) > precedence(token) ||
                            (precedence(top) == precedence(token) && isLeftAssociative(token))) {
                        output.add(operators.pop());
                    } else {
                        break;
                    }
                }
                operators.push(token);
                continue;
            }

            throw new IllegalArgumentException("Unknown token: " + token);
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        return output.toArray(new String[0]);
    }

    private double evaluatePostfix(String[] postfix) throws IllegalArgumentException {
        java.util.Stack<Double> stack = new java.util.Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                // Push number to stack
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                // Need two operands for binary operator
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid postfix: not enough operands");
                }

                double b = stack.pop();  // right operand
                double a = stack.pop();  // left operand

                double result;
                switch (token) {
                    case "+":
                        result = a + b;
                        break;
                    case "-":
                        result = a - b;
                        break;
                    case "*":
                        result = a * b;
                        break;
                    case "/":
                        if (b == 0) {
                            throw new IllegalArgumentException("Division by zero");
                        }
                        result = a / b;
                        break;
                    case "^":
                        result = Math.pow(a, b);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operator: " + token);
                }

                stack.push(result);
            } else {
                throw new IllegalArgumentException("Invalid token in postfix: " + token);
            }
        }

        // After processing, stack should have exactly one result
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix: too many/few results");
        }

        return stack.pop();
    }
}
