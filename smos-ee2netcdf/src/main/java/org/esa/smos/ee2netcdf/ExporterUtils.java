package org.esa.smos.ee2netcdf;

import org.esa.smos.ee2netcdf.variable.VariableDescriptor;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.core.util.io.WildcardMatcher;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;

public class ExporterUtils {

    static void assertTargetDirectoryExists(File targetDirectory) {
        if (!targetDirectory.isDirectory()) {
            if (!targetDirectory.mkdirs()) {
                throw new OperatorException("Unable to create target directory: " + targetDirectory.getAbsolutePath());
            }
        }
    }

    static TreeSet<File> createInputFileSet(String[] sourceProductPaths) {
        final TreeSet<File> globbedFileSet = new TreeSet<>();
        try {
            for (String path : sourceProductPaths) {
                path = path.trim();
                WildcardMatcher.glob(path, globbedFileSet);
            }
        } catch (IOException e) {
            throw new OperatorException(e.getMessage());
        }

        return ensureNoDuplicateDblFiles(globbedFileSet);
    }

    static TreeSet<File> ensureNoDuplicateDblFiles(TreeSet<File> globbedFileSet) {
        final TreeSet<File> sourceFileSet = new TreeSet<>();
        for (final File inputFile : globbedFileSet) {
            final String extension = FileUtils.getExtension(inputFile);
            if (".DBL".equalsIgnoreCase(extension) || ".HDR".equalsIgnoreCase(extension)) {
                final File exchangedFile = FileUtils.exchangeExtension(inputFile, ".HDR");
                boolean alreadyPresent = false;
                for (final File sourceFile : sourceFileSet) {
                    if (sourceFile.compareTo(exchangedFile) == 0) {
                        alreadyPresent = true;
                        break;
                    }
                }
                if (!alreadyPresent) {
                    sourceFileSet.add(exchangedFile);
                }

            } else {
                sourceFileSet.add(inputFile);
            }
        }

        return sourceFileSet;
    }

    static MetadataElement getSpecificProductHeader(Product product) {
        final MetadataElement metadataRoot = product.getMetadataRoot();
        final MetadataElement variableHeader = metadataRoot.getElement("Variable_Header");
        if (variableHeader == null) {
            return null;
        }

        final MetadataElement specificProductHeader = variableHeader.getElement("Specific_Product_Header");
        if (specificProductHeader == null) {
            return null;
        }
        return specificProductHeader;
    }

    static void correctScaleFactor(Map<String, VariableDescriptor> variableDescriptors, String variableName, double scaleCorrection) {
        final VariableDescriptor variableDescriptor = variableDescriptors.get(variableName);
        if (variableDescriptor != null) {
            final double originalScaleFactor = variableDescriptor.getScaleFactor();
            variableDescriptor.setScaleFactor(originalScaleFactor * scaleCorrection);
        }
    }

    public static String ensureNetCDFName(String variableName) {
        return variableName.replace(".", "_");
    }
}
