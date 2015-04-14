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
package org.esa.smos.dataio.smos;

import com.bc.ceres.binio.DataContext;
import org.esa.smos.EEFilePair;
import org.esa.smos.dataio.smos.dddb.BandDescriptor;
import org.esa.smos.dataio.smos.provider.AbstractValueProvider;
import org.esa.snap.framework.datamodel.Product;

import java.io.IOException;

/**
 * Represents a SMOS L1c Browse product file.
 *
 * @author Ralf Quast
 * @version $Revision$ $Date$
 * @since SMOS-Box 1.0
 */
public class L1cBrowseSmosFile extends L1cSmosFile {

    L1cBrowseSmosFile(EEFilePair eeFilePair, DataContext dataContext) throws IOException {
        super(eeFilePair, dataContext);
    }

    @Override
    protected final void addBand(Product product, BandDescriptor descriptor) {
        if (descriptor.getPolarization() < 0) {
            super.addBand(product, descriptor);
        } else {
            addBand(product, descriptor, getBtDataType());
        }
    }

    @Override
    protected final AbstractValueProvider createValueProvider(BandDescriptor descriptor) {
        final int polarization = descriptor.getPolarization();
        if (polarization < 0) {
            return super.createValueProvider(descriptor);
        }
        return new L1cBrowseValueProvider(this, getBtDataType().getMemberIndex(descriptor.getMemberName()), polarization);
    }
}
