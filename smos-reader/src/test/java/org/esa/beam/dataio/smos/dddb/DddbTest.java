/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.dataio.smos.dddb;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DddbTest {

    private static final String DBL_SM_XXXX_AUX_ECMWF_0200 = "DBL_SM_XXXX_AUX_ECMWF__0200";
    private static final String DBL_SM_XXXX_MIR_SMDAP2_0200 = "DBL_SM_XXXX_MIR_SMDAP2_0200";
    private static final String DBL_SM_XXXX_MIR_SMDAP2_0201 = "DBL_SM_XXXX_MIR_SMDAP2_0201";
    private static final String DBL_SM_XXXX_MIR_SMDAP2_0300 = "DBL_SM_XXXX_MIR_SMDAP2_0300";
    private static final String DBL_SM_XXXX_MIR_OSDAP2_0200 = "DBL_SM_XXXX_MIR_OSDAP2_0200";
    private static final String DBL_SM_XXXX_MIR_OSDAP2_0300 = "DBL_SM_XXXX_MIR_OSDAP2_0300";
    private static final String DBL_SM_XXXX_MIR_OSUDP2_0300 = "DBL_SM_XXXX_MIR_OSUDP2_0300";
    private static final String DBL_SM_XXXX_MIR_SMUDP2_0200 = "DBL_SM_XXXX_MIR_SMUDP2_0200";
    private Dddb dddb;

    @Before
    public void setUp() {
        dddb = Dddb.getInstance();
    }

    @Test
    public void testGetBandDescriptors() {
        final Family<BandDescriptor> descriptors = dddb.getBandDescriptors(DBL_SM_XXXX_AUX_ECMWF_0200);
        assertEquals(57, descriptors.asList().size());

        final BandDescriptor descriptor = descriptors.getMember("RR");
        assertNotNull(descriptor);

        assertEquals("RR", descriptor.getBandName());
        assertEquals("Rain_Rate", descriptor.getMemberName());
        assertTrue(descriptor.hasTypicalMin());
        assertTrue(descriptor.hasTypicalMax());
        assertFalse(descriptor.isCyclic());
        assertTrue(descriptor.hasFillValue());
        assertFalse(descriptor.getValidPixelExpression().isEmpty());
        assertEquals("RR.raw != -99999.0 && RR.raw != -99998.0", descriptor.getValidPixelExpression());
        assertFalse(descriptor.getUnit().isEmpty());
        assertFalse(descriptor.getDescription().isEmpty());
        assertEquals(0.0, descriptor.getTypicalMin(), 0.0);
        assertEquals("m 3h-1", descriptor.getUnit());
    }

    @Test
    public void testGetBandDescriptors_BUFR() throws Exception {
        final Family<BandDescriptor> descriptors = dddb.getBandDescriptors("BUFR");
        assertEquals(27, descriptors.asList().size());
    }

    @Test
    public void testGetFlagDescriptors_BUFR() throws Exception {
        final Family<FlagDescriptor> descriptors = dddb.getFlagDescriptors("BUFR_flags");
        assertEquals(13, descriptors.asList().size());
    }

    @Test
    public void testGetFlagDescriptors() {
        final Family<FlagDescriptor> descriptors = dddb.getFlagDescriptors(DBL_SM_XXXX_AUX_ECMWF_0200 + "_flags1");
        assertEquals(21, descriptors.asList().size());

        FlagDescriptor descriptor;
        descriptor = descriptors.getMember("RR_FLAG");
        assertNotNull(descriptor);

        assertEquals("RR_FLAG", descriptor.getFlagName());
        assertEquals(0x00000080, descriptor.getMask());
        assertNull(descriptor.getColor());
        assertEquals(0.5, descriptor.getTransparency(), 0.0);
        assertFalse(descriptor.getDescription().isEmpty());
    }

    @Test
    public void testGetFlagDescriptorsFromBandDescriptor() {
        final Family<BandDescriptor> bandDescriptor = dddb.getBandDescriptors(DBL_SM_XXXX_AUX_ECMWF_0200);
        final Family<FlagDescriptor> flagDescriptors = bandDescriptor.getMember("F1").getFlagDescriptors();
        assertNotNull(flagDescriptors);

        assertNotNull(dddb.getFlagDescriptors(DBL_SM_XXXX_AUX_ECMWF_0200 + "_flags1"));
        assertSame(flagDescriptors, dddb.getFlagDescriptors(DBL_SM_XXXX_AUX_ECMWF_0200 + "_flags1"));
    }

    @Test
    public void testFindBandDescriptorForMember() {
        final BandDescriptor descriptor = dddb.findBandDescriptorForMember(DBL_SM_XXXX_MIR_OSUDP2_0300, "Sigma_Tb_42.5Y");
        assertNotNull(descriptor);
        assertEquals("Sigma_TBY", descriptor.getBandName());
    }

    @Test
    public void testFindBandDescriptorForMember_unknownFormatName() {
        assertNull(dddb.findBandDescriptorForMember("schnick-schnack-for-mat", "Sigma_Tb_42.5Y"));
    }

    @Test
    public void testFindBandDescriptorForMember_unknownBandName() {
        assertNull(dddb.findBandDescriptorForMember(DBL_SM_XXXX_MIR_OSUDP2_0300, "rubber-band"));
    }

    @Test
    public void testGetSMDAP2_v0200Descriptors() {
        final Family<BandDescriptor> descriptors = dddb.getBandDescriptors(DBL_SM_XXXX_MIR_SMDAP2_0200);
        assertEquals(70, descriptors.asList().size());

        final BandDescriptor x_swath = descriptors.getMember("X_Swath");
        assertNotNull(x_swath);
    }

    @Test
    public void testGetSMDAP2_v0201Descriptors() {
        final Family<BandDescriptor> descriptors = dddb.getBandDescriptors(DBL_SM_XXXX_MIR_SMDAP2_0201);
        assertEquals(70, descriptors.asList().size());

        final BandDescriptor tSurf_init_std = descriptors.getMember("TSurf_Init_Std");
        assertNotNull(tSurf_init_std);
    }

    @Test
    public void testGetSMDAP2_v0300Descriptors() {
        final Family<BandDescriptor> descriptors = dddb.getBandDescriptors(DBL_SM_XXXX_MIR_SMDAP2_0300);
        assertEquals(70, descriptors.asList().size());

        final BandDescriptor hr_in_dqx = descriptors.getMember("HR_IN_DQX");
        assertNotNull(hr_in_dqx);
    }

    @Test
    public void testGetOSDAP2_v0200Descriptors() {
        final Family<BandDescriptor> descriptors = dddb.getBandDescriptors(DBL_SM_XXXX_MIR_OSDAP2_0200);
        assertEquals(133, descriptors.asList().size());

        final BandDescriptor param2_sigma_m3 = descriptors.getMember("Param2_sigma_M3");
        assertNotNull(param2_sigma_m3);

        final BandDescriptor param6_sigma_m2 = descriptors.getMember("Param6_sigma_M2");
        assertNotNull(param6_sigma_m2);
    }

    @Test
    public void testGetOSDAP2_v0300Descriptors() {
        final Family<BandDescriptor> descriptors = dddb.getBandDescriptors(DBL_SM_XXXX_MIR_OSDAP2_0300);
        assertEquals(133, descriptors.asList().size());

        final BandDescriptor out_of_lut_flags_4 = descriptors.getMember("Out_of_LUT_flags_4");
        assertNotNull(out_of_lut_flags_4);

        final BandDescriptor diff_tb_1 = descriptors.getMember("Diff_TB_1");
        assertNotNull(diff_tb_1);
    }

    @Test
    public void testGetSMUPD2_v0200Descriptors() {
        final Family<BandDescriptor> descriptors = dddb.getBandDescriptors(DBL_SM_XXXX_MIR_SMUDP2_0200);
        assertEquals(66, descriptors.asList().size());

        final BandDescriptor n_instrument_error = descriptors.getMember("N_Instrument_Error");
        assertNotNull(n_instrument_error);
    }
}
