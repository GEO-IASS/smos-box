package org.esa.smos.ee2netcdf.variable;


import com.bc.ceres.binio.CompoundData;
import com.bc.ceres.binio.SequenceData;

import java.io.IOException;

public interface VariableWriter {

    void write(CompoundData gridPointData, SequenceData btDataList, int index) throws IOException;

    void close() throws IOException;
}
