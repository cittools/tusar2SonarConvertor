<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />

	<!-- Specialize 'coverage' into line coverage and add branch coverage. -->
	<xsl:template match="coverage">
		<coverage>
			<line-coverage>
				<xsl:if test="@toolname">
					<xsl:attribute name="toolname">
					<xsl:value-of select="@toolname" />
				</xsl:attribute>
				</xsl:if>
				<xsl:if test="@version">
					<xsl:attribute name="version">
					<xsl:value-of select="@version" />
				</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="file" />
			</line-coverage>
		</coverage>
	</xsl:template>

	<!-- Copy everything else. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
