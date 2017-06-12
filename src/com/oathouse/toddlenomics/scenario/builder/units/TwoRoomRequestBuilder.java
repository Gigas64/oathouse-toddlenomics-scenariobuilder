/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * @(#)BookingRequestBuilder.java
 *
 * Copyright:	Copyright (c) 2013
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.toddlenomics.scenario.builder.units;

import com.oathouse.ccm.cma.booking.ChildBookingRequestService;
import com.oathouse.ccm.cma.profile.ChildService;
import com.oathouse.ccm.cos.bookings.BookingRequestBean;
import com.oathouse.ccm.cos.profile.AccountBean;
import com.oathouse.ccm.cos.profile.ChildBean;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import com.oathouse.oss.storage.valueholder.YWDHolder;
import com.oathouse.toddlenomics.scenario.builder.Scenario;
import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.commons.io.FileUtils;

/**
 * The {@code BookingRequestBuilder} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 12-May-2013
 */
public class TwoRoomRequestBuilder extends Scenario {

    public TwoRoomRequestBuilder(String rootStorePath, String authority, String logConfigFile) {
        super(rootStorePath, authority, logConfigFile);
    }

    @Override
    public void run() throws Exception {
        logger.info("Deleting Old BookingRequest Managers");
        FileUtils.deleteDirectory(new File(authorityPath, "bookingRequestManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "bookingRequestDayRange"));

        ChildService childService = engine().getChildService();
        ChildBookingRequestService requestService = engine().getChildBookingRequestService();

        int preIndex = 0;
        int aftIndex = 0;

        Set<Integer> regestSdList = new ConcurrentSkipListSet<>();

        for(AccountBean account : childService.getAccountManager().getAllObjects()) {
            int accountId = account.getAccountId();
            int liableContactId = childService.getLiableContactsForAccount(accountId).get(0).getContactId();
            regestSdList.add(4800599);
            BookingRequestBean allDay = requestService.createBookingRequest("All Day", "#9900CC", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(5400359);
            BookingRequestBean sessionMornAft = requestService.createBookingRequest("Session Morning/Afternoon", "#FF9933", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(7200179);
            BookingRequestBean sessionAft = requestService.createBookingRequest("Session Afternoon", "#FF0000", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(5400179);
            BookingRequestBean sessionMorn = requestService.createBookingRequest("Session Morning", "#FFFF00", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(9000179);
            BookingRequestBean schoolAft = requestService.createBookingRequest("School Club Afternoon", "#3366FF", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(4800059);
            BookingRequestBean schoolMorn = requestService.createBookingRequest("School Club Breakfast", "#00CC00", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(4800059);
            regestSdList.add(9000179);
            BookingRequestBean schoolMornAft = requestService.createBookingRequest("School Club Breakfast/Afternoon", "#006666", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            for(ChildBean child : childService.getChildrenForAccount(accountId)) {
                int startDate = CalendarStatic.getRelativeYW(-3);

                if(CalendarStatic.getAgeYearsMonths(child.getDateOfBirth())[0] < 5) {
                    if(preschool[preIndex][0] != NONE){
                        requestService.createBookingRequestDayRange(child.getChildId(), sessionMornAft.getRequestId(), startDate, YWDHolder.MAX_YWD, "term", BeanBuilder.getDays(preschool[preIndex][0]), owner);
                    }
                    if(preschool[preIndex][1] != NONE){
                        requestService.createBookingRequestDayRange(child.getChildId(), sessionMorn.getRequestId(), startDate, YWDHolder.MAX_YWD, "term", BeanBuilder.getDays(preschool[preIndex][1]), owner);
                    }
                    if(preschool[preIndex][2] != NONE){
                        requestService.createBookingRequestDayRange(child.getChildId(), sessionAft.getRequestId(), startDate, YWDHolder.MAX_YWD, "term", BeanBuilder.getDays(preschool[preIndex][2]), owner);
                    }
                    preIndex++;
                } else {
                    if(afterschool[aftIndex][0] != NONE){
                        requestService.createBookingRequestDayRange(child.getChildId(), schoolMornAft.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(afterschool[aftIndex][0]), owner);
                    }
                    if(afterschool[aftIndex][1] != NONE){
                        requestService.createBookingRequestDayRange(child.getChildId(), schoolMorn.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(afterschool[aftIndex][1]), owner);
                    }
                    if(afterschool[aftIndex][2] != NONE){
                        requestService.createBookingRequestDayRange(child.getChildId(), schoolAft.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(afterschool[aftIndex][2]), owner);
                    }
                    aftIndex++;
                }
            }
        }
    }

    private final int preschool[][] = {
        // { both , morn, aft }
        { MON+TUE,WED,0 },
        { THU+FRI,0,0 },
        { MON+TUE,0,THU },
        { THU,TUE+WED,FRI },
        { WED+THU,FRI,0 },
        { MON+WED,FRI,0 },
        { MON+TUE,WED,0 },
        { THU+FRI,0,0 },
        { TUE+WED,MON,0 },
        { MON+TUE,0,WED },
        { TUE+THU,MON,0 },
        { THU,TUE+FRI,MON }
    };

    private final int afterschool[][] = {
        // { both , morn, aft }
        { MON_FRI,0,0 },
        { MON_FRI,0,0 },
        { 0,MON_FRI,0 },
        { 0,0,0 },
        { MON_FRI,0,0 },
        { 0,MON_FRI,0 },
        { 0,0,MON_FRI },
        { MON_FRI,0,0 },
        { MON_FRI,0,0 },
        { 0,0,MON_FRI },
        { MON_FRI,0,0 },
        { 0,MON_FRI,0 },
        { MON_FRI,0,0 },
        { MON_FRI,0,0 },
        { 0,0,0 }
    };

    public static final int NONE = 0;
    public static final int MON = 1;
    public static final int TUE = 2;
    public static final int WED = 4;
    public static final int THU = 8;
    public static final int FRI = 16;
    public static final int MON_FRI = 31;


}
