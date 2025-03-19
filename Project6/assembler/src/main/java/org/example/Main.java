package org.example;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Error: Please provide a Hack assembly file.");
            System.err.println("Usage: java HackAssembler <file.asm>");
            return;
        }

        String inputFileName = args[0];
        String outputFileName = inputFileName.replace(".asm", ".hack");

        Parser parser = new Parser(inputFileName);
        SymbolTable symbols = new SymbolTable();
        Code coder = new Code();

        // First pass: Add labels to symbol table
        int lineNumber = 0;
        while (parser.hasMoreCommands()) {
            parser.advance();
            if (parser.commandType().equals("L")) {
                symbols.addEntry(parser.symbol(), lineNumber);
            } else {
                lineNumber++;
            }
        }

        // Restart parser for second pass
        parser.restart();

        // Second pass: Translate assembly to binary
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            int variableAddress = 16;
            while (parser.hasMoreCommands()) {
                parser.advance();
                String binaryCode = "";

                if (parser.commandType().equals("A")) {
                    String symbol = parser.symbol();
                    int value;

                    if (symbol.matches("\\d+")) { // Constant
                        value = Integer.parseInt(symbol);
                    } else if (symbols.contains(symbol)) { // Label or predefined variable
                        value = symbols.getAddress(symbol);
                    } else { // New variable
                        value = variableAddress;
                        symbols.addEntry(symbol, variableAddress++);
                    }

                    binaryCode = String.format("%16s", Integer.toBinaryString(value)).replace(' ', '0');
                } else if (parser.commandType().equals("C")) {
                    String comp = coder.comp(parser.comp());
                    String dest = coder.dest(parser.dest());
                    String jump = coder.jump(parser.jump());
                    binaryCode = "111" + comp + dest + jump;
                }

                if (!binaryCode.isEmpty()) {
                    writer.write(binaryCode);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }

    // ------------------------ Parser Class ------------------------
    static class Parser {
        private final List<String> commands;
        private int current;
        private String cmd;

        public Parser(String fileName) {
            commands = new ArrayList<>();
            current = -1;
            cmd = "";

            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.split("//")[0].trim().replace(" ", ""); // Remove comments and whitespace
                    if (!line.isEmpty()) {
                        commands.add(line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
                System.exit(1);
            }
        }

        public boolean hasMoreCommands() {
            return (current + 1) < commands.size();
        }

        public void advance() {
            if (hasMoreCommands()) {
                current++;
                cmd = commands.get(current);
            }
        }

        public void restart() {
            current = -1;
            cmd = "";
        }

        public String commandType() {
            if (cmd.startsWith("@")) return "A";
            if (cmd.startsWith("(")) return "L";
            return "C";
        }

        public String symbol() {
            if (commandType().equals("A")) return cmd.substring(1);
            if (commandType().equals("L")) return cmd.substring(1, cmd.length() - 1);
            return "";
        }

        public String dest() {
            return commandType().equals("C") && cmd.contains("=") ? cmd.split("=")[0] : "";
        }

        public String comp() {
            if (commandType().equals("C")) {
                String temp = cmd.contains("=") ? cmd.split("=")[1] : cmd;
                return temp.contains(";") ? temp.split(";")[0] : temp;
            }
            return "";
        }

        public String jump() {
            return commandType().equals("C") && cmd.contains(";") ? cmd.split(";")[1] : "";
        }
    }

    // ------------------------ Code Class ------------------------
    static class Code {
        private final Map<String, String> dTable, cTable, jTable;

        public Code() {
            dTable = Map.of("", "000", "M", "001", "D", "010", "MD", "011", "A", "100", "AM", "101", "AD", "110", "AMD", "111");

            cTable = Map.ofEntries(
                    Map.entry("0", "0101010"), Map.entry("1", "0111111"), Map.entry("-1", "0111010"),
                    Map.entry("D", "0001100"), Map.entry("A", "0110000"), Map.entry("M", "1110000"),
                    Map.entry("!D", "0001101"), Map.entry("!A", "0110001"), Map.entry("!M", "1110001"),
                    Map.entry("-D", "0001111"), Map.entry("-A", "0110011"), Map.entry("-M", "1110011"),
                    Map.entry("D+1", "0011111"), Map.entry("A+1", "0110111"), Map.entry("M+1", "1110111"),
                    Map.entry("D-1", "0001110"), Map.entry("A-1", "0110010"), Map.entry("M-1", "1110010"),
                    Map.entry("D+A", "0000010"), Map.entry("D+M", "1000010"), Map.entry("D-A", "0010011"),
                    Map.entry("D-M", "1010011"), Map.entry("A-D", "0000111"), Map.entry("M-D", "1000111"),
                    Map.entry("D&A", "0000000"), Map.entry("D&M", "1000000"), Map.entry("D|A", "0010101"),
                    Map.entry("D|M", "1010101")
            );

            jTable = Map.of("", "000", "JGT", "001", "JEQ", "010", "JGE", "011",
                    "JLT", "100", "JNE", "101", "JLE", "110", "JMP", "111");
        }

        public String dest(String mnemonic) {
            return dTable.getOrDefault(mnemonic, "000");
        }

        public String comp(String mnemonic) {
            return cTable.getOrDefault(mnemonic, "0000000");
        }

        public String jump(String mnemonic) {
            return jTable.getOrDefault(mnemonic, "000");
        }
    }

    // ------------------------ SymbolTable Class ------------------------
    static class SymbolTable {
        private final Map<String, Integer> table;

        public SymbolTable() {
            table = new HashMap<>(Map.ofEntries(
                    Map.entry("R0", 0), Map.entry("R1", 1), Map.entry("R2", 2), Map.entry("R3", 3),
                    Map.entry("R4", 4), Map.entry("R5", 5), Map.entry("R6", 6), Map.entry("R7", 7),
                    Map.entry("R8", 8), Map.entry("R9", 9), Map.entry("R10", 10), Map.entry("R11", 11),
                    Map.entry("R12", 12), Map.entry("R13", 13), Map.entry("R14", 14), Map.entry("R15", 15),
                    Map.entry("SCREEN", 16384), Map.entry("KBD", 24576),
                    Map.entry("SP", 0), Map.entry("LCL", 1), Map.entry("ARG", 2),
                    Map.entry("THIS", 3), Map.entry("THAT", 4)
            ));
        }

        public void addEntry(String symbol, int value) {
            table.put(symbol, value);
        }

        public boolean contains(String symbol) {
            return table.containsKey(symbol);
        }

        public int getAddress(String symbol) {
            return table.get(symbol);
        }
    }
}
