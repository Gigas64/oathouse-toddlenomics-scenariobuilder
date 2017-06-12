/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.toddlenomics.scenario.sandboxTests;

// common imports
import com.oathouse.oss.storage.objectstore.ObjectDBMS;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import java.io.File;
import java.util.*;
import static java.util.Arrays.*;
// Test Imports
import mockit.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Darryl Oatridge
 */
public class SanboxTest {

    /*
     *
     */
    @Test
    public void testPhoneNumber() throws Exception {
        System.out.println("phone = " + phone("07"));
        System.out.println("Postcode = " + postcode());
        int dob = CalendarStatic.getRelativeYW(-(3 * 52 + (int)(Math.random() * 50)));
        int[] ageYearsMonths = CalendarStatic.getAgeYearsMonths(dob);
        System.out.println(Integer.toString(ageYearsMonths[0]) + "y " + Integer.toString(ageYearsMonths[1]) + "m");
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
        sb.append(Integer.toString((int)(Math.random() * 9) + 1));
        sb.append(RandomStringUtils.randomAlphabetic(2).toUpperCase());
        return sb.toString();
    }


}