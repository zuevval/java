package ru.spbstu.pipeline.implementation.converter;

/** Segment - a unit representing an interval [left; right) in real numbers
 *  serves converter class for arithmetic coding
 */
class Segment {
    double left;
    double right;

    Segment (double l, double r){
        left = l;
        right = r;
    }

    Segment() {
        left = 0.0;
        right = 0.0;
    }
}
