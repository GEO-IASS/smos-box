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

package org.esa.beam.dataio.smos;

import org.esa.beam.dataio.smos.dddb.Dddb;
import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;
import org.esa.beam.util.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

public class SmosProductDirectoryReaderPlugIn implements ProductReaderPlugIn {
    private static final FilenameFilter FILENAME_FILTER = new ExplorerFilenameFilter();

    @Override
    public SmosProductReader createReaderInstance() {
        return new SmosProductReader(this);
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        File file = input instanceof File ? (File) input : new File(input.toString());

        if (file.isDirectory()) {
            final File[] files = file.listFiles(FILENAME_FILTER);
            if (files != null && files.length == 2) {
                file = files[0];
            }
            if (file.getName().endsWith(".HDR") || file.getName().endsWith(".DBL")) {
                final File hdrFile = FileUtils.exchangeExtension(file, ".HDR");
                final File dblFile = FileUtils.exchangeExtension(file, ".DBL");

                if (hdrFile.exists() && dblFile.exists()) {
                    try {
                        if (Dddb.getInstance().getDataFormat(hdrFile) != null) {
                            return DecodeQualification.INTENDED;
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }

        return DecodeQualification.UNABLE;
    }

    @Override
    public Class[] getInputTypes() {
        return new Class[]{File.class, String.class};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{""};
    }

    @Override
    public String getDescription(Locale locale) {
        return "SMOS Data Products";
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{"SMOS"};
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new BeamFileFilter(getFormatNames()[0], getDefaultFileExtensions(), getDescription(null)) {

            @Override
            public FileSelectionMode getFileSelectionMode() {
                return FileSelectionMode.DIRECTORIES_ONLY;
            }

            @Override
            public boolean isCompoundDocument(File dir) {
                return dir != null && dir.isDirectory() && acceptDir(dir);
            }

            private boolean acceptDir(File dir) {
                final File[] files = dir.listFiles(FILENAME_FILTER);
                return files != null && files.length == 2;
            }
        };
    }
}