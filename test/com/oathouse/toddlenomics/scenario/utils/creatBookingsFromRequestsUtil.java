/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.toddlenomics.scenario.utils;

import com.oathouse.ccm.cma.ServicePool;
import com.oathouse.ccm.cma.booking.ChildBookingRequestService;
import com.oathouse.ccm.cma.booking.ChildBookingService;
import com.oathouse.ccm.cos.properties.SystemPropertiesBean;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import com.oathouse.toddlenomics.scenario.builder.Engine;
import java.io.File;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl Oatridge
 */
public class creatBookingsFromRequestsUtil {
    private String owner = ObjectBean.SYSTEM_OWNED;
    private String project = "Web"; // set as Web or Api
    private String authority = "Toddlenomics"+project;
    private String rootStorePath = Paths.get("D:/Development/OathouseProjects/ToddlenomicApps/Toddlenomics"+project+"/oss/data").toString();
    private String logConfigFile = Paths.get(rootStorePath + "/conf/oss_log4j.properties").toString();
    private ServicePool engine;

    @Before
    public void setup() {
        engine = Engine.getInstance(rootStorePath, authority, logConfigFile);
    }


    @After
    public void tearDown() {

    }

    /*
     * takes all the current booking requests and makes them into booking for all weeks
     * as set by SystemProperties
     */
    @Test
    public void createAllBookingsFromRequest() throws Exception {
        ChildBookingRequestService requestService = engine.getChildBookingRequestService();
        SystemPropertiesBean properties = engine.getPropertiesService().getSystemProperties();

        for(int week = 0; week < properties.getConfirmedPeriodWeeks(); week++) {
            requestService.setYwBookingRequests(CalendarStatic.getRelativeYW(week), true, owner);
        }
    }

}