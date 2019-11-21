package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.*;

import java.util.ArrayList;
import java.util.List;

public class DummyReader implements Reader {
    protected List<Consumer> consumers;

    public DummyReader(String confPath){
        consumers = new ArrayList<>();
    }

    public void addConsumer(Consumer consumer) {
        if (consumers.contains(consumer)) return;
        consumers.add(consumer);
    }

    public void addConsumers(List<Consumer> consumers){
        for (Consumer consumer: consumers)
            addConsumer(consumer);
    }

    public Object get(){
        byte [] res = {1, 2, 3};
        return res;
    }

    public Status status() {
        return Status.OK;
    }

    public void run(){
        for (Consumer consumer: consumers)
            consumer.run();
    }
}
