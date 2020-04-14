package com.larryhsiao.nyx.core.web;


import java.io.IOException;
import java.io.InputStream;

/**
 * Java class resources
 */
public class ClassResourceFiles implements ResourceFiles {

    @Override
    public InputStream open(String path) throws IOException {
        return getClass().getClassLoader().getResource(path).openStream();
    }
}
