package org.hisp.dhis.webapi.controller.model;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Mithilesh Kumar Thakur
 */

@Controller
@RequestMapping(value = ModelTypeAttributeController.RESOURCE_PATH)
public class ModelTypeAttributeController extends AbstractCrudController<ModelTypeAttribute>
{
    public static final String RESOURCE_PATH = "/modelTypeAttributes";
}
