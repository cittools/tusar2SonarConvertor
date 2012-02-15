<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="4.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:tests="http://www.thalesgroup.com/sonar/tests/v3"
                xmlns:measures="http://www.thalesgroup.com/sonar/measures/v2"
                xmlns:violations="http://www.thalesgroup.com/sonar/violations/v2"
                xmlns:duplications="http://www.thalesgroup.com/sonar/duplications/v1"
                xmlns:coverage="http://www.thalesgroup.com/sonar/coverage/v3"
                xmlns:line-coverage="http://www.thalesgroup.com/sonar/line-coverage/v1"
                xmlns:branch-coverage="http://www.thalesgroup.com/sonar/branch-coverage/v1"
                xmlns:design="http://www.thalesgroup.com/sonar/design/v1"
                xmlns:documentation="http://www.thalesgroup.com/sonar/documentation/v1"
                xmlns:size="http://www.thalesgroup.com/sonar/size/v1"
                xmlns:sonar="http://www.thalesgroup.com/sonar/v4" xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:template match="*">
        <xsl:param name="namespace">sonar</xsl:param>

        <xsl:variable name="eltName" select="local-name()"/>
        <xsl:if test="$eltName='coverage'">
            <xsl:element name="sonar:{local-name()}">
                <xsl:element name="coverage:line-coverage">
                    <xsl:for-each select="@*">
                        <xsl:attribute name="{name()}">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                    </xsl:for-each>

                    <xsl:apply-templates select="node()">
                        <xsl:with-param name="namespace">coverage</xsl:with-param>
                    </xsl:apply-templates>
                </xsl:element>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$eltName='measures'">
            <xsl:element name="sonar:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()"/>
                
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
        
        <xsl:if test="$eltName='size'">
            <xsl:element name="measures:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">size</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>
        
        <xsl:if test="$eltName='duplications'">
            <xsl:element name="measures:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">duplications</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>
        
        <xsl:if test="$eltName='documentation'">
            <xsl:element name="measures:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">documentation</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>
        
        <xsl:if test="$eltName='design'">
            <xsl:element name="measures:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">design</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>
        
        <xsl:if test="$eltName='sonar'">
            <sonar:sonar xmlns:xs="http://www.w3.org/2001/XMLSchema"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xmlns:tests="http://www.thalesgroup.com/sonar/tests/v3"
                         xmlns:measures="http://www.thalesgroup.com/sonar/measures/v2"
                         xmlns:violations="http://www.thalesgroup.com/sonar/violations/v2"
                         xmlns:coverage="http://www.thalesgroup.com/sonar/coverage/v3"
                         xmlns:line-coverage="http://www.thalesgroup.com/sonar/line-coverage/v1"
                         xmlns:branch-coverage="http://www.thalesgroup.com/sonar/branch-coverage/v1"
                         xmlns:duplications="http://www.thalesgroup.com/sonar/duplications/v1"
                         xmlns:design="http://www.thalesgroup.com/sonar/design/v1"
                         xmlns:documentation="http://www.thalesgroup.com/sonar/documentation/v1"
                         xmlns:size="http://www.thalesgroup.com/sonar/size/v1"
                         xmlns:sonar="http://www.thalesgroup.com/sonar/v4"
                         version="4.0">
                <xsl:apply-templates select="node()"/>
            </sonar:sonar>
        </xsl:if>

        <xsl:if test="$namespace='coverage'">
            <xsl:element name="line-coverage:file">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">line-coverage</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>
        
        <xsl:if test="$namespace='line-coverage'">
            <xsl:element name="line-coverage:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>

                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">line-coverage</xsl:with-param>
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
                
                <xsl:apply-templates select="node()"/>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$namespace='size'">
            <xsl:element name="size:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>
                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">size</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>
        
        <xsl:if test="$namespace='duplications'">
            <xsl:element name="duplications:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>
                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">duplications</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>

		<xsl:if test="$namespace='design'">
            <xsl:element name="design:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>
                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">design</xsl:with-param>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:if>
        
        <xsl:if test="$namespace='documentation'">
            <xsl:element name="documentation:{local-name()}">
                <xsl:for-each select="@*">
                    <xsl:attribute name="{name()}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:for-each>
                <xsl:apply-templates select="node()">
                    <xsl:with-param name="namespace">documentation</xsl:with-param>
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

