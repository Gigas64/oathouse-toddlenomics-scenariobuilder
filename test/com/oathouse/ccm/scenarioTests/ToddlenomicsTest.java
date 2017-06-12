/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.ccm.scenarioTests;

// common imports
import com.oathouse.ccm.cma.ServicePool;
import com.oathouse.ccm.cma.VT;
import com.oathouse.ccm.cma.accounts.TransactionService;
import com.oathouse.ccm.cma.config.AgeRoomService;
import com.oathouse.ccm.cma.profile.ChildService;
import com.oathouse.ccm.cos.accounts.TDCalc;
import com.oathouse.ccm.cos.accounts.finance.BillingBean;
import com.oathouse.ccm.cos.accounts.finance.BillingManager;
import com.oathouse.ccm.cos.accounts.invoice.InvoiceBean;
import com.oathouse.ccm.cos.accounts.invoice.InvoiceManager;
import com.oathouse.ccm.cos.accounts.invoice.InvoiceType;
import com.oathouse.ccm.cos.bookings.BTIdBits;
import com.oathouse.ccm.cos.bookings.BookingBean;
import com.oathouse.ccm.cos.bookings.BookingManager;
import com.oathouse.ccm.cos.bookings.BookingRequestBean;
import com.oathouse.ccm.cos.bookings.BookingState;
import com.oathouse.ccm.cos.config.finance.BillingEnum;
import com.oathouse.ccm.cos.profile.AccountBean;
import com.oathouse.ccm.cos.profile.ChildBean;
import com.oathouse.toddlenomics.scenario.builder.Engine;
import com.oathouse.oss.storage.objectstore.ObjectDBMS;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.objectstore.ObjectMapStore;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import static java.util.Arrays.*;
import java.util.concurrent.ConcurrentSkipListSet;
// Test Imports
import mockit.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Darryl Oatridge
 */
public class ToddlenomicsTest {

    private String owner = ObjectBean.SYSTEM_OWNED;
    private String authority = "ToddlenomicsWeb";
//    private String authority = "Toddlenomics(app00bal)";
//    private String authority = "Toddlenomics(lit00pau)";
//    private String rootStorePath = Paths.get("D:/Development/OathouseProjects/ToddlenomicTools/JuglaConverterToVersion2/oss/data").toString();
    private String rootStorePath = Paths.get("D:\\Development\\OathouseProjects\\ToddlenomicApps\\ToddlenomicsWeb\\oss\\data").toString();
    private String logConfigFile = Paths.get(rootStorePath + "/conf/oss_log4j.properties").toString();
    private ServicePool engine;

    @Before
    public void setup() {
        engine = Engine.getInstance(rootStorePath, authority, logConfigFile);
    }

   /*
     *
     */
    @Test
    public void predictedBilling() throws Exception {
        for(int month = -2; month <= 5; month++) {
            int[] startEndYwd = CalendarStatic.getRelativeMonthStartEnd(month);
            System.out.print("Month [" + month + "] from '" + startEndYwd[0] + " to " + startEndYwd[1]);
            long rtnValues = engine.getBookingForecastService().getAccountHolderChargeForMonth(month);
            System.out.println(" = £" + rtnValues);
        }

    }

    /*
     *
     */
    public void tidyUpLittlePeople() throws Exception {
        BillingManager billingManager = engine.getBillingService().getBillingManager();
        BookingManager bookingManager = engine.getChildBookingService().getBookingManager();
        TransactionService transactionService = engine.getTransactionService();

        ObjectMapStore<BillingBean> oldBillingManager = (ObjectMapStore<BillingBean>) new ObjectMapStore<BillingBean>("billingManagerOld").init();

        if(!transactionService.getInvoiceManager().getAllKeys().isEmpty()) {
            throw new Exception("Delete the invoiceManager before you start");
        }

        for(int accountId : billingManager.getAllKeys()) {
            for(BillingBean billing : billingManager.getAllObjects(accountId)) {
                if(billingManager.isIdentifier(accountId, billing.getBillingId())) {
                    billingManager.setObjectInvoiceId(accountId, billing.getBillingId(), -1, owner);
                    billingManager.removeObject(accountId, billing.getBillingId());
                }
                if(billing.hasBillingBits(BillingEnum.GROUP_FIXED_ITEM)) {
                    BillingBean cloneBilling;
                    if(billing.getDescription().equalsIgnoreCase("System Transfer adjustment balance")) {
                        cloneBilling = billingManager.cloneObjectBean(billingManager.regenerateIdentifier(),oldBillingManager.getAllObjects(accountId).get(0));
                    } else {
                        cloneBilling = billingManager.cloneObjectBean(billingManager.regenerateIdentifier(), billing);
                    }
                    billingManager.setObject(accountId, cloneBilling);
                    if(cloneBilling.getDescription().equalsIgnoreCase("System Transfer adjustment balance")) {
                        int invoiceId = transactionService.getInvoiceManager().regenerateIdentifier();
                        int ywd = 2013210;
                        int lastYwd = 2013215;
                        int dueYwd = 2013216;
                        transactionService.getInvoiceManager().setObject(accountId, new InvoiceBean(invoiceId, accountId, ywd, InvoiceType.STANDARD, lastYwd, dueYwd, "", ObjectBean.SYSTEM_OWNED));
                        billingManager.setObjectInvoiceId(accountId, cloneBilling.getBillingId(), invoiceId, owner);
                    }
                }
            }
            transactionService.rebuildCustomerCreditManagerKey(accountId);
        }
        // tidy the booking that are RECONCILED
        for(int ywd : bookingManager.getAllKeys()) {
            for(BookingBean booking : bookingManager.getAllObjects(ywd)) {
                if(booking.isState(BookingState.RECONCILED)) {
                    bookingManager.setObjectState(ywd, booking.getBookingId(), BookingState.AUTHORISED, owner);
                }
            }
        }
    }

}