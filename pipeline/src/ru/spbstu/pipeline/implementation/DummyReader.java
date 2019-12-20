package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.*;
import ru.spbstu.pipeline.logging.Logger;

import java.util.ArrayList;
import java.util.List;
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

    class ReaderDataAccessor implements DataAccessor{
        TypeCaster caster = new TypeCaster();
        String canonicalTypeName = byte[].class.getCanonicalName();

        private void put(Object data){
            caster.put(data);
        }

        @Override
        public long size() {
            return caster.size(canonicalTypeName);
        }
        @Override
        public Object get(){
            return caster.get(canonicalTypeName);
        }
    }

    public DataAccessor getAccessor(String canonicalName){
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
