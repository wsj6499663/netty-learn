package netty.util;

import com.google.common.base.Predicates;
import org.springframework.util.StringUtils;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientUtils {
    public static List<InetSocketAddress> parseAndValidateAddresses(String addrs) {
        Predicates.notNull().apply(addrs);
        List<InetSocketAddress> addresses = new ArrayList();
        Arrays.asList(addrs.split(",")).stream().map(adr -> {
            try {
                return new URI(adr);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(ur -> {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ur.getHost(), ur.getPort());
            addresses.add(inetSocketAddress);
        });
        return addresses;
    }
}
