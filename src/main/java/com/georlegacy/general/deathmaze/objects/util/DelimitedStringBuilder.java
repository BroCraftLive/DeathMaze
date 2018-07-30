package com.georlegacy.general.deathmaze.objects.util;

public class DelimitedStringBuilder {

    private final String delimiter;

    private final StringBuilder builder;

    public DelimitedStringBuilder() {
        this(" ");
    }

    public DelimitedStringBuilder(String delimiter) {
        this.delimiter = delimiter;
        this.builder = new StringBuilder();
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }

    public void append(String s) {
        this.builder.append(s + this.delimiter);
    }

    public void append(char c) {
        this.builder.append(c + this.delimiter);
    }

}
