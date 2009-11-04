/* 
 * Copyright (C) 2002-2008 by Brockmann Consult
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.esa.beam.dataio.smos;

import com.bc.ceres.binio.CompoundType;
import com.bc.ceres.binio.DataFormat;
import com.bc.ceres.binio.SequenceData;
import com.bc.ceres.binio.SequenceType;
import com.bc.ceres.binio.Type;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Abstract representation of a SMOS L1c product file.
 *
 * @author Ralf Quast
 * @version $Revision$ $Date$
 * @since SMOS-Box 1.0
 */
public class L1cSmosFile extends SmosFile {

    private final int btDataListIndex;
    private final CompoundType btDataType;

    protected L1cSmosFile(File hdrFile, File dblFile, DataFormat format) throws IOException {
        super(hdrFile, dblFile, format);

        btDataListIndex = getGridPointType().getMemberIndex(SmosConstants.L1C_BT_DATA_LIST_NAME);
        if (btDataListIndex == -1) {
            throw new IOException("Grid point type does not include BT data list.");
        }

        final Type memberType = getGridPointType().getMemberType(btDataListIndex);
        if (!memberType.isSequenceType()) {
            throw new IOException(MessageFormat.format(
                    "Data type ''{0}'' is not of appropriate type", memberType.getName()));
        }

        final Type elementType = ((SequenceType) memberType).getElementType();
        if (!elementType.isCompoundType()) {
            throw new IOException(MessageFormat.format(
                    "Data type ''{0}'' is not a compound type", elementType.getName()));
        }

        btDataType = (CompoundType) elementType;
    }

    public final CompoundType getBtDataType() {
        return btDataType;
    }

    public final SequenceData getBtDataList(int gridPointIndex) throws IOException {
        return getGridPointData(gridPointIndex).getSequence(btDataListIndex);
    }
}
