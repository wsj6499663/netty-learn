package temp;

import com.google.common.base.CharMatcher;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

public class TempStart {
    public static void main(String[] args) {

        System.out.println(StringUtils.endsWithIgnoreCase("wwww@zatech.com","zatech.com"));
    }
}
