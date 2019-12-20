package ru.spbstu.pipeline.implementation;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.pipeline.*;
import ru.spbstu.pipeline.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DummyReader implements Reader {
    protected List<Consumer> consumers;
    protected Logger logger;
    protected ReaderDataAccessor dataAccessor = new ReaderDataAccessor();

    protected DummyReader(){
        consumers = new ArrayList<>();
    }

    public DummyReader(String configFilename, Logger logger){
        this();
        this.logger = logger;
    }

    public DummyReader(Logger logger, String configFilename){
        this(configFilename, logger);
    }

    @Override
    public void addConsumer(Consumer consumer) {
        if (consumers.contains(consumer)) return;
        if (consumer != null) consumers.add(consumer);
        else if (logger != null) logger.log("Warning in DummyReader: " +
                "tried to add consumer that is null");
    }

    @Override
    public void addConsumers(List<Consumer> consumers){
        for (Consumer consumer: consumers)
            addConsumer(consumer);
    }

    @Override
    public Set<String> outputDataTypes() {
        return TypeCaster.getSupportedTypes();
    }

    class ReaderDataAccessor extends ProducerDataAccessor{
        private void put(Object data){
            caster.put(data);
        }
    }

    @NotNull
    @Override
    public DataAccessor getAccessor(String canonicalName) {
        Objects.requireNonNull(dataAccessor);
        dataAccessor.canonicalTypeName = canonicalName;
        return dataAccessor;
    }

    protected byte[] getBytes(){
        byte [] res = {1, 2, 3};
        return res;
    }

    @Override
    public Status status() {
        return Status.OK;
    }

    @Override
    public void run(){
        dataAccessor.put(getBytes());
        for (Consumer consumer: consumers){
            consumer.loadDataFrom(this);
            consumer.run();
        }
    }
}
