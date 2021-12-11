// StaticTypeCheck.java

import java.util.*;
import java.util.function.Function;

// Static type checking for Clite is defined by the functions 
// V and the auxiliary functions typing and typeOf.  These
// functions use the classes in the Abstract Syntax of Clite.


public class StaticTypeCheck {

    public static TypeMap typing (Declarations d, Functions f) { // ���� + �Լ�
        TypeMap map = new TypeMap();
        for (Declaration di : d) //���� ���ؼ�
            map.put (di.v, di.t); // value and type
        
        for(Func fi : f) // �Լ��� ���ؼ�
        	map.put(new Variable(fi.id), new ProtoType(fi.t, fi.params));
        
        return map;
    }

    public static void check(boolean test, String msg) {
        if (test)  return; // ������ �ƹ��͵� �� �ϰ�
        System.err.println(msg); // Ʋ���� ���� ������
        System.exit(1);
    }

    public static void V (Declarations d, Functions f) { 
        // ������ �Լ� �̸� �ߺ� X
    	// ��� ������ Ÿ���� void�� �ƴ�
    	// main �ݵ�� �ϳ�
    	 boolean foundmain = false;
    	
    	//����� Ȯ��
    	for (int i=0; i<d.size() - 1; i++) {
            for (int j=i+1; j<d.size(); j++) {
                Declaration di = d.get(i);
                Declaration dj = d.get(j);
                check( ! (di.v.equals(dj.v)), // �������� �̸� Ȯ��
                       "duplicate declaration: " + dj.v);
                check( !(di.t.equals(Type.VOID)), // ������ void���� ����
                		"declaration VOID type error" + di.v);
            }
        }
    	
    	for (int i=0; i<d.size(); i++) {
            for (int j=0; j<f.size(); j++) {
                Declaration di = d.get(i);
                Func fj =  f.get(j); 
                check( ! (di.v.equals(fj.id)), // ������ �Լ��� Ȯ��
                       "duplicate id in Decl, Func: " + di.v);
                if(fj.id.equals("main")) {
            		if(foundmain)
            			check(false, "Duplicate main function");
            		else
            			foundmain=true;
    	
        
        
        
    } 

    //���α׷�
    // V(p) = V(TMp.globas U TMp.body)
    // �ٵ� �ִ� ��� �Լ����� Validity Check
    
    public static void V (Program p) { 
        V (p.globals, p.functions); // �۷ι�, �Լ��� validity check
        
       
        TypeMap tm = typing(p.globals, p.functions);
        
        // �Լ����� ��ȿ�� üũ��
         
        V(p.functions, tm);
        
        
        
       // V (p.body, typing (p.decpart));
    } 
    
    

    public static Type typeOf (Expression e, TypeMap tm) { // expression�� type 6.6
        if (e instanceof Value) return ((Value)e).type; // value
        if (e instanceof Variable) { // variable
            Variable v = (Variable)e;
            check (tm.containsKey(v), "undefined variable: " + v); //���� Ȯ��
            return (Type) tm.get(v);
        }
        if (e instanceof Binary) { // binary��
            Binary b = (Binary)e;
            if (b.op.ArithmeticOp( )) // ��� ������
                if (typeOf(b.term1,tm)== Type.FLOAT)
                    return (Type.FLOAT);
                else return (Type.INT);
            if (b.op.RelationalOp( ) || b.op.BooleanOp( ))  // ���質 boolean...
                return (Type.BOOL);
        }
        if (e instanceof Unary) {
            Unary u = (Unary)e;
            if (u.op.NotOp( ))        return (Type.BOOL); // !
            else if (u.op.NegateOp( )) return typeOf(u.term,tm); // -
            else if (u.op.intOp( ))    return (Type.INT); //����ȯ
            else if (u.op.floatOp( )) return (Type.FLOAT);
            else if (u.op.charOp( ))  return (Type.CHAR);
            else if (u.op.BooleanOp())  return (Type.BOOL);
        }
        if( e instanceof Call) {
        	Call c = (Call)e;
        	if(c.name.equals("getInt")) return Type.INT;
        	else if(c.name.equals("getFloat")) return Type.FLOAT;
        	
        }
        
        throw new IllegalArgumentException("should never reach here");
    }
    
    public static void V(Functions f, TypeMap tm) {
    	for(Func func : f) 
    		V(func, tm);
    	
    	
    	
    }
    
    public static void V(Func f, TypeMap tm) {
    	//�Ű������� ���������� �̸� ����
    	//�Լ� �� ���� ��ȿ��
    	// void �� �ƴѰ� return, void�� return ������ ����
    	
    	
    	
    	
    }

    public static void V (Expression e, TypeMap tm) { // Expression!validity 6.5
        if (e instanceof Value) 
            return;
        if (e instanceof Variable) {
            Variable v = (Variable)e;
            check( tm.containsKey(v) // ���� Ȯ��
                   , "undeclared variable: " + v);
            return;
        }
        if (e instanceof Binary) {
            Binary b = (Binary) e;
            Type typ1 = typeOf(b.term1, tm);
            Type typ2 = typeOf(b.term2, tm);
            V (b.term1, tm);
            V (b.term2, tm);
            if (b.op.ArithmeticOp( )) {   //��������� int or float
                check( typ1 == typ2 &&
                       (typ1 == Type.INT || typ1 == Type.FLOAT)
                       , "type error for " + b.op);
            }
            else if (b.op.RelationalOp( )) // �� ������
                check( typ1 == typ2 , "type error for " + b.op);
            else if (b.op.BooleanOp( )) // bool
                check( typ1 == Type.BOOL && typ2 == Type.BOOL,
                       b.op + ": non-bool operand");
            else
                throw new IllegalArgumentException("should never reach here");
            return;
        }
        if(e instanceof Unary) {
        	Unary u = (Unary)e;
        	V(u.term, tm);
        	Type typ = typeOf(u.term,tm);
        	if(u.op.NotOp()) // !
        		check(typ==Type.BOOL, " ! op and non-bool type "+u.op);
        	else if(u.op.NegateOp()) // -
        		check(typ==Type.INT || typ == Type.FLOAT, "- op and non number type "+u.op);
            else if (u.op.intOp( )) // ĳ���� - ��ȯ
            	check(typ==Type.FLOAT || typ==Type.CHAR , " int() and non float, char "+u.op);
            else if(u.op.floatOp( ))
            	check(typ==Type.INT, " float() and non int "+u.op);
            else if(u.op.charOp( )) 
            	check(typ==Type.INT, " char() and non int");
        	else
        		throw new IllegalArgumentException("should never reach here");
        	return;
        }
        if(e instanceof Call) {
        	Call c = (Call)e;
        	if(c.name.equals("getInt") || c.name.equals("getFloat"))
        		return;
        	
        	else { // �Լ� Call
        		// �Լ��� ����, �μ��� Ÿ���� ������ Ȯ��
        	}
        	}
        }
       
        throw new IllegalArgumentException("should never reach here");
    }

    public static void V (Statement s, TypeMap tm) { // Statement! Validity 6.4
        if ( s == null )
            throw new IllegalArgumentException( "AST error: null statement");
        if (s instanceof Skip) return; // skip�� �׻� valid
        if (s instanceof Assignment) { // target = source
            Assignment a = (Assignment)s;
            //check( tm.containsKey(a.target) // ���� Ȯ��
              //     , " undefined target in assignment: " + a.target);
            V(a.target, tm);
            V(a.source, tm);
            Type ttype = (Type)tm.get(a.target);
            Type srctype = typeOf(a.source, tm);
            if (ttype != srctype) { // ���� ������ �ڵ� ��ȯ���ִ�  int-float / char-int
                if (ttype == Type.FLOAT)
                    check( srctype == Type.INT // float = int ok
                           , "mixed mode assignment to " + a.target);
                else if (ttype == Type.INT)
                    check( srctype == Type.CHAR // int = char ok
                           , "mixed mode assignment to " + a.target);
                else
                    check( false
                           , "mixed mode assignment to " + a.target);
            }
            return;
        }
        if(s instanceof Conditional) {
        	Conditional c = (Conditional) s;
        	V(c.test, tm);
        	Type typ = typeOf(c.test,tm);
        	check(typ == Type.BOOL, "conditional test type error");
        	V(c.elsebranch, tm);
        	V(c.thenbranch, tm);
        	
        	
        	return;
        }
        if(s instanceof Loop) {
        	Loop l = (Loop) s;
        	V(l.test, tm);
        	Type typ = typeOf(l.test, tm);
        	check(typ == Type.BOOL, "Loop test type error");
        	V(l.body, tm);
        	
        	return;
        }
        if(s instanceof Block) {
        	Block b = (Block) s;
        	for(int i = 0; i < b.members.size(); i++)
        		V(b.members.get(i), tm);
        	return;
        }
        if( s instanceof Call) {       	
        	Call c = (Call) s;
        	else if(c.name.equals("put")) {
        		if(c.args == null)
        				return;
        		else
        			for(int i = 0 ; i < c.args.size(); i++) {
        				Expression arg = c.args.get(i);
        				V(arg, tm);
        			}
        	}
        	return;
        }
        
        throw new IllegalArgumentException("should never reach here");
    }

    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer("D:\\comet\\4\\���α׷��־���\\����2�ڵ�\\newton.cpp"));
        Program prog = parser.program();
        // prog.display();          
        System.out.println("\nBegin type checking...");
        System.out.println("Type map:");
        TypeMap map = typing(prog.decpart);
        map.display();  
        V(prog);
    } //main

} // class StaticTypeCheck

