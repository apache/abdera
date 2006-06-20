<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:a="http://www.w3.org/2005/Atom"
  version="1.0" >
  <xsl:output method="text" />
  <xsl:template match = "/" >This is a test <xsl:value-of select="a:feed/a:id" /></xsl:template>
</xsl:stylesheet>