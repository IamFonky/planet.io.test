package ch.elmootan.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.TreeMap;

/**
 * Created by David on 14.06.2017.
 */
public class Network {

    /**
     * Get all the network interfaces on the current machine.
     *
     * @return The array containing all the network interfaces.
     */
    static public ArrayList<NetworkInterface> getNetworkInterfaces() {

        ArrayList<NetworkInterface> networkInterfaces = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface networkInterface : Collections.list(interfaces)) {

                // We shouldn't care about loopback addresses
                if (networkInterface.isLoopback())
                    continue;

                // We shouldn't care about disconnected links
                if (!networkInterface.isUp())
                    continue;

                networkInterfaces.add(networkInterface);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return networkInterfaces;
    }


    public static String getIPV4AdressAsString(NetworkInterface networkInterface) {
        String interfaceName = networkInterface.getName();

        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

        ArrayList<InetAddress> inetAddresses = Collections.list(addresses);

        for (InetAddress address : inetAddresses) {

            if (address instanceof Inet4Address) {
                return address.toString().substring(1);
            }
        }
        return "";
    }

    public static InterfaceIP[] getInterfaceIPForComboBox() {
        ArrayList<InterfaceIP> interfaceIPS = new ArrayList<>();

        for (NetworkInterface networkInterface : Network.getNetworkInterfaces()) {
            interfaceIPS.add(new InterfaceIP(networkInterface));
        }

        return interfaceIPS.toArray(new InterfaceIP[interfaceIPS.size()]);
    }

    public static class InterfaceIP {
        private String interfaceName;
        private String ipAddress;

        public InterfaceIP(NetworkInterface networkInterface) {
            this.ipAddress = getIPV4AdressAsString(networkInterface);
            this.interfaceName = networkInterface.getName();
        }

        @Override
        public String toString() {
            return interfaceName + " " + ipAddress;
        }

        public String getIpAddress() {
            return ipAddress;
        }
    }
}





















