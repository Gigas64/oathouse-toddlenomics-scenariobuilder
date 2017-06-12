/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * @(#)Scenario.java
 *
 * Copyright:	Copyright (c) 2013
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.toddlenomics.scenario.builder;

import com.oathouse.ccm.cma.ServicePool;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import java.io.File;
import java.nio.file.Paths;
import org.apache.log4j.Logger;

/**
 * The {@code Scenario} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 09-Apr-2013
 */
public abstract class Scenario {
    protected final static Logger logger = Logger.getLogger(Scenario.class);

    protected String rootStorePath;
    protected String authority;
    protected File authorityPath;
    protected String owner;
    protected String logConfigFile;
    private ServicePool engine;

    public Scenario(String rootStorePath, String authority, String logConfigFile) {
        this.rootStorePath = rootStorePath;
        this.authority = authority;
        this.authorityPath = new File(Paths.get(rootStorePath + "/files/" + authority).toString());
        this.owner = ObjectBean.SYSTEM_OWNED;
        this.logConfigFile = logConfigFile;
        this.engine = null;
    }

    public ServicePool engine() {
        if(engine == null) {
            setServicePool();
        }
        return engine;
    }

    public void setServicePool() {
        engine = Engine.getInstance(rootStorePath, authority, logConfigFile);
    }

    public abstract void run() throws Exception;
}
