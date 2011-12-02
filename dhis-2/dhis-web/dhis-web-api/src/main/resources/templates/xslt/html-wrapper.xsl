<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" />

  <xsl:template match="/">
    <html>
      <head>
        <title>DHIS Web-API</title>
        <!-- stylesheets, javascript etc -->
      </head>

      <body>
        <p>Some CSS required!</p>
        <xsl:apply-templates />
      </body>

    </html>
  </xsl:template>

</xsl:stylesheet>
