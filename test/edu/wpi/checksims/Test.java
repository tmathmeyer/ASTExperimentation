package edu.wpi.checksims;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;

import edu.wpi.checksims.Java8Parser;
import edu.wpi.checksims.Java8Parser.ClassDeclarationContext;

public class Test
{
    public static void main(String ... args) throws FileNotFoundException, IOException
    {
        Java8Parser j8pa = ASTFactory.makeParser(new ANTLRInputStream(new FileInputStream(new File("Addition.java"))));
        Java8Parser j8pb = ASTFactory.makeParser(new ANTLRInputStream(new FileInputStream(new File("Division.java"))));
        
        ClassDeclarationContext cdca = j8pa.classDeclaration();
        ClassDeclarationContext cdcb = j8pb.classDeclaration();

        //AST ta = cdca.accept(new ReflectiveDynamicTreeWalker());
        //AST tb = cdcb.accept(new ReflectiveDynamicTreeWalker());
        
        AST ta = cdca.accept(new FullyImplementedTreeWalker());
        //AST tb = cdcb.accept(new FullyImplementedTreeWalker());
        
        
        //Real r = ta.compareToAST(tb);
        
        System.out.println(ta);
        
        //System.out.println(r);
    }
}
