/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * @(#)OneRoomScenario.java
 *
 * Copyright:	Copyright (c) 2013
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.toddlenomics.scenario.builder.suite;

import com.oathouse.toddlenomics.scenario.builder.Scenario;
import com.oathouse.toddlenomics.scenario.builder.units.AccountBuilder;
import com.oathouse.toddlenomics.scenario.builder.units.BookingBuilder;
import com.oathouse.toddlenomics.scenario.builder.units.RoomType;
import com.oathouse.toddlenomics.scenario.builder.units.TwoRoomRequestBuilder;
import java.io.File;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

/**
 * The {@code OneRoomScenario} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 28-Jun-2013
 */
public class TwoRoomScenario extends Scenario {

    public TwoRoomScenario(String rootStorePath, String authority, String logConfigFile) {
        super(rootStorePath, authority, logConfigFile);
    }

    @Override
    public void run() throws Exception {

        logger.info("Adding Accounts");
        new AccountBuilder(RoomType.TWO_ROOM, rootStorePath, authority, logConfigFile).run();
        logger.info("Adding Requests");
        new TwoRoomRequestBuilder(rootStorePath, authority, logConfigFile).run();
        logger.info("Adding Bookings");
        new BookingBuilder(rootStorePath, authority, logConfigFile).run();
   }

}
