package ph.alephzero.finance;

public enum Compounding {
    SIMPLE,                    // (1 + rt)
    COMPOUNDED,                // (1 + r)^t
    SIMPLE_THEN_COMPOUNDED,    // SIMPLE if t <= 1 year, else COMPOUNDED
    CONTINUOUS;                // exp(rt)
}
