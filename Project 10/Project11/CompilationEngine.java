
import java.io.*;

public class CompilationEngine {
    private final JackTokenizer tokenizer;
    private final VMWriter vm;

    public CompilationEngine(JackTokenizer tokenizer, File outputFile) throws IOException {
        this.tokenizer = tokenizer;
        this.vm = new VMWriter(outputFile);
    }

    public void compileClass() throws IOException {
        tokenizer.advance(); // 'class'
        tokenizer.advance(); // className
        tokenizer.advance(); // '{'

        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            if (tokenizer.keyword().equals("function")) {
                compileFunction();
            }
        }

        vm.close();
    }

    private void compileFunction() {
        tokenizer.advance(); // return type
        String name = tokenizer.identifier();
        tokenizer.advance(); // functionName
        tokenizer.advance(); // '('
        tokenizer.advance(); // ')'

        vm.writeFunction(name, 0);
    }
}
