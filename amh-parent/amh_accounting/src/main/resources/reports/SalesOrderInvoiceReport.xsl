<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
	<xsl:variable name="tableBorder" select="'solid'"/>
	<xsl:variable name="normalTextSize" select="'9pt'"/>
	<xsl:variable name="signatureBorder" select="'solid'"/>
	<xsl:variable name="imagePath" select="'$IMAGES_DIRECTORY$'"/>
	<xsl:variable name="lightGray">#CCCCCC</xsl:variable>
	
	<xsl:variable name="dateFormat" select="'[Y0001]-[M01]-[D01]'"/>

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			
			<fo:layout-master-set>
				<fo:simple-page-master master-name="main_page">
					<!-- Body -->
					<fo:region-body margin-top="4in" margin-bottom="2in"/>
					<!-- Header -->
					<fo:region-before extent="3.7in"/>
					<!-- Footer -->
					<fo:region-after extent="1.5in"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="main_page">
				
				<fo:static-content flow-name="xsl-region-before">
					<fo:table width="100%" table-layout="fixed">
						<fo:table-column column-width="50%"/>
						<fo:table-column column-width="50%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left">
										<xsl:text>Replace this with image...</xsl:text>
										<!--<fo:external-graphic src="url('$IMAGES_DIRECTORY$RMT2_logo.gif')"/>-->
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="24pt" text-align="right" font-weight="bold">
										<xsl:text>INVOICE</xsl:text>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					<fo:block>
						<fo:inline font-weight="bold">
							<xsl:text>*** This Sales Order has been invoiced and is final ***</xsl:text>
						</fo:inline>
					</fo:block>
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					
					<fo:table width="80%" table-layout="fixed">
						<fo:table-column column-width="25%"/>
						<fo:table-column column-width="75%"/>
						<fo:table-body>
							<xsl:apply-templates select="AccountingTransactionResponse/profile/sales_orders/sales_order"/>
						</fo:table-body>
					</fo:table>
				</fo:static-content>
				
				
				<fo:flow flow-name="xsl-region-body">
					<fo:block>Hello W3Schools</fo:block>
				</fo:flow>
				

			</fo:page-sequence>
		</fo:root>
	</xsl:template>

     <!-- Sales Invoice Template -->
	<xsl:template match="sales_order">
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="right" font-size="{$normalTextSize}">
					<fo:inline font-weight="bold">
						<xsl:text>Account No:&#xA0;</xsl:text>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left" font-size="{$normalTextSize}">
					<fo:inline>
						<xsl:value-of select="customer_account_no"/>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="right" font-size="{$normalTextSize}">
					<fo:inline font-weight="bold">
						<xsl:text>Account Name:&#xA0;</xsl:text>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left" font-size="{$normalTextSize}">
					<fo:inline>
						<xsl:value-of select="customer_name"/>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="right" font-size="{$normalTextSize}">
					<fo:inline font-weight="bold">
						<xsl:text>Order Number:&#xA0;</xsl:text>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left" font-size="{$normalTextSize}">
					<fo:inline>
						<xsl:value-of select="sales_order_id"/>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="right" font-size="{$normalTextSize}">
					<fo:inline font-weight="bold">
						<xsl:text>Order Date:&#xA0;</xsl:text>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left" font-size="{$normalTextSize}">
					<fo:inline>
						<xsl:value-of select="format-date(effective_date, $dateFormat)"/>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>	
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="right" font-size="{$normalTextSize}">
					<fo:inline font-weight="bold">
						<xsl:text>Invoice Date:&#xA0;</xsl:text>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left" font-size="{$normalTextSize}">
					<fo:inline>
						<xsl:value-of select="format-date(invoice_details/invoice_date, $dateFormat)"/>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="right" font-size="{$normalTextSize}">
					<fo:inline font-weight="bold">
						<xsl:text>Sales Reason:&#xA0;</xsl:text>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left" font-size="{$normalTextSize}">
					<fo:inline>
						<xsl:value-of select="invoice_details/transaction/xact_reason"/>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="right" font-size="{$normalTextSize}">
					<fo:inline font-weight="bold">
						<xsl:text>Invoice No:&#xA0;</xsl:text>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left" font-size="{$normalTextSize}">
					<fo:inline>
						<xsl:value-of select="invoice_details/invoice_no"/>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>	
	<!--	<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="left">
					<xsl:text>&#xA0;</xsl:text>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>	   -->
	</xsl:template>

</xsl:stylesheet>