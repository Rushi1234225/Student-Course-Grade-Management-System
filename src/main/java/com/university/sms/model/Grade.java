package com.university.sms.model;

/**
 * Letter grades supported by the system, each mapped to a GPA point value
 * on a standard 4.0 scale. NOT_GRADED represents an active enrollment
 * that has not yet been assessed.
 */
public enum Grade {
    A_PLUS(4.0),
    A(4.0),
    A_MINUS(3.7),
    B_PLUS(3.3),
    B(3.0),
    B_MINUS(2.7),
    C_PLUS(2.3),
    C(2.0),
    C_MINUS(1.7),
    D(1.0),
    F(0.0),
    NOT_GRADED(0.0);

    private final double gpaPoints;

    Grade(double gpaPoints) {
        this.gpaPoints = gpaPoints;
    }

    public double getGpaPoints() {
        return gpaPoints;
    }

    public boolean isGraded() {
        return this != NOT_GRADED;
    }
}
