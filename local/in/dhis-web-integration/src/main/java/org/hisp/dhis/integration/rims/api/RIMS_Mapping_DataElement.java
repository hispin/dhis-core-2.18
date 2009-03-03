package org.hisp.dhis.integration.rims.api;

public class RIMS_Mapping_DataElement
{

    private String tname;
    private String vaccine_code;
    private String antigen;
    private String dhisExpression;
    private String rimsColumn;
    private String deName;
    private String disease_code;
    private String aefiCode;
    private String ageCode;
    private boolean isTotal;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public RIMS_Mapping_DataElement()
    {

    }
    public RIMS_Mapping_DataElement( String tname, String deName,
        String dhisid, String rimsid, String vaccine_code, String antigen,
        String disease_code, String aefiCode, String ageCode )
    {
        this.tname = tname;
        this.vaccine_code = vaccine_code;
        this.disease_code = disease_code;
        this.antigen = antigen;
        this.dhisExpression = dhisid;
        this.rimsColumn = rimsid;
        this.deName = deName;
        this.aefiCode = aefiCode;
        this.ageCode = ageCode;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getTableName()
    {
        return tname;
    }
    public void setTname( String tname )
    {
        this.tname = tname;
    }
    public String getVaccine_code()
    {
        return vaccine_code;
    }
    public void setVaccine_code( String vaccine_code )
    {
        this.vaccine_code = vaccine_code;
    }
    public String getAntigen()
    {
        return antigen;
    }
    public void setAntigen( String antigen )
    {
        this.antigen = antigen;
    }
    public String getDhisExpression()
    {
        return dhisExpression;
    }
    public void setDhisExpression( String dhisid )
    {
        this.dhisExpression = dhisid;
    }
    public String getRimsColumn()
    {
        return rimsColumn;
    }
    public void setRimsColumn( String rimsid )
    {
        this.rimsColumn = rimsid;
    }
    public String getDeName()
    {
        return deName;
    }
    public void setDeName( String deName )
    {
        this.deName = deName;
    }
    public String getDisease_code()
    {
        return disease_code;
    }
    public void setDisease_code( String disease_code )
    {
        this.disease_code = disease_code;
    }
    public String getAefiCode()
    {
        return aefiCode;
    }
    public void setAefiCode( String aefiCode )
    {
        this.aefiCode = aefiCode;
    }

    public int getDhisDataElementId()
    {
        int lbracket = dhisExpression.indexOf( '[' );
        int period = dhisExpression.indexOf( '.' );

        try
        {
            return Integer.parseInt( dhisExpression.substring( lbracket + 1, period ) );
        }
        catch ( Exception e )
        {
            throw (NumberFormatException) new NumberFormatException(
                "\""+ dhisExpression + "\" is not a valid dataelement expression" )
                .initCause( e );
        }
    }

    public int getDhisOptionCombo()
    {
        int rbracket = dhisExpression.indexOf( ']' );
        int period = dhisExpression.indexOf( '.' );

        try
        {
            return Integer.parseInt( dhisExpression.substring( period + 1, rbracket ) );
        }
        catch ( Exception e )
        {
            throw (NumberFormatException) new NumberFormatException(
                "\"" + dhisExpression + "\" is not a valid dataelement expression" )
                .initCause( e );
        }
    }
    public String getAgeCode()
    {
        return ageCode;
    }
    public boolean isTotal()
    {
        return isTotal;
    }
    public void setTotal( boolean isTotal )
    {
        this.isTotal = isTotal;
    }
}
