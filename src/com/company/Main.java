package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Token {
    public enum TokenType {
        IDENTIFIER, ASSIGNMENT, INTEGER, FLOAT, PLUS, MINUS, MULTIPLY, DIVIDE, LPAREN, RPAREN, EOF
    }

    public TokenType type;
    public String lexeme;

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                '}';
    }
}

class Lexer {
    private String input;
    private int position;
    private List<Token> tokens;
    private int line;
    private int column;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.tokens = new ArrayList<>();
        this.line = 1;
        this.column = 1;
    }

    private char peek() {
        if (position < input.length()) {
            return input.charAt(position);
        }
        return '\0'; // End of input
    }

    private char consume() {
        char currentChar = peek();
        if (currentChar != '\0') {
            position++;
            if (currentChar == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
        return currentChar;
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(peek())) {
            consume();
        }
    }

    private void addToken(Token.TokenType type, String lexeme) {
        tokens.add(new Token(type, lexeme));
    }

    public List<Token> tokenize() {
        while (position < input.length()) {
            char currentChar = peek();

            if (Character.isDigit(currentChar) || currentChar == '.') {
                // Tokenize numbers (integers and floats)
                StringBuilder numberLexeme = new StringBuilder();
                boolean isFloat = false;
                while (Character.isDigit(peek()) || peek() == '.') {
                    char digit = consume();
                    if (digit == '.') {
                        if (isFloat) {
                            reportError("Invalid number format");
                            break;
                        }
                        isFloat = true;
                    }
                    numberLexeme.append(digit);
                }
                addToken(isFloat ? Token.TokenType.FLOAT : Token.TokenType.INTEGER, numberLexeme.toString());
            } else if (Character.isLetter(currentChar)) {
                // Tokenize identifiers
                StringBuilder identifierLexeme = new StringBuilder();
                while (Character.isLetterOrDigit(peek())) {
                    identifierLexeme.append(consume());
                }
                addToken(Token.TokenType.IDENTIFIER, identifierLexeme.toString());
            } else {
                switch (currentChar) {
                    case '=':
                        addToken(Token.TokenType.ASSIGNMENT, String.valueOf(consume()));
                        break;
                    case '+':
                        addToken(Token.TokenType.PLUS, String.valueOf(consume()));
                        break;
                    case '-':
                        addToken(Token.TokenType.MINUS, String.valueOf(consume()));
                        break;
                    case '*':
                        addToken(Token.TokenType.MULTIPLY, String.valueOf(consume()));
                        break;
                    case '/':
                        addToken(Token.TokenType.DIVIDE, String.valueOf(consume()));
                        break;
                    case '(':
                        addToken(Token.TokenType.LPAREN, String.valueOf(consume()));
                        break;
                    case ')':
                        addToken(Token.TokenType.RPAREN, String.valueOf(consume()));
                        break;
                    case '\n':
                        // Handle newline character (increment line, reset column)
                        consume();
                        line++;
                        column = 1;
                        break;
                    default:
                        // Invalid character encountered
                        reportError("Invalid character: " + currentChar);
                        consume();
                }
            }

            skipWhitespace();
        }

        // Add EOF token to signify the end of input
        tokens.add(new Token(Token.TokenType.EOF, ""));
        return tokens;
    }

    private void reportError(String message) {
        System.err.println("Lexer Error at Line " + line + ", Column " + column + ": " + message);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Your Input \n");
        String input = scanner.nextLine();
        scanner.close();

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
