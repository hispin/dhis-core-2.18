<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:output method="html" doctype-system="about:legacy-compat"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>DHIS Web-API</title>
        <link rel="stylesheet" href="/api/static/css/main.css" />

        <!-- stylesheets, javascript etc -->
      </head>

      <body>
        <xsl:apply-templates />
      </body>

    </html>
  </xsl:template>

</xsl:stylesheet>
