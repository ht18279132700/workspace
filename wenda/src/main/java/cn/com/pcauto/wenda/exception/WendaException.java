package cn.com.pcauto.wenda.exception;

public class WendaException extends RuntimeException {

	private static final long serialVersionUID = 8741143881080841501L;

	public WendaException() {
    	super();
    }
    
    public WendaException(String message) {
    	super(message);
    }
    
    public WendaException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WendaException(Throwable cause) {
        super(cause);
    }
}
