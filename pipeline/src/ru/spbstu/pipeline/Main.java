package ru.spbstu.pipeline;

import ru.spbstu.pipeline.implementation.DummyReader;
import ru.spbstu.pipeline.implementation.BasicWriter;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String [] args){
        Reader dr = new DummyReader();
        Writer bw = new BasicWriter();
        dr.addConsumer(bw);
        bw.addProducer(dr);
        dr.run();
    }
}
