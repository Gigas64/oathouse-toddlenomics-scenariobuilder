package com.oathouse.ccm.scenarioTests;

// common imports
import com.oathouse.ccm.cma.ServicePool;
import com.oathouse.ccm.cma.VABoolean;
import com.oathouse.ccm.cma.accounts.BillingService;
import com.oathouse.ccm.cma.booking.ChildBookingService;
import com.oathouse.ccm.cos.accounts.TDCalc;
import com.oathouse.ccm.cos.accounts.finance.BillingBean;
import com.oathouse.ccm.cos.bookings.BTFlagIdBits;
import com.oathouse.ccm.cos.bookings.BTIdBits;
import com.oathouse.ccm.cos.bookings.BookingBean;
import com.oathouse.ccm.cos.bookings.BookingState;
import com.oathouse.ccm.cos.profile.ChildBean;
import com.oathouse.ccm.cos.profile.ContactBean;
import com.oathouse.ccm.cos.profile.RelationType;
import com.oathouse.toddlenomics.scenario.builder.Engine;
import com.oathouse.oss.storage.objectstore.ObjectDBMS;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.exceptions.DuplicateIdentifierException;
import com.oathouse.oss.storage.exceptions.IllegalActionException;
import com.oathouse.oss.storage.exceptions.IllegalValueException;
import com.oathouse.oss.storage.exceptions.MaxCountReachedException;
import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.NoSuchKeyException;
import com.oathouse.oss.storage.exceptions.NullObjectException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import static java.util.Arrays.*;
// Test Imports
import mockit.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Darryl Oatridge
 */
public class DataViewer {
    private String authority = "lit00pau";
    private String sep = File.separator;
    private String rootStorePath = Paths.get("D:\\Development\\OathouseProjects\\ToddlenomicApps\\ToddlenomicsWeb\\oss\\data").toString();
    private String logConfigFile = rootStorePath+"\\conf\\oss_log4j.properties";
    private ServicePool engine;

    @Before
    public void setup() {
        engine = Engine.getInstance(rootStorePath, authority, logConfigFile);
    }

    @Test
    public void itemSearch() throws Exception {
        int childId = 61;
        ChildBean child = engine.getChildService().getChildManager().getObject(childId);
        System.out.println(child.toXML(ObjectDataOptionsEnum.COMPACTED, ObjectDataOptionsEnum.TRIMMED));
        ContactBean contact = engine.getChildService().getContactForChild(childId, RelationType.NON_PROFESSIONAL);
        System.out.println(contact.toXML(ObjectDataOptionsEnum.COMPACTED, ObjectDataOptionsEnum.TRIMMED));
    }



}