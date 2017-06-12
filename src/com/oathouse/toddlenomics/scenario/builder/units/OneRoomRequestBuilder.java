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
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import com.oathouse.oss.storage.valueholder.SDHolder;
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
public class OneRoomRequestBuilder extends Scenario {

    public OneRoomRequestBuilder(String rootStorePath, String authority, String logConfigFile) {
        super(rootStorePath, authority, logConfigFile);
    }

    @Override
    public void run() throws Exception {
        logger.info("Deleting Old BookingRequest Managers");
        FileUtils.deleteDirectory(new File(authorityPath, "bookingRequestManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "bookingRequestDayRange"));

        ChildService childService = engine().getChildService();
        ChildBookingRequestService requestService = engine().getChildBookingRequestService();

        int index = 0;

        Set<Integer> regestSdList = new ConcurrentSkipListSet<>();

        for(AccountBean account : childService.getAccountManager().getAllObjects()) {
            int accountId = account.getAccountId();
            int liableContactId = childService.getLiableContactsForAccount(accountId).get(0).getContactId();
            regestSdList.add(SDHolder.buildSD("08:00", "17:00"));
            BookingRequestBean allDay = requestService.createBookingRequest("All Day", "#9900CC", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(SDHolder.buildSD("09:00", "16:00"));
            BookingRequestBean shortDay = requestService.createBookingRequest("Short Day", "#FF9933", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(SDHolder.buildSD("09:00", "12:00"));
            BookingRequestBean morning = requestService.createBookingRequest("Morning Session", "#FF0000", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(SDHolder.buildSD("13:00", "16:00"));
            BookingRequestBean afternoon = requestService.createBookingRequest("Afternoon Session", "#FFFF00", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(SDHolder.buildSD("08:00", "12:00"));
            BookingRequestBean breakfast = requestService.createBookingRequest("Morning Session with Breakfast", "#3366FF", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(SDHolder.buildSD("13:00", "17:00"));
            BookingRequestBean snack = requestService.createBookingRequest("Afternoon Session With Snack", "#00CC00", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            regestSdList.clear();
            regestSdList.add(SDHolder.buildSD("09:00", "12:00"));
            regestSdList.add(SDHolder.buildSD("13:00", "16:00"));
            BookingRequestBean sessions = requestService.createBookingRequest("Sessions Only", "#006666", accountId, liableContactId, -1, -1, regestSdList, 8194, owner);

            for(ChildBean child : childService.getChildrenForAccount(accountId)) {
                int startDate = CalendarStatic.getRelativeYW(-3);

                if(toddlers[index][ALL_DAY] != NONE){
                    requestService.createBookingRequestDayRange(child.getChildId(), allDay.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(toddlers[index][ALL_DAY]), owner);
                }
                if(toddlers[index][SHORT_DAY] != NONE){
                    requestService.createBookingRequestDayRange(child.getChildId(), shortDay.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(toddlers[index][SHORT_DAY]), owner);
                }
                if(toddlers[index][MORNING] != NONE){
                    requestService.createBookingRequestDayRange(child.getChildId(), morning.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(toddlers[index][MORNING]), owner);
                }
                if(toddlers[index][AFTERNOON] != NONE){
                    requestService.createBookingRequestDayRange(child.getChildId(), afternoon.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(toddlers[index][AFTERNOON]), owner);
                }
                if(toddlers[index][BREAKFAST] != NONE){
                    requestService.createBookingRequestDayRange(child.getChildId(), breakfast.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(toddlers[index][BREAKFAST]), owner);
                }
                if(toddlers[index][SNACK] != NONE){
                    requestService.createBookingRequestDayRange(child.getChildId(), snack.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(toddlers[index][SNACK]), owner);
                }
                if(toddlers[index][SESSIONS] != NONE){
                    requestService.createBookingRequestDayRange(child.getChildId(), sessions.getRequestId(), startDate, YWDHolder.MAX_YWD, "", BeanBuilder.getDays(toddlers[index][SESSIONS]), owner);
                }
                index++;
            }
        }
    }
    private static int ALL_DAY = 0;
    private static int SHORT_DAY = 1;
    private static int MORNING = 2;
    private static int AFTERNOON = 3;
    private static int BREAKFAST = 4;
    private static int SNACK = 5;
    private static int SESSIONS = 6;

    private final int toddlers[][] = {
        // { all, short, morn, aft, break, snack, ses }
        { MON_FRI,0,0,0,0,0,0 }, // 1
        { MON_FRI,0,0,0,0,0,0 }, // 1
        { MON+WED,0,0,0,FRI,0,0 }, // 2
        { MON+WED,0,0,0,FRI,0,0 }, // 2
        { MON_FRI,0,0,0,0,0,0 }, // 3 (underage)
        { MON_FRI,0,0,0,0,0,0 }, // 3
        { 0,0,0,0,0,0,MON_FRI },
        { 0,TUE,0,WED+THU,0,0,0 },
        { MON,0,0,0,TUE+THU,0,0 },
        { 0,0,0,0,MON_FRI,0,0 },
        { 0,0,0,0,0,MON_FRI,0 },
        { 0,MON_FRI,0,0,0,0,0 },
        { 0,0,MON_FRI,0,0,0,0 },
        { WED,0,0,0,THU+FRI,0,0 },
        { WED,0,0,0,THU+FRI,0,0 }
    };

    public static final int NONE = 0;
    public static final int MON = 1;
    public static final int TUE = 2;
    public static final int WED = 4;
    public static final int THU = 8;
    public static final int FRI = 16;
    public static final int MON_FRI = 31;


}
