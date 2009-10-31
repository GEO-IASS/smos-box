package org.esa.beam.dataio.smos;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

public class SmosProductReaderTest {

    @Test
    public void testIs_SMUPD_File() {
        assertFalse(ProductFactory.is_SMUDP_File("Gnagnagna"));
        assertTrue(ProductFactory.is_SMUDP_File("SM_TEST_MIR_SMUDP2_20121117T160642_20121117T170041_303_001_1.DBL"));
    }

    @Test
    public void testIs_OSUDP_File() {
        assertFalse(ProductFactory.is_OSUDP_File("Blablabla"));
        assertTrue(ProductFactory.is_OSUDP_File("SM_TEST_MIR_OSUDP2_20121118T143742_20121118T153047_306_002_1.DBL"));
    }

    @Test
    public void testIs_L2_User_File() {
        assertFalse(ProductFactory.is_L2_User_File("Hibbel-Bibbel.zack"));
        assertTrue(ProductFactory.is_L2_User_File("SM_TEST_MIR_SMUDP2_20121117T160642_20121117T170041_303_001_1.DBL"));
        assertTrue(ProductFactory.is_L2_User_File("SM_TEST_MIR_OSUDP2_20121118T143742_20121118T153047_306_002_1.DBL"));
    }
}
