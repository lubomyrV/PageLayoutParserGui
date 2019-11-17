import java.util.EventObject;

public class FormEvent extends EventObject{
	
	private String source;
	private String destination;
	
	public FormEvent(Object obj) {
		super(obj);
	}
	
	public FormEvent(Object obj, String source, String destination) {
		super(obj);
		this.source = source;
		this.destination = destination;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
}
