package ph.alephzero.finance;

import java.io.Serializable;

/**
 * Use UnsupportedOperationException instead.
 * 
 * @author jon
 *
 */
@Deprecated
public class OperationNotSupportedException extends RuntimeException implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3948658829535425138L; 
	
	public OperationNotSupportedException() {
		super();
	}
	
	public OperationNotSupportedException(String msg) {
		super(msg);
	}
	
	public OperationNotSupportedException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
