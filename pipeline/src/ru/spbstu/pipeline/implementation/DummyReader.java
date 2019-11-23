package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.*;
import ru.spbstu.pipeline.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class DummyReader implements Reader {
    protected List<Consumer> consumers;
    protected Logger logger;

    public DummyReader(){
        consumers = new ArrayList<>();
    }

    public DummyReader(String configFilename){
        this();
    }

    public DummyReader(Logger logger){
        this();
        this.logger = logger;
    }

    public DummyReader(String configFilename, Logger logger){
        this(logger);
    }

    public void addConsumer(Consumer consumer) {
        if (consumers.contains(consumer)) return;
        if (consumer != null) consumers.add(consumer);
        else if (logger != null) logger.log("Warning in DummyReader: " +
                "tried to add consumer that is null");
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
        for (Consumer consumer: consumers){
            consumer.loadDataFrom(this);
            consumer.run();
        }
    }
}
