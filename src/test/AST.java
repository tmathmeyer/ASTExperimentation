package test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface AST
{
    public Real compareToAST(AST t);
    
    static class BlankAST implements AST
    {

        @Override
        public Real compareToAST(AST t)
        {
            return t.compareToAST(this);
        }

        @Override
        public Real compareUnorderedAST(UnorderedAST unorderedAST)
        {
            Real r = new Real(-1, 0);
            for(AST t : unorderedAST.contains)
            {
                r = r.simpleAverage(t.compareToAST(this));
            }
            return r;
        }

        @Override
        public Real compareOrderedAST(OrderedAST orderedAST)
        {
            Real r = new Real(1, 0);
            for(AST t : orderedAST.contains)
            {
                r = r.simpleAverage(t.compareToAST(this));
            }
            return r;
        }

        @Override
        public Real compareNodeAST(NodeAST nodeAST)
        {
            return new Real(0, 1);
        }
        
    }
    
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

        @Override
        public Real compareToAST(AST t)
        {
            return t.compareOrderedAST(this);
        }

        @Override
        public Real compareNodeAST(NodeAST nodeAST)
        {
            return nodeAST.compareOrderedAST(this);
        }

        @Override
        public Real compareUnorderedAST(UnorderedAST unorderedAST)
        {
            Real r = new Real(0, 0);
            Iterator<AST> a = this.contains.iterator();
            Iterator<AST> b = unorderedAST.contains.iterator();
            while(a.hasNext() || b.hasNext())
            {
                AST R = new BlankAST();
                if (a.hasNext())
                {
                    r = r.simpleAverage(R.compareToAST(a.next()));
                }
                if (b.hasNext())
                {
                    r = r.simpleAverage(R.compareToAST(b.next()));
                }
            }
            return r;
        }

        @Override
        public Real compareOrderedAST(OrderedAST orderedAST)
        {
            Real r = new Real(0, 0);
            Iterator<AST> a = this.contains.iterator();
            Iterator<AST> b = orderedAST.contains.iterator();
            while(a.hasNext() || b.hasNext())
            {
                AST A = new BlankAST(), B = new BlankAST();
                if (a.hasNext())
                {
                    A = a.next();
                }
                if (b.hasNext())
                {
                    B = b.next();
                }
                r = r.simpleAverage(A.compareToAST(B));
            }
            return r;
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

        @Override
        public Real compareToAST(AST t)
        {
            return t.compareUnorderedAST(this);
        }

        @Override
        public Real compareNodeAST(NodeAST nodeAST)
        {
            return nodeAST.compareUnorderedAST(this);
        }

        @Override
        public Real compareUnorderedAST(UnorderedAST unorderedAST)
        {
            Real r = new Real(0, 0);
            Iterator<AST> a = this.contains.iterator();
            Iterator<AST> b = unorderedAST.contains.iterator();
            while(a.hasNext() || b.hasNext())
            {
                AST A = new BlankAST(), B = new BlankAST();
                if (a.hasNext())
                {
                    A = a.next();
                }
                if (b.hasNext())
                {
                    B = b.next();
                }
                r = r.simpleAverage(A.compareToAST(B));
            }
            return r;
        }

        @Override
        public Real compareOrderedAST(OrderedAST orderedAST)
        {
            Real r = new Real(0, 0);
            Iterator<AST> a = this.contains.iterator();
            Iterator<AST> b = orderedAST.contains.iterator();
            while(a.hasNext() || b.hasNext())
            {
                AST R = new BlankAST();
                if (a.hasNext())
                {
                    r = r.simpleAverage(R.compareToAST(a.next()));
                }
                if (b.hasNext())
                {
                    r = r.simpleAverage(R.compareToAST(b.next()));
                }
            }
            return r;
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

        @Override
        public Real compareToAST(AST t)
        {
            return t.compareNodeAST(this);
        }

        @Override
        public Real compareNodeAST(NodeAST nodeAST)
        {
            return val.equals(nodeAST.val) ? new Real(1, 1) : new Real(0, 1);
        }

        @Override
        public Real compareUnorderedAST(UnorderedAST unorderedAST)
        {
            return new Real(0, 1);
        }

        @Override
        public Real compareOrderedAST(OrderedAST orderedAST)
        {
            return new Real(0, 1);
        } 
    }
    
    
    public static Real compareAST(AST a, AST b)
    {
        return a.compareToAST(b);
    }


    public Real compareUnorderedAST(UnorderedAST unorderedAST);


    public Real compareOrderedAST(OrderedAST orderedAST);


    public Real compareNodeAST(NodeAST nodeAST);
}
