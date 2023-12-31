package com.monghit.utils.RangeOverlap;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.monghit.utils.cidrutils.*;




/**
*
* @author monghit
*/
public class RangeOverlap {

	private ArrayList<String> ipsRanges;

    /**
     * This constructor is used to set an array of ips or CIDRs.
     * @param cidr is arrayList<String> of network CIDR or ips
     * @return String This return the BroadcastAddress
     * @throws IllegalArgumentException("not an valid CIDR format!")
     */
	public RangeOverlap(ArrayList<String> ipsRange) {
		super();
		this.ipsRanges = ipsRange;
	}

    /**
     * This method is used to evaluate if the lists of CIDRs or String are overlapping.
     * @return Boolean is overlapping ips
     */
	public Boolean isOverlap() {
		ArrayList<String> copyIpsRange = new ArrayList<String>(ipsRanges);
		Predicate<String> isOverLappingIps = ip -> ipInRange(ip,deleteIpFromRange(ip,copyIpsRange));
		return this.ipsRanges.stream().anyMatch(isOverLappingIps);
	}

     /**
     * This method is used to add an cidr/ip to the ArrayList of cidrs/Ips 
     * @param String cidr/ip
     * @return Boolean true if the new cidr/ip can add -> there is not overlapping with others cidrs/ips.
     */
	public boolean add(String ip) {
		if(!this.ipsRanges.contains(ip)) {
			this.ipsRanges.add(ip);
			if(isOverlap()) {
				this.ipsRanges.remove(ip);
				return false;
			}
		}
		return true;
	}
	
     /**
     * This method is used to add a List<String> of cidr/ip to the ArrayList of cidrs/Ips 
     * @param List<String> of cidr/ip
     * @return Boolean true if none of the new cidr/ip can add -> there is not overlapping with others cidrs/ips.
     */
	public boolean add(List<String> ipsRange) {
		if(!hasCommonElements(ipsRange)) {
			this.ipsRanges.addAll(ipsRange);
			if(isOverlap()) {
				this.ipsRanges.removeAll(ipsRange);
				return false;
			}
		}
		return true;
	}

	 /**
     * This method is used to return an  ArrayList<String> of all cidr or ips.
     * @return ArrayList<String> -> there is not overlapping with others cidrs/ips.
     */
	public ArrayList<String> getRanges(){
		return this.ipsRanges;
	}
	
	private static <T> Set<T> findCommonElements(List<T> first, List<T> second) {
        Set<T> collection = new HashSet<>(second);
        return first.stream().filter(collection::contains).collect(Collectors.toSet());
    }
	
	private Boolean hasCommonElements(List<String> ipsRange) {
		return !findCommonElements(ipsRanges,ipsRange).isEmpty();
	}
	


	private static ArrayList<String> deleteIpFromRange(String ip,ArrayList<String> ipRanges ) {
		ipRanges.remove(ip);
		return ipRanges;
	}

	private static boolean ipInRange(String ip,ArrayList<String> ipRanges) {
		Predicate<String> ipInRangeCompare = ipRange -> ipInRangeCompare(ip, ipRange);
		
		return ipRanges.stream().anyMatch(ipInRangeCompare);
	}

	private static boolean ipInRangeCompare(String ip, String ipRange)  {
	
		try {
		if (ipWithMask(ipRange)) {
			CIDRUtils 
				cidrIpRange = new CIDRUtils(ipRange);
			
			if(ipWithMask(ip)) {
				CIDRUtils cidrIp = new CIDRUtils(ip);
				return compareRanges(cidrIpRange, cidrIp)  ;
			}
			
			return cidrIpRange.isInRange(ip);

		} else {
			return ipCompareWithNoMaskIpRange(ip,ipRange);
		}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static boolean compareRanges(CIDRUtils cidrIpRange, CIDRUtils cidrIp) throws UnknownHostException {
		return compareRangeA_InsideRangeB(cidrIpRange, cidrIp) || compareRangeA_InsideRangeB(cidrIp,cidrIpRange);
	}

	private static boolean compareRangeA_InsideRangeB(CIDRUtils cidrIpRange, CIDRUtils cidrIp)
			throws UnknownHostException {
		return cidrIpRange.isInRange(cidrIp.getNetworkAddress()) || cidrIpRange.isInRange(cidrIp.getBroadcastAddress());
	}

	private static boolean ipWithMask(String ipRange) {
		return ipRange.contains("/");
	}

	private static boolean ipCompareWithNoMaskIpRange(String ip, String ipRange) throws UnknownHostException {
		if(ipWithMask(ip)) {
			CIDRUtils cidrUtils = new CIDRUtils(ip);
			return cidrUtils.isInRange(ipRange);
		}
		return ip.equals(ipRange);
	}


}
