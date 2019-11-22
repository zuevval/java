package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class BasicWriter implements Writer  {
    protected Object data;
    protected Status status;
    protected List<Producer> producers;

    public void addProducer(Producer producer){
        if (producers.contains(producer)) return;
        producers.add(producer);
    }

    public void addProducers(List<Producer> producers){
        for (Producer producer: producers)
            addProducer(producer);
    }

    public void loadDataFrom(Producer producer){
        data = producer.get();
    }

    public Status status(){
        return this.status;
    }

    public BasicWriter(){
        this.producers = new ArrayList<>();
        this.status = Status.OK;
    }

    public void run(){
        for (Producer p: producers)
            if (data == null) data = p.get(); else break;
        if (data == null) {
            status = Status.WRITER_ERROR;
            return;
        }
        System.out.println(Arrays.toString((byte[])data));
    }
}
