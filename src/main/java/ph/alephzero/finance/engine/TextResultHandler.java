package ph.alephzero.finance.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ph.alephzero.finance.products.ContractResult;

/**
 * Sample result handler. Writes results to a text file.
 * 
 * @author jon
 *
 */
public class TextResultHandler implements ResultHandler {    
    private boolean logBatchName;
    private BufferedWriter out;

    public TextResultHandler(String filename) {
        try {
            out = new BufferedWriter(new FileWriter(filename, true));            
        } catch (IOException e) {
            throw new RuntimeException(e);            
        }        
        setLogBatchName(true);
    }
    
    /**
     * Include batchName in the output log. 
     * 
     * @return
     */
    public boolean isLogBatchName() {
        return logBatchName;
    }

    public void setLogBatchName(boolean logBatchName) {
        this.logBatchName = logBatchName;
    }

    @Override
    public void starting(String batchName) {
        try {            
            out.write("Starting batch [" + batchName + "]\n");
        } catch (IOException e) {
            throw new RuntimeException(e);            
        }        
    }

    @Override
    public void finished(String batchName) {
        try {
            out.write("Finished batch [" + batchName + "]\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public void handleResults(String batchName, ContractResult result) {
        
        try {
            out.write("[" + batchName + ":" + result.getIdentifier() + "] ");
            if (!result.isError()) {
                out.write("valuation=" + result.getValuation());
                for (String type : result.getAmountTypes()) {
                    out.write(";" + type.toLowerCase() + "=" + result.getMiscAmount(type));
                }
                out.newLine();
            } else {
                out.write("ERROR: " + result.getErrorMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);            
        }
    }

}
