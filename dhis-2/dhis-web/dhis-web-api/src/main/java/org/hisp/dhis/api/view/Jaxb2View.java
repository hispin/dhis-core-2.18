package org.hisp.dhis.api.view;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mortenoh
 */
public class Jaxb2View extends AbstractView {
    public static final String DEFAULT_CONTENT_TYPE = "application/xml";

    public Jaxb2View() {
        setContentType(DEFAULT_CONTENT_TYPE);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(getContentType());

        Object value = filterModel(model);

        if (value instanceof Map) {
            Map map = (Map) value;

            if (map.size() == 1) {
                value = map.values().toArray()[0];
            }
        }

        OutputStream outputStream = response.getOutputStream();
        JAXBContext context = JAXBContext.newInstance(value.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

//        Marshaller.Listener listener = new IdentifiableObjectListener(request);
//        marshaller.setListener(listener);

        /* TODO This will only work on JAXB RI (and crash on others!) please fixme.. */

        /*
        marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
                "\n<?xml-stylesheet type=\"text/xsl\" href=\"dhis-web-api/xslt/chart.xslt\"?>\n");
        */

        marshaller.marshal(value, outputStream);
    }

    protected Object filterModel(Map<String, Object> model) {
        Map<String, Object> result = new HashMap<String, Object>(model.size());

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if (!(entry.getValue() instanceof BindingResult)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

}
