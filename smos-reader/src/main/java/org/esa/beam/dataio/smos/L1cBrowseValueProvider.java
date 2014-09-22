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

import org.esa.beam.dataio.smos.provider.AbstractValueProvider;

import java.awt.geom.Area;
import java.io.IOException;

class L1cBrowseValueProvider extends AbstractValueProvider {

    private final L1cBrowseSmosFile smosFile;
    private final int memberIndex;
    private final int polarisation;

    L1cBrowseValueProvider(L1cBrowseSmosFile smosFile, int memberIndex, int polarization) {
        this.smosFile = smosFile;
        this.memberIndex = memberIndex;
        this.polarisation = polarization;
    }

    @Override
    public final Area getArea() {
        return smosFile.getArea();
    }

    @Override
    public final int getGridPointIndex(int seqnum) {
        return smosFile.getGridPointIndex(seqnum);
    }

    @Override
    public byte getByte(int gridPointIndex) throws IOException {
        return smosFile.getBtDataList(gridPointIndex).getCompound(polarisation).getByte(memberIndex);
    }

    @Override
    public short getShort(int gridPointIndex) throws IOException {
        return smosFile.getBtDataList(gridPointIndex).getCompound(polarisation).getShort(memberIndex);
    }

    @Override
    public int getInt(int gridPointIndex) throws IOException {
        return smosFile.getBtDataList(gridPointIndex).getCompound(polarisation).getInt(memberIndex);
    }

    @Override
    public float getFloat(int gridPointIndex) throws IOException {
        return smosFile.getBtDataList(gridPointIndex).getCompound(polarisation).getFloat(memberIndex);
    }
}
