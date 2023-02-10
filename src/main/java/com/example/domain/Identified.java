package com.example.domain;

import io.micronaut.core.annotation.NonNull;

public interface Identified {

    @NonNull
    String getId();
}