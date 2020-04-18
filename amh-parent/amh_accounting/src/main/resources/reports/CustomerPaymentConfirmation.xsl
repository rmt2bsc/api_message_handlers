<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="4.0" omit-xml-declaration="no" indent="yes"/>
	<xsl:variable name="pageTitle" select="//RSPayload/pageTitle"/>
	<xsl:variable name="APP_ROOT" select="//RSPayload/appRoot"/>
	
	<xsl:template match="/">
	<html>
	     <head>
		    <meta http-equiv="Pragma" content="no-cache"/>
		    <meta http-equiv="Expires" content="-1"/>
		    <link rel="STYLESHEET" type="text/css">
		       <xsl:attribute name="href">
				  <xsl:value-of select="$APP_ROOT"/>
				  <xsl:text>/css/RMT2Table.css</xsl:text>
		 		   </xsl:attribute>
		    </link>
			<link rel="STYLESHEET" type="text/css">
			   <xsl:attribute name="href">
				  <xsl:value-of select="$APP_ROOT"/>
				  <xsl:text>/css/RMT2General.css</xsl:text>
			   </xsl:attribute>
			</link>   
		  </head>
		<body>
		    <h3>
				<xsl:text>RMT2 Business Systems Corp.</xsl:text> 
			</h3>
			<table width="85%" border="0">
				<caption align="left">
					<h4>
						<xsl:value-of select="$pageTitle"/>
					</h4>
				</caption>
				<tbody>
					<xsl:apply-templates select="//RSPayload/CustomerExt"/>
					<xsl:apply-templates select="//RSPayload/SalesOrder"/>
					<xsl:apply-templates select="//RSPayload/Xact"/>
				</tbody>
			</table>
			<br/><!-- Display any messgaes -->
			<table>
				<tbody>
					<tr>
						<td>
							<font color="#ff0000">Thank You for your payment! </font>
						</td>
					</tr>
				</tbody>
			</table>
		</body>
	</html>
</xsl:template>

	<xsl:template match="CustomerExt">
	<tr>
		<th class="clsTableListHeader" width="20%" style="text-align: right">Account Number:</th>
		<td width="80%">
			<xsl:value-of select="accountNo"/>
		</td>
	</tr>
	<tr>
		<th class="clsTableListHeader" width="20%" style="text-align: right">Account Name:</th>
		<td width="80%">
			<xsl:value-of select="name"/>
		</td>
	</tr>
</xsl:template>
	
	<xsl:template match="SalesOrder">
	<tr>
		<th class="clsTableListHeader" width="20%" style="text-align: right">Account Balance:</th>
		<td width="80%">
			<xsl:value-of select="format-number(orderTotal, '$#,##0.00')"/>
		</td>
	</tr>
</xsl:template>
	
	<xsl:template match="Xact">
	<tr>
		<th class="clsTableListHeader" width="20%" style="text-align: right">Transaction Id:</th>
		<td width="80%">
			<xsl:value-of select="xactId"/>
		</td>
	</tr>
	<tr>
		<th class="clsTableListHeader" width="20%" style="text-align: right">Transaction Date:</th>
		<td width="80%">
			<xsl:value-of select="xactDate"/>
		</td>
	</tr>
	<tr>
		<th class="clsTableListHeader" width="20%" style="text-align: right">Payment Amount:</th>
		<td width="80%">
			<xsl:value-of select="format-number(xactAmount, '$#,##0.00')"/>
		</td>
	</tr>
	<tr>
		<th class="clsTableListHeader" width="20%" style="text-align: right">Confirmation No.:</th>
		<td width="80%">
			<xsl:value-of select="confirmNo"/>
		</td>
	</tr>
	<tr>
		<th class="clsTableListHeader" vAlign="top" width="20%" style="text-align: right">Reason:</th>
		<td align="left" width="80%">
			<xsl:value-of select="reason"/>
		</td>
	</tr>
</xsl:template>
   
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2006. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="no" name="Test1" userelativepaths="yes" externalpreview="no" url="..\..\..\src\xml\CustomerPaymentEmail.xml" htmlbaseurl="" outputurl="" processortype="xalan" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator=""/><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="..\..\..\src\xml\CustomerPaymentEmail.xml" htmlbaseurl="" outputurl="" processortype="internal" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no" ><SourceSchema srcSchemaPath="..\..\..\src\xml\CustomerPaymentEmail.xml" srcSchemaRoot="RSPayload" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/></MapperInfo><MapperBlockPosition><template match="/"><block path="html/head/link/xsl:attribute/xsl:value&#x2D;of" x="246" y="198"/><block path="html/head/link[1]/xsl:attribute/xsl:value&#x2D;of" x="246" y="117"/><block path="html/body/table/caption/h3/xsl:value&#x2D;of" x="126" y="117"/><block path="html/body/table/tbody/xsl:apply&#x2D;templates" x="286" y="117"/><block path="html/body/table/tbody/xsl:apply&#x2D;templates[1]" x="206" y="117"/><block path="html/body/table/tbody/xsl:apply&#x2D;templates[2]" x="166" y="117"/></template></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
-->