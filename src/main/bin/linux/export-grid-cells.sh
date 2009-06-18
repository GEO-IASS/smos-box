#! /bin/sh

export BEAM4_HOME=${installer:sys.installationDir}

if [ -z "$BEAM4_HOME" ]; then
    echo
    echo Error: BEAM4_HOME not found in your environment.
    echo Please set the BEAM4_HOME variable in your environment to match the
    echo location of the BEAM 4.x installation
    echo
    exit 2
fi

. "$BEAM4_HOME/bin/detect_java_smos.sh"

"$app_java_home/bin/java" \
    -Xmx1024M \
    -Dceres.context=beam \
    "-Dbeam.mainClass=org.esa.beam.smos.visat.export.GridCellExport" \
    "-Dbeam.home=$BEAM4_HOME" \
    -jar "$BEAM4_HOME/bin/ceres-launcher.jar" "$@"

exit 0