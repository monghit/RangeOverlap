package com.monghit.utils.RangeOverlap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RangeOverlapTest {

	ArrayList<String> ipsRange;

	@BeforeEach
	void init() {
		ipsRange = new ArrayList<String>();
	}

	
	@ParameterizedTest
	@CsvSource(value = {
		    "10.1.2.11, 10.1.2.3/28,true,2",
		    "10.1.2.3/28, 10.1.2.11,true,2",
		    "10.1.2.3/28, 135.1.2.11,false,2",
		    "135.1.2.11,10.1.2.3/28,false,2",  
		}, ignoreLeadingAndTrailingWhitespace = true)
	void test_COLLISION_IP_AND_RANGE(String ip1,String ip2, boolean result,int totalIps) {
		ipsRange.add(ip1);
		ipsRange.add(ip2);
		RangeOverlap ro = new RangeOverlap(ipsRange);
		assertEquals(ro.getRanges().size(), totalIps);
		assertEquals(ro.isOverlap(), result);
		
	}
	
	
	@ParameterizedTest
	@CsvSource(value = {
		    "10.88.135.144/20,10.88.142.16/30,true,2",
		    "10.88.142.16/30, 10.88.135.144/20,true,2",
		    "10.1.2.3/28, 10.88.142.16/30,false,2",
		    "10.88.142.16/30,10.1.2.3/28,false,2",  
		}, ignoreLeadingAndTrailingWhitespace = true)
	void test_COLISSION_RANGES_ONE_INSIDE_OTHER(String ipOrCidr1,String ipOrCidr2, boolean result,int totalIps) {
		ipsRange.add(ipOrCidr1);
		ipsRange.add(ipOrCidr2);
		RangeOverlap ro = new RangeOverlap(ipsRange);
		assertEquals(ro.getRanges().size(), totalIps);
		assertEquals(ro.isOverlap(), result);
		
	}

	
	@ParameterizedTest
	@CsvSource(value = {
		    "10.1.2.3/28-10.1.4.11/20,true,2",
		    "10.1.2.3/28-10.1.4.11/28,false,2",
		    "10.1.2.3/28-10.1.4.11/28-15.1.4.11,false,3",
		    "10.1.2.3/28-10.1.4.11/28-15.1.4.11-10.1.2.11/20,true,4"  
		}, ignoreLeadingAndTrailingWhitespace = true)
	void test_COLISSION_RANGES(String ipOrCidrL, boolean result,int totalIps) {
		List<String> ipOrCidrList = Arrays.asList(ipOrCidrL.split("-"));
		ipOrCidrList.forEach((n)->ipsRange.add(n));
		RangeOverlap ro = new RangeOverlap(ipsRange);
		assertEquals(ro.isOverlap(), result);
		assertEquals(ro.getRanges().size(), totalIps);
	}



	
	@ParameterizedTest
	@CsvSource(value = {
		    "10.1.2.11, 10.1.2.3/28,false,1",
		    "10.1.2.3/28, 10.1.2.11,false,1",
		    "10.1.2.3/28, 135.1.2.11,true,2",
		    "135.1.2.11,10.1.2.3/28,true,2",  
		}, ignoreLeadingAndTrailingWhitespace = true)
	void test_Add_COLLISION_IP_AND_RANGE_P(String ipOrCidr1,String ipOrCidr2, boolean result,int totalIps) {
		ipsRange.add(ipOrCidr1);
		RangeOverlap ro = new RangeOverlap(ipsRange);
		assertEquals(ro.getRanges().size(), 1);

		ArrayList<String> al = new ArrayList<>();
		al.add(ipOrCidr2);
		
		assertEquals(ro.add(al),result);
		assertEquals(ro.getRanges().size(), totalIps);
	}

	
	
	@ParameterizedTest
	@CsvSource(value = {
		    "10.88.135.144/20, 10.88.142.16/30,false,1",
		    "10.88.142.16/30, 10.88.135.144/20,false,1",
		    "11.88.135.144/20, 10.88.142.16/30,true,2",
		    "10.88.142.16/30, 11.88.135.144/20,true,2",
		}, ignoreLeadingAndTrailingWhitespace = true)
	void test_Add_COLISSION_RANGES_ONE_INSIDE_OTHER(String cidr1,String cidr2,boolean result,int totalIps) {

		ipsRange.add(cidr1);
		RangeOverlap ro = new RangeOverlap(ipsRange);
		assertEquals(ro.getRanges().size(), 1);

		ArrayList<String> al = new ArrayList<>();
		al.add(cidr2);

		assertEquals(ro.add(al),result);
		assertEquals(ro.getRanges().size(), totalIps);
	}

}
