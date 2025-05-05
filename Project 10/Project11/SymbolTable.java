
import java.util.*;

public class SymbolTable {
    private final Map<String, Symbol> classScope = new HashMap<>();
    private final Map<String, Symbol> subroutineScope = new HashMap<>();
    private final Map<String, Integer> indices = new HashMap<>();

    public SymbolTable() {
        reset();
    }

    public void startSubroutine() {
        subroutineScope.clear();
        indices.put("argument", 0);
        indices.put("var", 0);
    }

    public void define(String name, String type, String kind) {
        int index = varCount(kind);
        Symbol symbol = new Symbol(type, kind, index);

        if (kind.equals("static") || kind.equals("field")) {
            classScope.put(name, symbol);
        } else {
            subroutineScope.put(name, symbol);
        }

        indices.put(kind, index + 1);
    }

    public int varCount(String kind) {
        return indices.getOrDefault(kind, 0);
    }

    public String kindOf(String name) {
        if (subroutineScope.containsKey(name)) {
            return subroutineScope.get(name).kind;
        } else if (classScope.containsKey(name)) {
            return classScope.get(name).kind;
        }
        return "none";
    }

    public String typeOf(String name) {
        if (subroutineScope.containsKey(name)) {
            return subroutineScope.get(name).type;
        } else if (classScope.containsKey(name)) {
            return classScope.get(name).type;
        }
        return null;
    }

    public int indexOf(String name) {
        if (subroutineScope.containsKey(name)) {
            return subroutineScope.get(name).index;
        } else if (classScope.containsKey(name)) {
            return classScope.get(name).index;
        }
        return -1;
    }

    public void reset() {
        classScope.clear();
        subroutineScope.clear();
        indices.clear();
        indices.put("static", 0);
        indices.put("field", 0);
        indices.put("argument", 0);
        indices.put("var", 0);
    }

    private static class Symbol {
        String type;
        String kind;
        int index;

        Symbol(String type, String kind, int index) {
            this.type = type;
            this.kind = kind;
            this.index = index;
        }
    }
}
