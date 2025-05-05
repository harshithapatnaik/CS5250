
import java.util.*;

public class JackTokenizer {
    private final List<String> tokens;
    private int current;
    private String token;

    public JackTokenizer(String input) {
        tokens = tokenize(input);
        current = 0;
    }

    private List<String> tokenize(String input) {
        List<String> list = new ArrayList<>();
        input = input.replaceAll("//.*|/\\*.*?\\*/", "").replaceAll("\\\\s+", " ");
        String symbols = "{}()[].,;+-*/&|<>=~";
        StringBuilder token = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (symbols.indexOf(c) != -1) {
                if (token.length() > 0) {
                    list.add(token.toString());
                    token.setLength(0);
                }
                list.add(String.valueOf(c));
            } else if (c == ' ') {
                if (token.length() > 0) {
                    list.add(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(c);
            }
        }
        if (token.length() > 0) list.add(token.toString());
        return list;
    }

    public boolean hasMoreTokens() {
        return current < tokens.size();
    }

    public void advance() {
        if (hasMoreTokens()) {
            token = tokens.get(current++);
        }
    }

    public String tokenType() {
        if (isKeyword(token)) return "keyword";
        if (isSymbol(token)) return "symbol";
        if (token.matches("\\d+")) return "integerConstant";
        if (token.startsWith("\"") && token.endsWith("\"")) return "stringConstant";
        return "identifier";
    }

    public String keyword() { return token; }
    public String symbol() { return token; }
    public String identifier() { return token; }
    public int intVal() { return Integer.parseInt(token); }
    public String stringVal() { return token.substring(1, token.length() - 1); }
    public String currentToken() { return token; }

    private boolean isKeyword(String s) {
        return Arrays.asList("class","constructor","function","method","field","static",
                "var","int","char","boolean","void","true","false",
                "null","this","let","do","if","else","while","return").contains(s);
    }

    private boolean isSymbol(String s) {
        return s.length() == 1 && "{}()[].,;+-*/&|<>=~".contains(s);
    }
}
