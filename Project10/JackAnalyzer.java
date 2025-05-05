//package org.example;

import java.io.File;

public class JackAnalyzer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: JackAnalyzer <inputFileOrDirectory>");
            return;
        }

        File input = new File(args[0]);
        if (input.isDirectory()) {
            for (File file : input.listFiles()) {
                if (file.getName().endsWith(".jack")) {
                    process(file);
                }
            }
        } else if (input.getName().endsWith(".jack")) {
            process(input);
        } else {
            System.out.println("Error: Input must be a .jack file or directory containing .jack files");
        }
    }

    private static void process(File jackFile) {
        try {
            JackTokenizer tokenizer = new JackTokenizer(jackFile);
            CompilationEngine engine = new CompilationEngine(tokenizer, jackFile);
            engine.compileClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
