import java.util.*;

public class TypeMap extends HashMap<Variable, Type> { 

// TypeMap is implemented as a Java HashMap.  
// Plus a 'display' method to facilitate experimentation.
	
	public TypeMap onion(TypeMap t) {
		TypeMap tm = new TypeMap();
		tm.putAll(this);
		tm.putAll(t);
		return tm;
	}
	
    public void display() {
    	for (Entry<Variable, Type> entry : this.entrySet()) {
    	    System.out.println("<" + entry.getKey() + "," + entry.getValue()+">");
    	}
    }
    
    public void display(Functions f, TypeMap tm) {
    	for (Entry<Variable, Type> entry : this.entrySet()) {
    	    System.out.println("<" + entry.getKey() + "," + entry.getValue()+">");
    	}
    }
	
}
