package org.hisp.dhis.mobile.test;

import org.hisp.dhis.mobile.reporting.connection.Base64;

import jmunit.framework.cldc11.AssertionFailedException;
import jmunit.framework.cldc11.TestCase;

/**
 * A test case to look at the Base64 encoding and verifying it correctly
 * generates characters
 */
public class Base64Test extends TestCase {

	public Base64Test() {
		super(1, "Base64Test");
	}

	public void test(int testNumber) {
		switch (testNumber) {
		case 0:
			testEncode();
			break;
		default:
			break;
		}
	}

	public void testEncode() throws AssertionFailedException {
		System.out.println("encode");
		byte[] bytes_1 = "admin:District@123".getBytes();
		String expResult_1 = "YWRtaW46RGlzdHJpY3RAMTIz";
		String result_1 = Base64.encode(bytes_1, 0, bytes_1.length);
		assertEquals(expResult_1, result_1);
	}
}
