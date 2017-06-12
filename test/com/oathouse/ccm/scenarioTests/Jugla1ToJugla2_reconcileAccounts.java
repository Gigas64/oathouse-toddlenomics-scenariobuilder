package com.oathouse.ccm.scenarioTests;

// common imports
import com.oathouse.ccm.cma.ServicePool;
import com.oathouse.ccm.cma.accounts.TransactionService;
import com.oathouse.ccm.cma.profile.ChildService;
import com.oathouse.ccm.cos.profile.AccountBean;
import com.oathouse.toddlenomics.scenario.builder.Engine;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import org.junit.*;

/**
 *
 * @author Darryl Oatridge
 */
public class Jugla1ToJugla2_reconcileAccounts {
    private String sep = File.separator;
    private String authority = "Toddlenomics(app00bal)";
//    private String authority = "Toddlenomics(lit00pau)";
    private String rootStorePath = Paths.get("D:/Development/OathouseProjects/ToddlenomicTools/JuglaConverterToVersion2/oss/data").toString();
    private String logConfigFile = Paths.get(rootStorePath+"/conf/oss_log4j.properties").toString();
    private ServicePool engine;

    @Before
    public void setup() {
        engine = Engine.getInstance(rootStorePath, authority, logConfigFile);
    }

    @Test
    public void reconcileAllAccounts() throws Exception {
        ChildService childService = engine.getChildService();
        TransactionService transactionService = engine.getTransactionService();

        for(AccountBean account : childService.getAccountManager().getAllObjects()) {
            String preBalance = getStringMoney(transactionService.getCustomerCreditManager().getBalance(account.getAccountId()),  "%,.2f");

            transactionService.rebuildCustomerCreditManagerKey(account.getAccountId());
            String postBalance = getStringMoney(transactionService.getCustomerCreditManager().getBalance(account.getAccountId()),  "%,.2f");
            System.out.println("Account " + account.getAccountRef() + " changed from £" + preBalance + " to £" + postBalance);
        }
    }

    public String getStringMoney(long money, String requiredFormat) {
        double dPounds = round(money) / 1000d;
        Formatter fmt = new Formatter();
        fmt.format(requiredFormat, dPounds);
        String output = fmt.toString();
        if (output.equals("-0")) {
            output = "0";
        }
        return output;
    }

    public long round(long money) {
        boolean neg = false;
        if (money < 0) {
            neg = true;
        }
        money = Math.abs(money);
        int tenths = (int) (money % 10);
        long whole = money / 10;
        if (tenths <= 5) {
            money = whole * 10;
        }
        else {
            money = whole * 10 + 10;
        }
        if (neg) {
            return (money * -1);
        }
        return money;
    }

}