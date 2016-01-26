package org.springframework.boot.autoconfigure.jdbc;


import javax.annotation.PostConstruct;

public class MyDataSourceInitializer extends DataSourceInitializer {

    @PostConstruct
    public void init() {
    }

}

