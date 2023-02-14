package viva;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import viva.base.lexers.AbstractLexer;
import viva.base.parsers.AbstractParser;
import viva.core.lexers.VivaLexer;
import viva.core.parsers.VivaParser;
import viva.utils.Formatter;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: vivac <file>");
            return;
        }
        
        File file = new File(args[0]);
        
        try (FileInputStream stream = new FileInputStream(file)) {
            AbstractLexer lexer = new VivaLexer(new String(stream.readAllBytes()));

            lexer.tokenize();

            AbstractParser parser = new VivaParser(lexer.getOutput());

            parser.parse();
            
            if (parser.getErrors() != 0) {
                return;
            }

            Formatter formatter = new Formatter();
            
            System.out.println("Reconstructed / Formatted Input:");
            System.out.println(formatter.visit(parser.getOutput()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}