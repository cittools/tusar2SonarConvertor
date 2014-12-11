<!--
Note: this XSL is not used and only keep here for "educational" purpose.
-->
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
	
	<xsl:variable name="tusar-ns" select="'http://www.thalesgroup.com/tusar/v2'" />
	<xsl:variable name="tests-ns" select="'http://www.thalesgroup.com/tusar/tests/v2'" />
	<xsl:variable name="measures-ns" select="'http://www.thalesgroup.com/tusar/measures/v2'" />
	<xsl:variable name="coverage-ns" select="'http://www.thalesgroup.com/tusar/coverage/v2'" />
	<xsl:variable name="violations-ns" select="'http://www.thalesgroup.com/tusar/violations/v2'" />

	<!-- Copy the root and set the children namespace. -->
	<xsl:template match="tusar">
		<xsl:element name="{local-name()}" namespace="{$tusar-ns}">
			<xsl:apply-templates>
				<xsl:with-param name="namespace" select="$tusar-ns" />
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>

	<!-- Copy a section and a new namespace for the children. -->
	<!-- Adapt for measures, coverage and violations. -->
	<xsl:template match="tests">
		<xsl:element name="{local-name()}" namespace="{$tusar-ns}">
			<xsl:apply-templates>
				<xsl:with-param name="namespace" select="$tests-ns" />
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>

	<!-- Copy other elements using a specified namespace. -->
	<xsl:template match="*">
		<xsl:param name="namespace" />
		<xsl:element name="{local-name()}" namespace="{$namespace}">
			<xsl:apply-templates select="@* | node()">
				<xsl:with-param name="namespace" select="$namespace" />
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>

	<!-- Copy attributes and other things. -->
	<xsl:template match="@* | comment() | processing-instruction()">
		<xsl:copy />
	</xsl:template>

</xsl:stylesheet>
