//package org.example;

import java.io.*;
import java.util.*;

public class JackTokenizer {
    private List<String> tokens;
    private int currentTokenIndex;
    private static final Set<String> keywords = new HashSet<>(Arrays.asList(
            "class", "constructor", "function", "method", "field", "static", "var",
            "int", "char", "boolean", "void", "true", "false", "null", "this",
            "let", "do", "if", "else", "while", "return"
    ));
    private static final Set<Character> symbols = new HashSet<>(Arrays.asList(
            '{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'
    ));

    public JackTokenizer(File inputFile) throws IOException {
        tokens = new ArrayList<>();
        currentTokenIndex = 0;
        String content = readFile(inputFile);
        content = removeComments(content);
        tokenize(content);
    }

    private String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        reader.close();
        return builder.toString();
    }

    private String removeComments(String input) {
        return input.replaceAll("//.*|/\\*(.|\\R)*?\\*/", "");
    }

    private void tokenize(String input) {
        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (symbols.contains(c)) {
                tokens.add(String.valueOf(c));
                i++;
                continue;
            }

            if (c == '"') {
                int j = i + 1;
                while (j < input.length() && input.charAt(j) != '"') {
                    j++;
                }
                tokens.add(input.substring(i, j + 1));
                i = j + 1;
                continue;
            }

            if (Character.isDigit(c)) {
                int j = i;
                while (j < input.length() && Character.isDigit(input.charAt(j))) {
                    j++;
                }
                tokens.add(input.substring(i, j));
                i = j;
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                int j = i;
                while (j < input.length() && (Character.isLetterOrDigit(input.charAt(j)) || input.charAt(j) == '_')) {
                    j++;
                }
                tokens.add(input.substring(i, j));
                i = j;
                continue;
            }

            i++;
        }
    }

    public boolean hasMoreTokens() {
        return currentTokenIndex < tokens.size();
    }

    public void advance() {
        if (hasMoreTokens()) {
            currentTokenIndex++;
        }
    }

    public String tokenType() {
        String token = tokens.get(currentTokenIndex);
        if (keywords.contains(token)) {
            return "keyword";
        } else if (symbols.contains(token.charAt(0)) && token.length() == 1) {
            return "symbol";
        } else if (token.startsWith("\"")) {
            return "stringConstant";
        } else if (Character.isDigit(token.charAt(0))) {
            return "integerConstant";
        } else {
            return "identifier";
        }
    }

    public String keyWord() {
        return tokens.get(currentTokenIndex);
    }

    public char symbol() {
        return tokens.get(currentTokenIndex).charAt(0);
    }

    public String identifier() {
        return tokens.get(currentTokenIndex);
    }

    public int intVal() {
        return Integer.parseInt(tokens.get(currentTokenIndex));
    }

    public String stringVal() {
        return tokens.get(currentTokenIndex).substring(1, tokens.get(currentTokenIndex).length() - 1);
    }

    public String currentToken() {
        return tokens.get(currentTokenIndex);
    }
}
