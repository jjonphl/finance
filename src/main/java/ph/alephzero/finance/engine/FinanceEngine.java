package ph.alephzero.finance.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ph.alephzero.finance.context.Context;
import ph.alephzero.finance.products.Contract;
import ph.alephzero.finance.products.ContractHandler;
import ph.alephzero.finance.products.ContractResult;

/**
 * The driver class for financial engine. Functional components, such as classes implementing 
 * actual computations and result storage, are "assembled" into a FinanceEngine. After configuring
 * the FinanceEngine, contracts can be submitted for financial calculations.
 *  
 * It is assumed that all components are thread-safe, resulting in FinanceEngine itself being
 * thread-safe. Contracts can be submitted in batches concurrently.
 * 
 * 
 * @author jon
 *
 */
public class FinanceEngine {
    public final String DEFAULT_BATCH_NAME = "DEFAULT_BATCH";
    private Context context;
    private HashMap<String, ContractHandler> contractHandlers;    
    private HashSet<String> runningBatches;
    private ResultHandler resultHandler;
    
    public FinanceEngine(Context context, ResultHandler resultHandler) {
        this.context = context;
        this.resultHandler = resultHandler;
        contractHandlers = new HashMap<>();
        runningBatches = new HashSet<>();
    }
    
    /**
     * Registers <code>batchName</code> as running. This is to prevent
     * it from running more than once (e.g. in multiple threads).
     * 
     * @param batchName
     * @return true if everything is ok
     */
    private synchronized boolean batchStarting(String batchName) {
        if (runningBatches.contains(batchName)) return false;
        runningBatches.add(batchName);
        return true;
    }
    
    /**
     * Unregisters <code>batchName</code> after it has finished running.
     * The user can send another batch with the same <code>batchName</code>.
     * 
     * @param batchName
     * @return
     */
    private synchronized boolean batchFinished(String batchName) {
        if (runningBatches.contains(batchName)) {
            runningBatches.remove(batchName);
            return true;
        }
        return false;
    }
    
    /**
     * Register a set of ContractHandler's using a Map so we can wire from
     * Spring. 
     * 
     * WARNING: This discards all existing contract handlers.
     * 
     * @param handlers
     */
    public void setContractHandlers(Map<String, ContractHandler> handlers) {
        contractHandlers = new HashMap<>(handlers);
    }
    
    public void setContractHandler(String type, ContractHandler handler) {
        contractHandlers.put(type, handler);
    }
    
    public void calculate(List<Contract> contracts) {
        calculate(DEFAULT_BATCH_NAME, contracts.iterator());
    }
    
    public void calculate(Iterator<Contract> contracts) {
        calculate(DEFAULT_BATCH_NAME, contracts);
    }
    
    public void calculate(String batchName, Iterator<Contract> contracts) {
        if (!batchStarting(batchName)) {
            throw new RuntimeException("Batch [" + batchName + "] is already defined and is currently running.");
        }
        resultHandler.starting(batchName);
        
        while (contracts.hasNext()) {
            Contract contract = contracts.next();
            if (contractHandlers.containsKey(contract.getType())) {
                ContractHandler handler = contractHandlers.get(contract.getType());
                ContractResult result = handler.calculate(context, contract);
                resultHandler.handleResults(batchName, result);
            } else {
                //TODO: log!!!
            }
        }
        
        resultHandler.finished(batchName);
        
        if (!batchFinished(batchName)) {
            throw new RuntimeException("BUG: Cannot remove [" + batchName + "].");
        }
    }
    
    /**
     * Run calculation in a separate thread.
     * 
     * @param contracts
     */
    public void calculateAsync(List<Contract> contracts) {
        
    }
}
