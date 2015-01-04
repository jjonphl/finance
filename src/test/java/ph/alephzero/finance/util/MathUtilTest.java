package ph.alephzero.finance.util;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

public class MathUtilTest {
    /**
     * @see CashFlowCalculatorTest.testInternalRateOfReturnExcelExample
     */
    @Test
    public void testGeometricSeriesSumExcelRateExample() {
     // Scenario: periods=4 years, monthly pmt=-200, pv=8000, assume monthly rate=10%
        assertEquals(MathUtil.geometricSeriesSum(-200.0, 1/1.10, 1, 4*12), -1979.385106, 0.000001);
    }
}
