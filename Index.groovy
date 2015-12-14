import groovy.json.JsonBuilder;
import org.bonitasoft.console.common.server.page.*;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.session.Session;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;

public class Get implements RestApiController {

    @Override
    RestApiResponse doHandle(HttpServletRequest request, PageResourceProvider pageResourceProvider, PageContext pageContext, RestApiResponseBuilder apiResponseBuilder, RestApiUtil restApiUtil) {

        Session session = pageContext.getApiSession();
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);

        Calendar calendar = Calendar.getInstance();
        long timeMilli = calendar.getTimeInMillis();

        Map<String, String> response = [:];
        response.put("now", timeMilli);
        response.putAll(request.parameterMap);
        apiResponseBuilder.with {
            withResponse new JsonBuilder(response).toPrettyString();
            build();
        }
    }
}
