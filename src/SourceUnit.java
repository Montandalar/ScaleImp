package org.imperial2metric;

public enum SourceUnit {
    REAL_IMPERIAL, SCALE_IMPERIAL, REAL_METRIC, SCALE_METRIC, INVAL;
    
    public SourceUnit next() {
        switch(this) {
            case REAL_IMPERIAL:
                return SCALE_IMPERIAL;
            case SCALE_IMPERIAL:
                return REAL_METRIC;
            case REAL_METRIC:
                return SCALE_METRIC;
            case SCALE_METRIC:
                return REAL_IMPERIAL;
            default:
                return INVAL;
        }
    }
    
    public SourceUnit prev() {
    switch(this) {
        case REAL_IMPERIAL:
            return SCALE_METRIC;
        case SCALE_IMPERIAL:
            return REAL_IMPERIAL;
        case REAL_METRIC:
            return SCALE_IMPERIAL;
        case SCALE_METRIC:
            return REAL_METRIC;
        default:
            return INVAL;
    }
    }
}