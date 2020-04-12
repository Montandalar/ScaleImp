package org.imperial2metric;

public class MeasurementModel {
    double real_mm;
    double real_ft;
    double real_in;
    double real_in_numerator;
    double real_in_denominator;
    double scale;
    double scale_in;
    double scale_mm;
    
    /// The number of millimetres in an inch.
    private final static double FACTOR_IN = 25.4;
    

    public boolean updateByRealImperial(
        double real_ft,
        double real_in,
        double real_in_numerator,
        double real_in_denominator
    )
    {
        // Bind variables
        this.real_ft = real_ft;
        this.real_in = real_in;
        this.real_in_numerator = real_in_numerator;
        this.real_in_denominator = real_in_denominator;

        System.out.format("real_ft = %f", real_ft);
        // Recalc real mm
        double result;
        result = FACTOR_IN*12*real_ft;
        System.out.format("result = %f", result);
        result += FACTOR_IN*real_in;
        double fraction = real_in_numerator/real_in_denominator;
        if (!(Double.isNaN(fraction) || Double.isInfinite(fraction))) {
            result += FACTOR_IN*fraction;
        }
        this.real_mm = result;
        
        // Recalc scale measurements
        this.scale_mm = result / this.scale;
        this.scale_in = this.scale_mm / FACTOR_IN;
        
        return true;
    }

    public boolean updateByScale(double newscale, SourceUnit sourceUnit) {
        this.scale = newscale;
        switch (sourceUnit) {
            case REAL_IMPERIAL:
                updateByRealImperial(real_ft, real_in,
                real_in_numerator, real_in_denominator);
                return true;
            case REAL_METRIC:
                updateByRealMetric(real_mm);
                return true;
            case SCALE_IMPERIAL:
                updateByScaleImperial(scale_in);
                return true;
            case SCALE_METRIC:
                updateByScaleMetric(scale_mm);
                return true;
            default:
                return false;
        }
    }

    private class ImperialMeasurement {
        public double ft;
        public double in;
        public double num;
        public double denom;

        private long floorWithUncertainty(double x) {
            return floorWithUncertainty(x, 0.0001);
        }
        private long floorWithUncertainty(double x, double uncertainty) {
            long x_ceil, x_floor;
            x_ceil = (long)Math.ceil(x);
            x_floor = (long)Math.floor(x);
            if (((x_ceil / x) - 1) < uncertainty) {
                return x_ceil;
            } else {
                return x_floor;
            }
        }
        
        public ImperialMeasurement(double mm)
        {
            System.out.format("mm = %f\n", mm);
            ft = mm/(MeasurementModel.FACTOR_IN*12);
            ft = floorWithUncertainty(ft);
            
            System.out.format("ft = %f\n", ft);
            in = (mm/MeasurementModel.FACTOR_IN)-(ft*12);
            in = floorWithUncertainty(in);
            System.out.format("in. before rounding: %f", mm/MeasurementModel.FACTOR_IN);
            System.out.format("in = %f\n", in);
            
            double mm_so_far = (ft*12*MeasurementModel.FACTOR_IN)+(in*MeasurementModel.FACTOR_IN);
            System.out.format("mm_so_far = %f\n", mm_so_far);
            double remainder = mm - mm_so_far;
            System.out.format("remainder = %f\n", remainder);
            assert remainder >= 0 : remainder;
            if (remainder == 0) {
                num = 0;
                denom = 0;
            } else {
                // Show thousandths only if there are no feet and inches
                num = remainder / (FACTOR_IN/64);
                num = floorWithUncertainty(num, 0.00001);
                denom = 64;
            }
            System.out.format("num = %f\n", num);
            System.out.format("denom = %f\n", denom);
        }
    }
    
    public void bindImperialFromMetric(double mm) {
        ImperialMeasurement realImp = new ImperialMeasurement(mm);
        this.real_ft = realImp.ft;
        this.real_in = realImp.in;
        this.real_in_numerator = realImp.num;
        this.real_in_denominator = realImp.denom;
    }

    public boolean updateByRealMetric(double real_mm) {
        this.real_mm = real_mm;
        bindImperialFromMetric(real_mm);
        this.scale_mm = real_mm / this.scale;
        this.scale_in = this.scale_mm / FACTOR_IN;
        return true;
    }
    
    public boolean updateByScaleImperial(double scale_in) {
        this.scale_in = scale_in;
        this.scale_mm = scale_in * FACTOR_IN;
        this.real_mm = scale_mm * this.scale;
        bindImperialFromMetric(real_mm);
        return true;
    }
    
    public boolean updateByScaleMetric(double scale_mm) {
        this.scale_mm = scale_mm;
        this.scale_in = scale_mm / FACTOR_IN;
        this.real_mm = scale_mm * scale;
        bindImperialFromMetric(this.real_mm);
        return true;
    }
    
    public MeasurementModel() {
        this(0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        1.0,
        0.0,
        0.0);
    }
    
    public MeasurementModel(double real_mm,
        double real_ft, double real_in,
        double real_in_numerator, double real_in_denominator,
        double scale, double scale_in, double scale_mm)
        {
            this.real_mm = real_mm;
            this.real_ft = real_ft;
            this.real_in = real_in;
            this.real_in_numerator = real_in_numerator;
            this.real_in_denominator = real_in_denominator;
            this.scale = scale;
            this.scale_in = scale_in;
            this.scale_mm = scale_mm;
        }
}