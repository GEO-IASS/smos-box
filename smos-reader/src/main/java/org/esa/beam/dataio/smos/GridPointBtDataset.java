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

import com.bc.ceres.binio.CompoundData;
import com.bc.ceres.binio.CompoundType;
import com.bc.ceres.binio.SequenceData;
import com.bc.ceres.binio.util.NumberUtils;
import org.esa.beam.dataio.smos.dddb.BandDescriptor;
import org.esa.beam.dataio.smos.dddb.Dddb;

import java.io.IOException;


public class GridPointBtDataset {

    private final CompoundType btDataType;
    private final Class[] columnClasses;
    private final Number[][] data;

    public static GridPointBtDataset read(L1cSmosFile smosFile, int gridPointIndex) throws IOException {
        SequenceData btDataList = smosFile.getBtDataList(gridPointIndex);

        CompoundType type = (CompoundType) btDataList.getType().getElementType();
        int memberCount = type.getMemberCount();

        int btDataListCount = btDataList.getElementCount();

        final Class[] columnClasses = new Class[memberCount];
        final BandDescriptor[] descriptors = new BandDescriptor[memberCount];

        final Dddb dddb = Dddb.getInstance();
        final String formatName = smosFile.getDataFormat().getName();
        for (int j = 0; j < memberCount; j++) {
            final String memberName = type.getMemberName(j);
            final BandDescriptor descriptor = dddb.findBandDescriptorForMember(formatName, memberName);
            if (descriptor == null || descriptor.getScalingFactor() == 1.0 && descriptor.getScalingOffset() == 0.0) {
                columnClasses[j] = NumberUtils.getNumericMemberType(type, j);
            } else {
                columnClasses[j] = Double.class;
            }
            descriptors[j] = descriptor;
        }

        final Number[][] tableData = new Number[btDataListCount][memberCount];
        for (int i = 0; i < btDataListCount; i++) {
            CompoundData btData = btDataList.getCompound(i);
            for (int j = 0; j < memberCount; j++) {
                final Number member = NumberUtils.getNumericMember(btData, j);
                final BandDescriptor descriptor = descriptors[j];
                if (descriptor == null || descriptor.getScalingFactor() == 1.0 && descriptor.getScalingOffset() == 0.0) {
                    tableData[i][j] = member;
                } else {
                    tableData[i][j] = member.doubleValue() * descriptor.getScalingFactor() + descriptor.getScalingOffset();
                }
            }
        }

        return new GridPointBtDataset(smosFile.getBtDataType(), columnClasses, tableData);
    }

    public GridPointBtDataset(CompoundType btDataType, Class[] columnClasses,
                       Number[][] data) {
        this.btDataType = btDataType;
        this.columnClasses = columnClasses;
        this.data = data;
    }

    public int getColumnIndex(String name) {
        return btDataType.getMemberIndex(name);
    }

    public Number[][] getData() {
        return data;
    }

    public Class[] getColumnClasses() {
        return columnClasses;
    }
}
