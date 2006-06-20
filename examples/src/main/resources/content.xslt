<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:a="http://example.org"
  version="1.0" >
  <xsl:output method="text" />
  <xsl:template match = "/" >This is a test <xsl:value-of select="a:a/a:b/a:c" /></xsl:template>
</xsl:stylesheet>