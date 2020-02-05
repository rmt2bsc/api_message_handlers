<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
	<xsl:variable name="tableBorder" select="'solid'"/>
	<xsl:variable name="normalTextSize" select="'9pt'"/>
	<xsl:variable name="signatureBorder" select="'solid'"/>
	<xsl:variable name="imagePath" select="'$IMAGES_DIRECTORY$'"/>
	<xsl:variable name="lightGray">#CCCCCC</xsl:variable>

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="main-page" page-height="11in" page-width="8.5in" margin-left="0.25in" margin-right="0.25in" margin-bottom="0.25in" margin-top="0.25in">
					<fo:region-body margin-top="4in" margin-bottom="2in"/>
					<!-- Header -->
					<fo:region-before extent="3.7in"/>
					<!-- Footer -->
					<fo:region-after extent="1.5in"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="main-page">

				<fo:static-content flow-name="xsl-region-before">
					<fo:table width="100%" table-layout="fixed">
						<fo:table-column column-width="50%"/>
						<fo:table-column column-width="50%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left">
										<fo:external-graphic src="url('$IMAGES_DIRECTORY$RMT2_logo.gif')"/>
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

					<fo:table width="40%" table-layout="fixed">
							<fo:table-column column-width="100%"/>
							<fo:table-body>
							       <xsl:apply-templates select="/dataitem/sales_invoice"/>
							</fo:table-body>
					</fo:table>

					<fo:table width="100%" table-layout="fixed">
						<fo:table-column column-width="30%"/>
						<fo:table-column column-width="10%"/>
						<fo:table-column column-width="38%"/>
						<fo:table-column column-width="22%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<fo:table width="100%" table-layout="fixed" border-style="solid" border-width="1pt" border-color="black">
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
												<xsl:apply-templates select="/dataitem/company"/>
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block/>
								</fo:table-cell>
 
								<fo:table-cell>
									<fo:block>
										<fo:table width="100%" table-layout="fixed" border-style="solid" border-width="1pt" border-top-color="black" border-bottom-color="black" border-left-color="black" border-right-color="black">
											<fo:table-column column-width="100%"/>
											<fo:table-body>
												<fo:table-row>
													<fo:table-cell number-columns-spanned="2" border-color="black" border-width=".5pt" background-color="{$lightGray}" border-style="solid">
														<fo:block text-align="left">
														    <fo:inline font-size="12pt" font-weight="bold">
															      <xsl:text>&#xA0;</xsl:text>
															      <xsl:text>TO&#xA0;&#xA0;</xsl:text>
															</fo:inline>
															<fo:inline  font-size="11pt">
																<xsl:text>Account No:&#xA0;</xsl:text>
																<xsl:value-of select="/dataitem/sales_invoice/account_no"/>
															</fo:inline>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<xsl:apply-templates select="/dataitem/VwBusinessAddressView/vw_business_address"/>  
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block/>
								</fo:table-cell>

							</fo:table-row>
							<fo:table-row>
							     <fo:table-cell>
								      <fo:block/>
								 </fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>

				<fo:static-content flow-name="xsl-region-after">
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
								          <fo:block font-size="{$normalTextSize}">
										       <xsl:text>Make all checks payable to&#xA0;</xsl:text>
											   <fo:inline font-weight="bold">
											   		<xsl:value-of select="/dataitem/company/name"/>
											   </fo:inline>
											   <xsl:text>.</xsl:text>
										  </fo:block>
								          <fo:block font-weight="bold" font-size="{$normalTextSize}">
										       <xsl:text>Total due in 30 days.</xsl:text>
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
								          <fo:block text-align="center" font-weight="bold" font-size="13pt">
										       <xsl:text>Thank you for your business!</xsl:text>
										  </fo:block>
								   </fo:table-cell>
							  </fo:table-row>
						  </fo:table-body>
					  </fo:table>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body">
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
											     <xsl:text>ITEM #</xsl:text>
											 </fo:block>
										</fo:table-cell>
								        <fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
										     <fo:block text-align="center" font-weight="bold" font-size="11pt">
											     <xsl:text>DESCRIPTION</xsl:text>
											 </fo:block>
										</fo:table-cell>
								        <fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
										     <fo:block text-align="center" font-weight="bold" font-size="11pt">
											     <xsl:text>QTY</xsl:text>
											 </fo:block>
										</fo:table-cell>
								        <fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
										     <fo:block text-align="right" font-weight="bold" font-size="11pt">
											     <xsl:text>UNIT PRICE</xsl:text>
											 </fo:block>
										</fo:table-cell>
								        <fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
										     <fo:block text-align="right" font-weight="bold" font-size="11pt">
											     <xsl:text>AMOUNT</xsl:text>
											 </fo:block>
										</fo:table-cell>
								  </fo:table-row>
  						    </fo:table-header>
							<fo:table-footer>
							     <fo:table-row>
								    <fo:table-cell number-columns-spanned="5">
									      <fo:block border-color="black" border-width=".5pt" border-style="solid"/>
									</fo:table-cell>
								 </fo:table-row>
							</fo:table-footer>
							<fo:table-body>
								  <xsl:apply-templates select="dataitem/salesorder_items"/>
							</fo:table-body>
					</fo:table>
                     <!-- Print Total line -->
					<xsl:apply-templates select="dataitem/xact"/>

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
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

  <!--  Display account number -->
	<xsl:template match="account_no">
	   <xsl:value-of select="account_no"/>
	</xsl:template>
	
     <!-- Sales Invoice -->
	<xsl:template match="sales_invoice">
	      <fo:table-row>
	            <fo:table-cell>
				      <fo:block text-align="left" font-size="{$normalTextSize}">
					         <fo:inline font-weight="bold">
						     	<xsl:text>Invoice No:&#xA0;</xsl:text>
							 </fo:inline>
							 <xsl:value-of select="invoice_no"/>
					 </fo:block>
				</fo:table-cell>
		  </fo:table-row>	
	      <fo:table-row>
	            <fo:table-cell>
				      <fo:block text-align="left" font-size="{$normalTextSize}">
					  		<fo:inline font-weight="bold">
						    	 <xsl:text>Invoice Date:&#xA0;</xsl:text>
							 </fo:inline>
							 <xsl:value-of select="invoice_date"/>
					 </fo:block>
				</fo:table-cell>
		  </fo:table-row>	
  	      <fo:table-row>
	            <fo:table-cell>
				      <fo:block text-align="left">
						     <xsl:text>&#xA0;</xsl:text>
					 </fo:block>
				</fo:table-cell>
		  </fo:table-row>	
	</xsl:template>

	<!-- Company Template -->
	<xsl:template match="company">
		<fo:table-row>
			<fo:table-cell>
				<fo:block font-size="{$normalTextSize}" font-weight="bold">
				    <xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="name"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
			     <xsl:if test="address1">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address1"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address2">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address2"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address3">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address3"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address4">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address4"/>
						</fo:block>
                 </xsl:if>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block font-size="{$normalTextSize}">
				    <xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="city"/>
					<xsl:text>,&#xA0;</xsl:text>
					<xsl:value-of select="state"/>
					<xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="zip"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<xsl:if test="phone">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Phone&#xA0;</xsl:text>
							<xsl:value-of select="phone"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="fax">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Fax &#xA0;</xsl:text>
							<xsl:value-of select="fax"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="email">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Email&#xA0;</xsl:text>
							<xsl:value-of select="email"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="website">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Website&#xA0;</xsl:text>
							<xsl:value-of select="website"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
	</xsl:template>

	<!--                                       -->
	<!-- Client Template         -->
	<!--                                       -->
	<xsl:template match="vw_business_address">
		<fo:table-row>
			<fo:table-cell>
				<fo:block font-size="{$normalTextSize}" font-weight="bold">
				    <xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="bus_longname"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
			     <xsl:if test="addr1 and string-length(addr1) gt 0">
							<fo:block font-size="{$normalTextSize}">
							    <xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="addr1"/>
							</fo:block>
           </xsl:if>
			     <xsl:if test="addr2 and string-length(addr2) gt 0">
							<fo:block font-size="{$normalTextSize}">
							    <xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="addr2"/>
							</fo:block>
           </xsl:if>
			     <xsl:if test="addr3 and string-length(addr3) gt 0">
							<fo:block font-size="{$normalTextSize}">
							  <xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="addr3"/>
							</fo:block>
           </xsl:if>
			     <xsl:if test="addr4 and string-length(addr4) gt 0">
							<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="addr4"/>
							</fo:block>
           </xsl:if>
           <xsl:if test="string-length(addr1) &lt;= 0 and string-length(addr2) &lt;= 0 and string-length(addr3) &lt;= 0 and string-length(addr4) &lt;= 0">
							<fo:block font-size="{$normalTextSize}">
	               <xsl:text>&#032;</xsl:text>
							</fo:block>
           </xsl:if>

			</fo:table-cell>
		</fo:table-row>
    <xsl:if test="string-length(zip_city) &gt; 0 and string-length(zip_state) &gt; 0 and (string-length(addr_zip) &gt; 0 and addr_zip != 0)">
				<fo:table-row>
				    <fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="zip_city"/>
							<xsl:text>,&#xA0;</xsl:text>
							<xsl:value-of select="zip_state"/>
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="addr_zip"/>
						</fo:block>
			        </fo:table-cell>	
				</fo:table-row>
		</xsl:if>
		<xsl:if test="phone">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Phone&#xA0;</xsl:text>
							<xsl:value-of select="addr_phone_main"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="fax">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Fax &#xA0;</xsl:text>
							<xsl:value-of select="addr_phone_fax"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="email">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Email&#xA0;</xsl:text>
							<xsl:value-of select="email"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="website">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Website&#xA0;</xsl:text>
							<xsl:value-of select="bus_website"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
	</xsl:template>

    <!-- Customer's Address -->
	<xsl:template match="address">
		<fo:table-row>
			<fo:table-cell>
			     <xsl:if test="address1">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address1"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address2">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address2"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address3">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address3"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address4">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address4"/>
						</fo:block>
                 </xsl:if>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
		    <xsl:apply-templates select="zipcode"/>
		</fo:table-row>
		<xsl:if test="phone">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Phone&#xA0;</xsl:text>
							<xsl:value-of select="phone"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="fax">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Fax &#xA0;</xsl:text>
							<xsl:value-of select="fax"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="email">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Email&#xA0;</xsl:text>
							<xsl:value-of select="email"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="website">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Website&#xA0;</xsl:text>
							<xsl:value-of select="website"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>	
	</xsl:template>

    <!-- Sales Order Items -->
	<xsl:template match="salesorder_items">
	      <fo:table-row>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="left" font-size="{$normalTextSize}">
				     <xsl:text>&#xA0;</xsl:text>
				 	   <xsl:value-of select="item_id"/>
				   </fo:block>
			    </fo:table-cell>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="left" font-size="{$normalTextSize}">
				 	 <xsl:text>&#xA0;</xsl:text>
				     <xsl:value-of select="item_name"/>
					 <xsl:if test="item_name_override and string-length(item_name_override) gt 0">
					   <xsl:text>&#xA0;-&#xA0;</xsl:text>
					   <xsl:value-of select="item_name_override"/>
					 </xsl:if>
				   </fo:block>
			    </fo:table-cell>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="center" font-size="{$normalTextSize}">
				     <xsl:value-of select="format-number(order_qty, '#,##0.##')"/>
				   </fo:block>
			    </fo:table-cell>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="right" font-size="{$normalTextSize}">
				     <xsl:value-of select="format-number(retail_price, '#,##0.00')"/>
				   </fo:block>
			    </fo:table-cell>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="right" font-size="{$normalTextSize}">
				     <xsl:value-of select="format-number(invoice_amount, '#,##0.00')"/>
				   </fo:block>
			    </fo:table-cell>
		  </fo:table-row>
	</xsl:template>

     <!-- Sales Order Total Line -->
	<xsl:template match="xact">
	<fo:table width="100%" table-layout="fixed">
	    <fo:table-column column-width="10%"/>
		<fo:table-column column-width="50%"/>
		<fo:table-column column-width="10%"/>
		<fo:table-column column-width="15%"/>
		<fo:table-column column-width="15%"/>
		<fo:table-body>
	      <fo:table-row>
	        <fo:table-cell>
			     <fo:block>
				 	 <xsl:text>&#xA0;</xsl:text>
				 </fo:block>
			</fo:table-cell>
	        <fo:table-cell>
			     <fo:block>
				 	 <xsl:text>&#xA0;</xsl:text>
				 </fo:block>
			</fo:table-cell>
			<fo:table-cell>
			     <fo:block>
				 	 <xsl:text>&#xA0;</xsl:text>
				 </fo:block>
			</fo:table-cell>
	        <fo:table-cell>
			     <fo:block text-align="center" font-size="11pt">
				     <xsl:text>TOTAL</xsl:text>
				 </fo:block>
			</fo:table-cell>
	        <fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
			     <fo:block text-align="right" font-size="{$normalTextSize}" font-weight="bold">
				     <xsl:value-of select="format-number(xact_amount, '$#,##0.00')"/>
				 </fo:block>
			</fo:table-cell>
		  </fo:table-row>
		</fo:table-body>
	</fo:table>
</xsl:template>

<!-- Print Zipcode related data -->
<xsl:template match="zipcode">
	<fo:table-cell>
		<fo:block font-size="{$normalTextSize}">
		    <xsl:text>&#xA0;</xsl:text>
			<xsl:value-of select="city"/>
			<xsl:text>,&#xA0;</xsl:text>
			<xsl:value-of select="state"/>
			<xsl:text>&#xA0;</xsl:text>
			<xsl:value-of select="zip"/>
		</fo:block>
	</fo:table-cell>	
</xsl:template>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="..\..\..\src\xml\SalesInvoiceReport_Example.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="renderx" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no"
		          validator="internal" customvalidator="">
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bSchemaAware" value="true"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="fatal"/>
		</scenario>
	</scenarios>
	<MapperMetaTag>
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no">
			<SourceSchema srcSchemaPath="timesheet.xml" srcSchemaRoot="dataitem" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/"></template>
			<template match="proj_event">
				<block path="fo:table-cell/fo:block/xsl:choose" x="248" y="144"/>
			</template>
			<template match="company"></template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->