package pt.unl.fct.civitas.util;

import org.apache.commons.lang3.*;

public class ParishAdapter {

    // special cases
    public static final String PARISH_UFMPA_SHORT = "UFMPA";
    public static final String PARISH_UFMPA_LONG = "Mação, Penhasco e Aboboreira";

    public static String adaptParish(String parish) {
        if(parish.equals(PARISH_UFMPA_LONG))
            return PARISH_UFMPA_SHORT;
        else
            return StringUtils.stripAccents( parish ).toUpperCase();
    }

    public static String deAdaptParish(String parish) {
        if(parish.equals(PARISH_UFMPA_SHORT))
            return PARISH_UFMPA_LONG;
        else
            return StringUtils.capitalize( parish.toLowerCase() );
    }

}
