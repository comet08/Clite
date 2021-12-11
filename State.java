import java.util.*;
import java.util.Map.Entry;




class Memory extends ArrayList<Value>{ // 메모리주소 -> 값
	int size;
	Memory(Value v, int s){
		this.size = s;
		for(int i = 0 ; i < this.size; i++)
			this.add(v);
	}
}

public class State extends HashMap<Variable, Value> { 
    // Defines the set of variables and their associated values 
    // that are active during interpretation
  /*  Environment gamma; // 환경
    Memory mu; // 메모리
    int a; // 스택 포인터
    int slink; // static link
    int dlink; // dynamic link
    */
    public State( ) {
    	/*
    	gamma = new Environment();
    	mu = new Memory(Value.mkValue(Type.UNUSED), 1024);
    	a = 0;
    	slink = 0;
    	dlink = 0;
    	*/
    }
    
    public State(Variable key, Value val) {
        put(key, val);
    }
    
    public State onion(Variable key, Value val) {
        put(key, val);
        return this;
    }
    
    public State onion (State t) {
        for (Variable key : t.keySet( ))
            put(key, t.get(key));
        return this;
    }
    

    
    public void display() {
    	for (Entry<Variable, Value> entry : this.entrySet()) {
    	    System.out.println("<" + entry.getKey() + "," + entry.getValue()+">");
    	}
    }

}
