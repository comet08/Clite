import java.util.*;
/*
 * 1. 암묵적 확대 변환 i2f c2i
 * 2. + : int+, float+ >> 무슨 타입의 연산인지
*/

public class TypeTransformer {

    public static Program T (Program p, TypeMap tm) {
        Block body = (Block)T(p.body, tm); // 변환된 block 돌려줌
        return new Program(p.decpart, body); // 선언은 변경필요X
    } 

    public static Expression T (Expression e, TypeMap tm) {
        if (e instanceof Value ) 
            return e;
        if (e instanceof Variable) 
            return e;
       
        if (e instanceof Binary) {
            Binary b = (Binary)e; 
            Type typ1 = StaticTypeCheck.typeOf(b.term1, tm);
            Type typ2 = StaticTypeCheck.typeOf(b.term2, tm);
            Expression t1 = T (b.term1, tm);
            Expression t2 = T (b.term2, tm);
            if (typ1 == Type.INT) 
                return new Binary(b.op.intMap(b.op.val), t1,t2); // ex) MINUS(-) -> INT_MINUS(INT-)
            else if (typ1 == Type.FLOAT) 
                return new Binary(b.op.floatMap(b.op.val), t1,t2);
            else if (typ1 == Type.CHAR) 
                return new Binary(b.op.charMap(b.op.val), t1,t2);
            else if (typ1 == Type.BOOL) 
                return new Binary(b.op.boolMap(b.op.val), t1,t2);
            throw new IllegalArgumentException("should never reach here");
        }
 
        // student exercise
        if(e instanceof Unary) {
        	Unary u = (Unary) e;
        	Type typ = StaticTypeCheck.typeOf(u.term, tm);
        	if(u.op.NegateOp()) // -    int or float
        	{
        		if(typ == Type.INT)
        			return new Unary(u.op.intMap(u.op.val), u.term);
        		else if(typ == Type.FLOAT)
        			return new Unary(u.op.floatMap(u.op.val), u.term);
        	}
        	else if(u.op.NotOp()) // !
        		return e;
            else if (typ==Type.INT) // 형변환
            		return new Unary(u.op.intMap(u.op.val), u.term);
            else if (typ==Type.FLOAT) 
            		return new Unary(u.op.floatMap(u.op.val), u.term);
            else if (typ==Type.CHAR) {
            		return new Unary(u.op.charMap(u.op.val), u.term);
            }
            else
            	throw new IllegalArgumentException("should never reach here");
        }
        if(e instanceof Call) {
        	return e;
        }
        throw new IllegalArgumentException("should never reach here");
    }

    public static Statement T (Statement s, TypeMap tm) {
        if (s instanceof Skip) return s;
        if (s instanceof Assignment) {
            Assignment a = (Assignment)s;
            Variable target = a.target;
            Expression src = T (a.source, tm);
            Type ttype = (Type)tm.get(a.target);
            Type srctype = StaticTypeCheck.typeOf(a.source, tm);
            if (ttype == Type.FLOAT) { // 자동 형변환
                if (srctype == Type.INT) {
                    src = new Unary(new Operator(Operator.I2F), src); // 그냥 value를 unary로
                    srctype = Type.FLOAT;
                }
            }
            else if (ttype == Type.INT) {
                if (srctype == Type.CHAR) {
                    src = new Unary(new Operator(Operator.C2I), src);
                    srctype = Type.INT;
                }
            }
            StaticTypeCheck.check( ttype == srctype,
                      "bug in assignment to " + target);
            return new Assignment(target, src);
        } 
        if (s instanceof Conditional) {
            Conditional c = (Conditional)s;
            Expression test = T (c.test, tm);
            Statement tbr = T (c.thenbranch, tm);
            Statement ebr = T (c.elsebranch, tm);
            return new Conditional(test,  tbr, ebr);
        }
        if (s instanceof Loop) {
            Loop l = (Loop)s;
            Expression test = T (l.test, tm);
            Statement body = T (l.body, tm);
            return new Loop(test, body);
        }
        if (s instanceof Block) {
            Block b = (Block)s;
            Block out = new Block();
            for (Statement stmt : b.members)
                out.members.add(T(stmt, tm));
            return out;
        }
        if(s instanceof Call) {
        	return s;
        }
        throw new IllegalArgumentException("should never reach here");
    }
    

    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer("D:\\comet\\4\\프로그래밍언어론\\과제2코딩\\type.cpp"));
        Program prog = parser.program();
        //prog.display(0);           // student exercise
        System.out.println("\nBegin type checking...");
        System.out.println("Type map:");
        TypeMap map = StaticTypeCheck.typing(prog.decpart);
        //map.display();    // student exercise
        StaticTypeCheck.V(prog);
        Program out = T(prog, map);
        System.out.println("Output AST");
        out.display(0);    // student exercise
    } //main

    } // class TypeTransformer

    
