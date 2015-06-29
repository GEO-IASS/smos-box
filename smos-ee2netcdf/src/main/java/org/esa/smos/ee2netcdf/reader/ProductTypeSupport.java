package org.esa.smos.ee2netcdf.reader;


import org.esa.smos.dataio.smos.GridPointInfo;
import org.esa.smos.dataio.smos.dddb.BandDescriptor;
import org.esa.smos.dataio.smos.provider.ValueProvider;
import org.esa.snap.framework.datamodel.Band;
import ucar.nc2.Variable;

import java.awt.geom.Area;

interface ProductTypeSupport {

    String getLatitudeBandName();
    String getLongitudeBandName();

    String restoreBandName(String ncBandName);

    void setScalingAndOffset(Band band, BandDescriptor bandDescriptor);

    ValueProvider createValueProvider(Variable variable, BandDescriptor descriptor, Area area, GridPointInfo gridPointInfo);
}
