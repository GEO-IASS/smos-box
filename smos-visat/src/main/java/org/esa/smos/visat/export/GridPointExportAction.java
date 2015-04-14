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

import org.esa.snap.framework.ui.command.CommandEvent;
import org.esa.snap.visat.actions.AbstractVisatAction;

/**
 * @author Ralf Quast
 * @author Marco Zuehlke
 * @version $Revision$ $Date$
 * @since SMOS 2.0
 */
public class GridPointExportAction extends AbstractVisatAction {

    @Override
    public void actionPerformed(CommandEvent event) {
        new GridPointExportDialog(getAppContext(), event.getCommand().getHelpId()).show();
    }
}
