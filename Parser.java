import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
    Scanner scanner;
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
        scanner = new Scanner(System.in); //테스트용
    }
  
    private String match (TokenType t) {
        String value = token.value();
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
   
    public Program program() {
        // Program -->  { Type Identifier FunctionOrGlobal } MainFunction
    	// MainFunction  int main ( ) { Declarations Statements }
    	// 
    	Declarations g = new Declarations();
    	Functions f = new Functions();
    	
    	while(!token.type().equals(TokenType.Eof))  // EOF체크..
    	{
    		Type t = type(); 
    		String id = match(token.type());
        	
    		if(id.equals(TokenType.Main) && t.equals(TokenType.Int))  // 함수인데 식별자가 main => MainFunction
    			f.add(MainFunction(t, id));
    		else 
	        	FunctionOrGlobal(g,f, t, id);
    	}

        return new Program(g,f);
    }
    
    private void FunctionOrGlobal(Declarations gs, Functions fs, Type t, String id) {
    	// FunctionOrGlobal ( Parameters ) { Declarations Statements } | Global

    	if(token.type().equals(TokenType.LeftParen)) { // 함수
    		fs.add(Function(id,t));
    	}
    	else{ // 전역변수
    		Global(gs, id, t);
    	}
    	return;
    }
    
    private Func Function(String id, Type t) {
    	// ( Parameters ) { Declarations Statements }
    	
    	match(TokenType.LeftParen);
    	Declarations params = Params();
    	match(TokenType.RightParen);
    	
    	match(TokenType.LeftBrace);
    	Declarations locals = declarations();
    	Block body = statements();
    	match(TokenType.RightBrace);
    	
    	return new Func(id, t, params, locals, body);
    }
    
    private Declarations Params() { // 함수의 파라미터
    	// Parameters  [ Parameter { , Parameter } ]
    	Declarations params = new Declarations();
    	
    	while(!token.type().equals(TokenType.RightParen)) {
    		Type t = type();
    		Variable v = new Variable(match(TokenType.Identifier));
        	params.add(new Declaration(v, t));
    		if(token.type().equals(TokenType.Comma))
    			match(token.type());
    	}
    	
    	if(params.size() == 0)
    		return null;
    		
    	return params; 
    }
    
    private void Global(Declarations global, String id, Type t) {
    	// Global  { , Identifier } ;
    	Declaration d = new Declaration(new Variable(id), t);
    	global.add(d);
    	
    	Variable v;
    	while(token.type().equals(TokenType.Comma)) {
    		match(token.type());
    		v = new Variable(match(TokenType.Identifier));
    		d = new Declaration(v, t); 
    		global.add(d);
    	}
    	match(TokenType.Semicolon);

    	return;
    }
    
    private Func MainFunction(Type t, String id) {
    	// int main (){ ... }
    	// int와 main은 앞에서 체크함.
    	match(TokenType.LeftParen);
    	match(TokenType.RightParen);
    	
    	Declarations params = null;
    	
    	match(TokenType.LeftBrace);
    	
    	Declarations locals = declarations();
    	Block body = statements();
    	
    	match(TokenType.RightBrace);
    	
    	return new Func(id, t, params, locals, body);	
    }
  
    private Declarations declarations () {
        // Declarations --> { Declaration }
    	Declarations decs = new Declarations(); //ArrayList<Declaration>
    	while(isType()) {
    		declaration(decs);
    	}
        return decs;  // student exercise
    }
  
    private void declaration (Declarations ds) {
        // Declaration  --> Type Identifier { , Identifier } ;
    	Declaration d;
    	Type t = type();
    	
    	Variable v = new Variable(match(TokenType.Identifier));
    	d = new Declaration(v, t);
    	ds.add(d);
    	
    	while(token.type().equals(TokenType.Comma)) {
    		match(token.type());
    		v = new Variable(match(TokenType.Identifier));
    		d = new Declaration(v, t);
        	ds.add(d);
    	}
    	match(TokenType.Semicolon);
    	return;   
    }
  
    private Type type () {
        // Type  -->  int | bool | float | char | void 
    	Type t= null;
    	if (token.type().equals(TokenType.Int)) 
            t = Type.INT;		
		else if (token.type().equals(TokenType.Bool)) 
				t = Type.BOOL;
		else if (token.type().equals(TokenType.Float)) 
				t = Type.FLOAT;
		else if (token.type().equals(TokenType.Char)) 
				t = Type.CHAR;
		else if(token.type().equals(TokenType.Void))
				t = Type.VOID;
		else 
			error ("Type");
    	
    	match(token.type());
    	return t;
    }
  
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement | Call
        Statement s=null;
        if(token.type().equals(TokenType.Semicolon)) { // skip
        	s = new Skip();
        	match(token.type());
        }
        else if(token.type().equals(TokenType.LeftBrace)) { // block
        	match(TokenType.LeftBrace);
        	s = statements();
        	match(TokenType.RightBrace);
        }
        else if(token.type().equals(TokenType.Identifier)) 
        	s=assignmentOrCall();
        
        else if(token.type().equals(TokenType.If))
        	s=ifStatement();
        else if(token.type().equals(TokenType.While))
        	s=whileStatement();
        else
        	error("statement");        
        return s;
    }

    
  
    private Block statements () {
        // Block --> '{' Statements '}'

        Block b = new Block();
        while(!token.type().equals(TokenType.RightBrace))
        	b.members.add(statement());
       
        return b;
    }
    
    
    private Statement assignmentOrCall() {
    	
    	Variable v = new Variable(match(token.type()));
    	Call c = new Call();
    	if(token.type().equals(TokenType.LeftParen)) { // Call
    		match(TokenType.LeftParen);
    		c.name = v.toString();
    		c.args = arguments();
    		match(TokenType.RightParen);
    		match(TokenType.Semicolon);
    		return c;
    	}
    	else if(token.type().equals(TokenType.Assign)){ // Assignment
    		match(TokenType.Assign);
        	Expression e = expression();
        	match(TokenType.Semicolon);
            return new Assignment(v, e);  // student exercise
    	}
    	else
    		error("Error in assignOrcCall");
    	return null;
    }
    
    private Expressions arguments() {
    	Expressions arg = new Expressions();
    	while(!token.type().equals(TokenType.RightParen)) {
    		Expression e = expression();
    		arg.add(e);
    		if(token.type().equals(TokenType.Comma))
    			match(token.type());
    	}
    	if(arg.size() == 0)
    		return null;
    		
    	return arg;
    }
  
    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
    	Conditional c=null;
    	match(TokenType.If);
    	match(TokenType.LeftParen);
    	Expression e = expression();
    	match(TokenType.RightParen);
    	
    	Statement s = statement();
    	c = new Conditional(e,s);
    	
    	if(token.type().equals(TokenType.Else)) {
    		match(token.type());
    		Statement s2 = statement();
    		c= new Conditional(e,s,s2);
    	}
    	
    	return c;
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
        match(TokenType.While);
        match(TokenType.LeftParen);
    	Expression e = expression();
    	match(TokenType.RightParen);
    	Statement s = statement();
    	
    	return new Loop(e,s);  // student exercise
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction } || getInt() || getFloat 수식..
    	Expression e = null;
	    e = conjunction();
	    while(token.type().equals(TokenType.Or)) {
	    	Operator op = new Operator(match(token.type()));
	    		Expression e2 = conjunction();
	    	e = new Binary(op, e, e2);
    	}
        return e;  // student exercise
    }
    
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
    	Expression e = equality();
    	while(token.type().equals(TokenType.And)) {
    		Operator op = new Operator(match(token.type()));
    		Expression e2 = equality();
    		e = new Binary(op, e, e2);
    	}
        return e;  // student exercise
    }
  
    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
    	Expression e = relation();
    	if(isEqualityOp())
    	{
    		Operator op = new Operator(match(token.type()));
    		Expression e2 = relation();
    		e = new Binary(op, e, e2);
    	}
        return e;  // student exercise
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition] 
    	Expression e = addition();
    	if(isRelationalOp())
    	{
    		Operator op = new Operator(match(token.type()));
    		Expression e2 = addition();
    		e = new Binary(op, e, e2);
    	}
        return e;  // student exercise
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
       
        if (token.type().equals(TokenType.Identifier)) {
            Variable v = new Variable(match(TokenType.Identifier));
            e=v;
            if(token.type().equals(TokenType.LeftParen)) {
            	
            	Call c = new Call();
            	match(TokenType.LeftParen);
            	c.name = v.toString();
            	c.args = arguments();
            	
            	match(TokenType.RightParen);    	
            	e =c;
            }
            
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();        
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
    	Value v = null;
    	String s = token.value();
    	//System.out.println(s);
    	if(token.type().equals(TokenType.IntLiteral))
			v = new IntValue((int)(Float.parseFloat(s)));
		else if(token.type().equals(TokenType.FloatLiteral))
			v = new FloatValue(Float.parseFloat(s));
		else if(token.type().equals(TokenType.True)) {
    		v = new BoolValue(true);
    	}
    	else if(token.type().equals(TokenType.False)) {
    		v = new BoolValue(false);
    	}
		else if(token.type().equals(TokenType.CharLiteral))
			v = new CharValue(s.charAt(0));
		else
			error("literal");
    	match(token.type());
        return v;  // student exercise
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
    
    private boolean isStatement() {
    	  return token.type().equals(TokenType.Semicolon) ||
    			  token.type().equals(TokenType.LeftBrace) ||
    	            token.type().equals(TokenType.Identifier) ||
    	            token.type().equals(TokenType.While) ||
    	            token.type().equals(TokenType.If);
    }
    

    
    public static void main(String args[]) {
    	
        Parser parser  = new Parser(new Lexer("D:\\comet\\4\\프로그래밍언어론\\과제2코딩\\test.cpp"));
        Program prog = parser.program();
        prog.display(0);           // display abstract syntax tree
    } //main

} // Parser
