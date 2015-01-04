package ph.alephzero.finance;

import java.io.IOException;
import java.util.Properties;

/**
 * Finance library metadata.
 * 
 * @author jon
 *
 */
public final class Library {
	public static final String VERSION;
	
    static {
        Properties props = new Properties();        
        try {
            props.load(Library.class.getResourceAsStream("library.properties"));            
        } catch (IOException e) {

        }
        
        VERSION = props.getProperty("version", "UNKNOWN");
        
    };
}
