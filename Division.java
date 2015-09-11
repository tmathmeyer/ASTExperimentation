public class Test
{
    public static void main(String ... args) throws FileNotFoundException, IOException
    {
        Java8Parser j8pa = ASTFactory.makeParser(new ANTLRInputStream(new FileInputStream(new File("Addition.java"))));
        Java8Parser j8pb = ASTFactory.makeParser(new ANTLRInputStream(new FileInputStream(new File("Division.java"))));
        
        ClassDeclarationContext cdca = j8pa.classDeclaration();
        ClassDeclarationContext cdcb = j8pb.classDeclaration();

        AST ta = cdca.accept(new ReflectiveDynamicTreeWalker());
        AST tb = cdcb.accept(new ReflectiveDynamicTreeWalker());
        
        Real r = ta.compareToAST(tb);
        
        System.out.println(r);
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
}
