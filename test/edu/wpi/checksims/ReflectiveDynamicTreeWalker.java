package edu.wpi.checksims;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

public class ReflectiveDynamicTreeWalker extends Java8BaseVisitor<AST>
{
    private final static Set<Class<?>> unorderedTypes = new HashSet<>();
    private final static Set<Class<?>> nodes = new HashSet<>();
    static
    {
        unorderedTypes.add(Java8Parser.ClassBodyDeclarationContext.class);
        
        nodes.add(Java8Parser.ClassModifierContext.class);
        nodes.add(Java8Parser.MethodModifierContext.class);
        nodes.add(Java8Parser.FormalParameterContext.class);
    }
    
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
        if (prc.children.size() == 1)
        {
            return sub.findFirst().get();
        }
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