<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="4.0" omit-xml-declaration="no" indent="yes"/>
	<xsl:variable name="pageTitle" select="//CustomerPaymentConfirmation/pageTitle"/>
	<xsl:variable name="APP_ROOT" select="//CustomerPaymentConfirmation/appRoot"/>
	
	<xsl:template match="/">
	<html>
	     <head>
		    <meta http-equiv="Pragma" content="no-cache"/>
		    <meta http-equiv="Expires" content="-1"/>
		    <link rel="STYLESHEET" type="text/css">
		       <xsl:attribute name="href">
				  <xsl:value-of select="$APP_ROOT"/>
				  <xsl:text>css/RMT2Table.css</xsl:text>
		 		   </xsl:attribute>
		    </link>
			<link rel="STYLESHEET" type="text/css">
			   <xsl:attribute name="href">
				  <xsl:value-of select="$APP_ROOT"/>
				  <xsl:text>css/RMT2General.css</xsl:text>
			   </xsl:attribute>
			</link>   
		  </head>
		<body>
		    <h3><xsl:text>RMT2 Business Systems Corp.</xsl:text></h3>
			<h4><xsl:value-of select="$pageTitle"/></h4>
			<table width="85%" border="0">
				<tbody>
					<xsl:apply-templates select="//CustomerPaymentConfirmation/CustomerData"/>
					<xsl:apply-templates select="//CustomerPaymentConfirmation/SalesOrderData"/>
					<xsl:apply-templates select="//CustomerPaymentConfirmation/XactData"/>
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

	<xsl:template match="CustomerData">
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
	
	<xsl:template match="SalesOrderData">
	<tr>
		<th class="clsTableListHeader" width="20%" style="text-align: right">Account Balance:</th>
		<td width="80%">
			<xsl:value-of select="format-number(orderTotal, '$#,##0.00')"/>
		</td>
	</tr>
</xsl:template>
	
	<xsl:template match="XactData">
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
	<tr>
		<th class="clsTableListHeader" vAlign="top" width="20%" style="text-align: right">Payment Method:</th>
		<td align="left" width="80%">
			<xsl:value-of select="tenderId"/>
		</td>
	</tr>
</xsl:template>
   
</xsl:stylesheet>