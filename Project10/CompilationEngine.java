//package org.example;

import java.io.*;

public class CompilationEngine {
    private JackTokenizer tokenizer;
    private PrintWriter writer;

    public CompilationEngine(JackTokenizer tokenizer, File inputFile) throws IOException {
        this.tokenizer = tokenizer;
        String outputFile = inputFile.getPath().replace(".jack", ".xml");
        this.writer = new PrintWriter(new FileWriter(outputFile));
    }

    public void compileClass() {
        writeTag("<class>");
        while (tokenizer.hasMoreTokens()) {
            String type = tokenizer.tokenType();
            switch (type) {
                case "keyword":
                    writeToken("keyword", tokenizer.keyWord());
                    break;
                case "symbol":
                    String symbol = tokenizer.symbol() + "";
                    if (symbol.equals("<")) symbol = "&lt;";
                    else if (symbol.equals(">")) symbol = "&gt;";
                    else if (symbol.equals("&")) symbol = "&amp;";
                    writeToken("symbol", symbol);
                    break;
                case "identifier":
                    writeToken("identifier", tokenizer.identifier());
                    break;
                case "integerConstant":
                    writeToken("integerConstant", String.valueOf(tokenizer.intVal()));
                    break;
                case "stringConstant":
                    writeToken("stringConstant", tokenizer.stringVal());
                    break;
            }
            tokenizer.advance();
        }
        writeTag("</class>");
        writer.close();
    }

    private void writeToken(String tag, String content) {
        writer.println("<" + tag + "> " + content + " </" + tag + ">");
    }

    private void writeTag(String tag) {
        writer.println(tag);
    }
}
