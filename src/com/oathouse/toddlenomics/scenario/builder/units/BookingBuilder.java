/*
 * @(#)BookingBuilder.java
 *
 * Copyright:	Copyright (c) 2013
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.toddlenomics.scenario.builder.units;

import com.oathouse.ccm.cma.booking.ChildBookingRequestService;
import com.oathouse.ccm.cma.exceptions.ExceedCapacityException;
import com.oathouse.ccm.cma.profile.ChildService;
import com.oathouse.ccm.cos.bookings.BTFlagIdBits;
import com.oathouse.ccm.cos.bookings.BTIdBits;
import com.oathouse.ccm.cos.bookings.BookingBean;
import com.oathouse.ccm.cos.profile.ChildBean;
import com.oathouse.ccm.cos.properties.SystemPropertiesBean;
import com.oathouse.oss.storage.exceptions.IllegalActionException;
import com.oathouse.oss.storage.exceptions.IllegalValueException;
import com.oathouse.oss.storage.exceptions.MaxCountReachedException;
import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.NoSuchKeyException;
import com.oathouse.oss.storage.exceptions.NullObjectException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import com.oathouse.oss.storage.valueholder.YWDHolder;
import com.oathouse.toddlenomics.scenario.builder.Scenario;
import java.io.File;
import org.apache.commons.io.FileUtils;

/**
 * The {@code BookingBuilder} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 30-Jun-2013
 */
public class BookingBuilder extends Scenario {

    public BookingBuilder(String rootStorePath, String authority, String logConfigFile) {
        super(rootStorePath, authority, logConfigFile);
    }

    @Override
    public void run() throws Exception {
        logger.info("Deleting Old Booking Managers");
        FileUtils.deleteDirectory(new File(authorityPath, "childBookingManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "childBookingHistory"));

        logger.info("Processing Future Bookings");
        ChildBookingRequestService bookingRequestService = engine().getChildBookingRequestService();
        SystemPropertiesBean properties = engine().getPropertiesService().getSystemProperties();
        for(int week = 1; week < properties.getConfirmedPeriodWeeks(); week++) {
            bookingRequestService.setYwBookingRequests(CalendarStatic.getRelativeYW(week), true, owner);
        }
        logger.info("Processing Past Bookings");
        for(BookingBean b : engine().getChildBookingService().getYwBookings(CalendarStatic.getRelativeYW(1), BTIdBits.TYPE_ALL, BTFlagIdBits.TYPE_ALL)) {
            for(int i = -6; i < 1; i++){
                int ywd = YWDHolder.add(b.getYwd(), (i*10));
                try {
                    engine().getChildBookingService().setBooking(ywd, b.getRoomId(), b.getBookingSd(), b.getProfileId(), b.getLiableContactId(), b.getBookingDropOffId(), b.getBookingPickupId(), b.getBookingTypeId(), "", ywd, owner);
                } catch(IllegalActionException | IllegalArgumentException iae) {
                    // because we are rolling over weekends ignore this as it is because the nursary is closed
                }
            }
        }
    }
}
