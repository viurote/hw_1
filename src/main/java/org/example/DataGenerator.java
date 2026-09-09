package org.example;

import java.util.List;

public interface DataGenerator<T> {
    List<T> generate(int count);
}
