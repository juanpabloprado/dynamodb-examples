package com.example.model;

import io.micronaut.core.annotation.NonNull;

public interface Identified {

    @NonNull
    String getId();
}