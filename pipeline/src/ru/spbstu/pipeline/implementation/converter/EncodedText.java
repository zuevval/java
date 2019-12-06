package ru.spbstu.pipeline.implementation.converter;

import java.util.Map;

public class EncodedText {
    public Map<Character, Double> dictionary;
    public double value;

    public EncodedText(Map<Character, Double> dic, double val){
        dictionary = dic;
        value = val;
    }
}
