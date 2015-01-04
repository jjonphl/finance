package ph.alephzero.finance.cashflows;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;


public class CashFlowCalculatorTest {

    /**
     * 
     */
    @Test
    public void testPresentValueExcelExample() {
        // Scenario: Pay 500 every month for 20 years with annualized interest rate of 8%
        assertEquals(CashFlowCalculator.presentValue(0.08/12, 12*20, 500, 0.0, true), 59777.145851, 0.000001);
        assertEquals(CashFlowCalculator.presentValue(0.08/12, 12*20, 500, 0.0, false), 60175.660157, 0.000001);

        CashFlows cf = BasicCashFlows.buildCashFlows(12*20, 0.0, 500.0, 0.0, true);
        assertEquals(CashFlowCalculator.presentValue(cf, 0.08/12), 59777.145851, 0.000001);
        
        cf = BasicCashFlows.buildCashFlows(12*20, 0.0, 500.0, 0.0, false);
        assertEquals(CashFlowCalculator.presentValue(cf, 0.08/12), 60175.660157, 0.000001);
    }

    @Test
    public void testPresentValueFractionalPeriods() {
        assertEquals(CashFlowCalculator.presentValue(0.05, 2.5, 10, 100, true), 111.48298658, 0.00000001);
        assertEquals(CashFlowCalculator.presentValue(0.05, 2.5, 10, 100, false), 112.63128524, 0.00000001);
    }
      
    /**
     * 
     */
    @Test
    public void testFutureValueExcelExample() {
        // Scenario: monthly pmt=-200, pv=-500, annual rate=6%, periods=10 months
        assertEquals(CashFlowCalculator.futureValue(0.06/12, 10, 200, 500, true), 2571.175348, 0.000001);
        assertEquals(CashFlowCalculator.futureValue(0.06/12, 10, 200, 500, false), 2581.403374, 0.000001);        
    }

    @Test
    public void testFutureValueFractionalPeriods() {
        assertEquals(CashFlowCalculator.futureValue(0.05, 2.5, 10, 100, true), 138.91789658, 0.00000001);
        assertEquals(CashFlowCalculator.futureValue(0.05, 2.5, 10, 100, false), 140.21515980, 0.00000001);
    }
   
    @Test
    public void testAnnuitiesExcelExample() {
        // Scenario: annual rate=8%, pv=10000, periods=10 months
        assertEquals(CashFlowCalculator.annuities(0.08/12, 10, 10000, 0.0, true), 1037.032089, 0.000001);
        assertEquals(CashFlowCalculator.annuities(0.08/12, 10, 10000, 0.0, false), 1030.164327, 0.000001);
        
        // Scenario: annual rate=6%, fv=50000, periods=18 years
        assertEquals(CashFlowCalculator.annuities(0.06/12, 18*12, 0.0, 50000, true), 129.081161, 0.000001);
        assertEquals(CashFlowCalculator.annuities(0.06/12, 18*12, 0.0, 50000, false), 128.438966, 0.000001);
    }
    
    @Test
    public void testInternalRateOfReturnExcelExample() {
        // Scenario: periods=4 years, monthly pmt=-200, pv=8000
        assertEquals(CashFlowCalculator.internalRateOfReturn(4*12, -200.0, 8000.0, 0.0, true), 0.007701, 0.000001);
    }
      
}
