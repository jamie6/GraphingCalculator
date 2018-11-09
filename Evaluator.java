public class Evaluator {
    private ExpressionTokenizer tokenizer;
    private double x;
    
    /**
     * Constructs an evaluator.
     * @param anExpression a string containing the expression to be evaluated
     */
    public Evaluator(String anExpression)
    {
        tokenizer = new ExpressionTokenizer(anExpression);
        x=0;
    }
    /**
     * Evaluates the expression
     * @return the value of the expression
     */
    public double getExpressionValue()
    {
        double value = getTermValue();
        boolean done = false;
        while (!done)
        {
            String next = tokenizer.peekToken();
            if ( "+".equals(next) || "-".equals(next))
            {
                tokenizer.nextToken(); // Discard "+" or "-"
                if ( "+".equals(tokenizer.peekToken()) || "-".equals(tokenizer.peekToken()) || "*".equals(tokenizer.peekToken()) || "/".equals(tokenizer.peekToken()) || "^".equals(tokenizer.peekToken())|| "%".equals(tokenizer.peekToken()) )
                    throw new SyntaxException( "Consecutive operators! " );
                
                double value2 = getTermValue();
                if ("+".equals(next)) { value = value + value2; }
                else {value = value - value2; }
            }
            else
            {
                done = true;
            }
        }
        return value;
    }
    /**
     * Evaluates the next term found in the expression.
     * @return the value of the term 
     */
    public double getTermValue()
    {
        double value = getFactorValue();
        boolean done = false;
        while (!done)
        {
            String next = tokenizer.peekToken();
            if ("*".equals(next) || "/".equals(next) || "^".equals(next) || "%".equals(next))
            {
                tokenizer.nextToken();
                if ( "+".equals(tokenizer.peekToken()) || "-".equals(tokenizer.peekToken()) || "*".equals(tokenizer.peekToken()) || "/".equals(tokenizer.peekToken()) || "^".equals(tokenizer.peekToken()) || "%".equals(tokenizer.peekToken()) )
                    throw new SyntaxException( "Consecutive operators! ");
                double value2 = getFactorValue();
                if ( "^".equals(next) ) { value = Math.pow(value, value2); }
                else if ( "%".equals(next) ) { value = value%value2; }
                else if ( "*".equals(next)) { value = value * value2; }
                else { value = value / value2; }
            }
            else
            {
                done = true;
            }
        }
        return value;
    }
    /**
     * Evaluates the next factor found in the expression.
     * @return the value of the factor
     */
    public double getFactorValue()
    {
        double value;
        String next = tokenizer.peekToken();
        if ( ")".equals(next) ) throw new SyntaxException("Mismatched paranthesis!" );
        if ("(".equals(next))
        {
            tokenizer.nextToken(); // Discard "("
            value = getExpressionValue();
            
            next = tokenizer.peekToken();
            if ( ")".equals(next))
                tokenizer.nextToken(); // Discard ")"
            else
                throw new SyntaxException("Mismatched paranthesis!" ); // exception
        }
        else
        {
            value = getFunctionValue(); // expects number of function
        }
        return value;
    }
    
    public double getFunctionValue()
    { // sin, cos, tan, arcsin, arccos, arctan, ln, 
        double value;
        String next = tokenizer.peekToken();
        if ( "s".equals(next)) //sin
        {
            tokenizer.nextToken(3);
            value = getFactorValue();
            value = Math.sin(degreesToRadians(value));
        }
        else if ( "c".equals(next)) // cos
        {
            tokenizer.nextToken(3);
            value = getFactorValue();
            value = Math.cos(degreesToRadians(value));
        }
        else if ( "t".equals(next)) // tan
        {
            tokenizer.nextToken(3);
            value = getFactorValue();
            value = Math.tan(degreesToRadians(value));
        }
        else if ( "a".equals(next)) // trig inverses 
        {
            
            tokenizer.nextToken();
            next = tokenizer.peekToken();
            if ( "s".equals(next)) // arcsin
            {
                tokenizer.nextToken(3);
                value = getFactorValue();
                value = radiansToDegrees(Math.asin((value)));
            }
            else if ( "c".equals(next)) // arccos
            {
                tokenizer.nextToken(3);
                value = getFactorValue();
                value = radiansToDegrees(Math.acos((value)));
            }
            else // if ( "t".equals(next)) // arctan
            {
                tokenizer.nextToken(3);
                value = getFactorValue();
                value = radiansToDegrees(Math.atan((value)));
            }
        }
        else if ( "l".equals(next)) // log
        {
            tokenizer.nextToken(2);
            value = getFactorValue();
            value = Math.log(value);
        }
        else
        {
            value = getNumber(); // expecting double
        }
        return value;
    }
    
    public double getNumber()
    {
        double value;
        String next = tokenizer.peekToken();
        if ( "x".equals(next))
        {
            tokenizer.nextToken();
            value = x;
        }
        else if ( "e".equals(next))
        {
            tokenizer.nextToken();
            value = Math.E;
        }
        else if ( "p".equals(next) ) //pi
        {
            tokenizer.nextToken(2);
                value = Math.PI;
        }
        else
        {
            try{
            value = Double.parseDouble(tokenizer.nextToken());
            }
            catch(Exception ex ) { throw new SyntaxException("Enter a numeric value!" );}
        }
        return value;
    }
    
    public double degreesToRadians( double value )
    {
        return value * Math.PI / 180.0;
    }
    
    public double radiansToDegrees( double value )
    {
        return value * 180.0 / Math.PI;
    }
    
    public void setX( double input )
    {
        x = input;
    }
    public double getX()
    {
        return x;
    }
    
    public class SyntaxException extends IllegalArgumentException
    {
        public SyntaxException() {}
        
        public SyntaxException(String message)
        {
            super(message);
        }
    }
            
}
