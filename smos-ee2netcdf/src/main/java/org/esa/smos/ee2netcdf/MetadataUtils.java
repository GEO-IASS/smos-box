package org.esa.smos.ee2netcdf;

import org.esa.snap.framework.datamodel.MetadataAttribute;
import org.esa.snap.framework.datamodel.MetadataElement;
import org.esa.snap.framework.datamodel.ProductData;
import ucar.nc2.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MetadataUtils {

    public static void extractAttributes(MetadataElement root, List<AttributeEntry> properties, String prefix) {
        final MetadataAttribute[] attributes = root.getAttributes();
        for (MetadataAttribute attribute : attributes) {
            addAttributeTo(properties, prefix, attribute);
        }

        final MetadataElement[] elements = root.getElements();
        final HashMap<String, List<MetadataElement>> uniqueNamedElements = getListWithUniqueNamedElements(elements);

        final Set<String> nameSet = uniqueNamedElements.keySet();
        for (final String elementName : nameSet) {
            final List<MetadataElement> elementsWithSameName = uniqueNamedElements.get(elementName);
            if (elementsWithSameName.size() == 1) {
                final MetadataElement metadataElement = elementsWithSameName.get(0);
                final String nextRecursionPrefix = prefix + metadataElement.getName() + ":";
                extractAttributes(metadataElement, properties, nextRecursionPrefix);
            } else {
                int index = 0;
                for (final MetadataElement metadataElement : elementsWithSameName) {
                    final String nextRecursionPrefix = prefix + metadataElement.getName() + "_" + Integer.toString(index) + ":";
                    extractAttributes(metadataElement, properties, nextRecursionPrefix);
                    ++index;
                }
            }
        }
    }

    private static void addAttributeTo(List<AttributeEntry> properties, String prefix, MetadataAttribute attribute) {
        properties.add(new AttributeEntry(prefix + attribute.getName(), attribute.getData().getElemString()));
    }

    private static HashMap<String, List<MetadataElement>> getListWithUniqueNamedElements(MetadataElement[] elements) {
        final HashMap<String, List<MetadataElement>> uniqueNamedElements = new HashMap<>(elements.length);
        for (final MetadataElement element : elements) {
            final String elementName = element.getName();
            final List<MetadataElement> elementList = uniqueNamedElements.get(elementName);
            if (elementList == null) {
                final ArrayList<MetadataElement> uniqueNamedElementsList = new ArrayList<>();
                uniqueNamedElementsList.add(element);
                uniqueNamedElements.put(elementName, uniqueNamedElementsList);
            } else {
                elementList.add(element);
            }
        }
        return uniqueNamedElements;
    }

    static List<AttributeEntry> convertNetcdfAttributes(List<Attribute> ncAttributes) {
        final ArrayList<AttributeEntry> attributeList = new ArrayList<>(ncAttributes.size());

        for (final Attribute attribute : ncAttributes) {
            final String name = attribute.getFullName();
            final String value = attribute.getStringValue();
            final AttributeEntry attributeEntry = new AttributeEntry(name, value);
            attributeList.add(attributeEntry);
        }
        return attributeList;
    }

    public static void parseMetadata(List<AttributeEntry> metaDataElements, MetadataElement metadataRoot) {
        for (final AttributeEntry attribute : metaDataElements) {
            final String name = attribute.getName();
            final String value = attribute.getValue();
            final MetadataAttribute metadataAttribute = new MetadataAttribute(name, ProductData.createInstance(value), false);
            metadataRoot.addAttribute(metadataAttribute);
        }
    }
}
