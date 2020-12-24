package com.diy.sigmund.client.domain;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * @author ylm-sigmund
 * @since 2020/9/19 13:55
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 1853959283676361119L;
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
