package de.seuhd.campuscoffee.domain.implementation;

import de.seuhd.campuscoffee.domain.exceptions.DuplicationException;
import de.seuhd.campuscoffee.domain.model.objects.DomainModel;
import de.seuhd.campuscoffee.domain.ports.data.CrudDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CrudServiceTest {

    CrudDataService<D, Long> ds;
    CrudServiceImpl<D, Long> s;

    @BeforeEach
    void setup() {
        ds = mock(CrudDataService.class);
        s = new S(ds);
    }

    @Test
    void delegates() {
        s.clear();
        s.getById(1L);
        s.delete(1L);

        verify(ds).clear();
        verify(ds).getById(1L);
        verify(ds).delete(1L);
    }

    @Test
    void upsert_create() {
        D d = new D(null);
        when(ds.upsert(d)).thenReturn(d);
        s.upsert(d);
        verify(ds).upsert(d);
    }

    @Test
    void upsert_update() {
        D d = new D(1L);
        when(ds.getById(1L)).thenReturn(d);
        when(ds.upsert(d)).thenReturn(d);
        s.upsert(d);
        verify(ds).getById(1L);
        verify(ds).upsert(d);
    }

    @Test
    void upsert_duplicate() {
        D d = new D(null);
        when(ds.upsert(d)).thenThrow(DuplicationException.class);
        assertThrows(DuplicationException.class, () -> s.upsert(d));
    }

    static class S extends CrudServiceImpl<D, Long> {
        CrudDataService<D, Long> ds;
        S(CrudDataService<D, Long> ds) { super(D.class); this.ds = ds; }
        protected CrudDataService<D, Long> dataService() { return ds; }
    }

    static class D implements DomainModel<Long> {
        Long id;
        D(Long id) { this.id = id; }
        public Long getId() { return id; }
    }
}