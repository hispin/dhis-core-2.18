package org.hisp.dhis.mapping.export;

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.period.Period;

public class SVGDocument
{

    static final String doctype = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\" ["
        + "<!ATTLIST svg   xmlns:attrib CDATA #IMPLIED> <!ATTLIST path attrib:divname CDATA #IMPLIED>]>";

    static final String namespace = "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:attrib=\"http://www.carto.net/attrib/\"  ";

    private String title;

    private String svg;

    private String legend;

    private Period period;

    private Indicator indicator;

    public SVGDocument()
    {

    }

    public StringBuffer getSVGForImage()
    {
        String title_ = "<g id=\"title\" style=\"display: block; visibility: visible;\"><text id=\"title\" x=\"30\" y=\"15\" font-size=\"14\" font-weight=\"bold\"><tspan>"
            + this.title + "</tspan></text></g>";

        String period_ = "<g id=\"period\" style=\"display: block; visibility: visible;\"><text id=\"period\" x=\"30\" y=\"30\" font-size=\"12\"><tspan>"
            + this.period.getName() + "</tspan></text></g>";

        String indicator_ = "<g id=\"indicator\" style=\"display: block; visibility: visible;\"><text id=\"indicator\" x=\"30\" y=\"45\" font-size=\"12\"><tspan>"
            + this.indicator.getName() + "</tspan></text></g>";

        String svg_ = doctype + this.svg;

        svg_ = svg_.replaceFirst( "<svg", "<svg " + namespace );

        svg_ = svg_.replaceFirst( "</svg>", title_ + period_ + indicator_ + "</svg>" );

        return new StringBuffer( svg_ );
    }

    public StringBuffer getSVGForExcel()
    {
        String svg_ = doctype + this.svg;

        svg_ = svg_.replaceFirst( "<svg", "<svg " + namespace );       

        return new StringBuffer( svg_ );
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getSvg()
    {
        return svg;
    }

    public void setSvg( String svg )
    {
        this.svg = svg;

    }

    public String getLegend()
    {
        return legend;
    }

    public void setLegend( String legend )
    {
        this.legend = legend;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public Indicator getIndicator()
    {
        return indicator;
    }

    public void setIndicator( Indicator indicator )
    {
        this.indicator = indicator;
    }

    @Override
    public String toString()
    {
        return this.svg;
    }

}
