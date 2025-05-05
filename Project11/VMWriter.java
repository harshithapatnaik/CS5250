
import java.io.*;

public class VMWriter {
    private final PrintWriter writer;

    public VMWriter(File outFile) throws IOException {
        writer = new PrintWriter(new FileWriter(outFile));
    }

    public void writePush(String segment, int index) {
        writer.printf("push %s %d%n", segment, index);
    }

    public void writePop(String segment, int index) {
        writer.printf("pop %s %d%n", segment, index);
    }

    public void writeArithmetic(String command) {
        writer.println(command);
    }

    public void writeLabel(String label) {
        writer.printf("label %s%n", label);
    }

    public void writeGoto(String label) {
        writer.printf("goto %s%n", label);
    }

    public void writeIf(String label) {
        writer.printf("if-goto %s%n", label);
    }

    public void writeCall(String name, int nArgs) {
        writer.printf("call %s %d%n", name, nArgs);
    }

    public void writeFunction(String name, int nLocals) {
        writer.printf("function %s %d%n", name, nLocals);
    }

    public void writeReturn() {
        writer.println("return");
    }

    public void close() {
        writer.close();
    }
}
