package com.larryhsiao.nyx.core.util;

import com.larryhsiao.clotho.Source;

import java.util.function.Function;

public class FactorySource<T> implements Source<T> {
    private final Function<Void, T> input;

    public FactorySource(Function<Void, T> input) {this.input = input;}

    @Override
    public T value() {
        return input.apply(null);
    }
}
