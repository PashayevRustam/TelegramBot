package org.example;

import java.util.List;

class ResponseData {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    public List<Choice> getChoices() {
        return choices;
    }
}

