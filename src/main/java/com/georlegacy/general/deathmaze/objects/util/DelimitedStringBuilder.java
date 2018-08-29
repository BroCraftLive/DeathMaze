package com.georlegacy.general.deathmaze.objects.util;

public class DelimitedStringBuilder {

    private final String delimiter;

    private final StringBuilder builder;

    boolean hasNone;

    public DelimitedStringBuilder() {
        this(" ");
    }

    public DelimitedStringBuilder(String delimiter) {
        this.delimiter = delimiter;
        this.builder = new StringBuilder();
        this.hasNone = true;
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }

    public void append(String s) {
        if (hasNone) {
            hasNone = false;
            this.builder.append(s);
        }
        else {
            this.builder.append(this.delimiter + s);
        }
    }


}
