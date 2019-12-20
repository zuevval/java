package ru.spbstu.pipeline.implementation.converter;

import java.util.Map;

class EncodedText {
    Map<Character, Double> dictionary;
    double value;

    EncodedText(Map<Character, Double> dic, double val){
        dictionary = dic;
        value = val;
    }
}
