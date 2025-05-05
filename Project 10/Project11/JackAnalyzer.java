
import java.io.*;

public class JackAnalyzer {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java JackAnalyzer <inputFile or directory>");
            return;
        }

        File input = new File(args[0]);
        File[] files;

        if (input.isDirectory()) {
            files = input.listFiles((dir, name) -> name.endsWith(".jack"));
        } else {
            files = new File[]{input};
        }

        if (files == null) {
            System.out.println("No .jack files found.");
            return;
        }

        for (File jackFile : files) {
            String content = readFile(jackFile);
            JackTokenizer tokenizer = new JackTokenizer(content);

            String outputName = jackFile.getAbsolutePath().replace(".jack", ".vm");
            File outputFile = new File(outputName);
            CompilationEngine engine = new CompilationEngine(tokenizer, outputFile);
            engine.compileClass();
        }
    }

    private static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append(" ");
        }
        reader.close();
        return builder.toString();
    }
}
