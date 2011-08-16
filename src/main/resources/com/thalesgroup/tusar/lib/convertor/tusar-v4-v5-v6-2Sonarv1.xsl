<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:tests="http://www.thalesgroup.com/sonar/tests/v1"
                xmlns:measures="http://www.thalesgroup.com/sonar/measures/v1"
                xmlns:violations="http://www.thalesgroup.com/sonar/violations/v1"
                xmlns:coverage="http://www.thalesgroup.com/sonar/coverage/v1"
                xmlns:sonar="http://www.thalesgroup.com/sonar/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:template match="*">
        <xsl:param name="namespace">sonar</xsl:param>

        <xsl:variable name="eltName" select="local-name()"/>

        <xsl:if test="$eltName='coverage'">
            <xsl:element name="sonar:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">coverage</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$eltName='measures'">
            <xsl:element name="sonar:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">measures</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$eltName='violations'">
            <xsl:element name="sonar:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">violations</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$eltName='tests'">
            <xsl:element name="sonar:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">tests</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$eltName='tusar'">
            <sonar:sonar xmlns:xs="http://www.w3.org/2001/XMLSchema"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xmlns:tests="http://www.thalesgroup.com/sonar/tests/v1"
                         xmlns:measures="http://www.thalesgroup.com/sonar/measures/v1"
                         xmlns:violations="http://www.thalesgroup.com/sonar/violations/v1"
                         xmlns:coverage="http://www.thalesgroup.com/sonar/coverage/v1"
                         xmlns:sonar="http://www.thalesgroup.com/sonar/v1">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()"/>
            </sonar:sonar>
        </xsl:if>

        <xsl:if test="$namespace='coverage'">
            <xsl:element name="coverage:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">coverage</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$namespace='measures'">
            <xsl:element name="measures:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">measures</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$namespace='violations'">
            <xsl:element name="violations:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">violations</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$namespace='tests'">
            <xsl:element name="tests:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">tests</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>

