package org.mobilehub.installment_service.util;

public final class InstallmentCalculator {

    private InstallmentCalculator() {
    }

    /**
     * Tính tiền trả góp hàng tháng theo công thức annuity (dư nợ giảm dần).
     *
     * @param loanAmount             số tiền vay (P)
     * @param monthlyInterestPercent lãi suất %/tháng (vd: 1.8 nghĩa là 1.8%/tháng)
     * @param tenorMonths            số tháng trả góp (n)
     * @return số tiền phải trả mỗi tháng (đã làm tròn)
     */
    public static long calculateAnnuityMonthlyPayment(long loanAmount,
                                                      double monthlyInterestPercent,
                                                      int tenorMonths) {

        if (tenorMonths <= 0) {
            throw new IllegalArgumentException("tenorMonths must be > 0");
        }

        double r = monthlyInterestPercent / 100.0; // 1.8 -> 0.018

        // Gói 0% lãi
        if (r == 0.0) {
            return Math.round((double) loanAmount / tenorMonths);
        }

        double factor = Math.pow(1 + r, tenorMonths);
        double payment = loanAmount * r * factor / (factor - 1);

        return Math.round(payment);
    }
}
