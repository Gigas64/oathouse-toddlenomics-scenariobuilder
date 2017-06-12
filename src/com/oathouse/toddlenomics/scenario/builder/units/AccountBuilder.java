/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * @(#)AccountBuilder.java
 *
 * Copyright:	Copyright (c) 2013
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.toddlenomics.scenario.builder.units;

import com.oathouse.ccm.cma.config.AgeRoomService;
import com.oathouse.ccm.cma.profile.ChildService;
import com.oathouse.ccm.cos.bookings.BookingManager;
import com.oathouse.ccm.cos.profile.AccountBean;
import com.oathouse.ccm.cos.profile.ChildBean;
import com.oathouse.ccm.cos.profile.ContactBean;
import com.oathouse.ccm.cos.profile.RelationType;
import com.oathouse.oss.storage.exceptions.IllegalActionException;
import com.oathouse.oss.storage.exceptions.MaxCountReachedException;
import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.NullObjectException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import com.oathouse.toddlenomics.scenario.builder.Scenario;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * The {@code AccountBuilder} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 09-Apr-2013
 */
public class AccountBuilder extends Scenario {

    private int[][] years;
    private int accountSize;

    public AccountBuilder(RoomType roomType, String rootStorePath, String authority, String logConfigFile) {
        super(rootStorePath, authority, logConfigFile);
        years = roomType == RoomType.ONE_ROOM ? oneRoom : twoRoom;
        accountSize = roomType == RoomType.ONE_ROOM ? oneRoom.length : twoRoom.length;
    }

    @Override
    public void run() throws Exception {
        logger.info("Deleting Old Account Managers");
        FileUtils.deleteDirectory(new File(authorityPath, "accountManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "childManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "childRoomStartManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "contactManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "custodialRelationship"));
        FileUtils.deleteDirectory(new File(authorityPath, "legalAccountRelationship"));
        // finance
        FileUtils.deleteDirectory(new File(authorityPath, "billingManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "invoiceManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "customerCreditManager"));
        FileUtils.deleteDirectory(new File(authorityPath, "customerReceiptManager"));


        logger.info("Processing New Accounts");
        int accountId;
        int index_child = 0;

        for(int index = 0; index < accountSize; index++) {
            accountId = setAccount(mothers[index], fathers[index], surname[index], line1[index], city[(int) (Math.random() * city.length)]);
            setChild(accountId, children[index_child][0], surname[index], children[index_child++][1], years[index][0]);
            if(years[index][1] > 2) {
                setChild(accountId, children[index_child][0], surname[index], children[index_child++][1], years[index][1]);
            }
        }
        setCustodian();
    }

    private void setCustodian() throws PersistenceException, NoSuchIdentifierException, NullObjectException {
        ChildService childService = engine().getChildService();
        for(AccountBean account : childService.getAccountManager().getAllObjects()) {
            int accountId = account.getAccountId();
            List<ChildBean> childList = childService.getChildrenForAccount(accountId);
            for(ContactBean liable : childService.getLiableContactsForAccount(accountId)) {
                for(ChildBean child : childList) {
                    childService.setCustodialRelationship(child.getChildId(), liable.getContactId(), RelationType.LEGAL_PARENT_GUARDIAN, owner);
                }
            }
        }
    }

    private int setAccount(String mother, String father, String surname, String line1, String city) throws PersistenceException, IllegalActionException, MaxCountReachedException, NullObjectException, NoSuchIdentifierException {
        ChildService childService = engine().getChildService();
        //parent
        String title = father.isEmpty() ? "Ms" : "Mrs";
        String homePhone = phone("01");
        ContactBean parent = childService.createContact(title, Arrays.asList(mother), surname, mother, line1, "", "", city, "Moray", postcode(), "UK", phone("01"), homePhone, "", phone("09"), "demo@oathouse.com", -1, 0, BookingManager.MAX_PERIOD_LENGTH, RelationType.NON_PROFESSIONAL, "", owner);
        // account
        AccountBean account = childService.createAccount("", parent.getContactId(), "", owner);
        String accountRef;
        int count = 1;
        do {
            accountRef = String.format("%-6s%02d", surname.length() > 5 ? surname.substring(0, 6) : surname, count++).replace(' ', '0').toUpperCase();
        } while(childService.getAccountManager().isAccountRef(accountRef));
        childService.setAccountRef(account.getAccountId(), accountRef, owner);

        if(!father.isEmpty()) {
            int contactId = childService.createContact("Mr", Arrays.asList(father), surname, father, line1, "", "", city, "Moray", postcode(), "UK", phone("01"), homePhone, "", phone("09"), "demo@oathouse.com", -1, 0, BookingManager.MAX_PERIOD_LENGTH, RelationType.NON_PROFESSIONAL, "", owner).getContactId();
            childService.setLegalAccountRelationship(account.getAccountId(), contactId, RelationType.LIABLE, owner);
        }
        // set all the extra bits
        childService.setAccountContract(account.getAccountId(), true, owner);
        return account.getAccountId();
    }

    private void setChild(int accountId, String forename, String surname, String gender, int years) throws PersistenceException, IllegalActionException, MaxCountReachedException, NullObjectException, NoSuchIdentifierException {
        ChildService childService = engine().getChildService();
        AgeRoomService ageRoomService = AgeRoomService.getInstance();
        int dob = CalendarStatic.getRelativeYW(-(years * 52 + (int) (Math.random() * 50)));
        int startYwd = CalendarStatic.getRelativeYW(dob, 2 * 52);
        int departYwd = CalendarStatic.getRelativeYW(dob, 12 * 52);
        ChildBean child = childService.createChild(accountId, "", Arrays.asList(forename), surname, forename, "", "", "", "", "", "", "", "", gender, dob, departYwd, -1, "", owner);
        childService.setChildMedFormReceived(child.getChildId(), true, owner);
        int roomId = ageRoomService.getRoomForChild(child.getChildId(), startYwd).getRoomId();
        ageRoomService.setChildRoomStartYwd(child.getChildId(), roomId, startYwd, owner);
    }

    private String phone(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        // phone numbers are 11 long
        for(int count = prefix.length(); count < 11; count++) {
            if(count == 5) {
                sb.append(" ");
            }
            int number = (int) (Math.random() * 10);
            if(count < 6 && number == 0) {
                number++;
            }
            sb.append(Integer.toString(number));
        }
        return sb.toString();
    }

    private String postcode() {
        StringBuilder sb = new StringBuilder("IV30 ");
        sb.append(Integer.toString((int) (Math.random() * 9) + 1));
        sb.append(RandomStringUtils.randomAlphabetic(2).toUpperCase());
        return sb.toString();
    }


    private final int[][] oneRoom = {
        {2, 4},
        {2, 4},
        {3, -1},
        {3, -1},
        {4, -1},
        {2, -1},
        {4, -1},
        {2, -1},
        {3, -1},
        {3, -1},
        {0, 2}
};

    private final int[][] twoRoom = {
        {3, -1},
        {3, 5},
        {8, -1},
        {3, -1},
        {7, -1},
        {3, 6},
        {10, -1},
        {3, -1},
        {3, 6},
        {3, -1},
        {7, 10},
        {10, -1},
        {3, -1},
        {6, 8},
        {8, -1},
        {6, -1},
        {3, 5},
        {3, -1},
        {3, 9},
        {3, -1},};
    private final String[] mothers = {
        "Jane",
        "Susan",
        "Karen",
        "Gillian",
        "Melissa",
        "Kimberly",
        "Angela",
        "Heather",
        "Stephanie",
        "Nicole",
        "Jessica",
        "Elizabeth",
        "Rebecca",
        "Julie",
        "Sarah",
        "Kathy",
        "Jeanette",
        "Ruth",
        "Angie",
        "Mindy"
    };
    private final String[] fathers = {
        "Michael",
        "Chris",
        "",
        "Jason",
        "",
        "",
        "David",
        "James",
        "John",
        "",
        "Robert",
        "Brian",
        "William",
        "",
        "Matthew",
        "",
        "Harry",
        "Clifford",
        "Terry",
        ""
    };
    private final String[] surname = {
        "Jeffs",
        "Thompson",
        "Adams",
        "Harrison",
        "McAdams",
        "Stephens",
        "Wheatherton",
        "Denby",
        "Smith",
        "Tandy",
        "Robson",
        "Smith",
        "Jones",
        "McPherson",
        "Young",
        "Hudson",
        "Barnes",
        "Mills",
        "Brown",
        "Evans",};
    private final String[][] children = {
        {"Emma", "F"},
        {"Adam", "M"},
        {"Roland", "M"},
        {"Sarah", "F"},
        {"Sasha", "F"},
        {"Alex", "M"},
        {"Karen", "F"},
        {"Lukas", "M"},
        {"Jenny", "F"},
        {"Petra", "F"},
        {"Matthew", "M"},
        {"Sophie", "F"},
        {"Herbert", "M"},
        {"Cyril", "M"},
        {"Ava", "F"},
        {"Abigail", "F"},
        {"Victoria", "F"},
        {"Alexis", "F"},
        {"Darren", "M"},
        {"Savanah", "F"},
        {"Stanley", "M"},
        {"Hayden", "M"},
        {"Phoebe", "F"},
        {"Georgia", "F"},
        {"Lexi", "F"},
        {"Zoe", "F"},
        {"Freddie", "M"},};
    private final String[] line1 = {
        "15 Young Street",
        "12 Peterson Ave",
        "19 Golf View",
        "2 Springside Road",
        "56 Academy Street",
        "39 Moss St",
        "4 Madison Road",
        "21 Muirfield Road",
        "5 NorthSt",
        "71 South Stree",
        "27 Market Drive",
        "15 Burn Place",
        "6 Meadow Crescent",
        "39 Redhaven Road",
        "3 Gordon Street",
        "41 Hay Street",
        "24 Brook Lane",
        "1 The Hights",
        "3 Paddock Side",
        "94 Bruce Close",};
    private final String[] city = {
        "Elgin",
        "Foress",
        "Fochabers",
        "Kinloss",
        "Lossiemouth"
    };
}
