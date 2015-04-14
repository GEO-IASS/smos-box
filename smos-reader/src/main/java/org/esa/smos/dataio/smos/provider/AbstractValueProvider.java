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

package org.esa.smos.dataio.smos.provider;

import java.io.IOException;

abstract public class AbstractValueProvider implements ValueProvider {

    @Override
    public byte getValue(int seqnum, byte noDataValue) {
        final int gridPointIndex = getGridPointIndex(seqnum);
        if (gridPointIndex == -1) {
            return noDataValue;
        }
        try {
            return getByte(gridPointIndex);
        } catch (IOException e) {
            return noDataValue;
        }
    }

    @Override
    public short getValue(int seqnum, short noDataValue) {
        final int gridPointIndex = getGridPointIndex(seqnum);
        if (gridPointIndex == -1) {
            return noDataValue;
        }
        try {
            return getShort(gridPointIndex);
        } catch (IOException e) {
            return noDataValue;
        }
    }

    @Override
    public int getValue(int seqnum, int noDataValue) {
        final int gridPointIndex = getGridPointIndex(seqnum);
        if (gridPointIndex == -1) {
            return noDataValue;
        }
        try {
            return getInt(gridPointIndex);
        } catch (IOException e) {
            return noDataValue;
        }
    }

    @Override
    public float getValue(int seqnum, float noDataValue) {
        final int gridPointIndex = getGridPointIndex(seqnum);
        if (gridPointIndex == -1) {
            return noDataValue;
        }
        try {
            return getFloat(gridPointIndex);
        } catch (IOException e) {
            return noDataValue;
        }
    }

    public abstract int getGridPointIndex(int seqnum);

    public abstract byte getByte(int gridPointIndex) throws IOException;

    public abstract short getShort(int gridPointIndex) throws IOException;

    public abstract int getInt(int gridPointIndex) throws IOException;

    public abstract float getFloat(int gridPointIndex) throws IOException;

    public static double angularAverage(double angle1, double angle2) {
        if (inQuadrant1(angle1) && inQuadrant4(angle2)) {
            angle2 = angle2 - 360.0;
        } else if (inQuadrant1(angle2) && inQuadrant4(angle1)) {
            angle1 = angle1 - 360.0;
        }
        return (angle1 + angle2) / 2.0;
    }

    private static boolean inQuadrant1(double angle) {
        return angle >= 0.0 && angle < 90.0;
    }

    private static boolean inQuadrant4(double angle) {
        return angle > 270.0 && angle <= 360.0;
    }
}
