/*
 * @(#)Engine.java
 *
 * Copyright:	Copyright (c) 2013
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.toddlenomics.scenario.builder;

import com.oathouse.ccm.cma.ServicePool;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum;
import java.util.List;

/**
 * The {@code Engine} Class is an abstract class
 *
 * @author Darryl Oatridge
 * @version 1.00 09-Apr-2013
 */
public class Engine {
    // Singleton Instance
    private volatile static ServicePool engine;

    public static ServicePool getInstance(String rootStorePath, String authority, String logConfigFile) {
        if(engine == null) {
            synchronized (ServicePool.class) {
                // Check again just incase before we synchronised an instance was created
                if(engine == null) {
                    engine = new ServicePool(rootStorePath, authority, logConfigFile);
                }
            }
        }
        return engine;
    }

    public static void printList(List<? extends ObjectBean> oList) {
        for(ObjectBean objectBean : oList) {
            System.out.println(objectBean.toXML(ObjectDataOptionsEnum.COMPACTED, ObjectDataOptionsEnum.TRIMMED));
        }
    }



    private Engine() {
    }
}
