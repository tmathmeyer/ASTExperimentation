package edu.wpi.checksims;

import org.antlr.v4.runtime.*;

import edu.wpi.checksims.Java8Lexer;
import edu.wpi.checksims.Java8Parser;


/**
 * The DijkstraFactory is responsible for constructing all, or parts of a Dijkstra
 * compiler. It is a standard Factory class.
 * 
 * @version Jan 26, 2015
 */
public class ASTFactory
{
    /**
     * Create a Dijkstra lexer using the specified input stream containing the text
     * @param inputText the ANTLRInputStream that contains the program text
     * @return the Dijkstra lexer
     */
    static public Java8Lexer makeLexer(ANTLRInputStream inputText) {
        final Java8Lexer lexer = new Java8Lexer(inputText);
        lexer.addErrorListener(
                new BaseErrorListener() {
                    @Override
                    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg,
                            RecognitionException e)
                    {
                        throw new RuntimeException(msg, e);
                    }
                }
        );
        return lexer;
    }
    
    /**
     * @param inputText
     * @return
     */
    static public Java8Parser makeParser(ANTLRInputStream inputText) {
        final Java8Lexer lexer = makeLexer(inputText);
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        final Java8Parser parser = new Java8Parser(tokenStream);
        parser.addErrorListener(
                new BaseErrorListener() {
                    @Override
                    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg,
                            RecognitionException e)
                    {
                        throw new RuntimeException(msg, e);
                    }
                }
        );
        return parser;
    }
}