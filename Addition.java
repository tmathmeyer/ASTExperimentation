public class Author
{
    private int TEXT = 3;
    
    public static void main(String[] args)
    {
        System.out.println("My Name is Ted");
        exampleFunction(3, 4, 5);
    }

    public void exampleFunction(int a, int b, int c)
    {
        System.out.println(a+b);
        System.out.println(b+c);
    }
}