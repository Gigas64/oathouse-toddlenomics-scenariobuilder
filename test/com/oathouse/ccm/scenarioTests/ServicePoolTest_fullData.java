/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.ccm.scenarioTests;

import com.oathouse.ccm.cma.ServicePool;
import com.oathouse.ccm.cma.accounts.TransactionService;
import com.oathouse.ccm.cma.booking.ChildBookingModel;
import com.oathouse.ccm.cma.dto.InvoiceLineDTO;
import com.oathouse.ccm.cma.dto.InvoiceLineTransformer;
import com.oathouse.ccm.cos.accounts.TDCalc;
import com.oathouse.ccm.cos.accounts.finance.BillingBean;
import com.oathouse.ccm.cos.config.finance.BillingEnum;
import com.oathouse.toddlenomics.scenario.builder.Engine;
import com.oathouse.oss.storage.exceptions.PersistenceException;
// common imports
import com.oathouse.oss.storage.objectstore.ObjectDBMS;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import java.io.File;
import java.util.*;
import static java.util.Arrays.*;
import java.util.concurrent.ConcurrentHashMap;
// Test Imports
import mockit.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
/**
 *
 * @author Darryl Oatridge
 */
public class ServicePoolTest_fullData {
    private String sep = File.separator;
    private String rootStorePath = "." + sep + "oss" + sep + "data";
    private String authority = ObjectBean.SYSTEM_OWNED;
    private String logConfigFile = rootStorePath + sep + "conf" + sep + "oss_log4j.properties";
    private ServicePool engine = Engine.getInstance(rootStorePath, authority, logConfigFile);

    /*
     * 
     */
    @Test
    public void accountHolderCharge() throws Exception {
        int monthOffset = 3;
        long charge = engine.getBookingForecastService().getAccountHolderChargeForMonth(monthOffset);
        double value = (TDCalc.getPrecisionValue(charge, 3)/1000);
        System.out.println("Charge month " + monthOffset + " = £" + value);
    }

    /*
     *
     */
    @Test
    public void predictedBilling() throws Exception {
        int accountId = 1;
        int startYwd;
        int endYwd;
        long[] rtnValues;

        for(int week = 0; week <= 20; week++) {
            System.out.print("Week [" + week);
            startYwd = CalendarStatic.getRelativeYW(week);
            endYwd = startYwd + 6;
            rtnValues = engine.getBookingForecastService().getForecastForRange(accountId, startYwd, endYwd);
            System.out.println("] £" + rtnValues[BillingBean.VALUE_ONLY]);
        }
        for(int month = 1; month <= 5; month++) {
            int[] startEndYwd = CalendarStatic.getRelativeMonthStartEnd(month);
            System.out.print("Month [" + month + "] from '" + startEndYwd[0] + " to " + startEndYwd[1]);
            rtnValues = engine.getBookingForecastService().getForecastForMonth(accountId, month);
            System.out.println(" = £" + (TDCalc.getPrecisionValue(rtnValues[BillingBean.VALUE_ONLY], 3)/1000));
        }
    }


}