<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	                          xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
	<xsl:variable name="hrTableBorder" select="'solid'"/>
	<xsl:variable name="signatureBorder" select="'solid'"/>
	<xsl:variable name="imagePath" select="'$IMAGES_DIRECTORY$'"/>
	<xsl:variable name="lightGray">#CCCCCC</xsl:variable>
	<xsl:variable name="dateFormat" select="'[Y0001]-[M01]-[D01]'"/>

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			
			<fo:layout-master-set>
				<fo:simple-page-master master-name="main-page" page-height="11in" page-width="8.5in"
					margin-left="0.25in" margin-right="0.25in" margin-bottom="0.25in"
					margin-top="0.25in">
					<fo:region-body margin-top="3in" margin-bottom="0.5in"/>
					<!-- Header -->
					<fo:region-before extent="4in"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="main-page">

				<fo:static-content flow-name="xsl-region-before">
					<fo:table width="100%" table-layout="fixed">
						<fo:table-column column-width="25%"/>
						<fo:table-column column-width="55%"/>
						<fo:table-column column-width="10%"/>
						<fo:table-column column-width="10%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell number-columns-spanned="4">
									<fo:block text-align="left">
										<fo:external-graphic src="url('\source\Api_Message_Handlers\amh-parent\amh_core\src\main\resources\images\RMT2_logo2.jpg')"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left">
										<xsl:text>Timesheet Id: </xsl:text>
										<xsl:value-of select="ProjectProfileResponse/profile/timesheet/display_value"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="24pt" text-align="center">
										<xsl:text>Timesheet</xsl:text>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right">
										<xsl:text>Page</xsl:text>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center">
										<fo:page-number/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body">
					<fo:table width="100%" table-layout="fixed">
						<fo:table-column column-width="45%"/>
						<fo:table-column column-width="2%"/>
						<fo:table-column column-width="53%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<fo:table width="100%" table-layout="fixed"	border-style="solid" border-width=".5pt" border-top-color="black" border-bottom-color="black" border-left-color="black" border-right-color="black">
											<fo:table-column column-width="100%"/>
												<fo:table-body>
													<fo:table-row>
														<fo:table-cell border-color="black"	border-width=".5pt" background-color="{$lightGray}"	border-style="solid">
															<fo:block text-align="left" font-size="11pt" font-weight="bold">
															    <xsl:text>Service Provider</xsl:text>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:apply-templates select="ProjectProfileResponse/profile/timesheet/service_provider"/>
												</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block/>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block>
										<fo:table width="100%" table-layout="fixed"	border-style="solid" border-width=".5pt" border-top-color="black" border-bottom-color="black" border-left-color="black" border-right-color="black">
											<fo:table-column column-width="35%"/>
											<fo:table-column column-width="65%"/>
											<fo:table-body>
												<fo:table-row>
													<fo:table-cell number-columns-spanned="2" border-color="black" border-width=".5pt" background-color="{$lightGray}"	border-style="solid">
														<fo:block text-align="left" font-size="11pt" font-weight="bold">
														    <xsl:text>Client</xsl:text>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<xsl:apply-templates select="ProjectProfileResponse/profile/timesheet/client"/>
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
								
							</fo:table-row>
						</fo:table-body>
					</fo:table>


					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>

					<!--  Create hours worked summary section -->
					<fo:table width="60%" table-layout="fixed">
						<fo:table-column column-width="20%"/>
						<fo:table-column column-width="80%"/>
						<fo:table-body>
							<xsl:apply-templates select="ProjectProfileResponse/profile/timesheet"/>
						</fo:table-body>
					</fo:table>


					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>

					<!--  Create signature line -->
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					<fo:block>
						<xsl:text>&#xA0;</xsl:text>
					</fo:block>
					<fo:table width="60%" table-layout="fixed">
						<fo:table-column column-width="50%"/>
						<fo:table-column column-width="15%"/>
						<fo:table-column column-width="35%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block border-bottom-style="solid"/>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<xsl:text>&#xA0;</xsl:text>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block border-bottom-style="solid"/>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-weight="bold">
										<xsl:text>Consultant</xsl:text>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<xsl:text>&#xA0;</xsl:text>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-weight="bold">
										<xsl:text>Date</xsl:text>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row>
								<fo:table-cell number-columns-spanned="3">
									<fo:block>
										<xsl:text>&#xA0;</xsl:text>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell number-columns-spanned="3">
									<fo:block>
										<xsl:text>&#xA0;</xsl:text>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell number-columns-spanned="3">
									<fo:block>
										<xsl:text>&#xA0;</xsl:text>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row>
								<fo:table-cell>
									<fo:block border-bottom-style="solid"/>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<xsl:text>&#xA0;</xsl:text>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block border-bottom-style="solid"/>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-weight="bold">
										<xsl:text>Client / Manager</xsl:text>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<xsl:text>&#xA0;</xsl:text>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-weight="bold">
										<xsl:text>Date</xsl:text>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				
				
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>


	<!--                                      -->
	<!-- Company Template -->
	<!--                                      -->
	<xsl:template match="service_provider">
		<fo:table-row>
			<fo:table-cell>
				<fo:block>
					<xsl:value-of select="long_name"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block>
					<xsl:if test="address/addr1">
						<fo:block>
							<xsl:value-of select="address/addr1"/>
						</fo:block>
					</xsl:if>
					<xsl:if test="address/addr2">
						<fo:block>
							<xsl:value-of select="address/addr2"/>
						</fo:block>
					</xsl:if>
					<xsl:if test="address/addr3">
						<fo:block>
							<xsl:value-of select="address/addr3"/>
						</fo:block>
					</xsl:if>
					<xsl:if test="address/addr4">
						<fo:block>
							<xsl:value-of select="address/addr4"/>
						</fo:block>
					</xsl:if>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block>
					<xsl:value-of select="address/zip/city"/>
					<xsl:text>,&#xA0;</xsl:text>
					<xsl:value-of select="address/zip/state"/>
					<xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="address/zip/zipcode"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<xsl:if test="phone">
			<fo:table-row>
				<fo:table-cell>
					<fo:block>
						<xsl:text>Phone&#xA0;</xsl:text>
						<xsl:value-of select="address/phone/phone_main"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="fax">
			<fo:table-row>
				<fo:table-cell>
					<fo:block>
						<xsl:text>Fax &#xA0;</xsl:text>
						<xsl:value-of select="address/phone/phone_fax"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="contact_email">
			<fo:table-row>
				<fo:table-cell>
					<fo:block>
						<xsl:text>Email&#xA0;</xsl:text>
						<xsl:value-of select="contact_email"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="website">
			<fo:table-row>
				<fo:table-cell>
					<fo:block>
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
	<xsl:template match="client">
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="left" font-weight="bold">
					<xsl:text>Name:</xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left">
					<xsl:value-of select="name"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="left" font-weight="bold">
					<xsl:text>Account No.:</xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>
					<xsl:value-of select="customer/account_no"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<xsl:template match="timesheet">
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="left" font-weight="bold">
					<xsl:text>Consultant:</xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left">
					<xsl:value-of select="employee/contact_details/short_name"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="left" font-weight="bold">
					<xsl:text>Period:</xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="left">
					<xsl:value-of select="format-date(period_end, $dateFormat)"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell number-columns-spanned="2">
				<fo:block>
					<xsl:text>&#xA0;</xsl:text>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell number-columns-spanned="2">
				<fo:block text-align="left" font-weight="bold">
					<xsl:text>Summary of hours worked</xsl:text>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>

		<!-- Display hours worked  -->
		<fo:table-row>
			<fo:table-cell number-columns-spanned="2">
				<fo:table width="100%" table-layout="fixed" border-style="solid" border-width="1pt"
					border-top-color="black" border-bottom-color="black" border-left-color="black"
					border-right-color="black">
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="16%"/>

					<fo:table-header>
						<fo:table-row border-bottom-color="black" border-bottom-width="2pt"
							background-color="{$lightGray}">
							<fo:table-cell border-style="solid" border-width="1pt"
								border-color="black">
								<fo:block text-align="center" font-weight="bold">
									<xsl:text>Sun</xsl:text>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="1pt"
								border-color="black">
								<fo:block text-align="center" font-weight="bold">
									<xsl:text>Mon</xsl:text>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="1pt"
								border-color="black">
								<fo:block text-align="center" font-weight="bold">
									<xsl:text>Tue</xsl:text>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center" font-weight="bold">
									<xsl:text>Wed</xsl:text>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center" font-weight="bold">
									<xsl:text>Thur</xsl:text>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center" font-weight="bold">
									<xsl:text>Fri</xsl:text>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center" font-weight="bold">
									<xsl:text>Sat</xsl:text>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center" font-weight="bold">
									<xsl:text>Total</xsl:text>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-header>

					<fo:table-body>
						<fo:table-row border-bottom-color="black" border-bottom-width="2pt">
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center">
									<xsl:choose>
										<xsl:when test="work_log_summary/day1_hrs">
											<xsl:value-of select="work_log_summary/day1_hrs"/>
										</xsl:when>
										<xsl:otherwise> 0 </xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center">
									<xsl:choose>
										<xsl:when test="work_log_summary/day2_hrs">
											<xsl:value-of select="work_log_summary/day2_hrs"/>
										</xsl:when>
										<xsl:otherwise> 0 </xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center">
									<xsl:choose>
										<xsl:when test="work_log_summary/day3_hrs">
											<xsl:value-of select="work_log_summary/day3_hrs"/>
										</xsl:when>
										<xsl:otherwise> 0 </xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center">
									<xsl:choose>
										<xsl:when test="work_log_summary/day4_hrs">
											<xsl:value-of select="work_log_summary/day4_hrs"/>
										</xsl:when>
										<xsl:otherwise> 0 </xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center">
									<xsl:choose>
										<xsl:when test="work_log_summary/day5_hrs">
											<xsl:value-of select="work_log_summary/day5_hrs"/>
										</xsl:when>
										<xsl:otherwise> 0 </xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center">
									<xsl:choose>
										<xsl:when test="work_log_summary/day6_hrs">
											<xsl:value-of select="work_log_summary/day6_hrs"/>
										</xsl:when>
										<xsl:otherwise> 0 </xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center">
									<xsl:choose>
										<xsl:when test="work_log_summary/day7_hrs">
											<xsl:value-of select="work_log_summary/day7_hrs"/>
										</xsl:when>
										<xsl:otherwise> 0 </xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width=".5pt"
								border-color="black">
								<fo:block text-align="center">
									<xsl:choose>
										<xsl:when test="work_log_summary/hours_total">
											<xsl:value-of select="work_log_summary/hours_total"/>
										</xsl:when>
										<xsl:otherwise> 0 </xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:table-cell>
		</fo:table-row>

		<fo:table-row>
			<fo:table-cell number-columns-spanned="2">
				<fo:block text-align="left" font-size="9pt">
					<xsl:text>Note: All hours are vaild only when signed by the Client and Employee</xsl:text>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

</xsl:stylesheet>
