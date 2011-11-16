package org.hisp.dhis.api.listener;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.Marshaller;

public class IdentifiableObjectListener extends Marshaller.Listener {
    private HttpServletRequest request;

/*    public IdentifiableObjectListener(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void beforeMarshal(Object source) {
        if (source instanceof XCharts) {
            XCharts charts = (XCharts) source;

            for (XChart chart : charts.getCharts()) {
                addHref(chart, request);
            }
        } else if (source instanceof XChart) {
            XChart chart = (XChart) source;
            addHref(chart, request);
        }
    }

    public void addHref(XIdentifiableObject identifiableObject, HttpServletRequest request) {
        String path = request.getRequestURL().toString();
        path = StringUtils.stripFilenameExtension(path);

        while (path.lastIndexOf("/") == path.length() - 1) {
            path = path.substring(0, path.length() - 1);
        }

        identifiableObject.setHref(path + "/" + identifiableObject.getId());
    }

    @Override
    public void afterMarshal(Object source) {
    }
*/
}
