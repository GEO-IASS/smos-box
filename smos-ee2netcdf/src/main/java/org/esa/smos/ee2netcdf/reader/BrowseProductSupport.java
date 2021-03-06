
package org.esa.smos.ee2netcdf.reader;

import org.esa.smos.dataio.smos.GridPointInfo;
import org.esa.smos.dataio.smos.dddb.BandDescriptor;
import org.esa.smos.dataio.smos.dddb.FlagDescriptor;
import org.esa.smos.dataio.smos.provider.AbstractValueProvider;
import org.esa.snap.core.datamodel.Band;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.awt.geom.Area;
import java.io.IOException;

class BrowseProductSupport extends AbstractProductTypeSupport {

    private final double radAccuracyScale;
    private final double footprintScale;

    BrowseProductSupport(NetcdfFile netcdfFile) {
        super(netcdfFile);

        final Attribute radAccuracyAttribute = netcdfFile.findGlobalAttribute("Variable_Header:Specific_Product_Header:Radiometric_Accuracy_Scale");
        if (radAccuracyAttribute != null) {
            radAccuracyScale = Double.valueOf(radAccuracyAttribute.getStringValue());
        } else {
            radAccuracyScale = 1.0;
        }
        final Attribute footprintScaleAttribute = netcdfFile.findGlobalAttribute("Variable_Header:Specific_Product_Header:Pixel_Footprint_Scale");
        if (footprintScaleAttribute != null) {
            footprintScale = Double.valueOf(footprintScaleAttribute.getStringValue());
        } else {
            footprintScale = 1.0;
        }
    }

    @Override
    public String getLatitudeBandName() {
        return "Grid_Point_Latitude";
    }

    @Override
    public String getLongitudeBandName() {
        return "Grid_Point_Longitude";
    }

    @Override
    public boolean canOpenFile() {
        return containsVariable("Grid_Point_Latitude") &&
                containsVariable("Grid_Point_Longitude") &&
                containsVariable("Grid_Point_ID");
    }

    @Override
    public void setScalingAndOffset(Band band, BandDescriptor bandDescriptor) {
        final String memberName = bandDescriptor.getMemberName();
        if (memberName.startsWith("Footprint_Axis")) {
            band.setScalingFactor(bandDescriptor.getScalingFactor() * footprintScale);
            band.setScalingOffset(bandDescriptor.getScalingOffset());
        } else if (memberName.startsWith("Pixel_Radiometric_Accuracy")) {
            band.setScalingFactor(bandDescriptor.getScalingFactor() * radAccuracyScale);
            band.setScalingOffset(bandDescriptor.getScalingOffset());
        } else {
            super.setScalingAndOffset(band, bandDescriptor);
        }
    }

    @Override
    public AbstractValueProvider createValueProvider(ArrayCache arrayCache, String variableName, BandDescriptor descriptor, Area area, GridPointInfo gridPointInfo) {
        final int polarization = descriptor.getPolarization();
        if (polarization < 0) {
            return new VariableValueProvider(arrayCache, variableName, area, gridPointInfo);
        } else {
            return new BrowseValueProvider(arrayCache, variableName, polarization, area, gridPointInfo);
        }
    }

    @Override
    public boolean canSupplyGridPointBtData() {
        return true;
    }

    @Override
    public FlagDescriptor[] getBtFlagDescriptors() {
        try {
            ensureDataStructuresInitialized();
        } catch (IOException e) {
            // @todo 2 tb/tb ad logging here
        }
        return flagDescriptors;
    }
}
