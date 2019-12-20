package ru.spbstu.pipeline.implementation;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.pipeline.Producer;

import java.util.Objects;

public abstract class ProducerDataAccessor
        implements Producer.DataAccessor {
    protected TypeCaster caster = new TypeCaster();
    String canonicalTypeName = byte[].class.getCanonicalName();

    @Override
    public long size() {
        return caster.size(canonicalTypeName);
    }

    @NotNull
    @Override
    public Object get(){
        Object output = caster.get(canonicalTypeName);
        Objects.requireNonNull(output);
        return output;
    }
}
