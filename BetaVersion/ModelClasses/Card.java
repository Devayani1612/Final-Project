package ModelClasses;

public class Card {
    private final String symbol;
    private boolean matched;
    private boolean flipped;
    
    public Card(String symbol) {
        this.symbol = symbol;
        this.matched = false;
        this.flipped = false;
    }
    
    public String getSymbol() { 
    	return symbol; 
    	}
    public boolean isMatched() { 
    	return matched; 
    	}
    public boolean isFlipped() { 
    	return flipped; 
    	}
    public void setMatched(boolean matched) {
    	this.matched = matched;
    	}
    public void setFlipped(boolean flipped) { 
    	this.flipped = flipped; 
    	}
}
