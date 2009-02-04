package org.esa.beam.smos.visat;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnapshotSelectionService {
    private final ProductManager productManager;
    private final List<SelectionListener> selectionListeners;
    private final Map<Product, Long> snapshotIds;
    private final ProductManagerHandler productManagerHandler;

    public SnapshotSelectionService(ProductManager productManager) {
        this.selectionListeners = new ArrayList<SelectionListener>();
        this.snapshotIds = new HashMap<Product, Long>();
        this.productManagerHandler = new ProductManagerHandler();
        this.productManager = productManager;
        this.productManager.addListener(productManagerHandler);
    }

    public synchronized void stop() {
        snapshotIds.clear();
        selectionListeners.clear();
        productManager.addListener(productManagerHandler);
    }

    public synchronized long getSelectedSnapshotId(Product product) {
        Long id = snapshotIds.get(product);
        return id != null ? id : -1;
    }

    public synchronized void setSelectedSnapshotId(Product product, long id) {
        long oldId = getSelectedSnapshotId(product);
        if (oldId != id) {
            if (id >= 0) {
                snapshotIds.put(product, id);
            } else {
                snapshotIds.remove(product);
            }
            fireSelectionChange(product, oldId, id);
        }
    }

    public synchronized void addSnapshotIdChangeListener(SelectionListener selectionListener) {
        selectionListeners.add(selectionListener);
    }

    public synchronized void removeSnapshotIdChangeListener(SelectionListener selectionListener) {
        selectionListeners.remove(selectionListener);
    }

    private void fireSelectionChange(Product product, long oldId, long newId) {
        for (SelectionListener selectionListener : selectionListeners) {
            selectionListener.handleSnapshotIdChanged(product, oldId, newId);
        }
    }

    public interface SelectionListener {
        void handleSnapshotIdChanged(Product product, long oldId, long newId);
    }

    private class ProductManagerHandler implements ProductManager.Listener {
        @Override
        public void productAdded(ProductManager.Event event) {
        }

        @Override
        public void productRemoved(ProductManager.Event event) {
            setSelectedSnapshotId(event.getProduct(), -1);
        }
    }
}
