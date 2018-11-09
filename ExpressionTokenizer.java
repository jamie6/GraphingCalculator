public class ExpressionTokenizer {
    private String input;
    private int start; // The start of the current token
    private int end; // The position after the end of the current token
    
    /**
     * Constructs a tokenizer.
     * @param anInput the string to tokenizer
     */
    public ExpressionTokenizer(String anInput)
    {
        input = anInput;
        start = 0;
        end = 0;
        
        removeWhiteSpace(); // removes white space from string to make reading easier for program
        nextToken(); // Find the first token
    }
    
    /**
     * Peeks at the next token without consuming it.
     * @return the next token or null if there are no more tokens
     */
    public String peekToken()
    {
        if (start >= input.length()) { return null; }
        else { return input.substring(start, end); }
    }
    /**
     * Gets the next token and moves the tokenizer to the following token.
     * @return the next token or null if there are no more tokens
     */
    public String nextToken()
    {
        String r = peekToken();
        start = end;
        if (start >= input.length()) { return r; }
        if (Character.isDigit(input.charAt(start)) || '.'==( input.charAt(start)))
        {
            end = start + 1;
            while (end < input.length() && (Character.isDigit(input.charAt(end)) || '.'==( input.charAt(end) )) )
            {
                end++;
            }
        }
        else
        {
            end = start + 1;
        }
        return r;
    }
    public String nextToken( int i )
    {
        String r = peekToken();
        for ( ; i > 0; i-- )
        {
            nextToken();
        }
        return r;
    }
    
    private void removeWhiteSpace()
    {
        for ( int i = 0; i < input.length(); i++ )
        {
            if ( input.charAt(i) == ' ' )
            {
                input = input.substring(0, i) + input.substring(i+1, input.length());
                i--;
            }
        }
    }
}
