package com.oathouse.toddlenomics.scenario;

import com.oathouse.toddlenomics.scenario.builder.Scenario;
import com.oathouse.oss.server.*;
import com.oathouse.oss.storage.exceptions.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.Paths;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.reflect.*;
import org.apache.log4j.*;

/**
 *
 * @author Darryl Oatridge
 */
public class ScenarioBuilder {
    // logger that is set up via a properites file found in ossProperties
    private static Logger logger = Logger.getLogger(ScenarioBuilder.class);

    /**
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // create the command line parser
        final CommandLineParser parser = new PosixParser();
        // create the Options
        final Options options = new Options();
        Option rootDirOption = OptionBuilder.withArgName("rootdir").hasArg().withDescription("The oss root directory from where the the data to be converted can be found (Note: the manager data will be looked for in <rootdir>/files/<authority>/<manager>").create("r");
        Option authorityOption = OptionBuilder.withArgName("authority").hasArg().withDescription("The authority").create("a");
        Option scenarioOption = OptionBuilder.withArgName("scenario").hasArg().withDescription("The scenario name (must be in the package com.oathouse.toddlenomics.scenario.builder.suite)").create("s");

        options.addOption("h", "help", false, "Prints this usage message");
        options.addOption(rootDirOption);
        options.addOption(authorityOption);
        options.addOption(scenarioOption);

        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(options, args);
            // parse the command line arguments
        }
        catch (ParseException exp) {
            System.err.println("Unexpected exception when reading command line options: " + exp.getMessage());
            usage(options);
        }
        if (cmdLine.hasOption('h') || cmdLine.hasOption("help")) {
            usage(options);
        }
        // create logger
        final String rootStorePath = Paths.get(cmdLine.getOptionValue("r", "./oss/data")).toString();
        final String authority = cmdLine.getOptionValue("a", "authority");
        final String ScenarioClass = cmdLine.getOptionValue("s", "");
        final String logConfigFile = Paths.get(rootStorePath + "/conf/fix_log4j.properties").toString();
        if (logConfigFile == null || !(new File(logConfigFile).exists())) {
            logger.setLevel(Level.OFF);
        }
        else {
            PropertyConfigurator.configure(logConfigFile);
        }
        logger.debug("Command Line Options:");
        logger.debug("   rootdir   = " + rootStorePath);
        logger.debug("   authority = " + authority);
        logger.debug("   scenario  = " + ScenarioClass);
        logger.debug("   LogLevel = " + logger.getEffectiveLevel().toString());

        if (ScenarioClass == null || ScenarioClass.isEmpty()) {
            System.err.println("STARTUP ERROR: You must have a fixerclass argument as mandatory. Using the -f argument please provide a class that extends Fixer class");
            usage(options);
        }
        String fullClsName = "com.oathouse.toddlenomics.scenario.builder.suite." + ScenarioClass;
        if (!Class.forName(fullClsName).getSuperclass().equals(com.oathouse.toddlenomics.scenario.builder.Scenario.class)
                && !Class.forName(fullClsName).getSuperclass().getSuperclass().equals(com.oathouse.toddlenomics.scenario.builder.Scenario.class)) {
            System.err.println("STARTUP ERROR: The scenario argument must be a concrete implemetation of the Scenario class");
            usage(options);
        }
        // run the fixer
        Object[] params = { rootStorePath, authority, logConfigFile };
        Scenario scenario = getObjectBeanInstance(getClass(fullClsName), params);
        scenario.setServicePool();
        scenario.run();
    }

    private static void usage(Options options) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java ObjectConverter [-h] [-r dir] -s name", options);
        System.exit(0);
    }

    private static Scenario getObjectBeanInstance(Class<?> cls, Object[] params) throws ObjectBeanSopException {
        String simpleClsName = cls.getSimpleName();
        Object obj = null;
        try {
            obj = ConstructorUtils.invokeConstructor(cls, params);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            System.err.println(ex.getMessage() + " " + ex.getClass());
            System.err.println(getStackTrace(ex));
            throw new ObjectBeanSopException("Unable to construct " + simpleClsName);
        }
        if (obj == null) {
            throw new ObjectBeanSopException("Constructing " + simpleClsName + " returned null");
        }
        if (!(obj instanceof Scenario)) {
            throw new ObjectBeanSopException(simpleClsName + " does not extend Scenario");
        }
        return ((Scenario) obj);
    }

    private static Class<?> getClass(String fullClsName) throws ObjectBeanSopException {
        Class<?> cls = null;
        try {
            cls = Class.forName(fullClsName);
        }
        catch (ClassNotFoundException e) {
            throw new ObjectBeanSopException(fullClsName + " does not exist. Class not found!");
        }
        if (cls == null) {
            throw new ObjectBeanSopException(fullClsName + " returned null when creating class");
        }
        return (cls);
    }

    public static String getStackTrace(Throwable e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder s = new StringBuilder();
        for(StackTraceElement ste:stackTrace) {
            s.append(ste.getClassName());
            s.append(".");
            s.append(ste.getMethodName());
            s.append("() [line ");
            s.append(ste.getLineNumber());
            s.append("]\n");
        }
        return s.toString();
    }
}
