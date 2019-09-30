package com.mycompany.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wangly
 * @version $Revision: 1.0 $, $Date: 2019/8/24 17:08 $
 */
public class InteAddressTest {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        System.out.println(address);
    }
}
