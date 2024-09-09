package org.sci.rhis.fwc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by hajjaz.ibrahim on 9/12/2018.
 */

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({
        NRCTest.class,
        GPTest.class,
        SearchTest.class
})

public class ActivityTestSuite {
}
