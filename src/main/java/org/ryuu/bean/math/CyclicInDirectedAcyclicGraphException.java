package org.ryuu.bean.math;

public class CyclicInDirectedAcyclicGraphException extends RuntimeException {
    public CyclicInDirectedAcyclicGraphException() {
        super();
    }

    public CyclicInDirectedAcyclicGraphException(String message) {
        super(message);
    }

    public CyclicInDirectedAcyclicGraphException(String message, Throwable cause) {
        super(message, cause);
    }

    public CyclicInDirectedAcyclicGraphException(Throwable cause) {
        super(cause);
    }

    public CyclicInDirectedAcyclicGraphException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
