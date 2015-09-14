package edu.wpi.checksims;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import edu.wpi.checksims.AST.OrderedAST;
import edu.wpi.checksims.Java8Parser.AdditiveExpressionContext;
import edu.wpi.checksims.Java8Parser.AndExpressionContext;
import edu.wpi.checksims.Java8Parser.ArgumentListContext;
import edu.wpi.checksims.Java8Parser.AssignmentExpressionContext;
import edu.wpi.checksims.Java8Parser.BlockContext;
import edu.wpi.checksims.Java8Parser.BlockStatementContext;
import edu.wpi.checksims.Java8Parser.BlockStatementsContext;
import edu.wpi.checksims.Java8Parser.ClassBodyContext;
import edu.wpi.checksims.Java8Parser.ClassBodyDeclarationContext;
import edu.wpi.checksims.Java8Parser.ClassDeclarationContext;
import edu.wpi.checksims.Java8Parser.ClassMemberDeclarationContext;
import edu.wpi.checksims.Java8Parser.ConditionalAndExpressionContext;
import edu.wpi.checksims.Java8Parser.ConditionalExpressionContext;
import edu.wpi.checksims.Java8Parser.ConditionalOrExpressionContext;
import edu.wpi.checksims.Java8Parser.EqualityExpressionContext;
import edu.wpi.checksims.Java8Parser.ExclusiveOrExpressionContext;
import edu.wpi.checksims.Java8Parser.ExpressionContext;
import edu.wpi.checksims.Java8Parser.ExpressionStatementContext;
import edu.wpi.checksims.Java8Parser.FormalParameterContext;
import edu.wpi.checksims.Java8Parser.FormalParameterListContext;
import edu.wpi.checksims.Java8Parser.FormalParametersContext;
import edu.wpi.checksims.Java8Parser.InclusiveOrExpressionContext;
import edu.wpi.checksims.Java8Parser.LastFormalParameterContext;
import edu.wpi.checksims.Java8Parser.MethodBodyContext;
import edu.wpi.checksims.Java8Parser.MethodDeclarationContext;
import edu.wpi.checksims.Java8Parser.MethodDeclaratorContext;
import edu.wpi.checksims.Java8Parser.MethodHeaderContext;
import edu.wpi.checksims.Java8Parser.MethodInvocationContext;
import edu.wpi.checksims.Java8Parser.MultiplicativeExpressionContext;
import edu.wpi.checksims.Java8Parser.NormalClassDeclarationContext;
import edu.wpi.checksims.Java8Parser.PostfixExpressionContext;
import edu.wpi.checksims.Java8Parser.RelationalExpressionContext;
import edu.wpi.checksims.Java8Parser.ShiftExpressionContext;
import edu.wpi.checksims.Java8Parser.StatementContext;
import edu.wpi.checksims.Java8Parser.StatementExpressionContext;
import edu.wpi.checksims.Java8Parser.StatementWithoutTrailingSubstatementContext;
import edu.wpi.checksims.Java8Parser.UnaryExpressionContext;
import edu.wpi.checksims.Java8Parser.UnaryExpressionNotPlusMinusContext;

public class FullyImplementedTreeWalker extends Java8BaseVisitor<AST>
{
    private final static Map<String, Method> myMethods = new HashMap<>();
    private final static Map<String, AST> names = new HashMap<>();
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
        Class<?> paramType = myMethods.get(callerName).getParameters()[0].getType();
        System.out.println("@Override\npublic AST " + callerName + "(" + paramType.getSimpleName()+" ctx)\n{\n" + s + "}\n");
        return super.visitChildren(rn);
    }
    
    @Override
    public AST visitClassDeclaration(ClassDeclarationContext ctx)
    {
        for(ParseTree pt : ctx.children)
        {
            switch (pt.getClass().getSimpleName())
            {
                case "NormalClassDeclarationContext":
                    return pt.accept(this);
            }
        }
        
        return null;
    }
    
    @Override
    public AST visitNormalClassDeclaration(NormalClassDeclarationContext ctx)
    {
        String name = ctx.children.get(2).getText();
        AST t = ctx.children.get(3).accept(this);
        
        names.put(name, t);
        return t;
    }
    
    @Override
    public AST visitClassBody(ClassBodyContext ctx)
    {
        List<AST> t = new LinkedList<>();
        for(ParseTree pt : ctx.children)
        {
            switch (pt.getClass().getSimpleName())
            {
                case "ClassBodyDeclarationContext":
                    AST y = pt.accept(this);
                    if (y != null)
                    {
                        t.add(y);
                    }
            }
        }
        
        return new AST.UnorderedAST(t.stream());
    }
    
    @Override
    public AST visitClassBodyDeclaration(ClassBodyDeclarationContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitClassMemberDeclaration(ClassMemberDeclarationContext ctx)
    {
        for(ParseTree pt : ctx.children)
        {
            //System.out.println(pt.getClass().getSimpleName());
            //System.out.println(pt.getText());
            //System.out.println();
            switch(pt.getClass().getSimpleName())
            {
                //case "FieldDeclarationContext":
                case "MethodDeclarationContext":
                //case "ClassDeclarationContext":
                //case "InterfaceDeclarationContext":
                    return pt.accept(this);
            }
        }
        
        return null;
    }
    
    @Override
    public AST visitMethodDeclaration(MethodDeclarationContext ctx)
    {
        List<AST> t = new LinkedList<>();
        
        for(ParseTree pt : ctx.children)
        {
            switch(pt.getClass().getSimpleName())
            {
                case "MethodBodyContext":
                case "MethodHeaderContext":
                    t.add(pt.accept(this));
            }
        }
        
        return new AST.OrderedAST(t.stream());
    }
    
    @Override
    public AST visitMethodHeader(MethodHeaderContext ctx)
    {
        List<AST> t = new LinkedList<>();
        for(ParseTree pt : ctx.children)
        {
            switch(pt.getClass().getSimpleName())
            {
                case "ResultContext":
                    t.add(new AST.NodeAST(pt.getText())); break;
                case "MethodDeclaratorContext":
                    t.add(pt.accept(this)); break;
            }
        }
        
        return new AST.OrderedAST(t.stream());
    }
    
    @Override
    public AST visitMethodDeclarator(MethodDeclaratorContext ctx)
    {
        List<AST> t = new LinkedList<>();
        
        t.add(new AST.NodeAST(ctx.children.get(0).getText()));
        t.add(ctx.children.get(2).accept(this));
        
        return new AST.OrderedAST(t.stream());
    }
    
    @Override
    public AST visitFormalParameterList(FormalParameterListContext ctx)
    {
        List<AST> t = new LinkedList<>();
        for(ParseTree pt : ctx.children)
        {
            switch(pt.getClass().getSimpleName())
            {
                case "LastFormalParameterContext":
                    t.add(pt.accept(this));
                    break;
                case "FormalParametersContext":
                    AST.OrderedAST res =  (OrderedAST) pt.accept(this);
                    t.addAll(res.getBody());
                    break;
            }
        }
        
        return new AST.OrderedAST(t.stream());
    }
    
    
    @Override
    public AST visitLastFormalParameter(LastFormalParameterContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitFormalParameters(FormalParametersContext ctx)
    {
        List<AST> t = new LinkedList<>();
        
        t.add(ctx.children.get(0).accept(this));
        t.add(ctx.children.get(2).accept(this));
        
        return new AST.OrderedAST(t.stream());
    }
    
    @Override
    public AST visitFormalParameter(FormalParameterContext ctx)
    {
        return new AST.NodeAST(ctx.children.get(0).getText());
    }
    
    @Override
    public AST visitMethodBody(MethodBodyContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitBlock(BlockContext ctx)
    {
        return ctx.children.get(1).accept(this);
    }
    
    @Override
    public AST visitBlockStatements(BlockStatementsContext ctx)
    {
        List<AST> t = new LinkedList<>();
        for(ParseTree pt : ctx.children)
        {
            t.add(pt.accept(this));
        }
        return new AST.UnorderedAST(t.stream());
    }
    
    @Override
    public AST visitBlockStatement(BlockStatementContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitStatement(StatementContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitStatementWithoutTrailingSubstatement(StatementWithoutTrailingSubstatementContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitExpressionStatement(ExpressionStatementContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitStatementExpression(StatementExpressionContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitMethodInvocation(MethodInvocationContext ctx)
    {
        List<AST> t = new LinkedList<>();
        switch(ctx.children.get(0).getClass().getSimpleName())
        {
            case "TypeNameContext": // TODO there is another case! read the g4 file, line 1104
                t.add(new AST.NodeAST(ctx.children.get(0).getText() + ctx.children.get(2).getText()));
                t.add(ctx.children.get(4).accept(this));
                break;
            case "MethodNameContext":
                t.add(new AST.NodeAST(ctx.children.get(0).getText()));
                t.add(ctx.children.get(2).accept(this));
                break;
            case "ExpressionNameContext":
        }
        return new AST.OrderedAST(t.stream());
    }
    
    @Override
    public AST visitArgumentList(ArgumentListContext ctx)
    {
        List<AST> t = new LinkedList<>();
        for(ParseTree pt : ctx.children)
        {
            switch(pt.getClass().getSimpleName())
            {
                case "ExpressionContext":
                    t.add(pt.accept(this));
            }
        }
        return new AST.OrderedAST(t.stream());
    }
    
    @Override
    public AST visitExpression(ExpressionContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitAssignmentExpression(AssignmentExpressionContext ctx)
    {
        return ctx.children.get(0).accept(this);
    }
    
    @Override
    public AST visitConditionalExpression(ConditionalExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO ternary expression
            return null;
        }
    }
    
    @Override
    public AST visitConditionalOrExpression(ConditionalOrExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO or expression
            return null;
        }
    }
    
    @Override
    public AST visitConditionalAndExpression(ConditionalAndExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO or expression
            return null;
        }
    }
    
    @Override
    public AST visitInclusiveOrExpression(InclusiveOrExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO inc-or expression
            return null;
        }
    }
    
    @Override
    public AST visitExclusiveOrExpression(ExclusiveOrExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO xor expression
            return null;
        }
    }
    
    @Override
    public AST visitAndExpression(AndExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO and expression
            return null;
        }
    }
    
    @Override
    public AST visitEqualityExpression(EqualityExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO equal/nequal expression
            return null;
        }
    }
    
    @Override
    public AST visitRelationalExpression(RelationalExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO gt/lt/gte/lte/instanceof expression
            return null;
        }
    }
    
    @Override
    public AST visitShiftExpression(ShiftExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO bitshift expression
            return null;
        }
    }
    
    @Override
    public AST visitAdditiveExpression(AdditiveExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO +/- expression
            return null;
        }
    }
    
    @Override
    public AST visitMultiplicativeExpression(MultiplicativeExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO * / % expression
            return null;
        }
    }
    
    @Override
    public AST visitUnaryExpression(UnaryExpressionContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO + - expression
            return null;
        }
    }
    
    @Override
    public AST visitUnaryExpressionNotPlusMinus(UnaryExpressionNotPlusMinusContext ctx)
    {
        if (ctx.children.size() == 1)
        {
            return ctx.children.get(0).accept(this);
        }
        else
        {
            //TODO + - expression
            return null;
        }
    }
    
    @Override
    public AST visitPostfixExpression(PostfixExpressionContext ctx)
    {
        for(ParseTree pt : ctx.children)
        {
            System.out.println(pt.getClass().getSimpleName());
            System.out.println(pt.getText());
            System.out.println();
        }
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    String s = 
    "    for(ParseTree pt : ctx.children)\n" +
    "    {\n" +
    "        System.out.println(pt.getClass().getSimpleName());\n" +
    "        System.out.println(pt.getText());\n" +
    "        System.out.println();\n" +
    "    }\n" +
    "    return null;\n";
    
}