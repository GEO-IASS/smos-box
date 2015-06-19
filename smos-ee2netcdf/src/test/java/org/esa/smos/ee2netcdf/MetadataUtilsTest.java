package org.esa.smos.ee2netcdf;

import org.esa.snap.framework.datamodel.MetadataAttribute;
import org.esa.snap.framework.datamodel.MetadataElement;
import org.esa.snap.framework.datamodel.ProductData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MetadataUtilsTest {

    private MetadataElement metadataRoot;
    private List<AttributeEntry> properties;

    @Before
    public void setUp() {
        metadataRoot = new MetadataElement("root");
        properties = new ArrayList<>();
    }

    @Test
    public void testExtractMetadata_noMetadata() {
        MetadataUtils.extractAttributes(metadataRoot, properties, "");

        assertEquals(0, properties.size());
    }

    @Test
    public void testExtractMetadata_simleMetadata() {
        metadataRoot.addAttribute(new MetadataAttribute("attribute_1", ProductData.ASCII.createInstance("hoppla_1"), true));
        metadataRoot.addAttribute(new MetadataAttribute("attribute_2", ProductData.ASCII.createInstance("hoppla_2"), true));

        MetadataUtils.extractAttributes(metadataRoot, properties, "");
        assertEquals(2, properties.size());
        assertEquals("attribute_1", properties.get(0).getName());
        assertEquals("hoppla_1", properties.get(0).getValue());
    }

    @Test
    public void testExtractMetadata_simleMetadata_withPrefix() {
        metadataRoot.addAttribute(new MetadataAttribute("attribute_1", ProductData.ASCII.createInstance("hoppla_1"), true));
        metadataRoot.addAttribute(new MetadataAttribute("attribute_2", ProductData.ASCII.createInstance("hoppla_2"), true));

        MetadataUtils.extractAttributes(metadataRoot, properties, "___");
        assertEquals(2, properties.size());
        assertEquals("___attribute_1", properties.get(0).getName());
        assertEquals("hoppla_1", properties.get(0).getValue());
    }

    @Test
    public void testExtractMetadata_secondLevel() {
        final MetadataElement secondary = new MetadataElement("secondary");
        secondary.addAttribute(new MetadataAttribute("attribute_1", ProductData.ASCII.createInstance("hoppla_1"), true));
        secondary.addAttribute(new MetadataAttribute("attribute_2", ProductData.ASCII.createInstance("hoppla_2"), true));
        metadataRoot.addElement(secondary);

        MetadataUtils.extractAttributes(metadataRoot, properties, "");
        assertEquals(2, properties.size());
        assertEquals("secondary:attribute_2", properties.get(1).getName());
        assertEquals("hoppla_2", properties.get(1).getValue());
    }

    @Test
    public void testExtractMetadata_secondLevel_withPrefix() {
        final MetadataElement secondary = new MetadataElement("secondary");
        secondary.addAttribute(new MetadataAttribute("attribute_1", ProductData.ASCII.createInstance("hoppla_1"), true));
        secondary.addAttribute(new MetadataAttribute("attribute_2", ProductData.ASCII.createInstance("hoppla_2"), true));
        metadataRoot.addElement(secondary);

        MetadataUtils.extractAttributes(metadataRoot, properties, "??");
        assertEquals(2, properties.size());
        assertEquals("??secondary:attribute_2", properties.get(1).getName());
        assertEquals("hoppla_2", properties.get(1).getValue());
    }

    @Test
    public void testExtractMetadata_thirdLevel() {
        final MetadataElement secondary = new MetadataElement("secondary");
        final MetadataElement third = new MetadataElement("third");
        third.addAttribute(new MetadataAttribute("attribute_1", ProductData.ASCII.createInstance("hoppla_1"), true));
        third.addAttribute(new MetadataAttribute("attribute_2", ProductData.ASCII.createInstance("hoppla_2"), true));
        secondary.addElement(third);
        metadataRoot.addElement(secondary);

        MetadataUtils.extractAttributes(metadataRoot, properties, "");
        assertEquals(2, properties.size());
        assertEquals("secondary:third:attribute_1", properties.get(0).getName());
        assertEquals("hoppla_1", properties.get(0).getValue());
    }

    @Test
    public void testExtractMetadata_mixedLevel() {
        final MetadataElement secondary = new MetadataElement("secondary");
        final MetadataElement third = new MetadataElement("third");
        third.addAttribute(new MetadataAttribute("att_3_1", ProductData.ASCII.createInstance("yeah_3"), true));
        third.addAttribute(new MetadataAttribute("att_3_2", ProductData.ASCII.createInstance("yeah_4"), true));
        secondary.addElement(third);
        secondary.addAttribute(new MetadataAttribute("att_2", ProductData.ASCII.createInstance("yeah_5"), true));
        metadataRoot.addElement(secondary);
        metadataRoot.addAttribute(new MetadataAttribute("root_1", ProductData.ASCII.createInstance("yeah_6"), true));
        metadataRoot.addAttribute(new MetadataAttribute("root_2", ProductData.ASCII.createInstance("yeah_7"), true));

        MetadataUtils.extractAttributes(metadataRoot, properties, "");
        assertEquals(5, properties.size());
        assertEquals("root_1", properties.get(0).getName());
        assertEquals("yeah_6", properties.get(0).getValue());
        assertEquals("root_2", properties.get(1).getName());
        assertEquals("yeah_7", properties.get(1).getValue());
        assertEquals("secondary:att_2", properties.get(2).getName());
        assertEquals("yeah_5", properties.get(2).getValue());
        assertEquals("secondary:third:att_3_1", properties.get(3).getName());
        assertEquals("yeah_3", properties.get(3).getValue());
        assertEquals("secondary:third:att_3_2", properties.get(4).getName());
        assertEquals("yeah_4", properties.get(4).getValue());
    }

    @Test
    public void testExtractMetadata_withDuplicateNamedElements() {
        final MetadataElement secondary = new MetadataElement("secondary");
        final MetadataElement third_1 = new MetadataElement("third");
        third_1.addAttribute(new MetadataAttribute("att_3_1", ProductData.ASCII.createInstance("Wilhelm"), true));
        final MetadataElement third_2 = new MetadataElement("third");
        third_2.addAttribute(new MetadataAttribute("att_3_1", ProductData.ASCII.createInstance("Busch"), true));
        secondary.addElement(third_1);
        secondary.addElement(third_2);
        metadataRoot.addElement(secondary);

        MetadataUtils.extractAttributes(metadataRoot, properties, "");
        assertEquals(2, properties.size());
        assertEquals("Wilhelm", properties.get(0).getValue());
        assertEquals("Busch", properties.get(1).getValue());
    }

}
