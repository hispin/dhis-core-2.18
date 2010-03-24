package org.hisp.dhis.mapping.export;

public class SVGDocument
{

    static final String doctype = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\" ["
        + "<!ATTLIST svg   xmlns:attrib CDATA #IMPLIED> <!ATTLIST path attrib:divname CDATA #IMPLIED>]>";

    static final String namespace = "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:attrib=\"http://www.carto.net/attrib/\"  ";
    
    

    private String title;

    private String svg;

    private String legend;

    public SVGDocument()
    {
        

    }
   
    
    public void repairForImage(){
        
        this.title = "<g id=\"title\" style=\"display: block; visibility: visible;\"><text id=\"title\" x=\"30\" y=\"15\" font-size=\"14\" font-weight=\"bold\"><tspan>" + title + "</tspan></text></g>";
        
        this.svg = doctype + this.svg;

        this.svg = this.svg.replaceFirst( "<svg", "<svg " + namespace );
        
        this.svg = this.svg.replaceFirst( "</svg>", this.title + "</svg>" );
    }
    
    public StringBuffer getSVGscript()
    {
        return new StringBuffer( this.svg );
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

    @Override
    public String toString()
    {
        return this.svg;
    }

}
