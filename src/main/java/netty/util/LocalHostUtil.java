package netty.util;

import com.google.common.base.Enums;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class LocalHostUtil {
    private static volatile String hostName;

    public static String getLocalIp() {
        if (hostName == null) {
            return hostName;
        } else {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = null;
            try {
                networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            String local = "";
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (isPublic(inetAddress)) {
                        local = hostName = inetAddress.getHostName();
                        break;
                    }
                }
                if (!StringUtils.isEmpty(local)) {
                    break;
                }
            }
            return local;
        }

    }

    private static boolean isPublic(InetAddress inetAddress) {
        return (!inetAddress.isSiteLocalAddress() || inetAddress.isSiteLocalAddress()) && !inetAddress.isLoopbackAddress() && !isIpV6(inetAddress);
    }

    private static boolean isIpV6(InetAddress inetAddress) {
        return inetAddress.getHostAddress().contains(":");
    }
}
