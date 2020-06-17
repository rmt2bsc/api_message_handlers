<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
	<xsl:variable name="tableBorder" select="'solid'"/>
	<xsl:variable name="normalTextSize" select="'normal'"/>
	<xsl:variable name="signatureBorder" select="'solid'"/>
	<xsl:variable name="lightGray">#CCCCCC</xsl:variable>
	<xsl:variable name="white">#FFFFFF</xsl:variable>
	<xsl:variable name="listColHeaderShade">#FACC2E</xsl:variable>
	
	
	<xsl:variable name="dateFormat" select="'[Y0001]-[M01]-[D01]'"/>

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			
			<fo:layout-master-set>
				<fo:simple-page-master master-name="main_page"  page-width="8.5in"
					page-height="11in" margin-left="1in" margin-right="1in"	margin-top="0.5in" margin-bottom="0.5in">
					<!-- Body -->
					<fo:region-body margin-top="3.25in" margin-bottom="1.25in"/>
					<!-- Header -->
					<fo:region-before extent="4in"/>
					<!-- Footer -->
					<fo:region-after extent="1in"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="main_page">
				
				<fo:static-content flow-name="xsl-region-before" initial-page-number="1">
					<fo:table width="100%" table-layout="fixed">
						<fo:table-column column-width="100%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left">
										<fo:external-graphic src="url('$IMAGES_DIRECTORY$')"/>
										<!-- Use for testing outside normal runtime environment 
										    <fo:external-graphic src="url('\source\Api_Message_Handlers\amh-parent\amh_core\src\main\resources\images\RMT2_logo2.jpg')"/> 
										 -->
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<xsl:text>&#xA0;</xsl:text>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-size="16pt" text-align="center" font-weight="bold">
										<xsl:text>Sales Order Invoice</xsl:text>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<xsl:text>&#xA0;</xsl:text>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					 
					 <!-- Identify the billable client and billing company -->
					<fo:table width="100%" table-layout="fixed">
						<fo:table-column column-width="50%"/>
						<fo:table-column column-width="50%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<fo:table width="100%">
											<fo:table-column column-width="100%"/>
											<fo:table-body>
												<fo:table-row>
													<fo:table-cell border-color="black" border-width=".5pt" background-color="{$lightGray}" border-style="solid">
														<fo:block text-align="left" font-size="11pt" font-weight="bold">
															<xsl:text>&#xA0;</xsl:text>
															<xsl:text>FROM</xsl:text>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell background-color="{$white}">
														<fo:block text-align="left" font-size="11pt" font-weight="bold">
															<xsl:apply-templates select="AccountingTransactionResponse/profile/company"/>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:table width="100%" table-layout="fixed">
											<fo:table-column column-width="100%"/>
											<fo:table-body>
												<fo:table-row>
													<fo:table-cell border-color="black" border-width=".5pt" background-color="{$lightGray}" border-style="solid">
														<fo:block text-align="left" font-size="11pt" font-weight="bold">
															<xsl:text>&#xA0;</xsl:text>
															<xsl:text>TO</xsl:text>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell background-color="{$white}">
														<fo:block text-align="left" font-size="11pt" font-weight="bold">
															<xsl:apply-templates select="AccountingTransactionResponse/profile/customers/customer"/>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>
				
				<fo:static-content flow-name="xsl-region-after">
					<fo:table width="100%" table-layout="fixed">
						<fo:table-column column-width="100%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="normal">
											<xsl:text>*** This Sales Order has been invoiced and is final ***</xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>
				
				<fo:flow flow-name="xsl-region-body">
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					<xsl:apply-templates select="AccountingTransactionResponse/profile/sales_orders/sales_order"/>
				</fo:flow>
				
				
				
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<!-- Sales Order Header Template -->
	<xsl:template match="sales_order">
		<fo:table width="100%" table-layout="fixed">
			<fo:table-column column-width="25%"/>
			<fo:table-column column-width="75%"/>
			<fo:table-body>
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
								<xsl:value-of select="../../customers/customer/business_contact_details/long_name"/>
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
								<xsl:text>Satus:&#xA0;</xsl:text>
							</fo:inline>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="{$normalTextSize}">
							<fo:inline>
								<xsl:value-of select="status/description"/>
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
			</fo:table-body>
		</fo:table>
		
		<fo:block>
			<xsl:text>&#xA0;</xsl:text>
		</fo:block>
		
		<fo:table width="100%" table-layout="fixed" border-color="black" border-width=".5pt" border-style="solid">
			<fo:table-column column-width="10%"/>
			<fo:table-column column-width="50%"/>
			<fo:table-column column-width="10%"/>
			<fo:table-column column-width="15%"/>
			<fo:table-column column-width="15%"/>
			<fo:table-header>
				<fo:table-row background-color="{$lightGray}">
					<fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
						<fo:block text-align="center" font-weight="bold" font-size="11pt">
							<xsl:text>Item No.</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
						<fo:block text-align="center" font-weight="bold" font-size="11pt">
							<xsl:text>Item Description</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
						<fo:block text-align="center" font-weight="bold" font-size="11pt">
							<xsl:text>Qty</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
						<fo:block text-align="right" font-weight="bold" font-size="11pt">
							<xsl:text>Unit Price</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
						<fo:block text-align="right" font-weight="bold" font-size="11pt">
							<xsl:text>Amount</xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-header>
			<fo:table-body>
				<xsl:apply-templates select="sales_order_items/sales_order_item"/>
			</fo:table-body>
		</fo:table>
		
		<fo:table width="100%" table-layout="fixed">
			<fo:table-column column-width="10%"/>
			<fo:table-column column-width="50%"/>
			<fo:table-column column-width="10%"/>
			<fo:table-column column-width="15%"/>
			<fo:table-column column-width="15%"/>
			<fo:table-body>
				<xsl:apply-templates select="invoice_details"/>
			</fo:table-body>
		</fo:table>
		
		<fo:block>
			<xsl:text>&#xA0;</xsl:text>
		</fo:block>
		
		<!-- Print Signature line -->
		<fo:table width="100%">
			<fo:table-column column-width="100%"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							<xsl:text>&#xA0;</xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							<xsl:text>&#xA0;</xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							<xsl:text>_____________________________</xsl:text>
						</fo:block>
						<fo:block font-weight="bold">
							<xsl:text>Customer Signature</xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							<xsl:text>&#xA0;</xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!-- Sales Order Items -->
	<xsl:template match="sales_order_item">
		<fo:table-row>
			<fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
				<fo:block text-align="left" font-size="11pt">
					<xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="sales_order_item_id"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
				<fo:block text-align="left" font-size="11pt">
					<xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="item/description"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
				<fo:block text-align="center" font-size="11pt">
					<xsl:value-of select="format-number(order_qty, '#,##0.##')"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
				<fo:block text-align="right" font-size="11pt">
					<xsl:value-of select="format-number(unit_cost, '#,##0.00')"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
				<fo:block text-align="right" font-size="11pt">
					<xsl:value-of select="format-number((unit_cost * order_qty), '#,##0.00')"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	
	<!-- Sales Order Line Item Total -->
	<xsl:template match="invoice_details">
		<fo:table-row>
			<fo:table-cell number-columns-spanned="5">
				<fo:block text-align="left" font-size="11pt">
					<xsl:text>&#xA0;</xsl:text>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell number-columns-spanned="3">
				<fo:block text-align="left" font-size="11pt">
					<xsl:text>&#xA0;</xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-weight="bold" font-size="11pt">
					<xsl:text>Total:</xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-weight="bold" font-size="11pt">
					<xsl:value-of select="format-number(invoice_total, '$#,##0.00')"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	
	<!-- Company Template -->
	<xsl:template match="company">
		<fo:table width="100%" table-layout="fixed">
			<fo:table-column column-width="100%"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block font-size="{$normalTextSize}" font-weight="bold">
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="long_name"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block font-size="{$normalTextSize}" font-weight="normal">
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address/addr1"/>
						</fo:block>
						<xsl:if test="address/addr2">
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="address/addr2"/>
							</fo:block>
						</xsl:if>
						<xsl:if test="address/addr3">
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="address/addr3"/>
							</fo:block>
						</xsl:if>
						<xsl:if test="address/addr4">
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="address/addr4"/>
							</fo:block>
						</xsl:if>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block font-size="{$normalTextSize}" font-weight="normal">
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address/zip/city"/>
							<xsl:text>,&#xA0;</xsl:text>
							<xsl:value-of select="address/zip/state"/>
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address/zip/zipcode"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:if test="contact_phone">
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:text>Phone&#xA0;</xsl:text>
								<xsl:value-of select="contact_phone"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				<xsl:if test="fax">
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:text>Fax &#xA0;</xsl:text>
								<xsl:value-of select="fax"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				<xsl:if test="contact_email">
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:text>Email&#xA0;</xsl:text>
								<xsl:value-of select="contact_email"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				<xsl:if test="website">
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:text>Website&#xA0;</xsl:text>
								<xsl:value-of select="website"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
	<!-- Customer Template -->
	<xsl:template match="customer">
		<fo:table width="100%" table-layout="fixed">
			<fo:table-column column-width="100%"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block font-size="{$normalTextSize}" font-weight="bold">
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="business_contact_details/long_name"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block font-size="{$normalTextSize}" font-weight="normal">
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="business_contact_details/address/addr1"/>
						</fo:block>
						<xsl:if test="business_contact_details/address/addr2">
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="business_contact_details/address/addr2"/>
							</fo:block>
						</xsl:if>
						<xsl:if test="business_contact_details/address/addr3">
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="business_contact_details/address/addr3"/>
							</fo:block>
						</xsl:if>
						<xsl:if test="business_contact_details/address/addr4">
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="business_contact_details/address/addr4"/>
							</fo:block>
						</xsl:if>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block font-size="{$normalTextSize}" font-weight="normal">
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="business_contact_details/address/zip/city"/>
							<xsl:text>,&#xA0;</xsl:text>
							<xsl:value-of select="business_contact_details/address/zip/state"/>
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="business_contact_details/address/zip/zipcode"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:if test="business_contact_details/contact_phone">
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:text>Phone&#xA0;</xsl:text>
								<xsl:value-of select="business_contact_details/contact_phone"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				<xsl:if test="fax">
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:text>Fax &#xA0;</xsl:text>
								<xsl:value-of select="fax"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				<xsl:if test="business_contact_details/contact_email">
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:text>Email&#xA0;</xsl:text>
								<xsl:value-of select="business_contact_details/contact_email"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				<xsl:if test="business_contact_details/website">
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="{$normalTextSize}" font-weight="normal">
								<xsl:text>&#xA0;</xsl:text>
								<xsl:text>Website&#xA0;</xsl:text>
								<xsl:value-of select="business_contact_details/website"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
</xsl:stylesheet>