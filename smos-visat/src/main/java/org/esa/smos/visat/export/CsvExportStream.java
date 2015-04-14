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
package org.esa.smos.visat.export;

import com.bc.ceres.binio.CompoundData;
import com.bc.ceres.binio.CompoundMember;
import com.bc.ceres.binio.CompoundType;
import com.bc.ceres.binio.SequenceData;
import com.bc.ceres.binio.SequenceType;
import com.bc.ceres.binio.SimpleType;
import com.bc.ceres.binio.Type;
import org.esa.smos.dataio.smos.SmosConstants;
import org.esa.smos.dataio.smos.SmosFile;
import org.esa.smos.dataio.smos.dddb.BandDescriptor;
import org.esa.smos.dataio.smos.dddb.Dddb;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Exports grid cells to CSV
 *
 * @author Marco Zuehlke
 * @version $Revision$ $Date$
 * @since SMOS-Box 2.0
 */
class CsvExportStream implements GridPointFilterStream {

    private final PrintWriter printWriter;
    private final String separator;

    public CsvExportStream(PrintWriter printWriter, String separator) {
        this.printWriter = printWriter;
        this.separator = separator;
    }

    @Override
    public void startFile(SmosFile smosFile) {
        printWriter.println("# " + smosFile.getDataFile().getParent());
        printTypeHeader(smosFile.getGridPointType());
        printWriter.println();
    }

    private void printTypeHeader(Type type) {
        if (type.isCompoundType()) {
            final CompoundType compoundType = (CompoundType) type;
            final int memberCount = compoundType.getMemberCount();
            for (int i = 0; i < memberCount; i++) {
                final CompoundMember member = compoundType.getMember(i);
                if (member.getType().isSimpleType()) {
                    printWriter.print(member.getName());
                } else {
                    printTypeHeader(member.getType());
                }
                if (i < memberCount - 1) {
                    printWriter.print(separator);
                }
            }
        } else if (type.isSequenceType()) {
            final SequenceType sequenceType = (SequenceType) type;
            final Type elementType = sequenceType.getElementType();
            printTypeHeader(elementType);
        }
    }

    @Override
    public void stopFile(SmosFile smosFile) {
        printWriter.println();
    }

    @Override
    public void handleGridPoint(int id, CompoundData gridPointData) throws IOException {
        final int btDataIndex = gridPointData.getMemberIndex(SmosConstants.BT_DATA_LIST_NAME);
        if (btDataIndex != -1) {
            final SequenceData sequence = gridPointData.getSequence(btDataIndex);
            for (int i = 0; i < sequence.getElementCount(); i++) {
                writeCompound(gridPointData);
                writeCompound(sequence.getCompound(i));
                printWriter.println();
            }
        } else {
            writeCompound(gridPointData);
            printWriter.println();
        }
    }

    private void writeCompound(CompoundData compoundData) throws IOException {
        int memberCount = compoundData.getMemberCount();
        CompoundType gridPointType = compoundData.getType();
        for (int i = 0; i < memberCount; i++) {
            CompoundMember member = gridPointType.getMember(i);
            if (member.getType().isSimpleType()) {
                String memberName = member.getName();
                if (member.getType() == SimpleType.DOUBLE || member.getType() == SimpleType.FLOAT) {
                    printWriter.print(compoundData.getDouble(i));
                } else {
                    final String formatName = compoundData.getContext().getFormat().getName();
                    final BandDescriptor descriptor =
                            Dddb.getInstance().findBandDescriptorForMember(formatName, memberName);
                    long longValue = compoundData.getLong(i);
                    if (descriptor != null) {
                        double doubleValue = descriptor.getScalingFactor() * longValue + descriptor.getScalingOffset();
                        printWriter.print(doubleValue);
                    } else {
                        printWriter.print(longValue);
                    }
                }
            } else if (member.getType().isCompoundType()) {
                writeCompound(compoundData.getCompound(i));
            }
            if (i < memberCount - 1) {
                printWriter.print(separator);
            }
        }
    }

    @Override
    public void close() throws IOException {
        printWriter.close();
    }
}
