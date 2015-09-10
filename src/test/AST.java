package test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface AST
{
    
    public static class OrderedAST implements AST
    {
        public List<AST> contains = new LinkedList<>();
        public OrderedAST(Stream<AST> sub)
        {
            sub.forEach(AST -> contains.add(AST));
        }
        
        public String toString()
        {
            return "o"+contains;
        }
    }
    
    public static class UnorderedAST implements AST
    {
        private final Set<AST> contains = new HashSet<>();
        public UnorderedAST(Stream<AST> sub)
        {
            sub.forEach(AST -> contains.add(AST));
        }
        
        public String toString()
        {
            return "u"+contains;
        }
    }
    
    public static class NodeAST implements AST
    {
        private final String val;
        
        public NodeAST(String text)
        {
            val = text;
        }
        
        public String toString()
        {
            return val;
        }
        
    }
}
