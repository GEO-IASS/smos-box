package org.esa.beam.smos.visat;

import org.junit.Before;
import org.junit.Test;

import static org.esa.beam.smos.visat.GridPointSelectionService.SelectionListener;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class GridPointSelectionServiceTest {

    private GridPointSelectionService service;

    @Before
    public void setUp(){
        service = new GridPointSelectionService();
    }

    @Test
    public void testGetSelectedGridPoint_initialValue() {
        assertEquals(-1, service.getSelectedGridPointId());
    }

    @Test
    public void testSetGetSelectedGridPoint() {
        service.setSelectedGridPointId(108);
        assertEquals(108, service.getSelectedGridPointId());
    }

    @Test
    public void testSetGetSelectedGridPoint_stopService() {
        service.setSelectedGridPointId(108);
        assertEquals(108, service.getSelectedGridPointId());

        service.stop();
        assertEquals(-1, service.getSelectedGridPointId());
    }

    @Test
    public void testAddListener_dispatchUpdate() {
        final SelectionListener listener = mock(SelectionListener.class);

        service.addGridPointSelectionListener(listener);

        service.setSelectedGridPointId(76);

        verify(listener, times(1)).handleGridPointSelectionChanged(-1, 76);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testAddListener_doesNotDispatchWhenIdsAreSame() {
        final SelectionListener listener = mock(SelectionListener.class);

        service.addGridPointSelectionListener(listener);

        service.setSelectedGridPointId(76);
        service.setSelectedGridPointId(76);

        verify(listener, times(1)).handleGridPointSelectionChanged(-1, 76);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testAddListener_removeListener() {
        final SelectionListener listener = mock(SelectionListener.class);

        service.addGridPointSelectionListener(listener);
        service.removeGridPointSelectionListener(listener);

        service.setSelectedGridPointId(76);

        verifyNoMoreInteractions(listener);
    }
}
