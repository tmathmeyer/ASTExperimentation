package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import edu.wpi.checksims.Java8BaseVisitor;
import edu.wpi.checksims.Java8Lexer;
import edu.wpi.checksims.Java8Parser;
import edu.wpi.checksims.Java8Parser.ClassDeclarationContext;

public class Test
{
    public static void main(String ... args) throws FileNotFoundException, IOException
    {
        Java8Parser j8p = ASTFactory.makeParser(new ANTLRInputStream(new FileInputStream(new File("Example.java"))));
        
        ClassDeclarationContext cdc = j8p.classDeclaration();
        
        AST t = cdc.accept(new ReflectiveDynamicTreeWalker());
        
        System.out.println(t);
    }
    
    private final static Set<Class<?>> unorderedTypes = new HashSet<>();
    private final static Set<Class<?>> nodes = new HashSet<>();
    static
    {
        unorderedTypes.add(Java8Parser.ClassBodyDeclarationContext.class);
        
        nodes.add(Java8Parser.ClassModifierContext.class);
        nodes.add(Java8Parser.MethodModifierContext.class);
        nodes.add(Java8Parser.FormalParameterContext.class);
    }
    
    
    public static class ReflectiveDynamicTreeWalker extends Java8BaseVisitor<AST>
    {
        private final static Map<String, Method> myMethods = new HashMap<>();
        static
        {
            for(Method m : Java8BaseVisitor.class.getMethods())
            {
                myMethods.put(m.getName(), m);
            }
        }
        
        @Override
        public AST visitChildren(RuleNode rn)
        {
            String callerName = Thread.currentThread().getStackTrace()[3].getMethodName();
            ParserRuleContext prc = (ParserRuleContext) rn;
            Stream<AST> sub = prc.children.stream().map(PT -> acceptorPass(PT));
            Class<?> paramType = myMethods.get(callerName).getParameters()[0].getType();
            if (unorderedTypes.contains(paramType))
            {
                return new AST.UnorderedAST(sub);
            }
            else if (nodes.contains(paramType))
            {
                return new AST.NodeAST(rn.getText());
            }
            return new AST.OrderedAST(sub);
        }
        
        private AST acceptorPass(ParseTree PT)
        {
            AST result = PT.accept(this);
            if (result == null)
            {
                return new AST.NodeAST(PT.getText());
            }
            return result;
        }
    }
}
